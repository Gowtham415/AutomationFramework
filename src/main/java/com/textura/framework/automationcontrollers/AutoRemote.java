package com.textura.framework.automationcontrollers;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class AutoRemote extends Thread {

	public boolean stop = false;
	public boolean pause = false;
	public double sleep = 0;

	public void run() {
		while (!stop) {
			JFrame frame = new JFrame("Automation Controller");
			frame.setVisible(true);
			frame.setLocationRelativeTo(null);
			Object[] options = { "Pause", "Resume", "Slowdown" };
			int n = JOptionPane.showOptionDialog(frame, "", "Automation Remote", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			frame.dispose();
			if (n == 0) {
				pause = true;
			}
			if (n == 1) {
				pause = false;
			}
			if (n == 2) {
				JFrame frame2 = new JFrame("");
				frame2.setVisible(true);
				frame2.setLocationRelativeTo(null);
				String input = TextInputOptionPane.showInputDialog(frame2, "Enter sleep amount (seconds)");
				frame2.dispose();
				if (input == null || input.equals("null") || input.length() < 1) {
					sleep = 0;
				} else {
					try {
						sleep = Double.parseDouble(input);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				System.out.println("Sleep set to: " + sleep);
			}
			if (n == -1) {
				pause = false;
				return;
			}
		}
	}
}