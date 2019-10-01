package com.textura.framework.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.textura.framework.objects.main.Page;

public class ExcelUtils {

	/**
	 * Reads the first sheet of an xls file and puts it into a string.
	 * 
	 * @param path
	 *            The file path of the xls file.
	 * @return xls sheet contents
	 */

	public static String readXLSAsStringFromStream(InputStream blob) {

		String str = "";

		HSSFWorkbook workbook = null;
		try {

			workbook = new HSSFWorkbook(blob);
			Sheet s = workbook.getSheetAt(0);

			int numRows = s.getLastRowNum() + 2;
			for (int i = 1; i < numRows; i++) {
				Row r = s.getRow(i - 1);

				if (r != null) {
					int numCols = s.getRow(i - 1).getLastCellNum() + 1;

					for (int j = 1; j < numCols; j++) {
						str += getCellValue(s, i, j);
						if (j < (numCols - 1))
							str += ",  ";
					}
				}
				str += '\n';
			}// end outer for loop
			workbook.close();
			blob.close();
			return str;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return str;
	}

	public static String readXLSSheetAsString(String path) {
		String str = "";
		Sheet sheet = getSheetFromXLSPath(path, 0);

		int numRows = sheet.getLastRowNum() + 2;
		for (int i = 1; i < numRows; i++) {
			Row r = sheet.getRow(i - 1);

			if (r != null) {
				int numCols = sheet.getRow(i - 1).getLastCellNum() + 1;

				for (int j = 1; j < numCols; j++) {
					str += getCellValue(sheet, i, j);
					if (j < (numCols - 1))
						str += ",  ";
				}
			}
			str += '\n';
		} // end outer for loop

		return str;

	}

	/*----------------------------------------------------------------------------------------------------*/

	public static int getLastCellNumber(String path, int row) {

		Sheet sheet = getSheetFromXLSPath(path, 0);
		Row r = sheet.getRow(row);
		int lastCell = r.getLastCellNum();
		return lastCell;

	}

	/*----------------------------------------------------------------------------------------------------*/

	/**
	 * Returns cell value in given sheet of xls file
	 * 
	 * @param s
	 *            Sheet of the xls file
	 * @param row
	 *            Row number -
	 * @param col
	 *            Column number
	 * @return The cell value at row, col
	 */
	public static String getCellValue(Sheet s, int row, int col) {
		Row r = s.getRow(row - 1);
		if (r == null)
			return "";

		Cell cell = r.getCell(col - 1);

		if (cell == null)
			return "";

		DataFormatter fmt = new DataFormatter();
		if (cell.getCellStyle().getDataFormatString().equals("M/D/YYYY h:mm:ss")) {
			return fmt.formatCellValue(cell).toString();
		} else if (cell.getCellStyle().getDataFormatString().equals("#,###0.00")) {
			return fmt.formatCellValue(cell).toString();
		} else {
			return cell.toString();
		}
	}

	/**
	 * Compares the first sheet of two xls files on a cell value by cell value basis.
	 * Location of cell mismatch is printed out to console.
	 * 
	 * @param textFile
	 *            The file path for the xls file to be compared.
	 * @param patternFile
	 *            The file path for the xls file that the other xls will be compared against. May contain wildcards.
	 * @return True if each xls' cell value matches the other xls' cell value.
	 */
	public static boolean wildCardCompareXLSSheets(String patternFile, String textFile) {
		Sheet s1 = getSheetFromXLSPath(patternFile, 0);
		Sheet s2 = getSheetFromXLSPath(textFile, 0);

		int numRows = s1.getLastRowNum() > s2.getLastRowNum() ? s1.getLastRowNum() + 2 : s2.getLastRowNum() + 2;

		char letter = 0x41;
		Row r1, r2;
		for (int i = 1; i < numRows; i++) {
			r1 = s1.getRow(i - 1);
			r2 = s2.getRow(i - 1);

			if ((r1 == null && r2 != null) || (r2 == null && r1 != null)) {
				Page.printFormattedMessage("Rows do not match.");
				Page.printFormattedMessage("patternFile row " + i);
				Page.printFormattedMessage("textFile row " + i);
				return false;
			}

			if (r1 != null && r2 != null) {
				int numCols = r1.getLastCellNum() > r2.getLastCellNum() ? r1.getLastCellNum() + 1 : r2.getLastCellNum() + 1;

				for (int j = 1; j < numCols; j++) {

					String val1 = getCellValue(s1, i, j);
					String val2 = getCellValue(s2, i, j);

					if (!val1.equals(val2) && !val1.equals("*")) {

						Page.printFormattedMessage("patternFile " + s1.getSheetName() + " cell: " + letter + (i) + " value: '" + val1
								+ "'");
						Page.printFormattedMessage("textFile " + s2.getSheetName() + " cell: " + letter + (i) + " value: '" + val2
								+ "'");

						return false;
					}
					letter++;
				}
			}
			letter = 0x41;
		}// end outer for loop

		return true;
	}

	public static boolean wildCardCompareXLSXSheets(String patternFile, String textFile) {

		FileInputStream fis1 = null;
		FileInputStream fis2 = null;
		XSSFWorkbook workbook1 = null;
		XSSFWorkbook workbook2 = null;
		try {
			fis1 = new FileInputStream(patternFile);
			fis2 = new FileInputStream(textFile);

			workbook1 = new XSSFWorkbook(fis1);
			workbook2 = new XSSFWorkbook(fis2);

			Sheet s1 = workbook1.getSheetAt(0);
			Sheet s2 = workbook2.getSheetAt(0);

			int numRows = s1.getLastRowNum() > s2.getLastRowNum() ? s1.getLastRowNum() + 2 : s2.getLastRowNum() + 2;

			char letter = 0x41;
			Row r1, r2;
			for (int i = 1; i < numRows; i++) {
				r1 = s1.getRow(i - 1);
				r2 = s2.getRow(i - 1);

				if ((r1 == null && r2 != null) || (r2 == null && r1 != null)) {
					System.out.println("Rows do not match.");
					System.out.println("patternFile row " + i);
					System.out.println("textFile row " + i);
					return false;
				}

				if (r1 != null && r2 != null) {
					int numCols = r1.getLastCellNum() > r2.getLastCellNum() ? r1.getLastCellNum() + 1 : r2.getLastCellNum() + 1;

					for (int j = 1; j < numCols; j++) {

						String val1 = getCellValue(s1, i, j);
						String val2 = getCellValue(s2, i, j);

						if (!val1.equals(val2) && !val1.equals("*")) {

							System.out.println();
							System.out.println("patternFile " + s1.getSheetName() + " cell: " + letter + (i) + " value: '" + val1 + "'");
							System.out.println("textFile " + s2.getSheetName() + " cell: " + letter + (i) + " value: '" + val2 + "'");

							return false;
						}
						letter++;
					}
				}
				letter = 0x41;
			}// end outer for loop

		}

		catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (fis1 != null) {
				try {
					fis1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fis2 != null) {
				try {
					fis2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (workbook1 != null) {
				try {
					workbook1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (workbook2 != null) {
				try {
					workbook2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return true;
	}

	public static String getCellFromExcelFile(String path, int sheet, int row, int col) {
		Sheet s = getSheetFromXLSPath(path, sheet);

		try {
			if (s.getRow(row) != null && s.getRow(row).getCell(col) != null)
				return s.getRow(row).getCell(col).toString();
			else
				return "";

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static String getCellFromXLSFile(String path, int row, int col) {
		return getCellFromXLSFile(path, 0, row, col);
	}

	public static String getCellFromXLSXFile(String path, int row, int col) {
		return getCellFromXLSXFile(path, 0, row, col);
	}

	public static String getCellFromXLSFile(String path, int sheet, int row, int col) {
		try (FileInputStream fis = new FileInputStream(new File(path)); HSSFWorkbook workbook = new HSSFWorkbook(fis)) {
			HSSFSheet s = workbook.getSheetAt(sheet);
			if (s.getRow(row) != null && s.getRow(row).getCell(col) != null) {
				DataFormatter dataFormatter = new DataFormatter();
				return dataFormatter.formatCellValue(s.getRow(row).getCell(col));
			} else
				return "";

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String getCellFromXLSXFile(String path, int sheet, int row, int col) {
		try (FileInputStream fis = new FileInputStream(new File(path)); XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
			XSSFSheet s = workbook.getSheetAt(sheet);
			return s.getRow(row).getCell(col).toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String getDataFormatCellStyleFromXLSFile(String path, int row, int col) {
		return getCellStyleFromXLSFile(path, 0, row, col, getDataFormat);
	}

	public static String getCellStyleFromExcelFile(String path, int row, int col) {
		return getCellStyleFromExcelFile(path, 0, row, col);
	}

	public static String getCellColorFromXLSFile(String path, int row, int col) {
		return getCellStyleFromXLSFile(path, 0, row, col, getFillColor);
	}

	static Function<Cell, String> getFillColor = (s) -> ((HSSFColor) s.getCellStyle().getFillForegroundColorColor()).getHexString();
	// also try getFillBackgroundColorColor, getFillForegroundColor, getFillForegroundColorColor

	static Function<Cell, String> getDataFormat = (s) -> s.getCellStyle().getDataFormatString();

	public static String getCellStyleFromXLSFile(String path, int sheet, int row, int col, Function<Cell, String> f) {
		try (FileInputStream fis = new FileInputStream(new File(path)); HSSFWorkbook workbook = new HSSFWorkbook(fis)) {
			HSSFSheet s = workbook.getSheetAt(sheet);
			return f.apply(s.getRow(row).getCell(col));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Takes an Excel file, replaces wildcard with whatever you pass, then saves it as a new file.
	 * 
	 * @param originalFile
	 * @param modifiedFile
	 * @param wildCard
	 * @param replacement
	 */

	public static void replaceWildCardCellsForExcelSheet(String originalFile, String modifiedFile, String wildCard, String replacement) {
		FileInputStream fis = null;
		Workbook workBook = null;
		FileOutputStream fos = null;

		try {
			fis = new FileInputStream(originalFile);
			workBook = WorkbookFactory.create(fis);
			Sheet sheet = workBook.getSheetAt(0);
			DataFormatter formatter = new DataFormatter();

			for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
				Row row = sheet.getRow(i);

				for (int j = 0; j < row.getPhysicalNumberOfCells(); j++) {
					Cell cell = row.getCell(j);

					if (formatter.formatCellValue(cell).contains(wildCard)) {
						CellType type = cell.getCellTypeEnum();
						cell.setCellType(CellType.STRING);
						cell.setCellValue(replacement);
						cell.setCellType(type);
					}
				}
			}

			fos = new FileOutputStream(modifiedFile);
			workBook.write(fos);
			workBook.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			e.printStackTrace();
		}
	}

	public static String getCellStyleFromExcelFile(String path, int sheet, int row, int col) {
		FileInputStream fis = null;
		Workbook workbook = null;
		try {
			fis = new FileInputStream(new File(path));
			workbook = WorkbookFactory.create(fis);
			Sheet s = workbook.getSheetAt(sheet);
			return s.getRow(row).getCell(col).getCellStyle().getDataFormatString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * compares r1 and r2 for equality using wildcards
	 */
	public static boolean wildCardAreRowsEqual(Row patternRow, Row actualRow) {
		char letter = 0x41;
		if (patternRow != null && actualRow != null) {
			int numCols = patternRow.getPhysicalNumberOfCells() > actualRow.getPhysicalNumberOfCells() ? patternRow
					.getPhysicalNumberOfCells() + 1 : actualRow.getPhysicalNumberOfCells() + 1;

			for (int j = 1; j < numCols; j++) {

				Cell c1 = patternRow.getCell(j);
				Cell c2 = actualRow.getCell(j);

				if (c1 != null && c2 != null) {
					String val1 = c1.toString();
					String val2 = c2.toString();

					if (!val1.equals(val2) && !val1.equals("*")) {

						System.out.println();
						System.out.println("patternFile cell: " + letter + (j) + " value: '" + val1 + "'");
						System.out.println("textFile cell: " + letter + (j) + " value: '" + val2 + "'");
						return false;
					}
				} else if (c1 == null && c2 == null) {
					continue;
				} else {
					System.out.println();
					System.out.println("patternFile cell: " + letter + (j));
					System.out.println("textFile cell: " + letter + (j));
					return false;
				}

				letter++;
			}
			letter = 0x41;
		}
		return true;
	}

	/**
	 * 
	 * @param actualPath
	 *            path to the file that contains the actual value
	 * @param expectedPath
	 *            path to the file that contains the expected value
	 * @return true if excel rows are all present, but not necessarily in the same order, false otherwise
	 */
	public static boolean wildCardMatchWithoutOrder(String actualPath, String expectedPath) {
		FileInputStream fis1 = null;
		FileInputStream fis2 = null;
		HSSFWorkbook expectedWorkbook = null;
		HSSFWorkbook actualWorkbook = null;
		try {
			fis1 = new FileInputStream(expectedPath);
			fis2 = new FileInputStream(actualPath);

			expectedWorkbook = new HSSFWorkbook(fis1);
			actualWorkbook = new HSSFWorkbook(fis2);

			Sheet expectedSheet = expectedWorkbook.getSheetAt(0);
			Sheet actualSheet = actualWorkbook.getSheetAt(0);

			int numRows = expectedSheet.getLastRowNum() > actualSheet.getLastRowNum() ? expectedSheet.getLastRowNum() + 2
					: actualSheet.getLastRowNum() + 2;

			Row expectedRow, currentRow;

			for (int i = 1; i < numRows; i++) { // loop through all the expected rows
				expectedRow = expectedSheet.getRow(i - 1);

				boolean found = false;

				for (int j = 1; j < numRows; j++) { // compare with actual sheet to make sure a row equals the expected row
					currentRow = actualSheet.getRow(j - 1);
					if (wildCardAreRowsEqual(expectedRow, currentRow)) {
						found = true;
						break;
					}
				}
				if (!found) {
					return false;
				}
			}// end row compare loop
			return true;
		}// end try
		catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (fis1 != null) {
				try {
					fis1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fis2 != null) {
				try {
					fis2.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (expectedWorkbook != null) {
				try {
					expectedWorkbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (actualWorkbook != null) {
				try {
					actualWorkbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * getNumberOfRows assumes that the excel file has a header
	 * 
	 * @param downloads
	 * @return returns string that represents number of rows minus the header
	 */
	public static String getNumberOfRows(String downloads) {
		FileInputStream fis = null;
		int numRows = 0;
		HSSFWorkbook workbook = null;
		try {
			fis = new FileInputStream(downloads);
			workbook = new HSSFWorkbook(fis);
			HSSFSheet sheet = workbook.getSheetAt(0);
			numRows = sheet.getPhysicalNumberOfRows();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String result = Integer.toString(numRows - 1);
		return result;
	}

	/**
	 * getNumberOfRows assumes that the excel file has a header
	 * 
	 * @param downloads
	 * @return returns string that represents number of rows minus the header
	 */
	public static String getNumberOfRowsOldExcel(String downloads) {
		FileInputStream fis = null;
		XSSFWorkbook workbook = null;
		int numRows = 0;
		try {
			fis = new FileInputStream(downloads);
			workbook = new XSSFWorkbook(fis);
			XSSFSheet sheet = workbook.getSheetAt(0);
			numRows = sheet.getPhysicalNumberOfRows();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		String result = Integer.toString(numRows - 1);
		return result;
	}

	public static boolean isXLSDownloaded(String path) {
		File f = new File(path);
		if (f.exists()) {

			do {

			} while (false);

			return true;
		}
		return false;
	}

	/**
	 * Gets the last row number with data on the first sheet. Row number is 1 based.
	 * 
	 * @param path
	 *            Path of the excel sheet
	 * @return The row number
	 */
	public static int getLastRowNumberForXls(String path) {
		FileInputStream fis = null;
		HSSFWorkbook workbook = null;
		try {
			fis = new FileInputStream(new File(path));
			workbook = new HSSFWorkbook(fis);
			Sheet s = workbook.getSheetAt(0);
			if (s.getLastRowNum() == 0 && s.getPhysicalNumberOfRows() == 0)
				return 0;
			else
				return s.getLastRowNum() + 1;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (workbook != null) {
				try {
					workbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Gets the number cells in given row that has content.
	 * 
	 * @param path
	 *            The xls file path
	 * @param sheetNumber
	 *            Excel sheet number, 0 based
	 * @param row
	 *            The row number, 0 based
	 * @return The number of non empty cells
	 */
	public static int getNumberofNonEmptyCells(String path, int sheetNumber, int row) {
		Workbook workbook = null;
		FileInputStream fis = null;
		int numCells = 0;
		try {
			fis = new FileInputStream(path);
			// creates the appropriate HSSFWorkbook / XSSFWorkbook from the given file
			workbook = WorkbookFactory.create(fis);
			Sheet sheet = workbook.getSheetAt(sheetNumber);
			Row r = sheet.getRow(row);

			Iterator<Cell> iterator = r.cellIterator();
			Cell c;
			while (iterator.hasNext()) {
				c = iterator.next();
				if (!c.toString().trim().isEmpty()) {
					numCells++;
				}
			}
			return numCells;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();

		}

		finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Returns the formula value of the cell at the given row and column. If it is not a formula
	 * cell, it returns its appropriate contents.
	 * 
	 * @param path
	 *            File path to excel file
	 * @param sheetNumber
	 *            Sheet number - 0 based
	 * @param row
	 *            The row - 0 based
	 * @param col
	 *            The column - 0 based
	 * @return Contents of the cell
	 */
	public static String getFormulaCellValue(String path, int sheetNumber, int row, int col) {

		String result = "";
		Workbook workbook = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(path);
			// creates the appropriate HSSFWorkbook / XSSFWorkbook from the given file
			workbook = WorkbookFactory.create(fis);
			FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			Sheet sheet = workbook.getSheetAt(sheetNumber);

			// return empty string for a non existant cell
			if (sheet.getRow(row) == null || sheet.getRow(row).getCell(col) == null) {
				return "";
			}

			if (sheet.getRow(row).getCell(col).getCellTypeEnum() == CellType.FORMULA) {

				CellReference cellReference = new CellReference(row, col);
				Row r = sheet.getRow(cellReference.getRow());
				Cell cell = r.getCell(cellReference.getCol());

				CellValue cellValue = evaluator.evaluate(cell);
				switch (cellValue.getCellTypeEnum()) {
				case BOOLEAN:
					result = String.valueOf(cellValue.getBooleanValue());
					break;
				case NUMERIC:
					result = String.valueOf(cellValue.getNumberValue());
					break;
				case STRING:
					result = cellValue.getStringValue();
					break;
				case BLANK:
					break;
				case ERROR:
					break;

				// CELL_TYPE_FORMULA will never happen
				case FORMULA:
					break;
				case _NONE:
					break;
				default:
					break;
				}
			}

			// not a formula cell
			else
				result = getCellValue(sheet, row + 1, col + 1);// row and cell are 1-based
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();

		}

		finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Will check each cell with values in the given row and verify
	 * the cell color is of the given color.
	 * 
	 * @param path
	 *            Path to the excel file
	 * @param row
	 *            Row in the excel file, 1-based
	 * @param color
	 *            The color to verify
	 * @return True if the row is of the given color, false otherwise
	 */
	public static boolean isRowAColor(String path, int row, String color) {
		row--;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(path));
			Workbook workbook = WorkbookFactory.create(fis);
			Sheet s = workbook.getSheetAt(0);
			Row r;
			if (s.getRow(row) != null) {
				r = s.getRow(row);
				int lastCol = getLastCellNumber(path, row);
				System.out.println("last col = " + lastCol);
				CellStyle cs;
				Color c;
				for (int col = 0; col < lastCol; col++) {
					cs = r.getCell(col).getCellStyle();
					c = cs.getFillForegroundColorColor();
					String hexString = "";
					if (workbook instanceof HSSFWorkbook) {
						hexString = Arrays.toString(((HSSFColor) c).getTriplet());
					}
					// XSSFWorkbook
					else {
						hexString = Arrays.toString(((XSSFColor) c).getRGB());
					}
					hexString = hexString.replace("[", "").replace("]", "");
					if (!color.contains(hexString)) {
						return false;
					}
				}

				return true;
			}

			else
				return false;

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 
	 * returns color as a string based on the type of workbook given
	 * 
	 * @param workbook
	 * @param color
	 * @return
	 */
	private static String getColorAsHexString(Workbook workbook, Color color) {
		String hexString = "";
		if (workbook instanceof HSSFWorkbook) {
			hexString = Arrays.toString(((HSSFColor) color).getTriplet());
		}
		// XSSFWorkbook
		else {
			hexString = Arrays.toString(((XSSFColor) color).getRGB());
		}
		hexString = hexString.replace("[", "").replace("]", "");
		return hexString;
	}

	/**
	 * Will check each cell with values in the given row and verify
	 * the cell color is of the given color.
	 * 
	 * @param path
	 *            Path to the excel file
	 * @param row
	 *            Row in the excel file, 1-based
	 * @return hex string of a row, empty if row is multiple colors
	 */
	public static String getRowColor(String path, int row) {
		row--;
		try (FileInputStream fis = new FileInputStream(new File(path))) {
			Workbook workbook = WorkbookFactory.create(fis);
			Sheet sheet = workbook.getSheetAt(0);
			Row sheetRow;
			if (sheet.getRow(row) != null) {
				sheetRow = sheet.getRow(row);
				int lastCol = getLastCellNumber(path, row);
				CellStyle cellStyle;
				String rowColor = null;
				for (int col = 0; col < lastCol; col++) {
					cellStyle = sheetRow.getCell(col).getCellStyle();
					String cellColor = getColorAsHexString(workbook, cellStyle.getFillForegroundColorColor());
					if (rowColor == null) {
						rowColor = cellColor;
					}
					if (!cellColor.equals(rowColor)) {
						return ""; // row is not one color
					}
				}
				return rowColor;
			}
			return "";
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the value of the cell for the row and column with the given column name.
	 * The column names are expected to be located in the first row.
	 * 
	 * @param path
	 *            path to the xls file
	 * @param row
	 *            row of the xls file
	 * @param colName
	 *            column name containing the desired cell value
	 * @return
	 */

	public static String getCellFromXLS(String path, int row, String colName) {
		row--;
		int headerRow = getColumnHeaderRowFromXLSSheet(getSheetFromXLSPath(path, 0));
		int lastCol = getNumberofNonEmptyCells(path, 0, headerRow);
		int col = 0;
		for (; col < lastCol; col++) {
			String cellName = getCellFromXLSFile(path, headerRow, col).trim();
			if (cellName.equals(colName)) {
				break;
			}
		}
		return getCellFromXLSFile(path, row, col);
	}

	public static String getCellFromXLSVariousHeaders(String path, int row, String colName) {
		row--;
		int headerRow = getChangingColumnHeaderRowFromXLSSheet(getSheetFromXLSPath(path, 0));
		int lastCol = getNumberofNonEmptyCells(path, 0, headerRow);
		int col = 0;
		for (; col < lastCol; col++) {
			String cellName = getCellFromXLSFile(path, headerRow, col).trim();
			if (cellName.equals(colName)) {
				break;
			}
		}
		return getCellFromXLSFile(path, row, col);
	}

	public static List<String> getSheetHeadersFromXLS(String path) {
		List<String> headers = new LinkedList<String>();
		String str = "";
		try (Workbook workbook = getWorkbookFromXLSPath(path)) {
			for (int count = 0; count < workbook.getNumberOfSheets(); count++) {
				Sheet s = workbook.getSheetAt(count);
				Row r = s.getRow(0);
				if (r != null) {
					int numCols = s.getRow(0).getLastCellNum() + 1;
					for (int j = 1; j < numCols; j++) {
						str += getCellValue(s, 1, j);
						if (j < (numCols - 1))
							str += ",  ";
					}
				}
				headers.add(str);
				str = "";
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return headers;
	}

	public static String getFirstSheetHeaderFromXLS(String path) {
		String header = "";

		Sheet s = getSheetFromXLSPath(path, 0);
		Row r = s.getRow(0);
		if (r != null) {
			int numCols = s.getRow(0).getLastCellNum() + 1;
			for (int j = 1; j < numCols; j++) {
				header += getCellValue(s, 1, j);
				if (j < (numCols - 1))
					header += ",  ";
			}
		}
		return header;
	}

	public static boolean areColumnsPresentInXLS(String path, List<String> columns) {
		List<String> headerFromXLS = Arrays.asList(getFirstSheetHeaderFromXLS(path).split(", "));
		for (String expectedHeader : columns) {
			if (headerFromXLS.contains(expectedHeader))
				return true;
		}
		return false;
	}

	public static double stringToDouble(String number) {
		Double oldDouble = Double.parseDouble(number);
		String sValue = String.format("%.2f", oldDouble);
		return Double.parseDouble(sValue);
	}

	public static List<String> getXLSSheetName(String path) {
		List<String> sheetName = new ArrayList<String>();
		try (Workbook workbook = getWorkbookFromXLSPath(path)) {
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				sheetName.add(workbook.getSheetName(i));
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;

		return sheetName;
	}

	public static String readXLSSheetAsString(String path, String sheetName) {

		String str = "";
		try (Workbook workbook = getWorkbookFromXLSPath(path)) {
			Sheet s = workbook.getSheet(sheetName);

			int numRows = s.getLastRowNum() + 2;
			for (int i = 1; i < numRows; i++) {
				Row r = s.getRow(i - 1);

				if (r != null) {
					int numCols = s.getRow(i - 1).getLastCellNum() + 1;

					for (int j = 1; j < numCols; j++) {
						str += getCellValue(s, i, j);
						if (j < (numCols - 1))
							str += ",  ";
					}
				}
				str += '\n';
			} // end outer for loop
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return str;

	}

	public static int getColumnHeaderRowFromXLSSheet(Sheet sheet) {
		int row = 0;
		String colorA = "";
		HSSFCellStyle style = (HSSFCellStyle) sheet.getRow(row).getCell(0).getCellStyle();
		HSSFColor color = style.getFillForegroundColorColor();
		colorA = color.getHexString();
		while (sheet.getRow(row).getCell(0).getStringCellValue().length() == 0 && !colorA.equals("3333:3333:9999")) {
			row++;
			while (sheet.getRow(row).getCell(0).getStringCellValue().length() != 0) {
				style = (HSSFCellStyle) sheet.getRow(row).getCell(0).getCellStyle();
				color = style.getFillForegroundColorColor();
				colorA = color.getHexString();
				if (colorA.equals("3333:3333:9999")) {
					return row;
				}
				row++;
			}
		}
		return row;
	}

	public static int getChangingColumnHeaderRowFromXLSSheet(Sheet sheet) {
		int row = 0;
		String colorA = "";
		HSSFCellStyle style = (HSSFCellStyle) sheet.getRow(row).getCell(0).getCellStyle();
		HSSFColor color = style.getFillForegroundColorColor();
		colorA = color.getHexString();
		while (!colorA.equals("3333:3333:9999")) {
			row++;
			while (sheet.getRow(row).getCell(0).getStringCellValue().length() != 0) {
				style = (HSSFCellStyle) sheet.getRow(row).getCell(0).getCellStyle();
				color = style.getFillForegroundColorColor();
				colorA = color.getHexString();
				if (colorA.equals("3333:3333:9999")) {
					return row;
				}
				row++;
			}
		}
		return row;
	}

	public static Sheet getSheetFromXLSPath(String path, int sheetNumber) {
		Sheet sheet = null;
		try (Workbook workbook = getWorkbookFromXLSPath(path)) {
			sheet = workbook.getSheetAt(sheetNumber);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sheet;
	}

	public static String getStringCellFromXLS(String path, int row, int col) {
		return getStringCellFromXLS(path, 0, row, col);
	}

	public static String getStringCellFromXLS(String path, int row, String colName) {
		row--;
		int lastCol = getNumberofNonEmptyCells(path, 0, 0);
		int headerRow = getColumnHeaderRowFromXLSSheet(getSheetFromXLSPath(path, 0));
		int col = 0;
		for (; col < lastCol; col++) {
			String cellName = getCellFromXLSFile(path, headerRow, col).trim();
			if (cellName.equals(colName)) {
				break;
			}
		}
		return getStringCellFromXLS(path, row, col);
	}

	public static String getStringCellFromXLS(String path, int sheet, int row, int col) {
		Sheet s = getSheetFromXLSPath(path, sheet);
		if (s.getRow(row) != null && s.getRow(row).getCell(col) != null) {
			DataFormatter dataFormatter = new DataFormatter();
			return dataFormatter.formatCellValue(s.getRow(row).getCell(col));
		} else {
			return "";
		}

	}

	private static Workbook getWorkbookFromXLSPath(String path) {
		// This workbook must be closed after using
		try (FileInputStream fis = new FileInputStream(path);) {
			// creates the appropriate HSSFWorkbook / XSSFWorkbook from the given file
			Workbook workbook = WorkbookFactory.create(fis);
			return workbook;

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();

		}
	}

	public static String getAllRowsCellDataFromXLS(String path, String colName) {
		StringBuilder builder = new StringBuilder();
		int rows = Integer.parseInt(getNumberOfRows(path));
		int lastCol = getNumberofNonEmptyCells(path, 0, 0);
		int headerRow = getColumnHeaderRowFromXLSSheet(getSheetFromXLSPath(path, 0));
		int col = 0;
		for (; col < lastCol; col++) {
			String cellName = getCellFromXLSFile(path, headerRow, col).trim();
			if (cellName.equals(colName)) {
				break;
			}
		}
		for (int i = 1; i <= rows; i++) {
			String data = getCellFromXLSFile(path, i, col);
			if (i == rows) {
				builder.append(data);
				break;
			}
			builder.append(data + ", ");
		}
		return builder.toString();
	}
}
