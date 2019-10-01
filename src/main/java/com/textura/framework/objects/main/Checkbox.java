package com.textura.framework.objects.main;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Checkbox{

	private WebDriver driver;
	private By checkboxBy;
	private By labelBy; 
	
	public Checkbox(WebDriver driver, By checkboxBy, By labelBy){
		this.driver = driver;
		this.checkboxBy = checkboxBy;
		this.labelBy = labelBy;
	}
	
	public void toggle(){
		driver.findElement(checkboxBy).click();
	}
	
	public String getLabelText(){
		return driver.findElement(labelBy).getText();
	}
	
	public boolean isSelected(){
		return driver.findElement(checkboxBy).isSelected();
	}
	
	public boolean isEnabled(){
		return driver.findElement(checkboxBy).isEnabled();
	}
	
	public boolean isLabelEnabled(){
		return (!driver.findElement(labelBy).getAttribute("class").equals("disabled"));
	}
}
