package com.textura.framework.objects.main;

import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.internal.Killable;
import org.openqa.selenium.remote.RemoteWebDriver;
import com.textura.framework.automationcontrollers.AutoRemote;
import com.textura.framework.environment.EnvironmentInfo;
import com.textura.framework.utils.GUIDialogs;

public class TexturaWebDriver implements WebDriver, TakesScreenshot, Killable, HasInputDevices, JavascriptExecutor {

	private static final Logger LOG = LogManager.getLogger(TexturaWebDriver.class);
	public RemoteWebDriver d;
	protected boolean maximized = false;
	protected boolean allowUserIntervention;
	protected String currentPage;
	protected EnvironmentInfo environmentInfo;

	public AutoRemote remote;
	private String username;
	private String password;

	public TexturaWebDriver(RemoteWebDriver driver, EnvironmentInfo environmentInfo) {
		this.environmentInfo = environmentInfo;
		d = driver;
		currentPage = "";
		allowUserIntervention = false;
		username = "";
	}

	public TexturaWebDriver allowUserIntervention() {
		allowUserIntervention = true;
		return this;
	}

	public boolean allowsIntervention() {
		return allowUserIntervention;
	}

	public TexturaWebDriver disallowUserIntervention() {
		allowUserIntervention = false;
		return this;
	}

	public void setCurrentPage(String s) {
		currentPage = s;
	}

	public String getCurrentPage() {
		return currentPage;
	}

	public void close() {
		d.close();
	}

	/**
	 * Find the first {@link WebElement} using the given method. This method is
	 * affected by the 'implicit wait' times in force at the time of execution.
	 * The findElement(..) invocation will return a matching row, or try again
	 * repeatedly until the configured timeout is reached.
	 * 
	 * findElement should not be used to look for non-present elements, use {@link #findElements(By)} and assert zero length response instead.
	 * 
	 * @param by
	 *            The locating mechanism
	 * @return The first matching element on the current page
	 * @throws NoSuchElementException
	 *             If no matching elements are found
	 * @see org.openqa.selenium.By
	 * @see org.openqa.selenium.WebDriver.Timeouts
	 */
	public WebElement findElement(By by) {
		if (remote.pause) {
			LOG.info("Automation paused");
			while (remote.pause) {
				sleep(1);
			}
			sleep(3);
			LOG.info("Automation resumed");
		}
		sleep(remote.sleep);
		WebElement w = null;
		try {
			w = new TexturaWebElement(d.findElement(by), this);
		}
		// try some stuff if finding failed
		catch (org.openqa.selenium.NoSuchElementException e) {
			Page.printFormattedMessage("Finding failed for: " + by);

			if (allowUserIntervention) {
				w = userInterventionLoop(by);
			}
			if (w == null) {
				throw new NoSuchElementException("Could not find element : " + by);
			}
		}
		return w;
	}

	public WebElement findElement(WebElementBasic element) {
		String frameid = element.frameID;
		By by = element.selector;
		d.switchTo().frame(frameid);
		d.switchTo().activeElement();

		if (remote.pause) {
			LOG.info("Automation paused");
			while (remote.pause) {
				sleep(1);
			}
			sleep(3);
			LOG.info("Automation resumed");
		}
		sleep(remote.sleep);
		WebElement w = null;
		try {
			w = new TexturaWebElement(d.findElement(by), this);
		}
		// try some stuff if finding failed
		catch (org.openqa.selenium.NoSuchElementException e) {
			Page.printFormattedMessage("Finding failed for: " + by);

			if (allowUserIntervention) {
				w = userInterventionLoop(by);
			}
			if (w == null) {
				throw new RuntimeException("Could not find element 1: " + by, e);
			}
		}
		return w;
	}

	public WebElement userInterventionLoop(By by) {
		while (true) {
			int n = GUIDialogs.askUserForHelp(by.toString());
			if (n == 0) {
				// retry
				try {
					return findElementFastFail(by);
				} catch (Exception a) {
				}
			}
			if (n == 1) {
				// ignore
				LOG.info("Returning dummy WebElement");
				return new DummyWebElement();
			} else if (n == 2) {
				GUIDialogs.showStackTrace();
			} else if (n == 3) {
				// allow failure
				return findElementFastFail(by);
			}
		}
	}

	/**
	 * This version of findElement will throw an exception if it cannot find the
	 * element in 3 seconds. Helpful for attempting retries.
	 * 
	 * @param by
	 * @return
	 * @throws TimeoutException
	 */
	public WebElement findElementFastFail(By by) {
		return findElement(by, 3);
	}

	/**
	 * This version of findElement will throw an exception if it cannot find the
	 * element in the specified number of seconds
	 * 
	 * @param by
	 * @return
	 * @throws TimeoutException
	 */
	public WebElement findElement(By by, double timeout) {
		d.manage().timeouts().implicitlyWait((long) (timeout * 1000), TimeUnit.MILLISECONDS);
		List<WebElement> list = TexturaWebElement.wrap(d.findElements(by), this);
		d.manage().timeouts().implicitlyWait(environmentInfo.timeout, TimeUnit.SECONDS);
		if (list.size() < 1) {
			throw new RuntimeException("Could not find element 2: " + by);
		}
		return new TexturaWebElement(list.get(0), this);
	}

	/**
	 * Returns true if 1 or more of the given xpaths are present
	 * 
	 * @param xpaths
	 * @return
	 */
	public boolean isAnyXpathsVisible(List<String> xpaths) {
		boolean result = false;
		for (int i = 0; i < xpaths.size(); i++) {
			result = result || isPresent(By.xpath(xpaths.get(i)), 0);
		}
		return result;
	}

	public boolean isPresent(By by, double seconds) {
		d.manage().timeouts().implicitlyWait((long) seconds * 1000, TimeUnit.MILLISECONDS);
		List<WebElement> found = TexturaWebElement.wrap(d.findElements(by), this);
		d.manage().timeouts().implicitlyWait(environmentInfo.timeout, TimeUnit.SECONDS);
		return found.size() > 0;
	}

	public List<WebElement> findElements(By by) {
		return TexturaWebElement.wrap(d.findElements(by), this);
	}

	public List<WebElement> findElements(By by, double timeout) {
		d.manage().timeouts().implicitlyWait((long) timeout * 1000, TimeUnit.MILLISECONDS);
		List<WebElement> result = TexturaWebElement.wrap(d.findElements(by), this);
		d.manage().timeouts().implicitlyWait(environmentInfo.timeout, TimeUnit.SECONDS);
		return result;
	}

	public void get(String url) {
		try {
			d.get(url);
			return;
		}
		// When page seems to hang and load nothing forever.
		// timeouts().pageLoadTimeout() triggers this
		catch (org.openqa.selenium.TimeoutException e) {
			LOG.info("driver.get(" + url + ") timed out. refreshing and getting again...");
			try {
				d.navigate().refresh();
				d.get(url);
			} catch (org.openqa.selenium.TimeoutException f) {
				LOG.info("get(url) timed out on retry.");
			}
		}

	}

	/**
	 * Find the first {@link WebElement} found using each given By.
	 * 
	 * @return The first matching element on the current page
	 * @throws NoSuchElementException
	 *             If no matching elements are found
	 */
	public WebElement findAnyElement(String... xpaths) {
		return findElement(By.xpath(combineXpaths(xpaths)));
	}

	private String combineXpaths(String... xpaths) {
		StringBuilder xpath = new StringBuilder();
		for (int i = 0; i < xpaths.length; i++) {
			xpath.append(xpaths[i]);
			if (i != xpaths.length - 1) {
				xpath.append(" | ");
			}
		}
		return xpath.toString();
	}

	public String getCurrentUrl() {
		return d.getCurrentUrl();
	}

	public String getPageSource() {
		return d.getPageSource();
	}

	public String getTitle() {
		sleep(1.5);
		return d.getTitle();
	}

	public String getWindowHandle() {
		return d.getWindowHandle();
	}

	public Set<String> getWindowHandles() {
		return d.getWindowHandles();
	}

	public Options manage() {
		return d.manage();
	}

	public org.openqa.selenium.Dimension getWindowSize() {
		return d.manage().window().getSize();
	}

	public void setWindowSize(org.openqa.selenium.Dimension size) {
		maximized = false;
		d.manage().window().setSize(size);
	}

	public void maximize() {
		maximized = true;
		d.manage().window().maximize();
	}

	public boolean isMaximized() {
		return maximized;
	}

	public Navigation navigate() {
		return d.navigate();
	}

	public void quit() {
		d.quit();
	}

	public TargetLocator switchTo() {
		return d.switchTo();
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
		try {
			return ((TakesScreenshot) d).getScreenshotAs(target);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void kill() {
		((Killable) d).kill();
	}

	@Override
	public Keyboard getKeyboard() {
		return ((HasInputDevices) d).getKeyboard();
	}

	@Override
	public Mouse getMouse() {
		return ((HasInputDevices) d).getMouse();
	}

	public void sleep(double f) {
		try {
			Thread.sleep((long) (f * 1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object executeScript(String script, Object... args) {
		return ((JavascriptExecutor) d).executeScript(script, args);
	}

	@Override
	public Object executeAsyncScript(String script, Object... args) {
		return ((JavascriptExecutor) d).executeAsyncScript(script, args);
	}

	/**
	 * Retrieves the hostname of the computer running the instance of webdriver
	 * 
	 * @return
	 */
	public String getHost() {
		if (environmentInfo.gridMode.equalsIgnoreCase("false")) {
			return "local";
		}
		String json = "";
		try {
			String session = ((RemoteWebDriver) d).getSessionId().toString();
			String gridUrl = environmentInfo.gridURL.replace("wd/hub", "");
			URL url = new URL(gridUrl + "grid/api/testsession?session=" + session);
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost request = new HttpPost(url.toURI());
			HttpResponse response = client.execute(request);
			StringWriter s = new StringWriter();
			IOUtils.copy(response.getEntity().getContent(), s);
			response.getEntity().getContent().close();
			json = s.toString();
			JSONObject j = new JSONObject(json);
			if (j.get("success").toString().equals("false")) {
				return "notfound";
			}
			String t = j.get("proxyId").toString();
			return new URL(t).getHost();
		} catch (HttpHostConnectException e) {
			return "local";
		} catch (Exception e) {
			LOG.info(json);
			e.printStackTrace();
			return "null";
		}
	}

	public void setRemote(AutoRemote remote) {
		this.remote = remote;
	}

	public String getSessionId() {
		return d.getSessionId().toString();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String newName) {
		username = newName;
	}

	public void setPassword(String Password) {
		password = Password;
	}

	public String getPassword() {
		return password;
	}

	public String getVersion() {
		return d.getCapabilities().getVersion();
	}
}