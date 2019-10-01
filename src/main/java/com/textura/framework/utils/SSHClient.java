package com.textura.framework.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.textura.framework.objects.main.Page;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class SSHClient {

	private static final Logger LOG = LogManager.getLogger(SSHClient.class);

	public static String execute(String username, String password, String host, String command) {
		boolean isAuthenticated = false;
		StringBuilder output = new StringBuilder();
		try {
			Connection conn = new Connection(host);
			conn.connect();

			isAuthenticated = conn.authenticateWithPassword(username, password);
			if (isAuthenticated == false)
				throw new IOException("Authentication failed.");

			Session sess = conn.openSession();
			sess.execCommand(command);

			InputStream stdout = new StreamGobbler(sess.getStdout());
			InputStream stderr = new StreamGobbler(sess.getStderr());

			InputStreamReader insrout = new InputStreamReader(stdout);
			InputStreamReader insrerr = new InputStreamReader(stderr);

			BufferedReader stdoutReader = new BufferedReader(insrout);
			BufferedReader stderrReader = new BufferedReader(insrerr);
			while (true) {
				String line = stdoutReader.readLine();
				if (line == null) {
					stdoutReader.close();
					break;
				}
				output.append(line + "\n");
			}
			while (true) {
				String line = stderrReader.readLine();
				if (line == null) {
					stderrReader.close();
					break;
				} else {
					Page.printFormattedMessage(line);
				}

			}
			sess.close();
			conn.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
		return output.toString();
	}

	public static String executeNoWait(String username, String password, String host, String command) {
		boolean isAuthenticated = false;
		StringBuilder output = new StringBuilder();
		try {
			Connection conn = new Connection(host);
			conn.connect();

			isAuthenticated = conn.authenticateWithPassword(username, password);
			if (isAuthenticated == false)
				throw new IOException("Authentication failed.");

			Session sess = conn.openSession();
			sess.execCommand(command);

			sess.close();
			conn.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
		return output.toString();
	}

	public static String execute(String username, String password, String host, String command, long timeout, boolean waitForErrorStream) {
		boolean isAuthenticated = false;
		StringBuilder output = new StringBuilder();
		StringBuilder err = new StringBuilder();
		try {
			Connection conn = new Connection(host);
			conn.connect();

			isAuthenticated = conn.authenticateWithPassword(username, password);
			if (isAuthenticated == false)
				throw new IOException("Authentication failed.");

			Session sess = conn.openSession();
			sess.execCommand(command);

			InputStream stdout = new StreamGobbler(sess.getStdout());
			InputStream stderr = new StreamGobbler(sess.getStderr());

			InputStreamReader insrout = new InputStreamReader(stdout);
			InputStreamReader insrerr = new InputStreamReader(stderr);

			BufferedReader stdoutReader = new BufferedReader(insrout);
			BufferedReader stderrReader = new BufferedReader(insrerr);
			if (waitForStreamReady(stdoutReader, timeout)) {
				while (waitForStreamReady(stdoutReader, Math.max(1, (timeout / 5)))) {

					String line = stdoutReader.readLine();
					if (line == null) {
						stdoutReader.close();
						break;
					}
					output.append(line + "\n");
				}
			} else {
				LOG.debug("Timed out waiting for stdoutReader to be ready for command " + command);
			}
			if (waitForErrorStream && waitForStreamReady(stderrReader, timeout)) {
				while (waitForStreamReady(stderrReader, timeout)) {
					String line = stderrReader.readLine();
					if (line == null) {
						stderrReader.close();
						break;
					}
					err.append(line + "\n");
				}
			} else {
				if (waitForErrorStream) {
					LOG.debug("Timed out waiting for stderrReader to be ready for command " + command);
				}
			}
			sess.close();
			conn.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
		return output.toString() + "\n" + err.toString();
	}

	private static boolean waitForStreamReady(BufferedReader br, long timeout) {
		boolean success = false;
		for (int attempt = 0; attempt < timeout; attempt++) {
			try {
				if (br.ready()) {
					success = true;
					break;
				} else {
					Thread.sleep(1000);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return success;
	}
}