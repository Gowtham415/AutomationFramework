package com.textura.framework.objects.main;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;

/**
 * This is used to return an inactive webelement when user wishes to ignore the
 * failure that occurs when webdriver cannot find an element.
 * 
 */
public class DummyWebElement implements WebElement, Locatable {

	@Override
	public void click() {
	}

	@Override
	public void submit() {
	}

	@Override
	public void sendKeys(CharSequence... keysToSend) {
	}

	@Override
	public void clear() {
	}

	@Override
	public String getTagName() {
		return "null";
	}

	@Override
	public String getAttribute(String name) {
		return "null";
	}

	@Override
	public boolean isSelected() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public String getText() {
		return "null";
	}

	@Override
	public List<WebElement> findElements(By by) {
		return new ArrayList<WebElement>();
	}

	@Override
	public WebElement findElement(By by) {
		return new DummyWebElement();
	}

	@Override
	public boolean isDisplayed() {
		return false;
	}

	@Override
	public Point getLocation() {
		return new Point(-1, -1);
	}

	@Override
	public Dimension getSize() {
		return new Dimension(0, 0);
	}

	@Override
	public String getCssValue(String propertyName) {
		return "";
	}

	@Override
	public Coordinates getCoordinates() {
		return new CoordinatesImpl();
	}


	private static class CoordinatesImpl implements Coordinates {

		@Override
		public Object getAuxiliary() {
			return null;
		}

		@Override
		public Point inViewPort() {
			return new Point(-1, -1);
		}

		@Override
		public Point onScreen() {
			return new Point(-1, -1);
		}

		@Override
		public Point onPage() {
			// TODO Auto-generated method stub
			return null;
		}
	}


	@Override
	public <X> X getScreenshotAs(OutputType<X> arg0) throws WebDriverException {
		return null;
	}

	@Override
	public Rectangle getRect() {
		return null;
	}
}