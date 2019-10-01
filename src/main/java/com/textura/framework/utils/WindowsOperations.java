package com.textura.framework.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.textura.framework.environment.Project;
import com.textura.framework.objects.main.Obj;

public class WindowsOperations {

	public static boolean closeWindow(String windowName, int timeout) {
		int exitCode = -1;
		if (Obj.environmentInfo.browser.equals("Firefox")) {
			exitCode = WinCommandLine.executeCommand(Project.scripts("closeWindow.exe") + " \"" + windowName + "\" " + Integer.toString(
					timeout));
		} else if (Obj.environmentInfo.browser.equals("Internet Explorer 8")) {
			windowName = "File Download";
			exitCode = WinCommandLine.executeCommand(Project.scripts("closeWindow.exe") + " \"" + windowName + "\" " + Integer.toString(
					timeout));
		} else if (Obj.environmentInfo.browser.equals("Chrome")) {
			windowName = "Save As";
			exitCode = WinCommandLine.executeCommand(Project.scripts("closeWindow.exe") + " \"" + windowName + "\" " + Integer.toString(
					timeout));
		}

		if (exitCode == 0)
			return true;
		else
			return false;
	}

	public static boolean listContainsPartialString(List<String> list, String partialString) {
		boolean result = false;
		for (String s : list) {
			if (s.contains(partialString)) {
				result = true;
				break;
			}
		}
		return result;
	}

	public static boolean closeWindowPartialName(String partialWindowTitle, int timeout) {
		for (int i = 0; i < timeout; i++) {
			List<String> stringList = getAllWindowNames();
			if (Obj.environmentInfo.browser.equals("Firefox")) {
				if (listContainsPartialString(stringList, partialWindowTitle)) {
					closeWindow(partialWindowTitle, 10);
					return true;
				}
			} else if (Obj.environmentInfo.browser.equals("Chrome")) {
				if (stringList.contains("Save As")) {
					closeWindow("Save As", 10);
					return true;
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	public static List<String> getAllWindowNames() {
		final List<String> windowNames = new ArrayList<String>();
		User32.INSTANCE.EnumWindows(new User32.WNDENUMPROC() {

			@Override
			public boolean callback(HWND hwnd, Pointer arg) {
				char[] windowText = new char[512];
				User32.INSTANCE.GetWindowText(hwnd, windowText, 512);
				String wText = Native.toString(windowText).trim();
				if (!wText.isEmpty()) {
					windowNames.add(wText);
				}
				return true;
			}
		}, null);
		return windowNames;
	}

	/**
	 * Load up script to wait for the file upload window to pop up. If loaded
	 * after the window has popped up, execution of the java code may halt and
	 * not execute the script.
	 * 
	 */
	public static boolean readyFileUpload(String windowName, String path, int timeout) {
		try {
			String[] commands = new String[] {};
			String exe = Project.scripts("attachFile.exe");
			commands = new String[] { exe, windowName, path, Integer.toString(timeout) };
			Runtime.getRuntime().exec(commands);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Saves a file for internet explorer.
	 * 
	 * @param path
	 * @param timeout
	 */
	public static void saveFileIE(String window1, String window2, String window3, String path, int timeout) {
		try {
			String[] commands = new String[] {};
			String exe = Project.scripts("saveFileIE.exe");
			commands = new String[] { exe.toString(), window1, window2, window3, path, Integer.toString(timeout) };
			Process p = Runtime.getRuntime().exec(commands);
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Saves a file for Firefox. Assumes the profile setting
	 * firefoxProfile.setPreference("browser.download.useDownloadDir", false);
	 * is set
	 * 
	 * @param path
	 * @param timeout
	 */
	public static void saveFileFF(String window1, String window2, String path, int timeout) {
		try {
			String[] commands = new String[] {};
			String exe = Project.scripts("saveFileFF.exe");
			Thread.sleep(5000);
			commands = new String[] { exe.toString(), window1, window2, path, Integer.toString(timeout) };
			Process p = Runtime.getRuntime().exec(commands);
			boolean timedOut = !p.waitFor(timeout, TimeUnit.SECONDS);
			if (timedOut) {
				p.destroy();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// OLD WAY OF CLOSING WINDOWS public static boolean closeWindow(String windowName, int timeout) {
	// int exitCode = -1;
	// if (environment.browser.equals("Firefox")) {
	// exitCode = WinCommandLine.executeCommand(Project.scripts("closeWindow.exe") + " \"" + windowName + "\" " + Integer.toString(timeout));
	// }
	// else if (environment.browser.equals("Internet Explorer 8")) {
	// windowName = "File Download";
	// exitCode = WinCommandLine.executeCommand(Project.scripts("closeWindow.exe") + " \"" + windowName + "\" " + Integer.toString(timeout));
	// }
	//
	// if (exitCode == 0)
	// return true;
	// else
	// return false;
	// }

	public static void runCommandQuiet(String startDirectory, String... args) {
		try {
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.directory(new File(startDirectory));
			Process p = pb.start();
			BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((b.readLine()) != null) {
			}
			b = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			while ((b.readLine()) != null) {
			}
			p.waitFor();
		} catch (Exception e) {
		}
	}

}
