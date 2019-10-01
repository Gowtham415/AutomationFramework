package com.textura.framework.objects.main;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import com.google.common.base.Function;

public class WebElementTable {
	private static final Logger LOG = LogManager.getLogger(WebElementTable.class);
	private By by;
	private WebDriver driver;

	public static final String YELLOW = "rgba(255, 255, 0, 1)";

	public static final String RED = "rgba(255, 0, 0, 1)";

	public static final String REDDISH = "rgba(187, 0, 0, 1)";

	public static final String SILVER = "rgba(204, 204, 204, 1)";

	/**
	 * Actually reading in the table is delayed until an element is accessed
	 */
	public WebElementTable(By b, WebDriver webDriver) {
		by = b;
		driver = webDriver;
	}

	public void setCellNow(int row, int col, String value) {
		WebElement cell = getFromTable(row, col);
		clickCell(row, col);
		try {
			cell.click();
		} catch (WebDriverException e) {
			new Assertions().assertTrue("Browser Timeout when clicking cell. Row: " + row + " Col: " + col + " " + e.getMessage(), false);
		}
		WebElement input = null;
		for (int attempt = 0; attempt < 5; attempt++) {
			try {
				input = cell.findElement(By.xpath(".//input"));
				break;
			} catch (Exception e) {
				cell.click();
				sleep(1);
				if (attempt >= 4) {
					input = cell.findElement(By.xpath(".//input"));
				}
			}
		}
		input.clear();
		input.sendKeys(value);
	}

	public void clickCell(int row, int col) {
		getFromTable(row, col).click();
	}

	public void clickCell(int row, String axis) {
		getFromTable(row, axis).click();
	}

	public void clickComment(int row, int col) {
		getFromTable(row, col).findElement(By.tagName("div")).click();
		for (int attempt = 0; attempt < 5; attempt++) {
			if (driver.findElements(By.className("dialog")).size() != 0)
				return;
			sleep(1);
		}
	}

	public void clickEditLink(int row, int col) {
		getFromTable(row, col).findElement(By.linkText("edit")).click();
	}

	public void clickImg(int row, int col) {
		getFromTable(row, col).findElement(By.tagName("img")).click();
	}

	public void clickDeleteIcon(int row, int col) {
		getFromTable(row, col).findElement(By.tagName("i")).click();
	}

	public void clickLink(int row, int col) {
		getFromTable(row, col).findElement(By.tagName("a")).click();
	}

	public void clickLink(int row, int col, String link) {
		getFromTable(row, col).findElement(By.linkText(link)).click();
	}

	public void clickLinkWhenPresent(int row, int col, String link) {
		if (isPresent(getFromTable(row, col), By.linkText(link), 5))
			clickLink(row, col, link);
	}

	public void expandNav(int row, int col) {
		sleep(0.5);
		getFromTable(row, col).findElement(By.tagName("img")).click();
		sleep(1.5);
	}

	public String getCellColor(int row, int col) {
		sleep(0.5);
		return getFromTable(row, col).getCssValue("background-color");
	}

	public String getCellError(int row, int col) {
		sleep(0.5);
		return getFromTable(row, col).getAttribute("title");
	}

	public String getCellFont(int row, int col) {
		sleep(0.5);
		return getFromTable(row, col).getCssValue("font-family");
	}

	public String getCellText(int row, int col) {
		sleep(0.3);
		return getFromTable(row, col).getText().trim();
	}

	public String getCellText(int row, String axis) {
		sleep(0.3);
		return getFromTable(row, axis).getText().trim();
	}

	public String getCellValue(int row, int col) {
		return getFromTable(row, col).findElement(By.tagName("input")).getAttribute("value");
	}

	public String getCellClass(int row, int col) {
		sleep(0.3);
		return getFromTable(row, col).findElement(By.xpath("./span/span/span")).getAttribute("class");
	}

	public ArrayList<String> getColumnValues(int col) {
		ArrayList<String> values = new ArrayList<String>();
		int rows = getNumRows();
		for (int row = 1; row <= rows; row++) {
			values.add(getCellText(row, col));
		}
		return values;
	}

	public ArrayList<String> getColumnValues(String columnName) {
		List<WebElement> columns = driver.findElements(By.xpath("//th"));
		int col = -1;
		for (int i = 0; i < columns.size(); i++) {
			if (columns.get(i).getText().equals(columnName)) {
				col = i + 1; // account for 0 based to 1 based
				break;
			}
		}
		if (col == -1) {
			throw new RuntimeException("Could not find column: '" + columnName + "'\n" + columns.toString());
		}
		return getColumnValues(col);
	}

	/**
	 * Returns WebElement from table. A WebElement is a reference to what is on
	 * the page so reading or updating using the WebElements interacts directly
	 * with the webpage.
	 * 
	 * @param row
	 *            starts at 1
	 * @param col
	 *            starts at 1
	 */
	public WebElement getFromTable(int row, int col) {
		waitForTable();
		WebElement w = null;
		w = driver.findElement(by).findElement(By.xpath(
				"(./*)[not(contains(@style, 'display: none')) and not(@type='hidden') and not(@language='JavaScript')][" + row
						+ "]/*[not(contains(@style, 'display: none')) and not(@type='hidden')][" + col + "]"));
		return w;
	}

	/**
	 * Returns WebElement from table. A WebElement is a reference to what is on
	 * the page so reading or updating using the WebElements interacts directly
	 * with the webpage.
	 * 
	 * @param row
	 *            starts at 1
	 * @param axis
	 *            axis attribute that identifies the column
	 */
	public WebElement getFromTable(int row, String axis) {
		WebElement w = null;
		w = driver.findElement(by).findElement(By.xpath(
				"(./*)[not(contains(@style, 'display: none')) and not(@type='hidden') and not(@language='JavaScript')][" + row
						+ "]/*[not(contains(@style, 'display: none')) and not(@type='hidden') and @axis = '" + axis + "']"));
		return w;
	}

	public String getHoverText(int row, int col) {
		sleep(0.5);
		String attribute = "";
		if (Obj.environmentInfo.browser.equals("Firefox") || Obj.environmentInfo.browser.equals("Chrome")) {
			attribute = "title";
		} else if (Obj.environmentInfo.browser.contains("Internet Explorer")) {
			attribute = "alt";
		}
		WebElement w = getFromTable(row, col);
		for (int i = 0; i < 3; i++) {
			if (w.getAttribute(attribute).length() > 0) {
				break;
			}
			sleep(1);
		}
		return getFromTable(row, col).getAttribute(attribute);
	}

	public int getNumCols(int row) {
		if (!waitForTable()) {
			return 0;
		}
		return driver.findElement(by).findElements(By.xpath(
				"(./*)[not(contains(@style, 'display: none')) and not(@type='hidden') and not(@language='JavaScript')][" + row
						+ "]/*[not(contains(@style, 'display: none')) and not(@type='hidden')]")).size();
	}

	public int getNumRows() {
		if (!waitForTable()) {
			return 0;
		}
		return driver.findElement(by).findElements(By.xpath(
				"(./*)[not(@style='display:none') and not(@type='hidden') and not(@language='JavaScript') and not(contains(@style,'display: none;'))]"))
				.size();
	}

	public String getRowColor(int row) {
		return getFromTable(row, 1).findElement(By.xpath("..")).getCssValue("background-color");
	}

	public ArrayList<String> getRowValues(int row) {
		ArrayList<String> values = new ArrayList<String>();
		int cols = getNumCols(row);
		for (int col = 1; col <= cols; col++) {
			values.add(getCellText(row, col));
		}
		return values;
	}
	

	public LinkedHashMap<String, String> getRowValuesAsMap(int row) {
		LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
		List<WebElement> columns = driver.findElement(by).findElements(By.xpath(
				"(./*)[not(contains(@style, 'display: none')) and not(@type='hidden') and not(@language='JavaScript')][" + row
				+ "]/*[not(contains(@style, 'display: none')) and not(@type='hidden')]"));
		for (WebElement elem: columns) {
			values.put(elem.getAttribute("axis"), elem.getText());
		}
		
		return values;
	}

	public String getSelectedValue(int row, int col) {
		Select s = new Select(getFromTable(row, col).findElement(By.tagName("select")));
		return s.getFirstSelectedOption().getText();
	}

	public ArrayList<String> getSelectOptions(int row, int col) {
		ArrayList<String> options = new ArrayList<String>();
		Select s = new Select(getFromTable(row, col).findElement(By.tagName("select")));
		for (WebElement w : s.getOptions()) {
			options.add(w.getText());
		}
		return options;
	}

	public boolean isCellACertainColor(int row, int col, String color) {
		WebElement cell = getFromTable(row, col);
		for (int time = 0; time < 5; time++) {
			String actualColor = cell.getCssValue("background-color");
			if (actualColor.equals(color)) {
				return true;
			} else {
				Obj.setTimeout(driver, 1);
				if (cell.findElements(By.tagName("span")).size() > 0) {
					actualColor = cell.findElement(By.tagName("span")).getCssValue("color");
					if (actualColor.equals(color)) {
						Obj.setTimeout(driver, Obj.environmentInfo.timeout);
						return true;
					}
				}
				sleep(1);
			}
		}
		Obj.setTimeout(driver, Obj.environmentInfo.timeout);
		return false;
	}

	public boolean isCellGreyOut(int row, int col) {
		WebElement cell = getFromTable(row, col);
		String actualColor = cell.getCssValue("background-color");
		return actualColor.equals(SILVER);
	}

	public boolean isCellReadOnly(int row, int col) {
		WebElement w = getFromTable(row, col);
		w.click();
		sleep(1.5);
		boolean a1 = w.getAttribute("class").equals("readOnly"); // if true readonly
		boolean a2 = w.getAttribute("class").contains("editable"); // if true editable
		Obj.setTimeout(driver, 1);
		boolean a3 = w.findElements(By.xpath(
				".//input[not(@type='hidden') and not(@readonly = 'readonly')] | .//select[not(@type='hidden')] | .//textarea[not(@readonly='readonly')] "))
				.size() > 0; // if true editable
		boolean a4 = false;
		if (w.getAttribute("type") != null) {
			a4 = w.getAttribute("type").equals("text");
		}

		Obj.setTimeout(driver, Obj.environmentInfo.timeout);
		if (a1)// || a5)
			return true;
		if (a2 || a3 || a4)
			return false;

		return true;
	}

	public boolean isCellReadOnly(int row, String axis) {
		WebElement w = getFromTable(row, axis);
		w.click();
		sleep(1.5);
		boolean a1 = w.getAttribute("class").equals("readOnly"); // if true readonly
		boolean a2 = w.getAttribute("class").contains("editable"); // if true editable
		Obj.setTimeout(driver, 1);
		boolean a3 = w.findElements(By.xpath(
				".//input[not(@type='hidden') and not(@readonly = 'readonly')] | .//select[not(@type='hidden')] | .//textarea[not(@readonly='readonly')] "))
				.size() > 0; // if true editable
		boolean a4 = false;
		if (w.getAttribute("type") != null) {
			a4 = w.getAttribute("type").equals("text");
		}

		Obj.setTimeout(driver, Obj.environmentInfo.timeout);
		if (a1)// || a5)
			return true;
		if (a2 || a3 || a4)
			return false;

		return true;
	}

	public boolean isCellRed(int row, int col) {
		return isCellACertainColor(row, col, RED);
	}

	public boolean isCellReddish(int row, int col) {
		return isCellACertainColor(row, col, REDDISH);
	}

	/**
	 * A cell is selected when the input element appears inside the cell
	 */
	public boolean isCellSelected(int row, int col) {
		WebElement w = getFromTable(row, col);
		Obj.setTimeout(driver, 5);
		boolean result = w.findElements(By.tagName("input")).size() > 0;
		Obj.setTimeout(driver, Obj.environmentInfo.timeout);
		return result;
	}

	public boolean isCellTextACertainColor(int row, int col, String color) {
		WebElement cell = getFromTable(row, col);
		for (int time = 0; time < 5; time++) {
			String actualColor = cell.getCssValue("color");
			if (actualColor.equals(color)) {
				return true;
			} else {
				Obj.setTimeout(driver, 1);
				if (cell.findElements(By.tagName("span")).size() > 0) {
					actualColor = cell.findElement(By.tagName("span")).getCssValue("color");
					if (actualColor.equals(color)) {
						Obj.setTimeout(driver, Obj.environmentInfo.timeout);
						return true;
					}
				}
				sleep(1);
			}
		}
		Obj.setTimeout(driver, Obj.environmentInfo.timeout);
		return false;
	}

	public boolean isCellTextRed(int row, int col) {
		return isCellTextACertainColor(row, col, RED);
	}

	public boolean isCellTextReddish(int row, int col) {
		return isCellTextACertainColor(row, col, REDDISH);
	}

	public boolean isCellYellow(int row, int col) {
		return isCellACertainColor(row, col, YELLOW);
	}

	public boolean isCheckBoxChecked(int row, int col) {
		return getFromTable(row, col).findElement(By.tagName("input")).isSelected();
	}

	public boolean isCheckBoxEnabled(int row, int col) {
		return getFromTable(row, col).findElement(By.tagName("input")).isEnabled();
	}

	public boolean isCheckBoxPresent(int row, int col) {
		Obj.setTimeout(driver, 1);
		int size = getFromTable(row, col).findElements(By.xpath(".//input[@type='checkbox']")).size();
		Obj.setTimeout(driver, Obj.environmentInfo.timeout);
		return size > 0;
	}

	public boolean isLinkPresent(int row, int col, String link) {
		return isPresent(getFromTable(row, col), By.linkText(link), 5);
	}

	public boolean isLinkPresentInTable(int row, int col, String link) {
		return isPresent(getFromTable(row, col), By.xpath(".//button[@type = 'button' and text() = 'view']"), 5);
	}

	public boolean isNewComponentLineEmpty(int row) {
		for (int i = 1; i <= getNumCols(row); i++) {
			WebElement w = getFromTable(row, i);
			if (w.getText().length() > 1 && !w.getText().equals("0.00")) {
				LOG.debug("notempty or equal to 0.00 " + w.getText().length() + w.getTagName());
				return false;
			}
		}
		return true;
	}

	public boolean isPresent(WebElement context, By by, int timeOut) {
		Obj.setTimeout(driver, timeOut);
		int size = context.findElements(by).size();
		Obj.setTimeout(driver, Obj.environmentInfo.timeout);
		return size > 0;
	}

	public boolean isRowExpandable(int row) {
		String image = getFromTable(row, 1).findElement(By.tagName("img")).getAttribute("src");
		if (image.contains("tv-expandable.gif")) {
			return true;
		}
		return false;
	}

	public boolean isTableEmpty() {
		return getNumRows() < 1;
	}

	public WebElement locateSelectedBox() {
		String xp = "//input[contains(@style, 'border: 1px dashed black;')]";
		return driver.findElement(By.xpath(xp));
	}

	public void printTable() {
		int maxrow = getNumRows();
		for (int i = 1; i <= maxrow; i++) {
			int maxcol = getNumCols(i);
			for (int j = 1; j <= maxcol; j++) {
				LOG.debug(getFromTable(i, j).getTagName() + " " + getFromTable(i, j).getText() + ">");
			}
			LOG.debug("");
		}
	}

	/**
	 * selects an option from a dropdown box
	 * 
	 * @param i
	 * @param j
	 */
	public void selectOption(int i, int j, String option) {
		Select box = new Select(getFromTable(i, j).findElement(By.tagName("select")));
		box.selectByVisibleText(option);
	}

	public void sendCtrlIns() {
		locateSelectedBox().sendKeys(Keys.chord(Keys.CONTROL, Keys.INSERT));
	}

	public void sendDownArrow() {
		locateSelectedBox().sendKeys(Keys.DOWN);
	}

	public void sendLeftArrow() {
		locateSelectedBox().sendKeys(Keys.LEFT);
	}

	public void sendRightArrow() {
		locateSelectedBox().sendKeys(Keys.RIGHT);
	}

	/**
	 * Since webdriver won't send keys to the entire page and Robot is not as
	 * reliable(too hacky, why shouldn't selenium do it, etc..), find the box
	 * that is selected and send keys to that.
	 */
	public void sendTab() {
		locateSelectedBox().sendKeys(Keys.TAB);
	}

	public void sendUpArrow() {
		locateSelectedBox().sendKeys(Keys.UP);
	}

	public void setCell(int row, int col, String value) {
		WebElement cell = getFromTable(row, col);
		try {
			cell.click();
			sleep(.15);// always gets a browser timeout here
		} catch (WebDriverException e) {
			new Assertions().assertTrue("Browser Timeout when clicking cell. Row: " + row + " Col: " + col + " " + e.getMessage(), false);
		}
		WebElement input = null;
		for (int attempt = 0; attempt < 5; attempt++) {
			try {
				input = cell.findElement(By.xpath(".//input"));
				break;
			} catch (Exception e) {
				cell.click();
				sleep(1);
				if (attempt >= 4) {
					input = cell;
				}
			}
		}
		input.clear();
		input.sendKeys(value);
		sleep(.15);
	}

	public void setCell(int row, String axis, String value) {
		WebElement cell = getFromTable(row, axis);
		try {
			cell.click();// always gets a browser timeout here
		} catch (WebDriverException e) {
			new Assertions().assertTrue("Browser Timeout when clicking cell. Row: " + row + " Col: " + axis + " " + e.getMessage(), false);
		}
		WebElement input = null;
		for (int attempt = 0; attempt < 5; attempt++) {
			try {
				input = cell.findElement(By.xpath(".//input"));
				break;
			} catch (Exception e) {
				cell.click();
				sleep(1);
				if (attempt >= 4) {
					input = cell.findElement(By.xpath(".//input"));
				}
			}
		}
		input.clear();
		input.sendKeys(value);
	}

	private void sleep(double d) {
		try {
			Thread.sleep((long) (d * 1000));
		} catch (InterruptedException e) {
		}
	}

	public void toggleBox(int row, int col) {
		WebElement w;
		for (int attempt = 0; attempt < 5; attempt++) {
			w = getFromTable(row, col).findElement(By.tagName("input"));
			boolean selected = w.isSelected();
			w.click();
			sleep(.2);
			w = getFromTable(row, col).findElement(By.tagName("input"));
			if (w.isSelected() != selected)
				return;
		}
	}

	public boolean verifyRowsEqual(int row1, int row2) {
		row1--;
		row2--;
		for (int i = 0; i < getNumCols(row1); i++) {
			if (!getFromTable(row1, i).getText().equals(getFromTable(row2, i).getText())) {
				return false;
			}
		}
		return true;
	}

	public void waitForCellTextChange(int row, int col, String initialText) {
		for (int attempt = 0; attempt < 15; attempt++) {
			if (!getFromTable(row, col).getText().equals(initialText)) {
				return;
			}
			sleep(0.5);
		}
	}

	public String waitForExpectedText(int row, int col, String text) {
		WebElement w = getFromTable(row, col);
		for (int attempt = 0; attempt < 5; attempt++) {
			if (w.getText().equals(text)) {
				return text;
			}
			sleep(1);
		}
		return w.getText();
	}

	public void waitForRows(int numRows) {
		for (int attempt = 0; attempt < 30; attempt++) {
			if (getNumRows() >= numRows) {
				return;
			}
			sleep(1);
		}
	}

	public boolean waitForTable() {

		Function<WebDriver, Boolean> expectedCondition = (driver) -> {
			List<WebElement> elements = driver.findElements(by);
			if (elements.isEmpty()) {
				return false;
			}
			WebElement table = elements.get(0);
			return table.isDisplayed() && table.isEnabled();
		};

		return Page.wait(expectedCondition, driver, 5, 500, "Element was not clickable " + by.toString());

	}

	public boolean isAttachmentPresent(int row, int col) {
		return isPresent(getFromTable(row, col), By.xpath("//tr[1]//td[9]//div[@id = 'hasAttachments_true']"), 5);
	}

	public boolean isEditRowIconPresent(int row, int col) {
		String image = getFromTable(row, col).findElement(By.tagName("img")).getAttribute("src");
		if (image.contains("OverRideIcon.gif")) {
			return true;
		}
		return false;
	}

	public boolean isRowModifiable(int row, int col) {
		return isPresent(getFromTable(row, col), By.tagName("img"), 5);
	}

	public String getCellBackgroundColor(int row, int col) {
		sleep(0.5);
		return getFromTable(row, col).findElement(By.xpath(".//div")).getCssValue("background-color");
	}

	public boolean isBellPresentInCell(int row, int col) {
		return isPresent(getFromTable(row, col), By.xpath("//span[@class = 'alert-bell ng-scope']"), 5);
	}

	public String getCellBorderColor(int row, int col) {
		return getFromTable(row, col).getCssValue("border-bottom-color");
	}
}