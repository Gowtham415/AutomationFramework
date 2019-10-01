package com.textura.framework.automationcontrollers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Manages a server to listen for commands for an Automation run. 
 * start() will start the server which will listen for requests. Requests can be sent using sendCommand()
 */
public class AutomationController extends Thread {

	private boolean pause = false;
	private boolean stop = false;
	private boolean throwExceptions = false;
	static int port = 8037;

	public static void main(String[] args) throws InterruptedException {
		AutomationController a = new AutomationController();
		a.start();

		System.out.println(a.shouldPause());
		sendCommand("localhost", Control.PAUSE);
		Thread.sleep(1000);
		System.out.println(a.shouldPause());
		sendCommand("localhost", Control.UNPAUSE);
		Thread.sleep(1000);
		System.out.println(a.shouldPause());
		a.stopServer();
	}

	public void stopServer() {
		stop = true;
	}
	
	public boolean throwExceptions() {
		return throwExceptions;
	}

	/**
	 * Starts the server to listen for commands
	 */
	public void run() {
		while (!stop) {
			try {
				ServerSocket serverSocket = null;
				try {
					serverSocket = new ServerSocket(port);
				} catch (IOException e) {
					Thread.sleep(5000);
				}
				Socket clientSocket = null;
				try {
					serverSocket.setSoTimeout(5000);
					clientSocket = serverSocket.accept();
				} catch (IOException e) {
					serverSocket.close();
					continue;
				}

				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					if (inputLine.equals("pause")) {
						pause = true;
					}
					if (inputLine.equals("unpause")) {
						pause = false;
					}
					if (inputLine.equals("throwExceptions")) {
						throwExceptions = true;
					}
				}
				serverSocket.close();
			} catch (Exception e) {
			}
		}
	}

	public boolean shouldPause() {
		return pause;
	}

	/**
	 * Client side method to connect to the server to execute commands
	 * @param host
	 * @param c
	 */
	public static void sendCommand(String host, Control c) {
		if (c.equals(Control.PAUSE)) {
			connectToSocket(host, port, "pause", false);
		} else if (c.equals(Control.UNPAUSE)) {
			connectToSocket(host, port, "unpause", false);
		} else if (c.equals(Control.THROWEXCEPTIONS)) {
			connectToSocket(host, port, "throwExceptions", false);
		}
	}

	protected static void connectToSocket(String host, int port, String data, boolean wait) {
		try {

			Socket echoSocket = new Socket();
			echoSocket.connect(new InetSocketAddress(host, port), 15000);
			PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

			BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

			out.println(data);
			if (wait) {
				in.readLine();
			}

			out.close();
			in.close();
			stdIn.close();
			echoSocket.close();
		} catch (Exception e) {
			System.err.println("Failed connecting to socket: " + host + ":" + port);
			e.printStackTrace();
		}
	}

	public enum Control {
		PAUSE, UNPAUSE, THROWEXCEPTIONS;
	}
}
