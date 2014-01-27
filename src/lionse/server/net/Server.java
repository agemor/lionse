package lionse.server.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import lionse.server.db.Database;
import lionse.server.net.user.User;
import lionse.server.security.CrossDomain;
import lionse.server.stage.Stage;
import lionse.server.util.Timestamp;

public class Server {

	private ServerSocket serverSocket;
	private CrossDomain crossDomain;
	private ClientReceiver clientReceiver;
	private int port;
	private int crossDomainPort;
	private boolean running = false;

	public static Map<Integer, Stage> stages = new HashMap<Integer, Stage>();
	public static String SECRET_KEY = "a7g3e4m3or";

	public Server(int port, int crossDomainPort) {
		this.port = port;
		this.crossDomainPort = crossDomainPort;
		// trace(SHA256.digest("message digest"));
	}

	public void start() {
		if (running)
			return;
		try {

			trace("LIONSE v1.23a\n");

			// connect to database server.
			trace(">> connect to database server at port 3306...");
			Database.connect();

			// initialize server socket
			trace(">> starting server socket at port " + port + "...");
			serverSocket = new ServerSocket(port);

			// start server for cross-domain policy request
			trace(">> starting cross-domain policy socket at port " + crossDomainPort + "...");
			crossDomain = new CrossDomain(crossDomainPort);
			crossDomain.start();

			// start client receiver
			clientReceiver = new ClientReceiver();
			clientReceiver.start();

			// load stages
			trace(">> loading stages...");
			loadStages();

			trace("\n[" + Timestamp.get() + "] server is running...");
			running = true;
			// close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			trace(">> server is shutting down...");
			crossDomain.close();
			serverSocket.close();
			// System.exit(0);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private void loadStages() {
		// load stages from external storage.
		for (int i = 0; i < 6; i++) {
			stages.put(i, new Stage());
		}
	}

	public static void trace(Object message) {
		System.out.println(message);
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public static class Configuration {
		public static int MAX_CONCURRENT_USERS = 1000;
		public static boolean DUPLICATION_ALLOWED = true;
	}

	private class ClientReceiver extends Thread {
		public void run() {
			while (!serverSocket.isClosed()) {
				try {
					Socket client = serverSocket.accept();
					User user = new User(client);
					Thread thread = new Thread(user);
					thread.start();
					trace("[" + Timestamp.get() + "] connection established at "
							+ user.getIPAddressAndPort());
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}
}
