package com.textura.framework.frames;

import org.openqa.selenium.WebDriver;

public class MainFrame extends IFrame {

	public MainFrame() {
		super();
		name = "Main Frame";
	}

	@Override
	public void navigateToFrame(IFrame currentFrame, WebDriver driver) {
		if (!this.equals(currentFrame)) {
			driver.switchTo().defaultContent();
		}
	}

}
