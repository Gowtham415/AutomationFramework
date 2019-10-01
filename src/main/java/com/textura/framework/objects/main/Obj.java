package com.textura.framework.objects.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import com.textura.framework.configadapter.ConfigComponents;
import com.textura.framework.environment.EnvironmentInfo;
import com.textura.framework.environment.Project;
import com.textura.framework.utils.DateHelpers;
import com.textura.framework.utils.JavaHelpers;

public class Obj {
	public TexturaWebDriver driver;
	public static EnvironmentInfo environmentInfo;
	public static EnvironmentInfo environment = null;
	private static List<TexturaWebDriver> runningDrivers = Collections.synchronizedList(new ArrayList<TexturaWebDriver>());
	private static final Logger LOG = LogManager.getLogger(Obj.class);

	public Page Page;
	private URL url;
	private String testCaseId;
	private String driverSessionId;
	private String driverType;
	private String seleniumRunTime;

	public static final String SELENIUM_RUN_TIME_MODE_LOCAL = "Local";
	public static final String SELENIUM_RUN_TIME_MODE_GRID = "Grid";
	public static final String DRIVER_TYPE_REMOTE_WEB_DRIVER = "RemoteWebDriver";
	public static final String DRIVER_TYPE_WEB_DRIVER = "WebDriver";

	public static int shortestTimeout = 1;
	public static int shortTimeout = 5;
	public static int longTimeout = 120;
	public static int TIMEOUT_5 = 5;
	public static int TIMEOUT_10 = 10;
	public static int TIMEOUT_30 = 30;
	public static int TIMEOUT_40 = 40;
	public static int TIMEOUT_50 = 50;

	/**
	 * 
	 * @param environmentInfo_
	 * @param seleniumRunTime
	 * @param product
	 * @param testCaseId
	 * @param requiredCapabilitiesC
	 *            This indicates capabilities required of a node.
	 */
	public Obj(EnvironmentInfo environmentInfo_, String seleniumRunTime, ConfigComponents product, String testCaseId,
			Map<String, Object> requiredCapabilities) {
		environmentInfo = environmentInfo_;
		this.seleniumRunTime = seleniumRunTime;
		this.testCaseId = testCaseId;

		if (environmentInfo.browser.equals("Firefox")) {

			FirefoxProfile firefoxProfile = new FirefoxProfile();
			firefoxProfile.setPreference("browser.download.useDownloadDir", false);
			firefoxProfile.setPreference("media.gmp-provider.enabled", false);
			firefoxProfile.setPreference("media.gmp-eme-adobe.enabled", false);
			firefoxProfile.setPreference("media.gmp-widevinecdm.enabled", false);
			firefoxProfile.setPreference("media.gmp-gmpopenh264.autoupdate", false);
			
			//disable all gmp/h264 related extensions and their automatic updates

			DesiredCapabilities capabilities = DesiredCapabilities.firefox();
			capabilities.setVersion("47.0.1");
			capabilities.setCapability(FirefoxDriver.PROFILE, firefoxProfile);

			if (gridMode()) {
				for (String key : requiredCapabilities.keySet()) {
					capabilities.setCapability(key, requiredCapabilities.get(key));
				}
				setProxyPreferences(capabilities);
				startGridDriver(capabilities);

			} else {
				setProxyPreferences(capabilities);
				driver = new TexturaWebDriver(new FirefoxDriver(capabilities), environmentInfo);
				driverSessionId = driver.getSessionId().toString();
				driverType = Obj.DRIVER_TYPE_WEB_DRIVER;
				LOG.info(DateHelpers.getCurrentDateAndTime() + " " + JavaHelpers.getTestCaseMethodName() + " driver, WebDriver "
						+ environmentInfo.url + " session: " + driver.getSessionId());
			}
		} else if (environmentInfo.browser.contains("Internet Explorer")) {

			File ieserver = new File(Project.pathRepository("resources/iedriver/2.35.3.0/IEDriverServer" + Project.executableExtension()));
			if (!ieserver.exists()) {
				String url = "http://10.12.130.86:8080/Automation/repository/resources/iedriver/2.35.3.0/IEDriverServer.exe";
				try {
					ieserver.getParentFile().mkdirs();
					URL website = new URL(url);
					ReadableByteChannel rbc = Channels.newChannel(website.openStream());
					FileOutputStream fos = new FileOutputStream(ieserver);
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
					fos.close();
				} catch (Exception e) {
					System.err.println("Could not find IEDriverServer.exe Tried downloading from but failed: " + url);
					e.printStackTrace();
				}
			}
			System.setProperty("webdriver.ie.driver", Project.pathRepository("resources/iedriver/2.35.3.0/IEDriverServer") + Project
					.executableExtension());
			if (gridMode()) {
				DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
				for (String key : requiredCapabilities.keySet()) {
					capabilities.setCapability(key, requiredCapabilities.get(key));
				}
				startGridDriver(capabilities);
			} else {
				driver = new TexturaWebDriver(new InternetExplorerDriver(), environmentInfo);
			}

		} else if (environmentInfo.browser.equals("Chrome")) {
			String chromeDriverPath = Project.pathRepository("resources/chromedriver/2.41/chromedriver") + Project.executableExtension();

			System.setProperty("webdriver.chrome.driver", chromeDriverPath);
			ChromeOptions options = new ChromeOptions();

			options.addArguments("test-type");
			options.addArguments("load-extension=" + Project.pathRepository("resources/chromedriver/Extensions/PDFViewer/1.0.1143_0/")); // this will load pdfviewer if disable
																																		 // extenisons is taken out in the
																																		 // future
			options.addArguments("disable-extensions");
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);
			if (gridMode()) {
				for (String key : requiredCapabilities.keySet()) {
					capabilities.setCapability(key, requiredCapabilities.get(key));
				}
				capabilities.setCapability(ChromeOptions.CAPABILITY, options);
				startGridDriver(capabilities);
			} else {
				driver = new TexturaWebDriver(new ChromeDriver(capabilities), environmentInfo);
			}
		} else if (environmentInfo.browser.equals("Safari")) {
			// More work is needed here: https://code.google.com/p/selenium/wiki/SafariDriver
			// assumeThat(isSupportedPlatform(), is(true));
			driver = new TexturaWebDriver(new SafariDriver(), environmentInfo);
		} else {
			throw new IllegalArgumentException("Invalid Browser in selenium.properties: " + environmentInfo.browser);
		}

		driver.manage().timeouts().implicitlyWait(environmentInfo.timeout, TimeUnit.SECONDS);

		Page = new Page(environmentInfo, driver);

		runningDrivers.add(driver);
	}

	private void startGridDriver(DesiredCapabilities capabilities) {

		capabilities.setCapability(CapabilityType.TAKES_SCREENSHOT, true);
		capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, org.openqa.selenium.UnexpectedAlertBehaviour.ACCEPT);
		String hubURL = environmentInfo.gridURL;

		try {
			url = new URL(hubURL);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		//driver = new TexturaWebDriver(new RemoteWebDriverTS(url, capabilities), environmentInfo);
		RemoteWebDriver test = new RemoteWebDriverTS(url, capabilities);
		test.setFileDetector(new LocalFileDetector());
		driver = new TexturaWebDriver(test, environmentInfo);
		
		driverSessionId = driver.getSessionId().toString();
		driverType = Obj.DRIVER_TYPE_REMOTE_WEB_DRIVER;
		LOG.info(DateHelpers.getCurrentDateAndTime() + " " + testCaseId + " driver, RemoteWebDriver " + driver.getHost()
				+ " session: " + driver.getSessionId());
	}

	private boolean gridMode() {
		return environmentInfo.gridMode.toLowerCase().equals("true") && seleniumRunTime.equals(SELENIUM_RUN_TIME_MODE_GRID) && Integer
				.parseInt(environmentInfo.gridNodes) > 1;
	}

	public String getDriverSessionId() {
		return driverSessionId;
	}

	public String getDriverType() {
		return driverType;
	}

	public String getTestCaseId() {
		return testCaseId;
	}

	// private boolean isSupportedPlatform() {
	// Platform current = Platform.getCurrent();
	// return Platform.MAC.is(current) || Platform.WINDOWS.is(current);
	// }

	public static void setTimeout(WebDriver driver, double timeout) {
		driver.manage().timeouts().implicitlyWait((long) (timeout * 1000.0), TimeUnit.MILLISECONDS);
	}

	/**
	 * Each test case should call this upon exit
	 */
	public void quit() {
		runningDrivers.remove(driver);
		driver.quit();
	}

	/**
	 * This can go in the after class
	 */
	public static void quitAllBrowsers() {
		for (TexturaWebDriver w : runningDrivers) {
			try {
				w.quit();
			} catch (Exception e) {
			}
		}
		runningDrivers.clear();
	}

	public void takeDebugScreenShot() {
		takeScreenShot(Project.pathFramework() + "debug.png");
	}

	/**
	 * Takes a screenshot of the webpage. filePath should include the entire path complete with a .png extension
	 * 
	 * @param filePath
	 */
	public void takeScreenShot(String filePath) {
		WebDriver augmentedDriver = new Augmenter().augment(driver);
		try {
			File screenshot = ((TakesScreenshot) augmentedDriver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(screenshot, new File(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setProxyPreferences(DesiredCapabilities capabilities) {
		// Proxy settings
		if (environmentInfo.proxyMode.equals("true") && !environmentInfo.proxy.trim().isEmpty()) {
			String PROXY = environmentInfo.proxy;
			Proxy p = new Proxy();
			p.setHttpProxy(PROXY);
			p.setFtpProxy(PROXY);
			p.setSslProxy(PROXY);
			capabilities.setCapability(CapabilityType.PROXY, p);
		}
	}
}
