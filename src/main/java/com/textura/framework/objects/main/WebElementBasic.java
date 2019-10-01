package com.textura.framework.objects.main;

import org.openqa.selenium.By;
public class WebElementBasic {

	public final By selector;
	public final String name;
	public final String frameID;
	
	public WebElementBasic(By by, String name) {
		this.selector = by;
		this.name = name;
		this.frameID=null;
	}
	
	public WebElementBasic(By by, String name, String frameID) {
		this.selector = by;
		this.name = name;
		this.frameID=frameID;

	}
}



