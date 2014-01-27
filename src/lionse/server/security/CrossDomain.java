package lionse.server.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class CrossDomain extends Thread {
	public static final String CROSS_DOMAIN_POLICY_REQUEST = "<policy-file-request/>";
	public static final String CROSS_DOMAIN_POLICY = "<?xml version=\"1.0\"?>"
			+ "<cross-domain-policy>" + "<allow-access-from domain=\"*\" to-ports=\"*\" />"
			+ "</cross-domain-policy>";

	private int port;
	private boolean listening = false;
	private ServerSocket serverSocket;

	public CrossDomain(int port) {
		this.port = port;
	}

	public void run() {
		try {
			serverSocket = new ServerSocket(port);
			listening = true;
			while (listening) {
				Socket request = serverSocket.accept();
				Connection connection = new Connection(request);
				connection.setDaemon(true);
				connection.start();
			}

		} catch (Exception e) {
		}
	}

	public void close() {
		try {

			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		listening = false;
	}

	public int getPort() {
		return this.port;
	}

	private class Connection extends Thread {

		private Socket request;
		private BufferedReader reader;
		private PrintWriter writer;

		public Connection(Socket request) {
			this.request = request;
		}

		public void run() {
			try {
				reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
				writer = new PrintWriter(request.getOutputStream(), true);
				if (read().equals(CROSS_DOMAIN_POLICY_REQUEST)) {
					write();
					// Server.trace("policy sent.");

				}
				reader.close();
				writer.close();
				request.close();

			} catch (Exception e) {

			}
		}

		private void write() {
			writer.write(CROSS_DOMAIN_POLICY + "\u0000");
			writer.flush();
		}

		private String read() {
			StringBuffer buffer = new StringBuffer();
			int codePoint;
			boolean zeroByteRead = false;
			try {
				do {
					codePoint = reader.read();

					if (codePoint == 0) {
						zeroByteRead = true;
					} else if (Character.isValidCodePoint(codePoint)) {
						buffer.appendCodePoint(codePoint);
					}
				} while (!zeroByteRead && buffer.length() < 200);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return buffer.toString();
		}

	}
}
