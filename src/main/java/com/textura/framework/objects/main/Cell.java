package com.textura.framework.objects.main;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

public class Cell implements WebElement {

	private WebElementTable table;
	private WebElement cell;
	private int row;
	private int col;
	private String axis;

	public Cell(WebElementTable table, int row, int col) {
		this.table = table;
		cell = table.getFromTable(row, col);
		this.row = row;
		this.col = col;
		this.axis = null;
	}

	public Cell(WebElementTable table, int row, String axis) {
		this.table = table;
		cell = table.getFromTable(row, axis);
		this.row = row;
		this.axis = axis;
		this.col = -1;
	}

	private void refreshCell() {
		if (axis != null) {
			cell = table.getFromTable(row, axis);
		} else if (col == -1) {
			cell = table.getFromTable(row, col);
		}
	}

	@Override
	public void clear() {
		cell.clear();
	}

	@Override
	public void click() {
		cell.click();
	}

	@Override
	public WebElement findElement(By by) {
		return cell.findElement(by);
	}

	@Override
	public List<WebElement> findElements(By by) {
		return cell.findElements(by);
	}

	@Override
	public String getAttribute(String name) {
		return cell.getAttribute(name);
	}

	@Override
	public String getCssValue(String propertyName) {
		return cell.getCssValue(propertyName);
	}

	@Override
	public Point getLocation() {
		return cell.getLocation();
	}

	@Override
	public Dimension getSize() {
		return cell.getSize();
	}

	@Override
	public String getTagName() {
		return cell.getTagName();
	}

	@Override
	public String getText() {
		return cell.getText();
	}

	@Override
	public boolean isDisplayed() {
		return cell.isDisplayed();
	}

	@Override
	public boolean isEnabled() {
		return cell.isEnabled();
	}

	@Override
	public boolean isSelected() {
		return cell.isSelected() || cell.findElements(By.tagName("input")).size() > 0;
	}

	@Override
	public void sendKeys(CharSequence... keysToSend) {
		cell.sendKeys(keysToSend);
	}

	@Override
	public void submit() {
		cell.submit();
	}

	private void sleep(double d) {
		try {
			Thread.sleep((long) (d * 1000));
		} catch (InterruptedException e) {
		}
	}

	public void set(String value) {
		try {
			for (int attempt = 0; attempt < 3; attempt++) {
				sleep(.15);
				cell.click();
				if (cell.findElements(By.tagName("input")).size() > 0) {
					break;
				}
			}

		} catch (WebDriverException e) {
			if (axis == null) {
				new Assertions().assertTrue("Browser Timeout when clicking cell. Row: " + row + " Col: " + col + " " + e.getMessage(),
						false);
			} else {
				new Assertions().assertTrue("Browser Timeout when clicking cell. Row: " + row + " Axis: " + axis + " " + e.getMessage(),
						false);
			}
		}
		WebElement input = null;
		for (int attempt = 0; attempt < 5; attempt++) {
			try {
				input = cell.findElement(By.tagName("input"));
				break;
			} catch (Exception e) {
				cell.click();
				sleep(1);
				if (attempt >= 4) {
					input = cell.findElement(By.tagName("input"));
				}
			}
		}
		input.clear();
		input.sendKeys(value);

		// remove focus from cell
		for (int i = 0; i < 5; i++) {
			if (!table.isPresent(cell, By.tagName("input"), 0)) {
				return;
			}
			input.sendKeys(Keys.DOWN);
		}

	}

	public void loseFocus() {

	}

	public String getColor() {
		return cell.getCssValue("background-color");
	}

	public String getHoverText() {
		return cell.getAttribute("title").trim();
	}

	public boolean isReadOnly() {
		cell.click();
		sleep(.15);
		cell.click();
		sleep(.15);
		boolean a1 = cell.getAttribute("class").equals("readOnly"); // if true readonly
		boolean a2 = cell.getAttribute("class").contains("editable"); // if true editable
		boolean a3 = cell
				.findElements(
						By.xpath(
								".//input[not(@type='hidden') and not(@readonly = 'readonly')] | .//select[not(@type='hidden')] | .//textarea[not(@readonly='readonly')] "))
				.size() > 0; // if true editable
		boolean a4 = false;
		if (cell.getAttribute("type") != null) {
			a4 = cell.getAttribute("type").equals("text");
		}

		if (a1)// || a5)
			return true;
		if (a2 || a3 || a4)
			return false;

		return true;
	}

	public boolean isEditable() {
		for (int attempt = 0; attempt < 2; attempt++) {
			cell.click();
			cell.click();
			if (cell.getAttribute("class").equals("editable")) {
				try {
					cell.findElement(By.tagName("input"));
					return cell.getAttribute("class").equals("editable");
				} catch (Exception e) {
				}
			} else {
				return false;
			}
		}
		return false;
	}

	public void toggleCheckbox() {
		WebElement checkbox;
		for (int attempt = 0; attempt < 5; attempt++) {
			checkbox = cell.findElement(By.tagName("input"));
			boolean selected = checkbox.isSelected();
			checkbox.click();
			sleep(.2);
			checkbox = cell.findElement(By.tagName("input"));
			if (checkbox.isSelected() != selected)
				return;
		}
	}

	public void waitForHoverText() {
		for (int attempt = 0; attempt < 3; attempt++) {
			if (!getHoverText().isEmpty()) {
				return;
			}
			sleep(.5);
		}
	}

	public String waitAndGetHoverText() {
		waitForHoverText();
		return getHoverText();
	}

	public void setAndWaitForHoverText(String value) {
		String old = getHoverText();
		for (int attempt = 0; attempt < 3; attempt++) {
			refreshCell();
			set(value + Keys.ENTER);
			sleep(.5);
			refreshCell();
			if (!getHoverText().equals(old)) {
				return;
			}
		}
	}

	public String getTextColor() {
		return cell.getCssValue("color");
	}

	public void waitForErrorToLeave() {
		for (int i = 0; i < 15; i++) {
			if (getHoverText().equals(""))
				return;
			sleep(1);
		}
	}

	public void waitForCellToBeVisible() {
		refreshCell();
		for (int attempt = 0; attempt < 5; attempt++) {
			if (cell.isDisplayed() && cell.isEnabled()) {
				return;
			} else {
				sleep(1);
			}
		}
	}

	public boolean isBorderedRed() {
		String red = "rgba(255, 0, 0, 1)";
		return cell.getCssValue("border-bottom-color").equals(red) && cell.getCssValue("border-right-color").equals(red)
				&& cell.getCssValue("border-left-color").equals(red) && cell.getCssValue("border-top-color").equals(red);
	}

	public boolean isEnterInvoiceCellReadOnly() {
		return cell.getAttribute("class").equals("readOnly") ? true : false;
	}

	public String getBorderColor() {
		return cell.getCssValue("border-bottom-color");
	}

	public boolean isLinkPresent(String linkText) {
		return cell.findElements(By.linkText(linkText)).size() > 0;
	}

	public void clickLink(String linkText) {
		cell.findElement(By.linkText(linkText)).click();
	}

	@Override
	public <X> X getScreenshotAs(OutputType<X> arg0) throws WebDriverException {
		return null;
	}

	@Override
	public Rectangle getRect() {
		return cell.getRect();
	}
}
