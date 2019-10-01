package com.textura.framework.objects.main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;
import com.textura.framework.utils.GUIDialogs;
import com.textura.framework.utils.JavaHelpers;

public class TexturaWebElement implements WebElement, WrapsElement, Locatable {

	private static final Logger LOG = LogManager.getLogger(TexturaWebElement.class);
	private WebElement w;
	private WebDriver d;

	public TexturaWebElement(WebElement w, WebDriver d) {
		this.w = w;
		this.d = d;
	}

	@Override
	public void clear() {
		try {
			w.clear();
		} catch (WebDriverException e1) {
			try {
				Thread.sleep(1000);
				w.clear();
			} catch (WebDriverException e2) {
				w.clear();
			} catch (InterruptedException e3) {
			}
		}
	}

	@Override
	public void click() {
		try {
			w.click();
		} catch (org.openqa.selenium.TimeoutException te) {
			te.printStackTrace();
		} catch (WebDriverException e) {
			String cause = e.getMessage().substring(0, e.getMessage().indexOf("Command duration"));
			Page.printFormattedMessage("Retrying click after exception cause: " + cause + ",\nAt line: " + JavaHelpers
					.getTestCaseClassName());
			try {
				Thread.sleep(1000);
				w.click();
			} catch (WebDriverException e2) {
				JavascriptExecutor j = (JavascriptExecutor) d;
				j.executeScript("arguments[0].click()", w);
			} catch (InterruptedException e3) {
			}
		}
	}

	@Override
	public WebElement findElement(By by) {

		WebElement r = null;
		try {
			return new TexturaWebElement(w.findElement(by), d);
		} catch (org.openqa.selenium.NoSuchElementException e) {
			LOG.debug("Finding failed for: " + by + " " + JavaHelpers.getTestCaseMethodName());
			if (d instanceof TexturaWebDriver && ((TexturaWebDriver) d).allowsIntervention()) {
				r = userInterventionLoop(by);
				if (r == null) {
					throw new RuntimeException("Could not find element: " + by, e);
				}
			} else {
				throw e;
			}
		}
		return r;
	}

	private WebElement userInterventionLoop(By by) {
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
				LOG.debug("Returning dummy PQMWebElement");
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
		List<WebElement> list = TexturaWebElement.wrap(d.findElements(by), d);
		d.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
		if (list.size() < 1) {
			throw new RuntimeException("Could not find element: " + by);
		}
		return new TexturaWebElement(list.get(0), d);
	}

	@Override
	public List<WebElement> findElements(By arg0) {
		return TexturaWebElement.wrap(w.findElements(arg0), d);
	}

	@Override
	public String getAttribute(String arg0) {
		return w.getAttribute(arg0);
	}

	@Override
	public String getCssValue(String arg0) {
		return w.getCssValue(arg0);
	}

	@Override
	public Point getLocation() {
		return w.getLocation();
	}

	@Override
	public Dimension getSize() {
		return w.getSize();
	}

	@Override
	public String getTagName() {
		return w.getTagName();
	}

	@Override
	public String getText() {
		return w.getText();
	}

	@Override
	public boolean isDisplayed() {
		return w.isDisplayed();
	}

	@Override
	public boolean isEnabled() {
		return w.isEnabled();
	}

	@Override
	public boolean isSelected() {
		return w.isSelected();
	}

	@Override
	public void sendKeys(CharSequence... arg0) {
		// this hack forces vaadin to consider the box as focused on
		// allows validation even though the window may not have focus
		if (w.getAttribute("class").contains("prompt")) {
			String oldClass = w.getAttribute("class");
			String newClass = oldClass.replace("prompt", "focus");
			JavascriptExecutor js = (JavascriptExecutor) d;
			js.executeScript("arguments[0].setAttribute('class', '" + newClass + "');", w);
		}
		w.sendKeys(arg0);
	}

	@Override
	public void submit() {
		w.submit();
	}

	public static List<WebElement> wrap(List<WebElement> elements, WebDriver d) {
		List<WebElement> result = new ArrayList<WebElement>();
		for (WebElement w : elements) {
			result.add(new TexturaWebElement(w, d));
		}
		return result;
	}

	@Override
	public WebElement getWrappedElement() {
		return w;
	}

	@Override
	public Coordinates getCoordinates() {
		return ((Locatable) w).getCoordinates();
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> arg0) throws WebDriverException {
		return null;
	}

	@Override
	public Rectangle getRect() {
		return w.getRect();
	}
}