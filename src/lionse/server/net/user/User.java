package lionse.server.net.user;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import lionse.server.db.Database;
import lionse.server.db.Database.UserInfo;
import lionse.server.net.ErrorCode;
import lionse.server.net.Header;
import lionse.server.net.Server;
import lionse.server.security.Session;
import lionse.server.security.crypto.MD5;
import lionse.server.stage.Character;
import lionse.server.util.Timestamp;

public class User implements Runnable {

	// ***************************************************************

	public final static String PACKET_END = "/";

	// ***************************************************************

	public final static String H_L = ";"; // header - level0
	public final static String H_M = "|"; // header - level1

	public final static String S_DB = "d"; // status is database reading

	// ***************************************************************

	public Socket connection;

	private DataInputStream inputStream;
	private BufferedReader reader;
	private PrintWriter writer;

	private boolean registered = false;

	// ***************************************************************

	// user db info
	private String id;

	// user character
	public Character character;

	public boolean banned = false;

	public int level = 0;

	public String lastlogin = "";

	public int loginFaildCounter = 0;

	private String session;

	// ***************************************************************

	public User(Socket connection) throws IOException {
		this.connection = connection;

		inputStream = new DataInputStream(connection.getInputStream());
		reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
		writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(), "utf-8"));

		// Server.trace(SHA256.digest("asd123"));
	}

	@Override
	public void run() {
		try {
			// check if it exceeds max concurrent users limit.
			if (Session.count() >= Server.Configuration.MAX_CONCURRENT_USERS) {
				// close the connection
				close(ErrorCode.EXCEEDS_MAXUSER);
			}
			// if user duplication is forbidden, check if it is duplication.
			if (!Server.Configuration.DUPLICATION_ALLOWED) {
				// if it is duplication, close the connection.
				if (Session.isAlive(getIPAddress())) {
					close(ErrorCode.DUPLICATED);
				} else {
					// if it isn't, than register the session.
					Session.addAliveIP(getIPAddress());
				}
			} else {
				Session.addAliveIP(getIPAddressAndPort());
			}

			send("welcome to lionse.\n");

			// repeat until client shut down
			while (true) {

				// read buffers from line;
				String buffer = reader.readLine();

				// this is for client's real-time check. do not remove.
				buffer.length();

				String header = buffer.split(H_L)[0];
				// Server.trace(header);
				// if unregistered, read message and register the client.
				if (!registered) {
					// client sends session key.
					if (header.equals(Header.SESSION)) {
						String target = Session
								.getSession(buffer.split(PACKET_END)[0].split(H_L)[1]);
						if (target == null) {
							// client sends session that is not registered.
							// report error.
							send(Header.ERROR + H_L + ErrorCode.CANNOT_FIND_SESSION);
							close(ErrorCode.SESSION_ERROR);
							break;
							// continue;
						}
						id = target;
						// send 'we are reading your db data' message.
						send(Header.SERVER + H_L + S_DB);
						character = Database.getCharacter(id);

						// send 'we are sending your character's data'
						send(Header.CHARACTER + H_L + character.getPacket());

						// welcome! but i would like to register you in the
						// stage and user list.
						getUserList().add(this);

						// you are new to this world. so I let you to know whom
						// is already in this world.
						send(Header.USER_LIST + H_M + getUserList().getPacket());

						// and i will send welcome messages to others for you.
						getUserList().sendToAll(Header.NEW + H_L + getPacket());

						// all procedures are finished.
						registered = true;

						Server.trace("reg com! all dta snt");

					} else if (header.equals(Header.LOGIN)) { // else,
																// login! to
																// receive
																// session
																// key!

						String[] chunk = buffer.split(PACKET_END)[0].split(H_L);
						// login check
						if (Database.login(chunk[1], chunk[2])) {

							// login succeed. i will send you session key.
							// TOP SECRET: SESSION KEY MANUFACTURING:
							// md5(id+secret_key)+md5(pw)
							session = MD5.digest(chunk[1] + Server.SECRET_KEY)
									+ MD5.digest(chunk[2]);

							// and get the character's name
							UserInfo userInfo = Database.getUserInfo(chunk[1]);
							banned = userInfo.banned; // you will be punished
														// later!
							level = userInfo.level;
							lastlogin = userInfo.lastlogin;

							// register session for future use.
							Session.startSession(session, userInfo.character);

							// send session.
							send(Header.SESSION + H_L + session);
							loginFaildCounter = 0;

						} else { // login failed... don't upset! just try again!

							if (loginFaildCounter > 5) { // you are assumed as a
															// bot! go away
								close(ErrorCode.TOO_MANY_LOGIN_FAILURE);
								break;
							}
							send(Header.ERROR + H_L + ErrorCode.LOGIN_FAILED);
							loginFaildCounter++;
						}
					} else if (header.equals(Header.SIGNUP)) { // sign up!

						String[] chunk = buffer.split(PACKET_END)[0].split(H_L);
						if (Database.newUser(chunk[1], chunk[2], chunk[3], chunk[4])) {
							send(Header.SERVER + H_L + S_DB); // succeed. good
																// luck!
						} else {
							send(Header.ERROR + H_L + ErrorCode.DB_ERROR);
						}

					} else {
						Server.trace(buffer);
					}
					// if not registered, do not receive other data. end.
					continue;
				}

				// welcome to registered zone

				// various requests! all we accept!
				switch (buffer.split(H_L)[0]) {
				case Header.ZAFOK:
					System.exit(0);
					break;
				case Header.BROADCAST: // can be used for chat.
					getUserList().sendToAll(buffer.trim());
					break;
				case Header.USER_LIST: // requesting user list again!
					send(Header.USER_LIST + H_M + getUserList().getPacket());
					break;
				case Header.MOVE: // start moving,
					// move,character,direction,x,y,z
					String[] data = buffer.split(PACKET_END)[0].split(H_L);
					character.location.x = Integer.parseInt(data[3]);
					character.location.y = Integer.parseInt(data[4]);
					character.location.z = Integer.parseInt(data[5]);
					getUserList().sendToAll(Header.MOVE + H_L + character.name + H_L + data[1]
							+ H_L + data[2] + H_L + data[3] + H_L + data[4] + H_L + data[5]);
					break;
				case Header.STOP: // stop user and update location variables.
					String[] position = buffer.split(PACKET_END)[0].split(H_L);
					character.location.x = Integer.parseInt(position[1]);
					character.location.y = Integer.parseInt(position[2]);
					character.location.z = Integer.parseInt(position[3]);
					getUserList().sendToAll(Header.STOP + H_L + character.name + H_L + position[1]
							+ H_L + position[2] + H_L + position[3]);
				}

			}
			// Server.trace("connection closed");
		} catch (Exception e) {
			e.printStackTrace();
			if (registered) { // this is for emergency
				upload();
			}
			try {
				close(ErrorCode.SOCKET_ERROR);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} finally {
			// Server.trace("connection closed");
		}
	}

	private UserList getUserList() {
		return Server.stages.get(character.location.stage).userList;
	}

	// upload data to database
	public void upload() {
		Database.updateCharacter(character);
	}

	public String getPacket() {
		return character.name + H_L + character.level + H_L + character.equipment.hat + H_L
				+ character.equipment.clothes + H_L + character.equipment.weapon + H_L
				+ character.location.x + H_L + character.location.y + H_L + character.location.z;
	}

	public void send(String data) {
		writer.print(data + PACKET_END + "\n");
		writer.flush();
	}

	String status = "";

	public void close(int reason) throws IOException {

		switch (reason) {
		case ErrorCode.USER_REQUEST:
			status = "user request";
			break;
		case ErrorCode.SESSION_ERROR:
			status = "session error";
			break;
		case ErrorCode.SOCKET_ERROR:
			status = "socket error";
			break;
		case ErrorCode.TOO_MANY_LOGIN_FAILURE:
			status = "too many login failure";
			break;
		case ErrorCode.EXCEEDS_MAXUSER:
			status = "exceeds max user";
			break;
		case ErrorCode.DUPLICATED:
			status = "duplicated";
			break;
		}

		Server.trace("[" + Timestamp.get() + "] connection closed(" + status + ") at "
				+ getIPAddressAndPort());

		// if registered user, remove from user list and broadcast that he is
		// leaving.
		if (registered) {
			// important! upload your data to database
			upload();
			// remove from the user list
			getUserList().remove(this);
			// broadcast
			getUserList().sendToAll(Header.LEAVE + H_L + character.name);

			// remove from session too
			Session.endSession(session);
		}

		inputStream.close();
		writer.close();
		connection.close();

		if (!Server.Configuration.DUPLICATION_ALLOWED) {
			Session.removeDeadIP(getIPAddress());
		} else {
			Session.removeDeadIP(getIPAddressAndPort());
		}
	}

	public String getIPAddress() {
		return String.valueOf(connection.getInetAddress());
	}

	public String getIPAddressAndPort() {
		return String.valueOf(connection.getInetAddress()) + ":"
				+ String.valueOf(connection.getPort());
	}
}
