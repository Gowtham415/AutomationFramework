package com.textura.framework.testsuites;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

@Listeners({ com.textura.framework.objects.main.TestListener.class })
public class FrameworkTest_01 extends TestBaseFramework {

	WebDriver driver = new FirefoxDriver();
	
	@BeforeClass
	public static void setUpBeforeClass() {
		
	}

	@BeforeMethod
	public void setUpBeforeTest() {
	}
	
	@Test
	public void SystemInfo() {
		System.out.println("Framework System Info");
		System.out.println(System.getenv());
		String GridNodeName = System.getenv("COMPUTERNAME");
		String JenkinsGridMode = System.getenv("GRIDMODE");
		if("True".equals(JenkinsGridMode)){
		driver.get("http://"+ GridNodeName + ":4444/grid/console");
		String title = driver.getTitle();
		if(title.equals("Problem loading page")){
			throw new RuntimeException("Hub script is not running, Hub console title is : " + title);
		}
		int nodes = driver.findElements(By.xpath("//div[@class = 'proxy']")).size();
		System.out.println("There are a total of " + nodes + " Nodes connected to " + GridNodeName + " HUB.");
		}
		driver.close();
	}

	@AfterMethod
	public void tearDownAfterTest() {
	}

	@AfterClass
	public static void tearDownAfterClass() {
	}

}