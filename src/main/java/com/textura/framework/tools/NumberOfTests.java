package com.textura.framework.tools;

import com.textura.framework.environment.Project;
import com.textura.framework.utils.DateHelpers;
import com.textura.framework.utils.JavaHelpers;

public class NumberOfTests {

	/**
	 * Recalculates the number of @Test methods in /src/test/java and appends to
	 * file DateNumberofTests.txt. It excludes the folder /testingpackage. Then
	 * prints out the txt file.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		//File testpackage = new File(Project.path() + "src/test/java");

	//	File[] files = JavaHelpers.findAllFiles(testpackage, true);

		int numberOfTests = 0;
//		// for all files found
//		for (File f : files) {
//			String path = f.getPath();
//			// exclude testingpackage folder
//			if (path.endsWith(".java") && !path.contains("testingpackage")  && !path.contains("datageneration")) {
//				String contents = "";
//				contents = JavaHelpers.readFileAsString(path);
//
//				// add number of "@Test"
//				numberOfTests += StringUtils.countMatches(contents, "@Test");
//			}
//		}
		numberOfTests = AllTestMethodNames.getTestSuiteMethodNames().size();

		// format the text as mm/dd/yyyy,numberOfTests\n
		// example: 03/19/2012,63
		StringBuilder builder = new StringBuilder();
		builder.append(DateHelpers.getCurrentDate() + "," + numberOfTests + System.getProperty("line.separator"));

		// write to the file.
		try {
			JavaHelpers.appendToFile(Project.tools("DateNumberofTests.txt"), builder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(readDateNumberofTests());
	}

	/**
	 * Reads in DateNumberofTests.txt
	 * 
	 * @return
	 */
	public static String readDateNumberofTests() {
		String result = "";
		try {
			result = JavaHelpers.readFileAsString(Project.tools("DateNumberofTests.txt"));
		} catch (Exception e) {
			return "DataNumberofTests not found";
		}
		return result;
	}

}
