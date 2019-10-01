package com.textura.framework.utils;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class GUIDialogs {

	public static void main(String[] args) {
		displayOKWindowAndWait("test");
	}
	
	/**
	 * Displays a window with an OK button and waits for window to close
	 */
	public static void displayOKWindowAndWait(String message) {
		System.out.println("Waiting for OK button click...");
		JFrame frame = new JFrame("Java paused");
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		JOptionPane.showMessageDialog(frame, message, "Java paused", JOptionPane.INFORMATION_MESSAGE);
		frame.dispose();
		System.out.println("Code continuing");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void displayOKWindowAndWait() {
		displayOKWindowAndWait("");
	}

	/**
	 * Displays a window that allows user to input a String
	 * 
	 * @param prompt
	 *            A prompt for the user
	 * @return user input
	 */
	public static String promptUserForInput(String prompt) {
		JFrame frame = new JFrame("Java paused");
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		String s = (String) JOptionPane.showInputDialog(null, prompt, "Input Prompt", JOptionPane.PLAIN_MESSAGE, null, null, "");
		frame.dispose();
		return s;
	}

	public static int askUserForHelp(String by) {
		System.out.println("Asking user for help...");
		JFrame frame = new JFrame("User Intervention Required");
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		Object[] options = { "Retry finding element", "Ignore failure and continue", "Show stack trace",
				"Allow failure to be reported" };
		int n = JOptionPane.showOptionDialog(frame, "Exception finding element: " + by, "User Intervention Required",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
		frame.dispose();
		return n;
	}

	public static void showStackTrace() {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		StringBuilder s = new StringBuilder();
		for (StackTraceElement t : ste) {
			s.append(t.toString() + "\n");
			System.out.println(t.toString());
		}
		JFrame frame = new JFrame("Stacktrace");
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		JOptionPane.showMessageDialog(frame, s.toString(), "Stacktrace", JOptionPane.INFORMATION_MESSAGE);
		frame.dispose();
	}

}