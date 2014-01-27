package lionse.client.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import lionse.client.asset.Asset;
import lionse.client.debug.Debugger;
import lionse.client.net.user.User;
import lionse.client.net.user.UserList;
import lionse.client.security.crypto.BASE64;
import lionse.client.security.crypto.SHA256;
import lionse.client.stage.Path;
import lionse.client.stage.Stage.Point;
import lionse.client.stage.Character;

public class Server {

	public static final String PACKET_END = "/";

	public static final String H_L = ";"; // header - level0
	public static final String H_M = "\\|"; // header - level1

	public static final String ENCODING = "UTF-8";

	// current socket writing state
	public static final int WAIT = 0;
	public static final int LOGIN_TRY = 1;
	public static final int JOIN_TRY = 2;
	public static final int REGISTER_TRY = 3;

	// server connection
	public static String host;
	public static int port;

	// socket
	public static Socket socket;
	public static Receiver receiver;
	public static PrintWriter writer;

	// flags
	public static boolean connected = false;
	public static boolean logined = false;
	public static boolean joined = false;
	public static boolean loaded = false;
	private static boolean signal = true;

	public static int state = 0;

	// session
	public static String session;

	// users
	public static UserList users = new UserList();
	public static User me;

	private static ServerEvent serverEventTarget;
	private static UserEvent userEventTarget;

	public Server() {

	}

	// connect server
	public static void connect(String host, int port) {

		Server.host = host;
		Server.port = port;

		socket = new Socket();

		try {
			socket.connect(new InetSocketAddress(host, port));
			writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), ENCODING));
			signal = true;
			receiver = new Receiver(socket);
			receiver.start();
			if (socket.isConnected())
				connected = true;

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setServerEventListener(ServerEvent serverEventTarget) {
		Server.serverEventTarget = serverEventTarget;
	}

	public static void setUserEventListener(UserEvent userEventTarget) {
		Server.userEventTarget = userEventTarget;
	}

	public static void send(String message) {
		if (!connected)
			return;
		writer.write(message + PACKET_END + '\n');
		writer.flush();
	}

	// login
	public static void login(String id, String password) {
		if (!connected)
			return;
		state = LOGIN_TRY;
		send(Header.LOGIN + H_L + id + H_L + SHA256.digest(password));
	}

	// register
	public static void register(String id, String password, String character, String email) {
		if (!connected)
			return;
		state = REGISTER_TRY;
		send(Header.REGISTER + H_L + id + H_L + SHA256.digest(password) + H_L
				+ BASE64.encodeString(character) + H_L + email);

	}

	public static void close() {
		try {
			socket.close();
			writer.close();
			signal = false;
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// game join
	public static void join() {
		if (!connected || !logined || session == null)
			return;
		send(Header.SESSION + H_L + session);
		state = JOIN_TRY;
	}

	// get server's message
	public static class Receiver extends Thread {

		public Socket socket;
		public BufferedReader reader;

		public Receiver(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try {

				reader = new BufferedReader(
						new InputStreamReader(socket.getInputStream(), ENCODING));

				while (true) {
					String data = reader.readLine();

					// multi-packet processing
					String[] buffer = data.split(PACKET_END);
					for (int i = 0; i < buffer.length; i++) {
						handlePacket(buffer[i]);
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// methods for handling packets.
		public void handlePacket(String packet) {
			// Debugger.log("packet: " + packet);
			// manufacture packet. (remove tails.)
			String[] buffer = packet.split(H_L);

			// header of the packet
			String header = buffer[0];

			// caution! header of the user list is different (H_M)
			if (String.valueOf(buffer[0].charAt(0)).equals(Header.USER_LIST)) {
				header = Header.USER_LIST;
			}

			switch (state) {
			case LOGIN_TRY:
				// session sent. login succeed.
				if (header.equals(Header.SESSION)) {

					// set variables.
					session = buffer[1];
					logined = true;
					state = WAIT;

					// dispatch event.
					if (serverEventTarget != null)
						serverEventTarget.login(true);

					Debugger.log("Login succeed: " + session);
					// login failed
				} else if (header.equals(Header.ERROR) && buffer[1].equals(ErrorCode.LOGIN_FAILED)) {

					// set variables.
					logined = false;
					state = WAIT;

					// dispatch event
					if (serverEventTarget != null)
						serverEventTarget.login(false);
					Debugger.log("Login failed.");
				}
				break;
			case JOIN_TRY:
				// join succeed
				if (header.equals(Header.SERVER) && buffer[1].equals("d")) {
					joined = true;
					state = WAIT;
					if (serverEventTarget != null)
						serverEventTarget.join(true);
					Debugger.log("Join succeed.");
					// join failed -> wrong session
				} else if (header.equals(Header.ERROR)
						&& buffer[1].equals(ErrorCode.CANNOT_FIND_SESSION)) {
					if (serverEventTarget != null)
						serverEventTarget.join(false);
					Debugger.log("Join failed.");
				}
				break;
			case REGISTER_TRY:
				// sign up succeed
				if (header.equals(Header.SERVER) && buffer[1].equals("d")) {

					state = WAIT;
					if (serverEventTarget != null)
						serverEventTarget.register(true);
					Debugger.log("Register succeed.");
					// sign up failed
				} else if (header.equals(Header.ERROR) && buffer[1].equals(ErrorCode.DB_ERROR)) {
					state = WAIT;
					if (serverEventTarget != null)
						serverEventTarget.register(false);
					Debugger.log("Register failed.");
				}

				break;
			}

			// not using switch(string) for android, under java 1.70

			// new user arrival
			if (header.equals(Header.NEW)) {
				if (buffer[1].equals(me.code))
					return;

				// set user variables.
				User user = new User(buffer[1]);
				user.level = Integer.parseInt(buffer[2]);
				user.head = Integer.parseInt(buffer[3]);
				user.body = Integer.parseInt(buffer[4]);
				user.weapon = Integer.parseInt(buffer[5]);
				user.stage = me.stage;
				user.position = new Point(Integer.parseInt(buffer[6]), Integer.parseInt(buffer[7]),
						Integer.parseInt(buffer[8]));
				user.character = new Character(user.name, Asset.Character.get("HEAD"),
						Asset.Character.get("FACE"), Asset.Character.get("BODY"), 0);
				users.add(user);

				// dispatch event
				if (userEventTarget != null)
					userEventTarget.arrive(user);

				// user leave
			} else if (header.equals(Header.LEAVE)) {
				if (buffer[1].equals(me.code))
					return;
				User user = users.get(buffer[1]);
				users.remove(user.code);

				// dispatch event
				if (userEventTarget != null)
					userEventTarget.leave(user);

				// you' re character info receipt!
			} else if (header.equals(Header.CHARACTER)) {

				me = new User(buffer[1]);
				me.level = Integer.parseInt(buffer[2]);
				me.experience = Integer.parseInt(buffer[3]);
				me.maxHealth = Integer.parseInt(buffer[4]);
				me.health = Integer.parseInt(buffer[5]);
				me.money = Integer.parseInt(buffer[6]);
				me.head = Integer.parseInt(buffer[7]);
				me.body = Integer.parseInt(buffer[8]);
				me.weapon = Integer.parseInt(buffer[9]);
				me.stage = Integer.parseInt(buffer[10]);
				me.position = new Point(Integer.parseInt(buffer[11]), Integer.parseInt(buffer[12]),
						Integer.parseInt(buffer[13]));
				me.character = new Character(me.name, Asset.Character.get("HEAD"),
						Asset.Character.get("FACE"), Asset.Character.get("BODY"), 0);
				me.character.position = me.position;
				me.character.me = true;
				users.add(me);

				// set flags
				loaded = true;

				// dispatch event
				if (userEventTarget != null)
					userEventTarget.load(me);

				// current stage's user list
			} else if (header.equals(Header.USER_LIST)) {

				// clear previous user list
				users.clear();

				// splitter's different.
				String[] data = packet.split(H_M);
				for (int i = 1; i < data.length; i++) {
					String[] chunk = data[i].split(H_L);
					if (data[i].equals("") || chunk[0].equals(me.code))
						continue;
					User user = new User(chunk[0]);
					user.level = Integer.parseInt(chunk[1]);
					user.head = Integer.parseInt(chunk[2]);
					user.body = Integer.parseInt(chunk[3]);
					user.weapon = Integer.parseInt(chunk[4]);
					user.stage = me.stage;
					user.position = new Point(Integer.parseInt(chunk[5]),
							Integer.parseInt(chunk[6]), Integer.parseInt(chunk[7]));
					user.character = new Character(user.name, Asset.Character.get("HEAD"),
							Asset.Character.get("FACE"), Asset.Character.get("BODY"), 0);

					users.add(user);
				}
				users.add(me);
				// dispatch event
				if (userEventTarget != null)
					userEventTarget.userList(users.getList());

				// custom broadcasting format
			} else if (header.equals(Header.BROADCAST)) {

				// custom format #1. chatting
				if (buffer[1].equals(Header.CHAT)) {
					User user = users.get(buffer[2]);
					user.message = buffer[3];
					if (userEventTarget != null)
						userEventTarget.chat(user);
				}

				// user move and stop
			} else if (header.equals(Header.MOVE)) {
				if (buffer[1].equals(me.code))
					return;

				// 이동 패스는 위에 스택이 쌓이면 바로 무시된다.

				User user = users.get(buffer[1]);

				user.direction = Integer.parseInt(buffer[2]);
				user.speed = Float.valueOf(buffer[3]);
				user.state = User.MOVE;

				// 이동을 시작하였거나, 방향을 전환한 좌표
				Point point = new Point(Float.valueOf(buffer[4]), Float.valueOf(buffer[5]),
						Float.valueOf(buffer[6]));

				// 보관 중이거나 실행 중인 방향 이동 명령을 비활성화시킨다.
				if (user.movePath != null)
					user.movePath.disable();

				// 좌표를 체크해서 이동-정지 명령인지, 이동 명령인지 구분
				if (Math.sqrt(Math.pow(point.x - user.character.position.x, 2)
						+ Math.pow(point.y - user.character.position.y, 2)) > 3) {

					// 이동-정지 명령으로 판명. 오차범위가 3보다 크다.

					// 정지 명령을 추가한다.
					// @@ 문제 발생의 여지가 있다. 방향 문제가 생길 경우, 이전 이동 방향의 정방향, 역방향인지를
					// 체크하는 메서드로 대체할 것.

					Path path = new Path(user.character.getDirection(point), point);

					// x 변위와 y 변위로 스케일을 계산한다.
					float xScale = Math.abs(point.x - user.character.position.x);
					float yScale = Math.abs(point.y - user.character.position.y);

					if (xScale > yScale) { // x변위가 더 크다. y속도에 감소값을 곱해준다.
						path.velocityX = point.x - user.character.position.x > 0 ? 1 : -1;
						path.velocityY = (point.y - user.character.position.y > 0 ? 1 : -1)
								* yScale / xScale;
					} else {
						path.velocityX = (point.x - user.character.position.x > 0 ? 1 : -1)
								* xScale / yScale;
						path.velocityY = point.y - user.character.position.y > 0 ? 1 : -1;
					}

					user.character.path.add(path);
				}

				// 캐릭터의 스택에 이동 명령 추가
				Path move = new Path(user.direction, null);
				user.character.path.add(move);

				// 나중에 정지 명령 하달 시 취소할 수 있도록 레퍼런스 등록
				user.movePath = move;

				// dispatch event
				if (userEventTarget != null)
					userEventTarget.move(user);
				// user stop
			} else if (header.equals(Header.STOP)) {
				if (buffer[1].equals(me.code))
					return;

				User user = users.get(buffer[1]);
				user.state = User.WAIT;

				// 보관 중이거나 실행 중인 방향 이동 명령을 비활성화시킨다.
				user.movePath.disable();

				// 이동을 멈춘 좌표
				Point point = new Point(Float.valueOf(buffer[2]), Float.valueOf(buffer[3]),
						Float.valueOf(buffer[4]));
				// 한계값 체크. (서버에 캐릭터의 실위치가 소수부분 없이 전송이 되기 때문에 그냥 비교하면 오차가 생긴다)
				if (Math.sqrt(Math.pow(point.x - user.character.position.x, 2)
						+ Math.pow(point.y - user.character.position.y, 2)) > 3) {
					// 이동-정지 명령으로 판명. 오차범위가 3보다 크다.
					Path path = new Path(user.character.getDirection(point), point);

					// x 변위와 y 변위로 스케일을 계산한다.
					float xScale = Math.abs(point.x - user.character.position.x);
					float yScale = Math.abs(point.y - user.character.position.y);

					if (xScale > yScale) { // x변위가 더 크다. y속도에 감소값을 곱해준다.
						path.velocityX = point.x - user.character.position.x > 0 ? 1 : -1;
						path.velocityY = (point.y - user.character.position.y > 0 ? 1 : -1)
								* yScale / xScale;
					} else {
						path.velocityX = (point.x - user.character.position.x > 0 ? 1 : -1)
								* xScale / yScale;
						path.velocityY = point.y - user.character.position.y > 0 ? 1 : -1;
					}

					user.character.path.add(path);
				}
				// dispatch event
				if (userEventTarget != null)
					userEventTarget.stop(user);
			} else {
				// undefined header

			}

		}
	}

}
