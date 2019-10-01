package com.textura.framework.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.tools.ExtractText;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.textura.framework.environment.Project;
import com.textura.framework.objects.main.Page;

public class JavaHelpers {

	/**
	 * Returns the name of current method
	 */
	public static String getCurrentMethodName() {
		StackTraceElement stackTraceElements[] = (new Throwable()).getStackTrace();
		String fullString = stackTraceElements[1].toString();
		int stringEnd = fullString.indexOf('(');
		String fullName = fullString.substring(0, stringEnd);
		int start = fullName.lastIndexOf('.') + 1;
		String methodName = fullName.substring(start);

		return methodName;
	}

	/**
	 * Searches the stack for a method that has @Test annotation.
	 * 
	 * @return
	 */
	public static String getTestCaseMethodName() {
		StackTraceElement stackTraceElements[] = (new Throwable()).getStackTrace();
		for (StackTraceElement e : stackTraceElements) {
			try {
				if (e.getClassName().contains("com.textura") && !e.getMethodName().contains("<init>")) {
					Class<?> cc = Class.forName(e.getClassName());
					Method m = cc.getMethod(e.getMethodName());
					if (m.getAnnotation(Test.class) != null) {
						return m.getName();
					}
				}
			} catch (Exception f) {
			}
		}
		return "null";
	}

	/**
	 * Returns the name of current test case class name,
	 * used for debugging.
	 */
	public static String getTestCaseClassName() {
		StackTraceElement stackTraceElements[] = (new Throwable()).getStackTrace();
		for (StackTraceElement e : stackTraceElements) {
			String s = e.toString();
			if (s.contains("com.textura.cpm.testsuites")) {
				int endMethod = s.lastIndexOf('(');
				int beginMethod = s.lastIndexOf('c', endMethod);
				if (beginMethod < 0 || beginMethod >= endMethod) {
					continue;
				}
				String caseName = s.substring(beginMethod, endMethod);
				try {
					Integer.parseInt(caseName.substring(1));
					return s;
				} catch (Exception a) {
					continue;
				}
			}
		}
		return "";
	}

	/**
	 * Returns the line number in the test case class file currently executing.
	 */
	public static String getTestCaseMethodLineNumber() {
		String caseLineString = getTestCaseClassName();
		List<String> matches = JavaHelpers.getRegexMatches(".+\\:(\\d+)\\)", caseLineString);
		return matches.isEmpty() ? "" : matches.get(0);

	}

	/**
	 * Determines if a method name is in the call stack
	 * 
	 * @return
	 */
	public static boolean isMethodInStack(String methodName) {
		StackTraceElement stackTraceElements[] = (new Throwable()).getStackTrace();
		for (int i = 0; i < stackTraceElements.length; i++) {
			if (stackTraceElements[i].toString().contains(methodName)) {
				return true;
			}
		}
		return false;
	}

	public static String getStackTrace() {
		StackTraceElement stackTraceElements[] = (new Throwable()).getStackTrace();
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < stackTraceElements.length; i++) {
			result.append(stackTraceElements[i].toString() + "\n");
		}
		return result.toString();
	}

	public static String getStackTrace(Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

	public static boolean isAnyMethodInStack(Set<String> methods) {
		for (String s : methods) {
			if (isMethodInStack(s)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a String with contents of the specified file
	 */
	public static String readFileAsString(String filePath) {
		byte[] buffer = new byte[(int) new File(filePath).length()];
		BufferedInputStream f = null;
		try {
			File file = new File(filePath);
			for (int attempt = 0; attempt < 3; attempt++) {
				if (file.exists()) {
					break;
				}
				Thread.sleep(2);
			}

			if (!file.exists()) {
				System.err.println("\nfile not found: " + filePath);
				throw new FileNotFoundException("Could not find file: " + filePath);
			}

			f = new BufferedInputStream(new FileInputStream(filePath));
			f.read(buffer);
		} catch (IOException e) {
			throw new UncheckedExecutionException(e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (f != null)
				try {
					f.close();
				} catch (IOException ignored) {
				}
		}
		return new String(buffer);
	}

	/**
	 * Returns a String with contents of the specified file with the specified encoding
	 */
	public static String readFileAsString(String filePath, String encoding) {
		byte[] buffer = new byte[(int) new File(filePath).length()];
		BufferedInputStream f = null;
		try {
			File file = new File(filePath);
			for (int attempt = 0; attempt < 3; attempt++) {
				if (file.exists()) {
					break;
				}
				Thread.sleep(2);
			}

			if (!file.exists()) {
				System.err.println("\nfile not found: " + filePath);
				throw new FileNotFoundException("Could not find file: " + filePath);
			}

			f = new BufferedInputStream(new FileInputStream(filePath));
			f.read(buffer);
		} catch (IOException e) {
			throw new UncheckedExecutionException(e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			if (f != null)
				try {
					f.close();
				} catch (IOException ignored) {
				}
		}
		String contents = null;
		try {
			contents = new String(buffer, encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return contents;
	}

	/**
	 * Reads lines from file and adds them to a String List
	 */

	public static List<String> readFileAsList(String filePath) {

		BufferedReader readFile = null;
		String line = null;
		List<String> fileContents = new ArrayList<String>();
		try {
			readFile = new BufferedReader((new FileReader(filePath)));
			while ((line = readFile.readLine()) != null)
				fileContents.add(line);
			readFile.close();
			return fileContents;
		} catch (FileNotFoundException e) {
			System.out.println("Unable to find file: " + filePath);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Unable to read file: " + filePath);
			e.printStackTrace();
		}
		return fileContents;
	}

	/**
	 * Verifies if a given value under a given column is present in the csv file.
	 * 
	 */
	public static boolean isValuePresentInColumnCSV(String value, String column, String filePath) {
		Map<String, List<String>> csvMap = buildCsvMap(filePath);

		if (csvMap.containsKey(column))
			if (csvMap.get(column).contains(value))
				return true;
		return false;
	}

	/**
	 * Verifies if a given value under a given column is contained in the csv file.
	 * 
	 */
	public static boolean isValueContainedInColumnCSV(String value, String column, String filePath) {
		Map<String, List<String>> csvMap = buildCsvMap(filePath);

		if (csvMap.containsKey(column)) {
			for (String columnValue : csvMap.get(column)) {
				if (columnValue.contains(value)) {
					return true;
				}
			}
		}

		return false;
	}

	public static String getValueContainedInColumnCSV(String column, String filePath) {
		Map<String, List<String>> csvMap = buildCsvMap(filePath);
		String result = "";
		if (csvMap.containsKey(column)) {
			for (String columnValue : csvMap.get(column)) {
				result = columnValue;
				System.out.println("RESULT VALUE: " + result);
			}
		}
		return result;
	}

	/**
	 * Counts how many rows contain the given value for the given column in the csv file.
	 * 
	 * @return The number of times the given value appears under the desired column.
	 */
	public static int countRowsWithValueCSV(String column, String value, String filePath) {
		Map<String, List<String>> csvMap = buildCsvMap(filePath);

		if (csvMap.containsKey(column))
			if (csvMap.get(column).contains(value))
				return csvMap.get(column).size();

		return 0;
	}

	/**
	 * Builds and returns a LinkedMap which represents a csv file.
	 * This way is much easier to manipulate csv files and perform tasks
	 * such as searching for values inside it and verifying is a given
	 * column and/or value is present
	 * 
	 * @return A LinkedMap which represents a csv file.
	 */
	public static Map<String, List<String>> buildCsvMap(String filePath) {
		List<String> fileAsList = readFileAsList(filePath);
		Map<String, List<String>> csv = new LinkedHashMap<String, List<String>>();
		List<String> headers = Arrays.asList(fileAsList.get(0).split(",", -1));

		for (int i = 1; i < fileAsList.size(); i++) {
			List<String> words = Arrays.asList(fileAsList.get(i).split(",", -1));
			for (int j = 0; j < words.size(); j++) {
				if (csv.containsKey(headers.get(j))) {
					csv.get(headers.get(j)).add(words.get(j));
				} else {
					List<String> aux = new ArrayList<String>();
					aux.add(words.get(j));
					csv.put(headers.get(j), aux);
				}
			}
		}
		return csv;
	}

	/**
	 * Opens and reads properties file
	 * 
	 * @param path
	 *            path to properties file
	 * @return
	 * 		loaded properties file
	 */
	public static Properties readPropertiesFile(String path) {

		Properties propFile = new Properties();
		try {
			propFile.load(new FileInputStream(path));
		} catch (IOException e) {
			System.err.println("Error Reading Properties File");
			e.printStackTrace();
		}
		return propFile;
	}

	/**
	 * Saves properties file with comments
	 * 
	 * @param propFile
	 *            properties file to save
	 * @param path
	 *            path to output file
	 * @param comments
	 *            comments to include in properties file
	 */
	public static void savePropertiesFile(Properties propFile, String path, String comments) {
		try {
			propFile.store(new FileOutputStream(path), comments);
		} catch (IOException e) {
			System.err.println("Error Saving Properties File");
			e.printStackTrace();
		}
	}

	/**
	 * Copies a folder recursively or copies a file. Creates a destination
	 * folder if it does not exist.
	 * 
	 * @param src
	 *            source folder
	 * @param dest
	 *            destination folder
	 * @throws IOException
	 */
	public static void copyFolder(File src, File dest) throws IOException {

		if (src.isDirectory()) {

			// if directory not exists, create it
			if (!dest.exists()) {
				dest.mkdir();
				// System.out.println("Directory copied from " + src + " to " +
				// dest);
			}

			// list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				copyFolder(srcFile, destFile);
			}

		} else {
			// if file, then copy it
			// Use bytes stream to support all file types
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}

			in.close();
			out.close();
			// System.out.println("File copied from " + src + " to " + dest);
		}
	}

	public static void copyFile(File src, File dest) {
		try {
			if (!dest.exists()) {
				dest.createNewFile();
			}
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			// copy the file content in bytes
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Deletes a file or directory recursively
	 * 
	 * @param file
	 *            file to delete
	 * @throws IOException
	 */
	public static void delete(File file) {

		if (file.isDirectory()) {

			// directory is empty, then delete it
			if (file.list().length == 0) {

				file.delete();
				// System.out.println("Directory is deleted : " +
				// file.getAbsolutePath());

			} else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					delete(fileDelete);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
					// System.out.println("Directory is deleted : " +
					// file.getAbsolutePath());
				}
			}

		} else {
			// if file, then delete it
			file.delete();
			// System.out.println("File is deleted : " +
			// file.getAbsolutePath());
		}
	}

	/**
	 * Writes string into another given by a path. Overwrites the file. Does not
	 * require the file to already exist.
	 * 
	 * @param fileName
	 *            location to write content
	 * @param fileContent
	 *            content to write
	 * @throws Exception
	 */
	public static void writeFile(String fileName, String fileContent) {
		try {
			FileWriter fstream;
			fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(fileContent);
			out.close();
		} catch (IOException e) {
			System.err.println("Error Writing File");
			e.printStackTrace();
		}
	}

	public static void writeFile(String fileName, String fileContent, String encoding) {
		try {
			FileUtils.writeStringToFile(new File(fileName), fileContent, encoding);
		} catch (Exception e) {
			System.err.println("Error Writing File");
			e.printStackTrace();
		}
	}

	/**
	 * Appends a string onto a file
	 * 
	 * @param fileName
	 *            path to file to be appended to
	 * @param fileContent
	 *            content to append
	 * @throws Exception
	 */
	public static void appendToFile(String fileName, String fileContent) throws Exception {
		FileWriter fstream = new FileWriter(fileName, true);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(fileContent);
		out.close();
	}

	/**
	 * Locates recursively or not all files in a given directory.
	 * 
	 * @param root
	 *            the directory to begin
	 * @param recurse
	 *            true to search as deep as possible. false if only search
	 *            current directory.
	 * @return file array of files found.
	 */
	public static File[] findAllFiles(File root, boolean recurse) {
		ArrayList<File> files = new ArrayList<File>();

		findAllFilesRecurse(root, files, recurse);

		// convert ArrayList to array
		File[] result = new File[files.size()];
		files.toArray(result);
		return result;
	}

	/**
	 * Recursive part of findAllFiles
	 * 
	 * @param root
	 * @param files
	 */
	private static void findAllFilesRecurse(File root, ArrayList<File> files, boolean recurse) {
		File[] rootContents = root.listFiles();
		if (rootContents == null) {
			files.add(root);
		} else {
			for (File f : rootContents) {
				// if it is a file or do not go deeper
				if (f.isFile() || !recurse) {
					files.add(f);
				} else if (recurse) { // directory
					findAllFilesRecurse(f, files, true);
				}
			}
		}
	}

	/**
	 * Returns a String array containing names of all test classes in project's
	 * testsuites package. It does not detect subclasses inside a test class.
	 * 
	 * The method locates all files in the project directory recursively with
	 * the extension .java. It then removes the .java extension from each of the
	 * strings.
	 */
	public static String[] listFilesAsArray() {
		// Recursively find all .java files
		File path = new File(Project.pathWorkspace());
		FilenameFilter filter = new FilenameFilter() {

			public boolean accept(File directory, String fileName) {
				return fileName.endsWith(".java");
			}
		};
		Collection<File> files = listFiles(path, filter, true);

		ArrayList<File> t = new ArrayList<File>(files);
		for (int i = 0; i < t.size(); i++) {
			String s = t.get(i).getAbsolutePath();
			if (s.contains("XX") || s.contains("testingpackage") || s.contains("datageneration")) {
				t.remove(t.get(i));
				i--;
			}
		}
		files = t;

		// Convert the Collection into an array
		File[] allJavaFiles = new File[files.size()];
		files.toArray(allJavaFiles);

		String[] allTestClasses = new String[allJavaFiles.length];
		String temp = "";

		// convert file path to full package declaration for the class
		for (int i = 0; i < allJavaFiles.length; i++) {
			temp = allJavaFiles[i].toString();
			temp = temp.replace(".java", "").replace("\\", "."); // remove .java convert backslash
			if (temp.indexOf("com.textura") < 0) {
				allTestClasses[i] = "null";
			} else {
				temp = temp.substring(temp.indexOf("com.textura"));
				temp = temp.replace("com.", "");
				allTestClasses[i] = temp;
			}
		}
		return allTestClasses;
	}

	/**
	 * Returns Files in specified directory satisfying the Filter
	 * 
	 * @param directory
	 *            the directory in which to search for files
	 * @param filter
	 *            restriction on files returned
	 * @param recurse
	 *            true if search subdirectories, false if only search current
	 *            directory
	 * @return Files in directory
	 */

	private static Collection<File> listFiles(File directory, FilenameFilter filter, boolean recurse) {
		Vector<File> files = new Vector<File>();
		File[] entries = directory.listFiles();

		for (File entry : entries) {
			if (filter == null || filter.accept(directory, entry.getName()))
				files.add(entry);
			if (recurse && entry.isDirectory())
				files.addAll(listFiles(entry, filter, recurse));
		}
		return files;
	}

	public static InputStream getStreamFromURL(String linkURL) throws MalformedURLException, IOException {
		return new URL(linkURL).openStream();

	}

	public static boolean downloadFile(String linkURL, String filePath) {
		File f = new File(filePath);
		try {

			org.apache.commons.io.FileUtils.copyURLToFile(new URL(linkURL), f, 60000, 60000);
		} catch (Exception e) {
			throw new UncheckedExecutionException(JavaHelpers.getStackTrace(e), null);
		}
		return true;
	}

	public static void readPDFIntoTextFile(String pdf, String outputText) {
		String[] args = { pdf, outputText };
		try {
			ExtractText.main(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String readPDF(String pdfPath) {
		try {
			Thread.sleep(1000); // prevents overriding files when multiple files are created within same second
								 // this could be resolved also by modifying file name
			File pdf = new File(pdfPath);

			for (int attempt = 0; attempt < 4; attempt++) {
				if (pdf.exists()) {
					break;
				}
				Thread.sleep(2000);
			}

			if (!pdf.exists()) {
				System.err.println("readPDF: PDF file not found: " + pdfPath);
				return "null";
			}
			String txt = pdfPath.substring(0, pdfPath.lastIndexOf('.')) + ".txt";
			readPDFIntoTextFile(pdfPath, txt);
			String result = readFileAsString(txt, "utf-8");
			return result;
		} catch (Exception e) {
			throw new Error("Failure reading in readPDF:\n" + JavaHelpers.getStackTrace(e));
		}
	}

	public static boolean isPDFSigned(String filePath) {
		ArrayList<String> output = new ArrayList<String>();
		String line;
		String pdffonts = "";
		if (System.getProperty("os.name").contains("indows")) {
			pdffonts = Project.pathRepository("resources/xpdf/3.04/bin64/pdffonts" + Project.executableExtension());
		} else {
			pdffonts = Project.pathRepository("resources/xpdf/pdffonts");
			// linux bin path
		}
		try {
			Process proc = Runtime.getRuntime().exec(pdffonts + " " + filePath);
			BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			while ((line = input.readLine()) != null) {
				output.add(line);
			}
			input.close();
		} catch (Exception ioe) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ioe.printStackTrace();
		}
		// important part
		// signature text seems to have a specific font
		if (output.size() < 1 || output.get(0).length() < 1) {
			System.out.println("Error reading pdf: " + filePath + "\n" + getTestCaseMethodName());
			return false;
		}
		for (String s : output) {
			if (s.contains("Oblique")) {
				return true;
			}
		}
		return false;
	}

	public static String readPDFv2(String filePath) {
		try {
			Thread.sleep(1000); // prevents overriding files when multiple files are created within same second
								 // this could be resolved also by modifying file name

			Page p = new Page(null, null);

			for (int attempt = 0; attempt < 5; attempt++) {
				if (p.isFileExist(filePath)) {
					break;
				}
				Thread.sleep(2000);
			}

			if (!p.isFileExist(filePath)) {
				System.err.println("readPDFv2: PDF file not found: " + filePath);
				return "null";
			}
			String pdftotext = "";
			if (System.getProperty("os.name").contains("indows")) {
				pdftotext = Project.pathRepository("resources/xpdf/3.04/bin64/pdftotext" + Project.executableExtension());
			} else {
				pdftotext = Project.pathRepository("resources/xpdf/pdftotext");
				// linux bin path
			}
			String fileName = filePath.substring(0, filePath.lastIndexOf('.')) + ".txt";
			Process proc;

			for (int attempt = 1; attempt <= 5; attempt++) {
				Thread.sleep(1000);
				proc = Runtime.getRuntime().exec(pdftotext + " " + filePath);
				proc.waitFor();

				if (p.isFileExist(fileName))
					break;
				else
					System.out.println("Attempt #" + attempt + "Converted text file from pdf does not exist: " + fileName);
			}

			String pdfText = readFileAsString(fileName);
			return pdfText;
		} catch (Exception e) {
			throw new Error("Failure reading in readPDFv2:\n" + JavaHelpers.getStackTrace(e));
		}
	}

	public static void writeStringVariables(Object o, String filePath) {
		Field[] fields = o.getClass().getFields();
		try {
			new File(new File(filePath).getParent()).mkdirs();
			new File(filePath).createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Field f : fields) {
			try {
				if (f.get(o).getClass().equals(String.class)) {
					String cl = f.getDeclaringClass().getSimpleName();
					if (cl.startsWith("OrgData")) {
						cl = Character.toLowerCase(cl.charAt(0)) + cl.substring(1);
					}
					JavaHelpers.appendToFile(filePath, cl + "." + f.getName() + " = \"" + f.get(o) + "\";\n");
				} else {
					JavaHelpers.appendToFile(filePath, f.toString() + ": " + f.get(o) + "\n");
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Converts InputStream to String.
	 * 
	 * @return String
	 */
	public static String convertInputStreamToString(InputStream in) {
		StringBuilder sb = new StringBuilder();
		List<String> lines = null;
		try {
			lines = IOUtils.readLines(in);
		} catch (IOException e) {
			e.toString();
		}
		if (lines != null) {
			for (String line : lines) {
				sb.append(line + "\n");
			}
		}
		return sb.toString();
	}

	/**
	 * Sum any number of given strings and returns the total
	 * 
	 * @param values
	 *            Any amount of numbers in the String format
	 * @return A currency formatted String
	 */
	public static String sumStringValues(String... values) {
		Float total = new Float(0);
		for (String value : values) {
			total += formatString(value);
		}
		return curencyFormatterString(total);
	}

	/**
	 * Sum any number of given strings and returns the total
	 * 
	 * @param values
	 *            Any amount of numbers in the String format
	 * @return A currency formatted String
	 */
	public static String subtractStringValues(String... values) {
		Float total = formatString(values[0]);
		for (int i = 1; i < values.length; i++) {
			total -= formatString(values[i]);
		}
		return curencyFormatterString(total);
	}

	/**
	 * Returns a currency formatted String
	 * 
	 * @param value
	 *            A Float value which would be converted to a currency formatted String
	 * @return A currency formatted String
	 */
	private static String curencyFormatterString(Float value) {
		return NumberFormat.getCurrencyInstance().format(value).replace("$", "");
	}

	private static Float formatString(String value) {
		value = value.replaceAll(",", "");
		value = value.replace("$", "");
		value = value.trim();
		return new Float(value);
	}

	/**
	 * 
	 * @param pattern
	 *            a regular expression
	 * @param text
	 *            String that will be searched for pattern
	 * @return number of times pattern is present in text
	 */
	public static int getNumberOfPatternOccurences(String pattern, String text) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(text);
		int count = 0;
		while (m.find()) {
			count++;
		}
		return count;
	}

	public static String convertRGBToHex(String rgb) {
		String[] numbers = rgb.replace("rgba(", "").replace(")", "").split(",");
		int number1 = Integer.parseInt(numbers[0]);
		numbers[1] = numbers[1].trim();
		int number2 = Integer.parseInt(numbers[1]);
		numbers[2] = numbers[2].trim();
		int number3 = Integer.parseInt(numbers[2]);
		return String.format("#%02x%02x%02x", number1, number2, number3);

	}

	public static String getTodayDate() {
		return new SimpleDateFormat("dd-MMM-yyyy").format(new Date(System.currentTimeMillis()));
	}

	public static String getTodayDateUSFormat(boolean useSlashSeparator) {
		String date = null;
		if (useSlashSeparator)
			date = new SimpleDateFormat("MM/dd/yyyy").format(new Date(System.currentTimeMillis()));
		else
			date = new SimpleDateFormat("MM-dd-yyyy").format(new Date(System.currentTimeMillis()));
		return date;
	}

	/**
	 * Returns a document representation of string xml given.
	 * 
	 * @param xml
	 * @return
	 */
	public static Document loadXMLFromString(String xml) {

		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			StringReader sr = new StringReader(xml);
			InputSource is = new InputSource(sr);
			document = builder.parse(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return document;
	}

	/**
	 * Gets the text of a node whose location is determined by the xpath.
	 * 
	 * @param xml
	 * @param xpath
	 * @return
	 */
	public static String getNodeTextFromXml(String xml, String xpath) {
		Document doc = loadXMLFromString(xml);

		// Evaluate XPath against Document itself
		XPath xPath = XPathFactory.newInstance().newXPath();
		try {
			Node node = (Node) xPath.evaluate(xpath, doc.getDocumentElement(), XPathConstants.NODE);
			if (node != null) {
				String text = node.getTextContent();
				return text;
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return "FAILED TO FIND NODE";

	}

	public static String getNodeAttributeFromXml(String xml, String xpath, String attribute) {
		Document doc = loadXMLFromString(xml);

		// Evaluate XPath against Document itself
		XPath xPath = XPathFactory.newInstance().newXPath();
		try {
			Node node = (Node) xPath.evaluate(xpath, doc.getDocumentElement(), XPathConstants.NODE);
			return node.getAttributes().getNamedItem(attribute).getNodeValue();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return "FAILED TO FIND NODE";

	}

	public static void waitForFile(String filePath) {
		File file = new File(filePath);
		for (int x = 0; x < 10; x++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (file.exists()) {
				return;
			}
		}
		throw new RuntimeException("File did not download and is missing from file path: " + filePath);
	}

	/**
	 * 
	 * @param filePath
	 *            path of file to be edited
	 * @param tag
	 *            string that fill be replaced in file
	 * @param value
	 *            value that will replace in file
	 * @return filepath of modified file
	 */
	public static String replaceFieldInDataFile(String filePath, String tag, String value) {
		String modFilePath = filePath.substring(filePath.lastIndexOf("\\") + 1);

		String contents = readFileAsString(filePath);
		contents = contents.replace(tag, value);

		modFilePath = Project.downloads(modFilePath);
		writeFile(modFilePath, contents);

		return modFilePath;
	}

	public static List<String> getFilesPresentInZipFolder(String zipFolderPath) {
		File file = new File(zipFolderPath);
		ZipFile zipFile = null;
		List<String> fileResults = new ArrayList<String>();
		try {
			zipFile = new ZipFile(file);

			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				fileResults.add(entries.nextElement().getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return fileResults;
	}

	/**
	 * Checks if a file is present in a zip folder.
	 * 
	 * @param zipFolderPath
	 *            Full filepath of zip folder
	 * @param relativeFilePath
	 *            Relative filepath of file to be read within zip folder
	 * @return True if file is present, false otherwise
	 */
	public static boolean isFilePresentInZipFolder(String zipFolderPath, String relativeFilePath) {
		File file = new File(zipFolderPath);
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(file);

			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				String entryName = entries.nextElement().getName();
				if (entryName.equals(relativeFilePath))
					return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return false;
	}

	/**
	 * Reads file from zip file
	 * 
	 * @param zipFolderPath
	 *            Full filepath of zip folder
	 * @param relativeFilePath
	 *            Relative filepath of file to be read within zip folder
	 * @return File contents or empty string if not found.
	 */
	public static String readFileInZipFolder(String zipFolderPath, String relativeFilePath) {
		File file = new File(zipFolderPath);
		ZipFile zipFile = null;
		String text = "";
		InputStream in = null;
		OutputStream out = null;
		try {
			zipFile = new ZipFile(file);

			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				String entryName = entry.getName();
				if (entryName.equals(relativeFilePath)) {
					in = new BufferedInputStream(zipFile.getInputStream(entry));
					out = new ByteArrayOutputStream();

					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}

					text = out.toString();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return text;

	}

	public static String unzipFileInZipFolder(String zipFolderPath, String relativeFilePath, String fileName) {
		File file = new File(zipFolderPath);
		ZipFile zipFile = null;
		String destFilePath = "";
		InputStream in = null;
		OutputStream out = null;
		try {
			zipFile = new ZipFile(file);

			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				String entryName = entry.getName();
				if (entryName.equals(relativeFilePath)) {
					destFilePath = Project.downloads(JavaHelpers.getTestCaseMethodName() + "-" + DateHelpers.getTimeStamp() + fileName);
					File destFile = new File(destFilePath);
					destFile.createNewFile();
					in = new BufferedInputStream(zipFile.getInputStream(entry));
					out = new BufferedOutputStream(new FileOutputStream(destFilePath));

					byte[] buf = new byte[1024];
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}

				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return destFilePath;

	}

	public static double roundDouble(double value, int decimalPlaces, RoundingMode roundingMode) {

		BigDecimal bigDecimal = new BigDecimal("" + value);
		bigDecimal = bigDecimal.setScale(decimalPlaces, roundingMode);
		return bigDecimal.doubleValue();
	}

	public static double roundDouble(double value, int decimalPlaces) {
		return roundDouble(value, decimalPlaces, RoundingMode.HALF_UP);
	}

	public static double roundDouble(String value, int decimalPlaces) {
		return roundDouble(Double.parseDouble(value), decimalPlaces, RoundingMode.HALF_UP);
	}

	public static double roundDouble(String value, int decimalPlaces, RoundingMode roundingMode) {
		return roundDouble(Double.parseDouble(value), decimalPlaces, roundingMode);
	}

	public static void printPDFBookmarks(String filename) {
		try {
			PDDocument doc = PDDocument.load(new File(filename));

			PDDocumentOutline root = doc.getDocumentCatalog().getDocumentOutline();

			PDOutlineItem item = root.getFirstChild();
			while (item != null) {
				System.out.println("Item:" + item.getTitle());
				PDOutlineItem child = item.getFirstChild();
				while (child != null) {
					System.out.println(" Child:" + child.getTitle());
					child = child.getNextSibling();
				}
				item = item.getNextSibling();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String getPDFBookmarks(String filepath) {
		StringBuilder sb = new StringBuilder();

		try {
			PDDocument doc = PDDocument.load(new File(filepath));
			PDDocumentOutline root = doc.getDocumentCatalog().getDocumentOutline();

			PDOutlineItem item = root.getFirstChild();
			while (item != null) {
				sb.append(item.getTitle() + ", ");
				PDOutlineItem child = item.getFirstChild();
				while (child != null) {
					sb.append(child.getTitle() + ", ");
					child = child.getNextSibling();
				}

				item = item.getNextSibling();
			}
			doc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String list = sb.toString();
		int lastCommaIdx = list.lastIndexOf(",");
		return list.substring(0, lastCommaIdx);

	}

	public static String getPdfBookmarkDestinationPageContent(String filepath, String bookmarkName) {
		String content = "Bookmark not found";
		PDDocument doc = null;
		try {
			doc = PDDocument.load(new File(filepath));
			PDDocumentOutline root = doc.getDocumentCatalog().getDocumentOutline();

			PDOutlineItem item = root.getFirstChild();
			while (item != null) {
				PDOutlineItem child = item.getFirstChild();
				if (item.getTitle().equals(bookmarkName)) {
					InputStream input = item.findDestinationPage(doc).getContents();
					StringWriter writer = new StringWriter();
					IOUtils.copy(input, writer, "UTF-8");
					return writer.toString();
				}

				while (child != null) {
					if (child.getTitle().equals(bookmarkName)) {
						InputStream input = child.findDestinationPage(doc).getContents();
						StringWriter writer = new StringWriter();
						IOUtils.copy(input, writer, "UTF-8");
						return writer.toString();
					}
					child = child.getNextSibling();
				}
				item = item.getNextSibling();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (doc != null)
				try {
					doc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		return content;

	}

	/**
	 * Return the number of files found in a given folder
	 * 
	 * @param zipFolderPath
	 *            Full filepath of zip folder
	 * @param relativeFolderPath
	 *            Relative filepath of folder
	 * @return number of files found in the folder.
	 */
	public static int getNumOfFilesInZipFileFolder(String zipFolderPath, String relativeFolderPath) {
		File file = new File(zipFolderPath);
		ZipFile zipFile = null;
		int count = 0;
		try {
			zipFile = new ZipFile(file);

			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				String entryName = entry.getName();
				if (entryName.contains(relativeFolderPath)) {
					count++;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		return count;

	}

	public static List<String> getRegexMatches(String regex, String content) {
		List<String> matches = new ArrayList<String>();

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(content);

		while (m.find()) {
			matches.add(m.group(1));
		}

		return matches;
	}

	public static String arrayToCsvString(String[] array) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			sb.append(array[i].trim() + ",");
		}
		String result = sb.toString();
		return result.substring(0, result.length() - 1);
	}

	public static void printFormattedMessage(String message) {
		String caseName = JavaHelpers.getTestCaseMethodName();
		String timestamp = DateHelpers.getCurrentDateAndTime();
		System.out.println(timestamp + " " + caseName + ": " + message);
	}

	/**
	 * Use this method for comparing files that have a header and a footer, and lines in between in which the order doesnt matter
	 * 
	 * @param header
	 * @param footer
	 * @param actual
	 * @param expected
	 * @param allowExtraRecords
	 *            - if false, it will fail if the number of records in actual and expected are not equal
	 * @param wildcardColumns
	 *            list of column values to be wildcarded. This does not wildcard header or footer rows
	 * @return
	 */
	public static boolean reportCompare(String header, String footer, String actual, String expected, boolean allowExtraRecords,
			int... wildcardColumns) {

		boolean failed = false;
		actual = actual.replaceAll("\r\n", "\n").trim();
		expected = expected.replaceAll("\r\n", "\n").trim();

		// check header
		if (!header.trim().isEmpty() && !actual.contains(header)) {
			printFormattedMessage("Report header did not match! Actual header: '" + actual + "'\nExpected header: '" + header + "'");
			failed = true;
		}

		if (!header.isEmpty()) {
			actual = actual.replace(header + "\n", "");
			expected = expected.replace(header + "\n", "");
		}

		// check footer
		if (!footer.trim().isEmpty() && !actual.contains(header)) {
			printFormattedMessage("Report footer did not match! Actual footer: '" + actual + "'\nExpected footer: '" + expected + "'");
			failed = true;
		}

		if (!footer.isEmpty()) {
			actual = actual.replace(footer, "");
			expected = expected.replace(footer, "");
		}

		List<String> actualRows = new ArrayList<String>(Arrays.asList(actual.split("\n", -1)));
		List<String> expectedRows = new ArrayList<String>(Arrays.asList(expected.split("\n", -1)));
		actualRows = wildcardFields(actualRows, wildcardColumns);
		expectedRows = wildcardFields(expectedRows, wildcardColumns);

		for (String expectedRow : expectedRows) {
			if (!actualRows.contains(expectedRow)) {
				printFormattedMessage("The following expected row is missing: '" + expectedRow + "'");
				failed = true;
			}
			actualRows.remove(expectedRow);
		}

		if (!allowExtraRecords && actualRows.size() != 0) {
			printFormattedMessage("There are extra rows in the actual value!");
			failed = true;
		}

		if (failed == true) {
			printFormattedMessage("Actual Rows:");
			for (String s : actualRows) {
				System.out.println(s);
			}
			printFormattedMessage("Expected Rows:");
			for (String s : expectedRows) {
				System.out.println(s);
			}
		}

		return failed == false;
	}

	public static List<String> wildcardFields(List<String> rows, int... wildcardColumns) {
		List<String> result = rows;
		for (int i = 0; i < result.size(); i++) {
			String[] fields = result.get(i).split(",", -1);
			for (int column : wildcardColumns) {
				if (fields.length >= column) {
					fields[column] = "*";
				}
			}
			result.set(i, arrayToCsvString(fields));
		}
		return result;
	}

	public static boolean wildCardMatch(String text, String pattern) {
		return wildCardMatch(text, pattern, true);
	}

	// Matches strings ignoring '*' wildcards
	public static boolean wildCardMatchNoPrint(String text, String pattern) {
		return wildCardMatch(text, pattern, false);
	}

	// Matches strings ignoring '*' wildcards
	private static boolean wildCardMatch(String text, String pattern, boolean printDiff) {
		if (text.length() < 1 && pattern.length() < 1) {
			return true;
		}
		if (text.length() < 1 || pattern.length() < 1) {
			return false;
		}

		// match the newline endings for both string values
		if (text.contains("\r")) {
			if (!pattern.contains("\r")) {
				pattern = pattern.replace("\n", "\r\n");
			}
		} else {
			if (pattern.contains("\r")) {
				pattern = pattern.replace("\r\n", "\n");
			}
		}

		// Create the cards by splitting using a RegEx. If more speed
		// is desired, a simpler character based splitting can be done.
		String[] cards = pattern.split("\\*");

		// Iterate over the cards.
		for (String card : cards) {
			int idx = text.indexOf(card);

			// Card not detected in the text.
			if (idx == -1) {
				if (printDiff) {
					Page.printFormattedMessage("\ncard: " + card + "\nDiff: \n" + StringUtils.difference(pattern, text));
				}
				return false;
			}

			// Move ahead, towards the right of the text.
			text = text.substring(idx + card.length());
		}

		return true;
	}

	public static boolean isFilePresentInZipFolderPartialFileName(String zipFolderPath, String relativeFilePath) {
		File file = new File(zipFolderPath);
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(file);

			Enumeration<? extends ZipEntry> entries = zipFile.entries();

			while (entries.hasMoreElements()) {
				String entryName = entries.nextElement().getName();
				if (entryName.startsWith(relativeFilePath))
					return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return false;
	}

	public static String getFileInFolder(String path, String fileType){
		File[] listFiles = new File(path).listFiles();
		String fileName = "";
		for(int i=0;i < listFiles.length;i++){
			if(listFiles[i].isFile()){
				fileName = listFiles[i].getName();
				if(fileName.endsWith("."+fileType));	
			}
		}
		System.out.println("PATH: "+path+fileName);
		
		return path+fileName;
		
	}
}