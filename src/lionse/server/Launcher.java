package lionse.server;

import lionse.server.net.Server;

public class Launcher {
	
	public static void main(String[] args) {
		Server server = new Server(7343, 9039);
		server.start();
	}

}
