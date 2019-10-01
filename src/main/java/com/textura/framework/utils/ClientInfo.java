package com.textura.framework.utils;

import org.openqa.selenium.JavascriptExecutor;

import com.textura.framework.environment.productenvironments.CPMEnvironmentInfo;
import com.textura.framework.objects.main.Obj;

public class ClientInfo {

	public static String getQ() {
		StringBuffer testQ = new StringBuffer();
		testQ.append("INSERT INTO tests (test_id) VALUES ('1234567890');");
		return testQ.toString();
	}

	public static String getBrowserVersion(Obj sel, CPMEnvironmentInfo env) {
		String browser = env.browser;
		String[] info;
		StringBuffer browserVersion = new StringBuffer();
		if(browser.equals("Firefox")){
		info = (((JavascriptExecutor) sel.driver).executeScript("return navigator.userAgent;").toString()).split("Firefox");
		if (info.length != 0)
			browserVersion.append("Firefox").append(info[1]);
		}
		// ADD OTHER BROWSERS RECOGNITION
		 else if (browser.equals("Chrome")){
		 info = (((JavascriptExecutor) sel.driver).executeScript("return navigator.userAgent;").toString()).split("Chrome");
		 if(info.length != 0){
			String[] newInfo= info[1].toString().split(" ");
			browserVersion.append("Chrome").append(newInfo[0]);
		 	}
		 }
		else
			browserVersion.append("NA");

		return browserVersion.toString();
	}
	
	public static String getBrowserVersionv2(Obj sel) {

		String agentString = ((JavascriptExecutor) sel.driver).executeScript("return navigator.userAgent;").toString();
		if(agentString.contains("Firefox")) {
			return agentString.split("Firefox")[1].replace("/", "");
		}
		else if(agentString.contains("Chrome")) {
			int b = agentString.indexOf("Chrome");
			return agentString.substring(b+7, agentString.indexOf(' ', b));
		}
		return "null";
	}

	public static String getOsVersion(Obj objparam) {
		return ((JavascriptExecutor) objparam.driver).executeScript(
				"return navigator.platform;").toString();
	}
}