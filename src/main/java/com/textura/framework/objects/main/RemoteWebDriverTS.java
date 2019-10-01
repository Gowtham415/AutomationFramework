package com.textura.framework.objects.main;

import java.net.URL;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DriverCommand;
import org.openqa.selenium.remote.RemoteWebDriver;

public class RemoteWebDriverTS extends RemoteWebDriver implements TakesScreenshot {

	public RemoteWebDriverTS(URL hubAddress, Capabilities nodeCapabilities){
		super(hubAddress, nodeCapabilities);
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
		if ((Boolean) getCapabilities().getCapability(CapabilityType.TAKES_SCREENSHOT)) {
			String base64String = execute(DriverCommand.SCREENSHOT).getValue().toString();
			return target.convertFromBase64Png(base64String);
		}
		return null;
	}
}
