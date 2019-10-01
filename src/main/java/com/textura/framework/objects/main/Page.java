package com.textura.framework.objects.main;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import com.google.common.base.Function;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.textura.framework.environment.EnvironmentInfo;
import com.textura.framework.environment.Project;
import com.textura.framework.environment.productenvironments.CPMEnvironmentInfo;
import com.textura.framework.frames.IFrame;
import com.textura.framework.frames.MainFrame;
import com.textura.framework.utils.DBParams;
import com.textura.framework.utils.Database;
import com.textura.framework.utils.DateHelpers;
import com.textura.framework.utils.JavaHelpers;
import com.textura.framework.utils.WindowsOperations;

public class Page {

	protected WebDriver driver;
	public EnvironmentInfo environmentInfo;
	static String currentUser;
	private static final Logger LOG = LogManager.getLogger(Page.class);
	public static final IFrame MAIN_FRAME = new MainFrame();

	public Page(EnvironmentInfo environmentInfo, WebDriver webDriver) {
		driver = webDriver;
		this.environmentInfo = environmentInfo;
	}

	public static void setCurrentUserName(String user) {
		currentUser = user;
	}

	public static String getUserName() {
		return currentUser;
	}

	public void openURL(String URL) {
		driver.get(URL);
	}

	public void openURLAndWait(String URL, double seconds) {
		driver.get(URL);
		sleep(seconds);
	}

	public void closeWindow() {
		driver.close();
	}

	public void closeModalDialog(String dialogName) {
		WindowsOperations.closeWindow(dialogName, 30);
	}

	public void quitDriver() {
		driver.quit();
	}

	public void back() {
		driver.navigate().back();
		sleep(0.5);
	}

	// testing purposes
	public void printAllLinks() {
		List<WebElement> links = driver.findElements(By.tagName("a"));
		for (int i = 0; i < links.size(); i++) {
			WebElement w = links.get(i);
			LOG.debug("link " + i + ": text '" + w.getText() + "' onclick '" + w.getAttribute("onclick") + "' href '" + w.getAttribute(
					"href") + "'");
			if (w.getAttribute("onclick") != null) {
				LOG.debug("link " + i + " onclick is not null");
			}
		}
	}

	public void clickLink(String linkText) {
		String username = ((TexturaWebDriver) driver).getUsername();
		String password = ((TexturaWebDriver) driver).getPassword();
		if (username.isEmpty()) {
			username = getUserName();
		}

		if (isPresent(By.xpath("//div[text()='Login']"), 1)) {
			printFormattedMessage("Randomly logged out in clicklink '" + linkText + "'.");
			driver.findElement(By.id("username")).clear();
			driver.findElement(By.id("username")).sendKeys(username);
			driver.findElement(By.id("password")).clear();
			driver.findElement(By.id("password")).sendKeys(password);
			driver.findElement(By.name("login")).click();
		}

		/*
		 * normalize space removes trailing and leading whitespace along with changing
		 * internal sequences of white space to only one. ex. normalize-space("  11    55    6  ") == "11 55 6"
		 */
		String xp = "//a[normalize-space(text())=normalize-space(\"" + linkText + "\")]";
		WebElement w = null;
		if (isPresent(By.xpath(xp))) {
			w = driver.findElement(By.xpath(xp));
		} else {
			try {

				int second = 0;
				while (isLinkPresent(linkText) == false) {
					// isPresent will wait 10 seconds for link to appear
					if (second >= Obj.TIMEOUT_10) {
						break;
					}
					second++;
					if (second == 1) {
						String currentURL = getURL().replaceAll("\\?logout=true", "");
						driver.get(currentURL);
					} else {
						refresh();
					}
					printFormattedMessage("Error finding link. Looking for '" + linkText + "'.");
				}
				waitForElementToBeClickable(By.linkText(linkText), 1);
				w = driver.findElement(By.linkText(linkText));
			} catch (TimeoutException e) {
				new Assertions().assertTrue("Timeout waiting for link: '" + linkText + "' to be clickable", false);
			}
		}
		if (w.getAttribute("onclick") != null || !w.getAttribute("href").contains("http")) {
			w.click();
		} else {
			driver.get(w.getAttribute("href"));
		}
	}

	/**
	 * Clicks the 'linkInstance'th of a link.
	 * 
	 * @param linkInstance
	 *            Starts at 1
	 */
	public void clickLinkInstance(String linkText, int linkInstance) {
		linkInstance--;

		List<WebElement> links = driver.findElements(By.linkText(linkText));
		if (linkInstance >= links.size() || linkInstance < 0) {
			System.err.println("Could not find " + (linkInstance + 1) + "th '" + linkText + "' link");
		} else {
			String linkURL = links.get(linkInstance).getAttribute("href");
			driver.get(linkURL);
		}

	}

	public void clickLinkInstanceByText(String linkText, int linkInstance) {
		linkInstance--;

		List<WebElement> links = driver.findElements(By.linkText(linkText));
		if (linkInstance >= links.size() || linkInstance < 0) {
			System.err.println("Could not find " + (linkInstance + 1) + "th '" + linkText + "' link");
		} else {
			links.get(linkInstance).click();
		}
	}

	public void clickPartialLink(String linkText) {
		String xp = "//a[contains(normalize-space(text()),normalize-space(\"" + linkText + "\"))]";
		WebElement w = null;
		if (isPresent(By.xpath(xp))) {
			w = driver.findElement(By.xpath(xp));
		} else {
			waitForElementToBeClickable(By.partialLinkText(linkText));
			w = driver.findElement(By.partialLinkText(linkText));
		}
		if (w.getAttribute("onclick") != null)
			w.click();
		else
			driver.get(w.getAttribute("href"));
	}

	public void clickLinkByText(String linkText) {
		int second = 0;
		while (isLinkPresent(linkText) == false) {
			second++;
			refresh();
			// isPresent will wait 5 seconds for link to appear
			if (second >= Obj.TIMEOUT_5) {
				printFormattedMessage("Element Not found! : '" + linkText + "'.");
				throw new RuntimeException("Not clicking link ' " + linkText + " ' because it is not present.");
			}
		}
		driver.findElement(By.linkText(linkText)).click();
	}

	public String getURLFromLink(String linkText) {
		int second = 0;
		while (isLinkPresent(linkText) == false) {
			second++;
			refresh();
			// isPresent will wait 5 seconds for link to appear
			if (second >= Obj.TIMEOUT_5) {
				printFormattedMessage("Element Not found! : '" + linkText + "'.");
				throw new RuntimeException("Cant get URL from link ' " + linkText + " ' because it is not present.");
			}
		}
		return driver.findElement(By.linkText(linkText)).getAttribute("href");
	}

	public String getDivText() {
		String allDivs = "";
		List<WebElement> divs = driver.findElements(By.tagName("div"));

		for (int i = 1; i < divs.size(); i++)
			allDivs += divs.get(i).getText();

		return allDivs;
	}

	public String getHeaderTitle() {
		try {
			// WebElement element = driver.findElement(By.className("page-title"));
			WebElement element = driver.findElement(By.xpath("//*[@class = 'headingtitle' or @class = 'page-title']"));
			return element.getText();
		} catch (Exception e) {
			return "";
		}

	}

	public String getTitle() {
		return driver.getTitle();
	}

	public String getURL() {
		return driver.getCurrentUrl();
	}

	public void waitForLoaded() {
		String js = "window.addEventListener('load', arguments[0]());";
		executeAsyncJavaScript(js);
	}

	public void refresh() {
		driver.navigate().refresh();
	}

	public static void sleep(double seconds) {
		double milliseconds = seconds * 1000;
		long sleepTime = (long) milliseconds;

		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			LOG.debug("Error while sleeping \n" + e);
		}
	}

	public boolean isButtonVisible(String value) {
		String xp = "//input[@value='" + value + "']";
		if (isPresent(By.xpath(xp))) {
			WebElement w = driver.findElement(By.xpath(xp));
			if (w.isDisplayed()) {
				return true;
			}
		}
		return false;
	}

	public boolean isTableVisible(String classname) {
		String xp = "//table[@class='" + classname + "']";
		if (isPresent(By.xpath(xp))) {
			WebElement w = driver.findElement(By.xpath(xp));
			if (w.isDisplayed()) {
				return true;
			}
		}
		return false;
	}

	public boolean isTableExist(String classname) {
		String xp = "//table[@class='" + classname + "']";
		return isPresent(By.xpath(xp));
	}

	public boolean isTextPresent(String text) {
		return isTextPresent(text, 10);
	}

	public String getPageText() {
		String bodyText = driver.findElement(By.tagName("body")).getText();
		return bodyText;
	}

	public boolean isExactTextPresent(String text) {
		return isPresent(By.xpath("//*[text()='" + text + "']"));
	}

	public boolean isContainsTextPresent(String text) {
		return isPresent(By.xpath("//*[contains(text(),\"" + text + "\")]"));
	}

	public String getCursorValue(String text, int instance) {
		instance--;

		List<WebElement> links = driver.findElements(By.xpath("//*[text() = '" + text + "']"));
		if (instance >= links.size() || instance < 0) {
			String error = "Could not find instance: " + (instance + 1) + "text: '" + text + "'";
			System.err.println(error);
			return error;
		} else {
			return links.get(instance).getCssValue("cursor");
		}

	}

	public boolean isLinkPresent(String linkText) {
		return isLinkPresent(linkText, 5);
	}

	public boolean isLinkPresent(String linkText, double timeout) {
		return isPresent(By.linkText(linkText), timeout);
	}

	public boolean isLinkNotPresent(String linkText) {
		return !isPresent(By.linkText(linkText), 1);
	}

	public boolean refreshUntilLinkVisible(String linkText) {
		for (int attempt = 0; attempt < 10; attempt++) {
			if (isPresent(By.linkText(linkText))) {
				return true;
			}
			refresh();
		}
		return false;
	}

	public boolean refreshUntilElementVisible(By by) {
		for (int attempt = 0; attempt < 10; attempt++) {
			if (isPresent(by)) {
				return true;
			}
			refresh();
		}
		return false;
	}

	public void clickLinkWhenVisible(String linkText) {
		int second = 0;

		while (isLinkPresent(linkText) == false) {
			second++;
			refresh();
			// isPresent will wait 5 seconds for link to appear
			if (second >= Obj.TIMEOUT_5) {
				printFormattedMessage("Element Not found! : '" + linkText + "'.");
				throw new RuntimeException("Not clicking link ' " + linkText + " ' because it is not present.");
			}
		}
		clickLink(linkText);
	}

	public String getDynamicID(String wildCard) {
		// This needs to be rewritten to use jsoup HTML parser, see getSourceLink()
		String source = driver.getPageSource();
		String[] arIDs = source.split("id=\"");
		String idString = "";
		String id = "";
		String returnID = "";

		for (int i = 0; i < arIDs.length; i++) {
			idString = arIDs[i];
			id = idString.substring(0, idString.indexOf("\""));
			// LOG.debug(sID);

			if (id.contains(wildCard)) {
				// LOG.debug("The element ID you're looking for is: " + sID);
				returnID = id;
				return returnID;
			}
		}
		return returnID;
	}

	public String getSourceLink(String linkName) {
		String source = driver.getPageSource();
		Document doc = Jsoup.parse(source);
		Elements links = doc.select("a[href]");
		String linkURL = null;
		for (Element link : links) {
			if (linkName.equals(link.text())) {
				linkURL = link.attr("href");
				break;
			}
		}
		return linkURL;
	}

	public Object executeJavaScript(String code) {
		return executeJavaScript(code, driver);
	}

	public static Object executeJavaScript(String code, WebDriver driver) {
		return ((JavascriptExecutor) driver).executeScript(code);
	}

	public Object executeJavaScript(String code, Object arg) {
		return executeJavaScript(code, arg, driver);
	}

	public static Object executeJavaScript(String code, Object arg, WebDriver driver) {
		return ((JavascriptExecutor) driver).executeScript(code, arg);
	}

	public Object executeAsyncJavaScript(String code) {
		return executeAsyncJavaScript(code, driver, environmentInfo.timeout);
	}

	public static Object executeAsyncJavaScript(String code, WebDriver driver, long timeout) {
		driver.manage().timeouts().setScriptTimeout(timeout, TimeUnit.SECONDS); // this timeout only works for asynchronous javascript
		return ((JavascriptExecutor) driver).executeAsyncScript(code);
	}

	/**
	 * For the Validating and Saving dialog, use waitForValidatingBox() instead
	 * 
	 * @param initialURL
	 * @param timeoutSec
	 * @return
	 */
	public boolean waitForURLChange(String initialURL, int timeoutSec) {
		try {
			String changedURL = initialURL;
			long initialTime = System.currentTimeMillis() / 1000;
			long currentTime = System.currentTimeMillis() / 1000;

			while (initialURL.equals(changedURL) && timeoutSec > (currentTime - initialTime)) {

				Thread.sleep(500);
				changedURL = driver.getCurrentUrl();
				currentTime = System.currentTimeMillis() / 1000;
			}
			if (timeoutSec <= (currentTime - initialTime)) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			LOG.debug(e);
			return false;
		}
	}

	public boolean waitForValidatingBox() {
		return waitForNonPresence(By.xpath("//div[@class='blockUI']"));
	}

	public boolean waitForNonPresence(By b) {
		return waitForNonPresence(b, 60);
	}

	public boolean waitForNonPresence(By b, long timeout) {
		for (int attempt = 1; attempt < timeout; attempt++) {
			if (!isPresent(b, 0.1)) {
				return true;
			}
		}
		LOG.debug("wait for non presence timed out. " + b);
		return false;
	}

	/**
	 * Switches driver to a specified popup by searching through open windows
	 */
	public void switchToPopup(String windowTitle) {
		if (environmentInfo.browser.equals("Android")) {
			Set<String> openWindows = driver.getWindowHandles();
			String initialPage = driver.getWindowHandle();
			for (String s : openWindows) {
				driver.switchTo().window(s);
				if (!driver.getTitle().contains(initialPage)) {
					return;
				}
			}
		} else {
			for (int attempt = 0; attempt < 3; attempt++) {
				Set<String> openWindows = driver.getWindowHandles();
				String initialPage = driver.getWindowHandle();
				for (String s : openWindows) {
					driver.switchTo().window(s);
					if (driver.getTitle().contains(windowTitle)) {
						return;
					}
				}
				LOG.debug("Could not find popup " + windowTitle);
				// switch back
				driver.switchTo().window(initialPage);
				sleep(4);
			}
		}
	}

	/**
	 * Closes a specified popup by switching the driver to the pop up and closing it
	 */
	public void closePopup(String windowTitle) {
		String parent = driver.getWindowHandle();
		switchToPopup(windowTitle);
		if (driver.getWindowHandle() != parent) {
			driver.findElement(By.id("close")).click();
			sleep(1);
		}
		try {
			driver.switchTo().window(parent);
		} catch (Exception e) {
			driver.switchTo().window(driver.getWindowHandles().iterator().next());
		}
	}

	/**
	 * Switches to an iframe
	 */
	public void switchToFrame(String frameid) {
		driver.switchTo().frame(frameid);
		driver.switchTo().activeElement();
	}

	/**
	 * Switches to an iframe
	 */
	public void switchToFrame(int frameid) {
		driver.switchTo().frame(frameid);
		driver.switchTo().activeElement();
	}

	public void switchToMainFrame() {
		switchToMainFrame(driver);
	}

	public static void switchToMainFrame(WebDriver driver) {
		driver.switchTo().defaultContent();
		sleep(1);
	}

	public void clickAlertOK() {
		waitForAlert();
		try {
			Alert alert = driver.switchTo().alert();
			alert.accept();
		} catch (Exception e) {

		}
	}

	public void clickAlertCancel() {
		waitForAlert();
		Alert alert = driver.switchTo().alert();
		alert.dismiss();
	}

	public void waitForAlert() {
		if (!waitForAlertToBePresent(environmentInfo.timeout))
			printFormattedMessage("Alert box not present.");
	}

	public String getAlertText() {
		waitForAlert();
		Alert alert = driver.switchTo().alert();
		sleep(1);
		return alert.getText();
	}

	public boolean alertIsPresent() {
		try {
			driver.switchTo().alert();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void clickAlertCancelIfPresent() {
		if (alertIsPresent())
			clickAlertCancel();
	}

	public void clickAlertOKIfPresent() {
		if (alertIsPresent())
			clickAlertOK();
	}

	public void setClipboardData(String string) {
		StringSelection stringSelection = new StringSelection(string);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
	}

	public boolean isPresent(By by) {
		return isPresent(by, 5);
	}

	public boolean isPresent(WebElement context, By by, double timeout) {
		Obj.setTimeout(driver, 5);
		int size = context.findElements(by).size();
		Obj.setTimeout(driver, timeout);
		return size > 0;
	}

	/**
	 * Similar to isPresent but returns the WebElement if present, null if not
	 */
	public WebElement getPresence(By by, int sec) {
		Obj.setTimeout(driver, sec);
		List<WebElement> list = driver.findElements(by);
		Obj.setTimeout(driver, environmentInfo.timeout);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public boolean isPresent(By by, double timeOut) {
		return isPresent(by, timeOut, driver);
	}

	public static boolean isPresent(By by, double timeOut, WebDriver driver) {
		Obj.setTimeout(driver, timeOut);
		int size = driver.findElements(by).size();
		Obj.setTimeout(driver, getOriginalTimeout(driver));
		return size > 0;
	}

	public boolean isPresent(WebElement context, By by) {
		Obj.setTimeout(driver, 5);
		int size = context.findElements(by).size();
		Obj.setTimeout(driver, environmentInfo.timeout);
		return size > 0;
	}

	public boolean isButtonPresent(String value) {
		return isPresent(By.xpath("//input[@value='" + value + "']"));
	}

	public boolean waitForPageHeading(String headingTitle, int timeoutSec) {
		By locator = By.xpath("//*[@class = 'headingtitle' or @class = 'page-title' or @id = 'pageName']");
		return waitForTextToBePresentInElementLocated(locator, headingTitle, timeoutSec);
	}

	public void pressEnter() {
		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_ENTER);
			robot.keyRelease(KeyEvent.VK_ENTER);
		} catch (Exception e) {
		}
	}

	// Matches strings ignoring '*' wildcards
	public boolean wildCardMatch(String text, String pattern) {
		return JavaHelpers.wildCardMatch(text, pattern);
	}

	// Matches strings ignoring '*' wildcards
	public boolean wildCardMatchNoPrint(String text, String pattern) {
		return JavaHelpers.wildCardMatchNoPrint(text, pattern);

	}

	public boolean stringWildCardMatch(String text, String pattern) {
		if (text.length() < 1 && pattern.length() < 1) {
			return true;
		}
		if (text.length() < 1 || pattern.length() < 1) {
			return false;
		}

		String[] actual = text.split("\n");
		String[] expected = pattern.split("\n");

		Boolean checker = false;

		for (int a = 0; actual.length > a; a++) {
			if (expected[a].contains("*")) {
				checker = true;
			} else {
				checker = actual[a].equals(expected[a]);
				if (checker == false) {
					LOG.debug("String are not equal: Expected: " + expected[a] + " Actual:" + actual[a]);
					break;
				}
			}
		}

		return checker;
	}

	public boolean isMailToLink(String linkText) {
		By locator = By.linkText(linkText);
		waitForElementToBeClickable(locator);
		return driver.findElement(locator).getAttribute("href").contains("mailto:");
	}

	public void pressESC() {
		try {
			Robot robot = new Robot();
			// Press ESC
			robot.keyPress(KeyEvent.VK_ESCAPE);
			robot.keyRelease(KeyEvent.VK_ESCAPE);
		} catch (Exception e) {
		}
	}

	public void pressCtrlInsert() {
		try {
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_CONTROL);
			robot.keyPress(KeyEvent.VK_INSERT);
			sleep(0.1);
			robot.keyRelease(KeyEvent.VK_INSERT);
			sleep(0.1);
			robot.keyRelease(KeyEvent.VK_CONTROL);
			sleep(1);
		} catch (Exception e) {
			LOG.debug(e);
		}
	}

	public boolean isButtonEnabled(String value) {
		return driver.findElement(By.xpath("//input[@value='" + value + "']")).isEnabled();
	}

	public boolean isButtonEnabledByText(String buttonName) {
		return driver.findElement(By.xpath("//span[text() = 'Retrieve Data']")).isEnabled();
	}

	public void clearCookies() {
		// Set<Cookie> cookies = driver.manage().getCookies();
		// LOG.debug("cookies = " + cookies);
		// int cookiesSize = cookies.size();

		// LOG.debug("Number of cookies in this site " + cookiesSize);

		// driver.manage().deleteAllCookies();

		// for (Cookie cookie : cookies) {
		// LOG.debug(cookie.getName() + " " + cookie.getValue());

		// This will delete cookie By Name
		// driver.manage().deleteCookieNamed(cookie.getName());

		// This will delete the cookie
		// driver.manage().deleteCookie(cookie);
		// }

		// cookiesSize = cookies.size();

		// LOG.debug("Number of cookies in this site " + cookiesSize);

		driver.manage().deleteAllCookies();
		driver.manage().deleteCookieNamed("Local Storage");
		driver.manage().deleteAllCookies();
		if (driver instanceof TexturaWebDriver) {
			((TexturaWebDriver) driver).setUsername("");
			((TexturaWebDriver) driver).setPassword("");
		}
	}

	public String getCurrentLoginName() {
		try {
			String cookie = driver.manage().getCookieNamed("TEXTURA_AUTH").toString();
			String userName = cookie.substring((cookie.indexOf("\"") + 1), cookie.indexOf("|"));
			return userName;
		} catch (Exception ex) {
			LOG.debug("Failed to get current user name from cookie");
			return null;
		}
	}

	public int numberOfTextOccurrences(String string) {
		return driver.findElements(By.xpath("//*[contains(text(), '" + string + "')]")).size();
	}

	public boolean databaseQuery161548() {
		DBParams params = new DBParams();
		String query = "update contract set allownegativebalancetocomplete = True where contractid = 4789;";
		params.dbServer = ((CPMEnvironmentInfo) environmentInfo).testDatabase;
		params.dbName = ((CPMEnvironmentInfo) environmentInfo).cpm_databasename;
		params.user = "postgres";
		params.password = "";
		boolean result = Database.executeUpdatePSQL(params, query);
		sleep(2);
		return result;
	}

	public static boolean isSorted(ArrayList<String> iterable) {
		Iterator<String> iter = iterable.iterator();
		if (!iter.hasNext()) {
			return true;
		}
		String t = iter.next();
		while (iter.hasNext()) {
			String t2 = iter.next();
			t = t.toLowerCase();
			t2 = t2.toLowerCase();
			if (compareTo(t, t2) < 0) {
				LOG.debug(t + " " + t2);
				return false;
			}
			t = t2;
		}
		return true;
	}

	public static boolean isSorted(List<Boolean> iterable) {
		Iterator<Boolean> iter = iterable.iterator();
		if (!iter.hasNext()) {
			return true;
		}
		Boolean t = iter.next();
		while (iter.hasNext()) {
			Boolean t2 = iter.next();
			if (t.compareTo(t2) > 0) {
				LOG.debug(t + " " + t2);
				return false;
			}
			t = t2;
		}
		return true;
	}

	public static boolean isDateSorted(ArrayList<String> iterable) {
		Iterator<String> iter = iterable.iterator();
		if (!iter.hasNext()) {
			return true;
		}
		String t = iter.next();
		while (iter.hasNext()) {
			String t2 = iter.next();
			t = t.toLowerCase();
			t2 = t2.toLowerCase();
			if (DateHelpers.compareTo(t, t2) > 0) {
				LOG.debug(t + " " + t2);
				return false;
			}
			t = t2;
		}
		return true;
	}

	public static boolean isSortedUpperCaseFirst(ArrayList<String> iterable) {
		Iterator<String> iter = iterable.iterator();
		if (!iter.hasNext()) {
			return true;
		}
		String t = iter.next();
		while (iter.hasNext()) {
			String t2 = iter.next();
			if (compareTo(t, t2) < 0) {
				LOG.debug(t + " " + t2);
				return false;
			}
			t = t2;
		}
		return true;
	}

	public static int compareTo(String s1, String s2) {
		int shortest = s1.length() < s2.length() ? s1.length() : s2.length();
		if (s1.equals(s2))
			return 0;
		for (int i = 0; i < shortest; i++) {
			char c = s1.charAt(i);
			if (c == '(' || c == '-') {
				return 0;
			}
			if (s1.charAt(i) > s2.charAt(i)) {
				return -1;
			} else if (s1.charAt(i) < s2.charAt(i)) {
				return 1;
			}
		}
		return 1;
	}

	public ArrayList<String> toString(List<WebElement> webElements) {
		return listToString(webElements);
	}

	public static ArrayList<String> listToString(List<WebElement> webElements) {
		ArrayList<String> result = new ArrayList<String>();
		for (WebElement w : webElements) {
			result.add(w.getText());
		}
		return result;
	}

	protected void readyFileUpload(String path) {
		if (environmentInfo.browser.equals("Firefox")) {
			WindowsOperations.readyFileUpload("File Upload", path, 30);
		} else if (environmentInfo.browser.equals("Internet Explorer 8")) {
			WindowsOperations.readyFileUpload("Choose File to Upload", path, 30);
		} else {
			System.err.println("Unsupported browser : " + environmentInfo.browser);
		}
	}

	/**
	 * Appends a session to the raw link
	 */
	protected String appendLoginToLink(String link) {
		return link + "&login=True&username=" + getCurrentLoginName() + "&password=wert66";
	}

	protected String buildFullFilePath(String fName, String extension) {
		return Project.downloads("") + fName + "-" + JavaHelpers.getTestCaseMethodName() + "-" + DateHelpers.getTimeStamp() + "."
				+ extension;
	}

	/**
	 * Downloads a file from a given link by appending login credentials to the
	 * link. It saves the file in the downloads folder. Returns path to the
	 * file.
	 * 
	 * @param link
	 *            html link
	 * @param fileName
	 *            a one or two word description of the file
	 * @param extension
	 * @return path to file
	 */
	protected String clickFile(String link, String fileName, String extension) {
		if (!link.contains("http")) {
			link = driver.findElement(By.linkText(link)).getAttribute("href");
		}
		// String login = appendLoginToLink(link);
		String fullPath = buildFullFilePath(fileName, extension);
		HTTPRequestor.downloadGetFile(link, getCookie(), fullPath);
		return fullPath;
	}

	/**
	 * Downloads a file using the dialogs in the browser. clickFile(String,
	 * String, String) is preferred over this.
	 * 
	 * @param link
	 * @param fileName
	 * @param extension
	 * @return path to file
	 */
	protected String clickFile(WebElement link, String fileName, String extension) {

		String fullPath = buildFullFilePath(fileName, extension);

		if (environmentInfo.browser.equals("Firefox")) {
			link.click();
			WindowsOperations.saveFileFF("Opening", "Enter name of file", fullPath, 180);
		} else if (environmentInfo.browser.equals("Internet Explorer 8")) {
			link.click();
			WindowsOperations.saveFileIE("File Download", "Save As", "Download complete", fullPath, 180);
		} else {
			throw new UnsupportedOperationException("Unsupported browser in clicking " + link + ": " + environmentInfo.browser);
		}
		return fullPath;
	}

	/**
	 * Clicks the button that launches the file input window. Handles inputing
	 * the file to upload. This method spawns a thread which ensures that the
	 * file upload window will eventually be closed.
	 * 
	 * @param w
	 * @param path
	 */
	public void clickBrowse(WebElement w, String path) {
		readyFileUpload(path);

		MonitorThread m = new MonitorThread();
		m.start();

		w.click(); // blocks until file upload window is closed

		if (m.error) {
			throw new IllegalStateException("\nFile Upload window did not properly input file '" + path + "' " + JavaHelpers
					.getTestCaseMethodName());
		}
		m.requestStop = true;
	}

	/**
	 * Since the file upload box blocks the java code from continuing, have a
	 * Thread monitor that the file box has been closed so test execution is not
	 * blocked indefinitely.
	 * 
	 */
	class MonitorThread extends Thread {

		public boolean requestStop;
		public boolean error;

		MonitorThread() {
			requestStop = false;
			error = false;
		}

		public void run() {
			for (int i = 0; i < 30; i++) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					LOG.debug(e);
				}
				if (requestStop) {
					return;
				}
			}
			// if thread has not returned yet, box is still open
			error = true;
			WindowsOperations.closeWindowPartialName("File Upload", 10);
			WindowsOperations.closeWindowPartialName("File Upload", 1);
		}
	}

	public boolean waitForClassName(String className) {
		boolean result = waitForElementToBePresent(By.className(className));
		if (result == false) {
			printFormattedMessage("Could not find className: " + className);
		}
		return result;
	}

	public boolean waitForIdNotClickable(String id) {
		boolean result = waitForElementToBeInvisible(By.id(id));
		if (result == false) {
			printFormattedMessage("Could not find className: " + id + " in " + JavaHelpers.getTestCaseMethodName());
		}
		return result;
	}

	public boolean waitForIdClickable(String id) {
		boolean result = waitForElementToBeClickable(By.id(id));
		if (result == false) {
			printFormattedMessage("Could not find className: " + id + " in " + JavaHelpers.getTestCaseMethodName());
		}
		return result;
	}

	public boolean waitForId(String id) {
		boolean result = waitForElementToBePresent(By.id(id));
		if (result == false) {
			printFormattedMessage("Could not find id: " + id + " in " + JavaHelpers.getTestCaseMethodName());
		}
		return result;
	}

	public boolean waitForLinkText(String linkText) {
		boolean result = waitForElementToBePresent(By.linkText(linkText));
		if (result == false) {
			printFormattedMessage("Could not find linkText: " + linkText + " in " + JavaHelpers.getTestCaseMethodName());
		}
		return result;
	}

	public boolean waitForPartialLinkText(String partialLinkText) {
		boolean result = waitForElementToBePresent(By.partialLinkText(partialLinkText));
		if (result == false) {
			printFormattedMessage("Could not find partialLinkText: " + partialLinkText + " in " + JavaHelpers.getTestCaseMethodName());
		}
		return result;
	}

	public boolean waitForXpath(String xpath) {
		boolean result = waitForElementToBePresent(By.xpath(xpath));
		if (result == false) {
			printFormattedMessage("Could not find xpath: " + xpath + " in " + JavaHelpers.getTestCaseMethodName());
		}
		return result;
	}

	public void hoverOverWebElement(WebElement w) {
		new Actions(driver).moveToElement(w).pause(1).perform();
	}

	public Dimension getWindowSize() {
		return driver.manage().window().getSize();
	}

	public void resizeWindow(Dimension d) {
		driver.manage().window().setSize(d);
	}

	public void maximizeWindow() {
		driver.manage().window().maximize();
	}

	public void minimizeWindow() {
		driver.manage().window().setPosition(new Point(-2000, 0));
	}

	public void waitForXpaths(List<String> xpaths) {
		for (int i = 0; i < xpaths.size(); i++) {
			if (!waitForElementToBePresent(By.xpath(xpaths.get(i)), environmentInfo.timeout * 3)) {
				throw new TimeoutException("Could not find xpath: " + xpaths.get(i));
			}
		}
	}

	public void rightClick(WebElement element) {
		new Actions(driver).contextClick(element).perform();
	}

	public void focusWindow() {
		executeJavaScript("window.focus();");
	}

	/**
	 * Uses Selenium to create a screenshot. Captures the entire webpage even if
	 * it is not entirely visible.
	 * 
	 * @param pathToSaveScreenShot
	 */
	public void captureScreenshot(String pathToSaveScreenShot) {
		if (!environmentInfo.browser.equals("Android")) {
			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			try {
				FileUtils.copyFile(screenshot, new File(pathToSaveScreenShot));
			} catch (IOException e) {
				LOG.debug(e);
			}
		}
	}

	public Set<String> getOpenWindows() {
		return driver.getWindowHandles();
	}

	public String getWindowHandle() {
		return driver.getWindowHandle();
	}

	public void switchToWindow(String windowID) {
		driver.switchTo().window(windowID);
	}

	public boolean isObjectDisplayed(By by) {
		if (isPresent(by, 3)) {
			WebElement w = driver.findElement(by);
			return (w.isDisplayed());
		}
		return false;
	}

	public void openLinkMultipleTimes(String linkText, int clickNTimes) {
		String xp = "//a[normalize-space(text())=normalize-space(\"" + linkText + "\")]";
		WebElement w = null;
		if (isPresent(By.xpath(xp))) {
			w = driver.findElement(By.xpath(xp));
		} else {
			try {
				waitForElementToBeClickable(By.linkText(linkText));
				w = driver.findElement(By.linkText(linkText));
			} catch (TimeoutException e) {
				new Assertions().assertTrue("Timeout waiting for link: '" + linkText + "' to be clickable", false);
			}
		}
		String url = w.getAttribute("href");
		for (int i = 0; i < clickNTimes; i++) {
			((JavascriptExecutor) driver).executeScript("window.location.assign('" + url + "')");
		}
	}

	public String getTextAlignByID(String id) {
		return driver.findElement(By.id(id)).getCssValue("text-align");
	}

	public String getTextAlignByText(String text) {
		return driver.findElement(By.xpath("//*[text()='" + text + "']")).getCssValue("text-align");
	}

	public String getTextColor(String text) {
		try {
			return driver.findElement(By.xpath("//*[text()='" + text + "']")).getCssValue("color");
		} catch (Exception e) {
			return "Could not find text: '" + text + "'.";
		}
	}

	public String getCookie() {
		return driver.manage().getCookies().toString().replaceAll("\\[|\\]", "");
		// doing a replace here for the brackets becuase python 2.7 parses cookies differently than 2.4
		// return (String) executeJavaScript("return document.cookie");
	}
	
	public String getSessionCookie() {
		return driver.manage().getCookieNamed("session").toString();
	}

	public static boolean isSortedInDescending(ArrayList<String> iterable) {
		ArrayList<String> reversed = new ArrayList<String>();
		for (int i = 0; i < iterable.size(); i++) {
			reversed.add(iterable.get(i));
		}

		Iterator<String> iter = reversed.iterator();

		if (!iter.hasNext()) {
			return true;
		}
		String t = iter.next();
		while (iter.hasNext()) {
			String t2 = iter.next();
			t = t.toLowerCase();
			t2 = t2.toLowerCase();
			if (compareTo(t, t2) > 0) {
				LOG.debug(t + " " + t2);
				return false;
			}
			t = t2;
		}
		return true;
	}

	public static void scrollElementIntoView(WebElement element, WebDriver driver) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(false);", element);
	}

	public void scrollElementIntoView(WebElement element) {
		scrollElementIntoView(element, driver);
	}

	public static void scrollElementIntoViewInFrame(WebElement element, WebDriver driver) {
		// the header cuts off scrolled items
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(false);", element);
	}

	public void scrollElementIntoViewInFrame(WebElement element) {
		scrollElementIntoViewInFrame(element, driver);
	}

	public void scrollToBottomOfPage() {
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		int length = driver.manage().window().getSize().getHeight();
		executor.executeScript("window.scrollBy(0," + length + ")");
	}

	public boolean isHorizontalScrollbarPresent() {
		String execScript = "return document.documentElement.scrollWidth>document.documentElement.clientWidth;";
		JavascriptExecutor scrollBarPresent = (JavascriptExecutor) driver;
		Boolean test = (Boolean) (scrollBarPresent.executeScript(execScript));
		return test;
	}

	public boolean isVerticalScrollbarPresent() {
		String execScript = "return document.documentElement.scrollHeight>document.documentElement.clientHeight;";
		JavascriptExecutor scrollBarPresent = (JavascriptExecutor) driver;
		Boolean isPresent = (Boolean) (scrollBarPresent.executeScript(execScript));
		return isPresent.booleanValue();
	}

	protected List<String> sortListAscending(List<String> values) {
		Collections.sort(values);
		return values;
	}

	protected List<String> sortListDescending(List<String> values) {
		Collections.sort(values);
		Collections.reverse(values);
		return values;
	}

	public boolean isFileExist(String filePath) {
		try {
			JavaHelpers.readFileAsString(filePath);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	public void closePDFPopUp() {
		driver.findElement(By.xpath("//div[@class = 'popupContent']//div[@class = 'v-window-closebox']")).click();
	}

	/**
	 * Downloads a file from a given link by appending login credentials to the
	 * link. It saves the file in the downloads folder. Returns path to the
	 * file.
	 * 
	 * @param link
	 *            html link
	 * @param fileName
	 *            a one or two word description of the file
	 * @param extension
	 * @return path to file
	 */
	protected String clickFile(String cookie, String link, String fileName, String extension) {
		long start = System.currentTimeMillis();
		if (!link.contains("http")) {
			link = driver.findElement(By.linkText(link)).getAttribute("href");
		}
		// String login = appendLoginToLink(link);
		printFormattedMessage("Starting clickfile");
		String fullPath = buildFullFilePath(fileName, extension);
		HTTPRequestor.downloadGetFile(link, cookie, fullPath);
		long end = System.currentTimeMillis();
		printFormattedMessage("Clickfile finished in " + (end - start) + " ms");
		return fullPath;
	}

	protected void focusOnElement(WebElement w) {
		if (w.getTagName().equals("input")) {
			w.sendKeys("");
		} else {
			new Actions(driver).moveToElement(w).perform();
		}
	}

	protected void dragAndDrop(WebElement element, WebElement target) {
		(new Actions(driver)).dragAndDrop(element, target).perform();
	}

	public boolean isWindowPresent(String windowTitle) {
		String oldHandle = driver.getWindowHandle();
		for (String handle : driver.getWindowHandles()) {
			driver.switchTo().window(handle);
			if (driver.getTitle().equals(windowTitle)) {
				driver.switchTo().window(oldHandle);
				return true;
			}
		}
		driver.switchTo().window(oldHandle);
		return false;
	}

	public void switchToWindowByTitle(String windowTitle) {
		for (String handle : driver.getWindowHandles()) {
			driver.switchTo().window(handle);
			if (driver.getTitle().equals(windowTitle))
				return;
		}
		return;
	}

	public static void printFormattedMessage(String message) {
		String caseName = JavaHelpers.getTestCaseMethodName();
		String timestamp = DateHelpers.getCurrentDateAndTime();
		LOG.debug(timestamp + " " + caseName + ": " + message);
	}

	public static void printEmailFormattedMessage(String message) {
		String caseName = JavaHelpers.getTestCaseMethodName();
		String timestamp = DateHelpers.getCurrentDateAndTime();
		LOG.debug("	[" + timestamp + "] [" + caseName + "]: " + message);
	}

	public void openNewTab() {
		if (environmentInfo.browser == "Firefox") {
			String selectLinkOpeninNewTab = Keys.chord(Keys.CONTROL, "t");
			driver.findElement(By.xpath("//body")).sendKeys(selectLinkOpeninNewTab);
		} else {
			executeJavaScript("window.open();");
		}
	}

	public void switchTab(int tab) {
		sleep(0.1); // delay call of getOpenWindows to make sure browser has actually opened up a new tab
		List<String> windows = new ArrayList<String>(getOpenWindows());
		if (windows.size() == 1) {
			sleep(0.1); // delay call of getOpenWindows before trying again
			windows = new ArrayList<String>(getOpenWindows());
		}
		switchToWindow(windows.get(tab));
	}

	public void closeNewestTab() {
		List<String> windows = new ArrayList<String>(getOpenWindows());
		if (windows.size() == 1) {
			throw new RuntimeException("New Tab Not Found");
		}
		switchToWindow(windows.get(windows.size() - 1));
		driver.close();
		windows.remove(windows.size() - 1);
		switchToWindow(windows.get(windows.size() - 1));
	}

	public void closeFirstTab() {
		List<String> windows = new ArrayList<String>(getOpenWindows());
		if (windows.size() == 1) {
			throw new RuntimeException("Popup window did not open");
		}
		switchToWindow(windows.get(0));
		driver.close();
		switchToWindow(windows.get(1));
	}

	public boolean isPartialLinkPresent(String link) {
		return isPresent(By.partialLinkText(link), 0);
	}

	public String getUCLWRescindedMessage() {
		String lineOne = driver.findElement(By.xpath("//td[@class = 'StatusMessage']/p[2]")).getText();
		String lineTwo = driver.findElement(By.xpath("//td[@class = 'StatusMessage']/ul/li")).getText();
		return lineOne + "\n" + lineTwo;
	}

	public int getNumOccurrences(By by, double timeOut) {
		Obj.setTimeout(driver, timeOut);
		int size = driver.findElements(by).size();
		Obj.setTimeout(driver, environmentInfo.timeout);
		return size;
	}

	protected boolean waitForElementToBeInvisible(By by) {
		return waitForElementToBeInvisible(by, environmentInfo.timeout);
	}

	protected boolean waitForElementToBePresent(By by) {
		return waitForElementToBePresent(by, environmentInfo.timeout);
	}

	public boolean isACheckboxPresent() {
		return isPresent(By.xpath("//input[@type = 'checkbox']"), 0);
	}

	public boolean isAButtonPresent() {
		return isPresent(By.xpath("//button"), 0);
	}

	public boolean isDisplayed(By by, double timeOut) {
		return isDisplayed(by, timeOut, driver);
	}

	public static boolean isDisplayed(By by, double timeOut, WebDriver driver) {
		for (int attempt = 0; attempt < 3; attempt++) {
			try {
				if (!isPresent(by, timeOut, driver))
					return false;

				Obj.setTimeout(driver, timeOut);
				boolean isDisplayed = driver.findElement(by).isDisplayed();
				Obj.setTimeout(driver, getOriginalTimeout(driver));
				return isDisplayed;
			} catch (StaleElementReferenceException ser) {
				LOG.debug(ser);
			} catch (RuntimeException e) {
				// Element not found after checking if it was present
				return false;
			}
		}
		return false;
	}

	protected boolean waitForElementToBeSelected(By by, boolean selected) {
		return waitForElementToBeSelected(by, selected, environmentInfo.timeout);
	}

	public static String isSortedOrganization(ArrayList<String> iterable, String currentCharacter) {
		Iterator<String> iter = iterable.iterator();
		if (!iter.hasNext()) {
			return "AC results do not have any organizations present to sort for current character:  " + currentCharacter;
		}
		String org = iter.next();
		while (iter.hasNext()) {
			String org2 = iter.next();
			if (compareToOrganization(org, org2) < 0) {
				return "AC results for organization are Not in order for '" + currentCharacter + "'. Organization " + org
						+ " is not in order to Organization " + org2;
			}
			org = org2;
		}
		return "AC results for organization are in alphabetical order.";
	}

	public static int compareToOrganization(String s1, String s2) {
		int shortest = s1.length() < s2.length() ? s1.length() : s2.length();
		if (s1.equals(s2))
			return 0;
		for (int i = 0; i < shortest; i++) {
			if (s1.charAt(i) > s2.charAt(i)) {
				return -1;
			} else if (s1.charAt(i) < s2.charAt(i)) {
				return 1;
			}
		}
		return 1;
	}

	protected boolean isElementAUnderElementB(By elementABy, By elementBBy) {
		Point locationA = driver.findElement(elementABy).getLocation();
		Point locationB = driver.findElement(elementBBy).getLocation();
		return locationA.y > locationB.y;
	}

	public String getBrowserConsoleErrors() {
		LogEntries logs = driver.manage().logs().get("browser");
		return logs.toString();
	}

	public List<Cookie> saveCookies() {
		List<Cookie> cookies = new ArrayList<Cookie>();
		for (Cookie ck : driver.manage().getCookies()) {
			cookies.add(ck);
		}
		return cookies;
	}

	public void injectCookies(List<Cookie> cookies) {
		for (Cookie ck : cookies) {
			driver.manage().addCookie(ck);
		}

	}

	public void deleteCookies() {
		driver.manage().deleteAllCookies();
		if (driver instanceof TexturaWebDriver) {
			((TexturaWebDriver) driver).setUsername("");
			((TexturaWebDriver) driver).setPassword("");
		}
	}

	public void saveFileInFirefoxWithAutoIt(String saveFilePath, String popuWindowTitle) {
		if (!environmentInfo.gridNodes.equals("1")) {
			throw new RuntimeException("saveFileInFirefoxWithAutoIt must only be used when gridnodes=1 for datechange or externals.");
		}
		boolean visible = false;
		for (int attempt = 0; attempt < 5; attempt++) {
			sleep(1);// wait for popup to load
			WinDef.HWND hWnd5 = User32.INSTANCE.FindWindow(null, popuWindowTitle);
			visible = User32.INSTANCE.IsWindowVisible(hWnd5);
			if (visible == true) {
				break;
			}
		}
		LOG.debug("In Page saveFileInFirefoxWithAutoIt, save dialog is visble after waiting 5 seconds? " + visible);
		WindowsOperations.saveFileFF("Opening", "Enter name of file", saveFilePath, 80);
	}

	public void pressTAB() {
		try {
			Robot robot = new Robot();
			// Press ESC
			robot.keyPress(KeyEvent.VK_TAB);
			robot.keyRelease(KeyEvent.VK_TAB);
		} catch (Exception e) {
		}
	}

	public Cookie getCookieFromList(List<Cookie> cookies, String name) {
		for (Cookie c : cookies) {
			if (c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}

	public static boolean wait(Function<WebDriver, Boolean> expectedCondition, WebDriver driver, long timeout, long pollingEvery,
			String message) {
		try {
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(timeout, TimeUnit.SECONDS).pollingEvery(pollingEvery,
					TimeUnit.MILLISECONDS).withMessage(message).ignoring(StaleElementReferenceException.class);
			boolean result = wait.until(expectedCondition);
			return result;
		} catch (TimeoutException te) {
			printFormattedMessage(message);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	protected boolean waitForElementToBeClickable(By by) {
		return waitForElementToBeClickable(by, environmentInfo.timeout);
	}

	protected boolean waitForPresenceOfAllElementsLocatedBy(By by, long timeout) {
		Function<WebDriver, Boolean> expectedCondition = (driver) -> {
			return driver.findElements(by).size() >= 1;
		};
		return wait(expectedCondition, driver, timeout, 1000, "There was not at least one element given by " + by.toString());
	}

	protected boolean waitForAlertToBePresent(long timeout) {
		Function<WebDriver, Boolean> expectedCondition = (driver) -> {
			try {
				return driver.switchTo().alert() != null;
			} catch (Exception e) {
				return false;
			}
		};
		return wait(expectedCondition, driver, timeout, 500, "Alert was not present after timeout: " + timeout + " seconds");
	}

	public boolean waitForElementToBeInvisible(By by, long timeout) {
		return waitForElementToBeInvisible(by, timeout, driver);
	}

	public static boolean waitForElementToBeInvisible(By by, long timeout, WebDriver driver) {
		Function<WebDriver, Boolean> expectedCondition = (fDriver) -> {
			return !isPresent(by, 0, fDriver) || !isDisplayed(by, 0, driver);
		};
		return wait(expectedCondition, driver, timeout, 500, "Element did not go invisible " + by.toString() + " after: " + timeout
				+ " seconds.");
	}

	protected boolean waitForElementToBePresent(By by, long timeout) {
		return waitForElementToBePresent(by, timeout, driver);
	}

	protected static boolean waitForElementToBePresent(By by, long timeout, WebDriver driver) {
		Function<WebDriver, Boolean> expectedCondition = (fDriver) -> {
			return isPresent(by, 0, fDriver);
		};
		return wait(expectedCondition, driver, timeout, 500, "Element was not present " + by.toString());

	}

	protected boolean waitUntilTitleContains(String text, long timeout) {
		Function<WebDriver, Boolean> expectedCondition = (driver) -> {
			return driver.getTitle().contains(text);
		};
		return wait(expectedCondition, driver, timeout, 500, "Title did not contain text" + text);
	}

	protected boolean waitForTextToBePresentInElement(WebElement element, String text, long timeout) {
		Function<WebDriver, Boolean> expectedCondition = (driver) -> {
			return element.getText().contains(text);
		};
		return wait(expectedCondition, driver, timeout, 500, "Element did not contain text " + text);
	}

	protected boolean waitForTextToBePresentInElementLocated(By by, String text, long timeout) {
		Function<WebDriver, Boolean> expectedCondition = (driver) -> {
			if (!isPresent(by, 0)) {
				return false;
			}
			return driver.findElement(by).getText().contains(text);
		};
		return wait(expectedCondition, driver, timeout, 500, "Element did not contain text " + text + ", By: " + by.toString());
	}

	protected boolean waitForNumberOfElemenentsToBe(By xpath, int num, Long timeout) {
		Function<WebDriver, Boolean> expectedCondition = (driver) -> {
			return getNumOccurrences(xpath, 0) == num;
		};
		return wait(expectedCondition, driver, timeout, 500, "Number of elements by " + xpath.toString() + " were not " + num);
	}

	protected boolean waitUntilVisibilityOfAllElementsLocatedBy(By by, long timeout) {
		Function<WebDriver, Boolean> expectedCondition = (driver) -> {
			List<WebElement> elements = driver.findElements(by);
			return elements.stream().filter(w -> w.isDisplayed()).count() == elements.size();
		};
		return wait(expectedCondition, driver, timeout, 1000, "All elements were not visible " + by.toString());
	}

	protected boolean waitForInvisibilityOfAllElements(List<WebElement> elements, long timeout) {
		Function<WebDriver, Boolean> expectedCondition = (driver) -> {
			return elements.stream().filter(w -> !w.isDisplayed()).count() == elements.size();
		};
		return wait(expectedCondition, driver, timeout, 500, "All elements were not invisible ");
	}

	protected boolean waitForElementToBeClickable(By by, long timeout) {
		return waitForElementToBeClickable(driver, by, timeout);
	}

	protected boolean waitForElementToBeStale(WebElement element, long timeout) {
		Function<WebDriver, Boolean> expectedCondition = (driver) -> {
			try {
				element.getText();
				return false;
			} catch (StaleElementReferenceException e) {
				return true;
			}
		};
		return wait(expectedCondition, driver, timeout, 500, "Element did not go stale ");
	}

	protected boolean waitForElementToBeSelected(By by, boolean selected, long timeout) {
		WebElement w = driver.findElement(by);
		Function<WebDriver, Boolean> expectedCondition = (driver) -> {
			return w.isSelected() == selected;
		};
		return wait(expectedCondition, driver, timeout, 500, "Element " + by.toString() + " was not in selected state " + selected);
	}

	public boolean isTextPresent(String text, long waitTime) {
		return waitForTextToBePresentInElementLocated(By.tagName("body"), text, waitTime);
	}

	public static boolean waitForElementToBeVisible(By by, long timeout, WebDriver driver) {
		Function<WebDriver, Boolean> expectedCondition = (tDriver) -> {
			return Page.isPresent(by, 0, tDriver) && Page.isDisplayed(by, 0, tDriver);
		};
		return Page.wait(expectedCondition, driver, timeout, 500, "Element was not visible " + by.toString());
	}

	protected boolean waitForElementToBeVisible(By by, long timeout) {
		return waitForElementToBeVisible(by, timeout, driver);
	}

	protected boolean waitForElementToBeVisible(By by) {
		return waitForElementToBeVisible(by, environmentInfo.timeout, driver);
	}

	public static boolean waitForElementToBeClickable(WebDriver driver, By by, long timeout) {
		Function<WebDriver, Boolean> expectedCondition = (fDriver) -> {
			boolean result = false;
			try {
				// checking to see if exception is thrown somewhere
				boolean isPresent = isPresent(by, 0, fDriver);
				boolean isDisplayed = isDisplayed(by, 0, fDriver);
				boolean isEnabled = fDriver.findElement(by).isEnabled();
				printFormattedMessage("waitForElementToBeClickable element: " + by.toString() + " conditions- present: " + isPresent
						+ " displayed: " + isDisplayed + " enabled: " + isEnabled);
				result = isPresent && isDisplayed && isEnabled;
			} catch (StaleElementReferenceException e) {
				result = false;
			} catch (Exception e) {
				throw new RuntimeException("Wait for element to be clickable threw exeption", e);
			}
			return result;
		};
		return wait(expectedCondition, driver, timeout, 500, "Element was not clickable " + by.toString());
	}

	protected boolean waitForTextToNotBePresentInElementLocated(By by, String text, long timeout) {
		Function<WebDriver, Boolean> expectedCondition = (driver) -> {
			if (!isPresent(by, 0)) {
				return false;
			}
			return !driver.findElement(by).getText().contains(text);
		};
		return wait(expectedCondition, driver, timeout, 500, "Element did not stop containing text " + text + ", By: " + by.toString());
	}

	public static boolean waitForDocumentReady(WebDriver driver, long timeout) {
		Function<WebDriver, Boolean> expectedCondition = (fDriver) -> {
			return executeJavaScript("return document.readyState;", driver).equals("complete");
		};
		return wait(expectedCondition, driver, timeout, 500, "Document was not in ready state!");
	}

	public static long getOriginalTimeout(WebDriver driver) {
		if (driver instanceof TexturaWebDriver) {
			return ((TexturaWebDriver) driver).environmentInfo.timeout;
		}
		// PQMWebDriver is not in framework, making it difficult to work into this situation.
		return (long) 15;
	}

	public long getOriginalTimeout() {
		return getOriginalTimeout(driver);
	}

	public void triggerEvent(By by, String event) {
		String js = "var event = document.createEvent(\"HTMLEvents\"); event.initEvent(\"" + event
				+ "\", false, true); arguments[0].dispatchEvent(event);";
		for (int attempt = 0; attempt < 3; attempt++) {
			try {
				executeJavaScript(js, driver.findElement(by));
				return;
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 
	 * uses the browser to send an httpget through javascript. Cookies and session data are obtained from the browser itself.
	 * Only use in postdeploymentsmoketest cases where clickFile does not work on stricter prod servers;
	 * 
	 * @return http get response text
	 */
	public String jsGet(String url) {
		String js = "window.jsGetResponse='undefined'; window.get = new XMLHttpRequest(); window.get.open('GET', '" + url
				+ "', false); window.get.onreadystatechange = function() {window.jsGetResponse = window.get.responseText; console.log(window.jsGetResponse)}; window.get.send(); return get.responseText;";
		return (String) executeJavaScript(js);
	}

	/*
	 * Will resize the window to the given percentage from the current size.
	 * 
	 * @param widthPercentage
	 * percent of the current width
	 * 
	 * @param heighPercentage
	 * percent of the current width
	 */
	public void resizeWindowToPercentage(int widthPercentage, int heighPercentage) {
		Dimension currentDim = driver.manage().window().getSize();
		Dimension newDim = new Dimension((int) (currentDim.width * (widthPercentage / 100.00)), (int) (currentDim.height * (heighPercentage
				/ 100.00)));
		driver.manage().window().setSize(newDim);
	}

	public static int getNumOccurrences(WebDriver driver, By by, double timeOut) {
		Obj.setTimeout(driver, timeOut);
		int size = driver.findElements(by).size();
		Obj.setTimeout(driver, getOriginalTimeout(driver));
		return size;
	}

	public void clickLinkPartialNormal(String linkText) {
		By by = By.xpath("//a[contains(text(),normalize-space('" + linkText + "'))]");
		driver.findElement(by).click();
	}

	public void clickLinkNormal(String linkText) {
		By by = By.xpath("//a[normalize-space(text())=normalize-space('" + linkText + "')]");
		driver.findElement(by).click();
	}

	public void clickBack() {
		driver.navigate().back();
	}

	public void click(By by) {
		driver.findElement(by).click();
	}

	public void scrollBy(WebElement element, int byAmount) {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollBy(0,'" + byAmount + "');", element);
	}

	public String getText(By by) {
		return driver.findElement(by).getText();
	}

	public boolean waitForElementToBeEnabled(By by, double time) {
		boolean result = false;
		for (int i = 0; i < time; i++) {
			if (isPresent(by, time)) {
				String data = driver.findElement(by).getAttribute("disabled");
				if (data != null && data.equals("true")) {
					sleep(1); // delay
				} else {
					return true;
				}
			}
		}
		return result;
	}

	public boolean waitForWebElementToBeDisplayed(WebElement webElement, double time) {
		int index = 0;
		while (!webElement.isDisplayed() && index < time) {
			sleep(1); // delay
			index++;
		}
		return webElement.isDisplayed();
	}

	public String getElementText(By by) {
		if (waitForElementToBePresent(by, environmentInfo.timeout)) {
			for (int i = 0; i < 5; i++) {
				String text = driver.findElement(by).getText();
				if (!text.isEmpty()) {
					return text;
				}
				sleep(1); // delay
			}
		}
		return "Text is empty for element " + by.toString();
	}

	public boolean isTextBold(By by) {
		WebElement w = driver.findElement(by);
		return w.getCssValue("font-weight").equals("700") || w.getCssValue("font-weight").equals("bold");
	}

	public boolean areButtonCornersRounded(By by) {
		WebElement w = driver.findElement(by);
		if (!w.getCssValue("border-bottom-left-radius").equals("0px") &&
				(!w.getCssValue("border-bottom-right-radius").equals("0px")) &&
				(!w.getCssValue("border-top-left-radius").equals("0px")) &&
				(!w.getCssValue("border-top-left-radius").equals("0px")))
			return true;
		else
			return false;

	}
}