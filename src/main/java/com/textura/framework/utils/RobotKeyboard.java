package com.textura.framework.utils;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import org.testng.annotations.Test;

public class RobotKeyboard {

	@Test
	public void sendKeySequence() {
		try {
			int key[] = { KeyEvent.VK_ENTER, KeyEvent.VK_ENTER, KeyEvent.VK_ENTER, KeyEvent.VK_ENTER };
			Robot r = new Robot();

			for (int i = 0; i < key.length; i++) {
				r.keyPress(key[i]);
				r.keyRelease(key[i]);

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}