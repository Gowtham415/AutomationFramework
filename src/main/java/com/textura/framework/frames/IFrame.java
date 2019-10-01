package com.textura.framework.frames;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public abstract class IFrame {

	protected By frameBy;
	protected String name;

	public abstract void navigateToFrame(IFrame currentFrame, WebDriver driver);

	public boolean equals(IFrame otherFrame) {
		return name.equals(otherFrame.name);
	}

	public String getName() {
		return name;
	}

}