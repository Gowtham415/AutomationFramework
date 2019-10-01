package com.textura.framework.training;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.Test;

public class WebDriverExample {

	// create a new instance of a FireFox driver
	WebDriver driver = new FirefoxDriver();
	
	@Test
	public void test01 () {
		
		// open FireFox browser and navigate to the below web page
		driver.get("http://dfqacpm23.texturallc.net/qa1/");

		// locate element username and enter user name
		driver.findElement(By.id("username")).clear();
		driver.findElement(By.id("username")).sendKeys("smgc1");

		// locate element password and enter password
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys("wert66");
		
		// locate login button and click on it
		driver.findElement(By.name("login")).click();

		// click on link
		driver.findElement(By.linkText("Edit User")).click();
		
		// print page title
		System.out.println("Page Title: " + driver.getTitle());
		
	}
}