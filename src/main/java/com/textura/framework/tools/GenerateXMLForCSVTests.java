package com.textura.framework.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.testng.annotations.Test;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlSuite.FailurePolicy;
import org.testng.xml.XmlSuite.ParallelMode;
import org.testng.xml.XmlTest;
import com.google.common.util.concurrent.UncheckedExecutionException;

public class GenerateXMLForCSVTests {

	/**
	 * Generates testng.xml for the test case ids provided in "testCasesList.csv" file.
	 * 
	 * Input: Add TestCase Ids in "testCasesList.csv" file in "," separated format for which xml file needs to be generated.
	 * Ex:c9407093,c8957665,c8872365
	 * Output: testng.xml for the cases mentioned in the csv file.
	 * 
	 * 
	 * Path for input & output files:
	 * Input (testCasesList.csv): "C:\Automation\Textura\CPM\src\main\config\testCasesList.csv"
	 * Output (testng.xml): C:\Automation\Textura\CPM\src\main\config\testngFileCsvTests-failed.xml
	 * 
	 * Do's: Provide only Parallel execution cases that doesn't have any following group associated with @Test.
	 * 
	 * Don't: Doesn't provide xml for groups "ExternalExec.All,DateTimeChange,ProductionIssues,ObsoleteTestCases"
	 * 
	 */

	@Test
	public static void generateXmlForCsvTests() throws Exception {
		String ids = readFileAsString("C:\\Automation\\Textura\\CPM\\src\\main\\config\\testCasesList.csv");
		String[] listOfCases = ids.split(",");
		System.out.println("Test Case Ids :" + Arrays.toString(listOfCases));
		System.out.println("Total Number of cases:" + listOfCases.length);
		HashMap<String, List<String>> map = getClassNamesForGivenTestCases(listOfCases);
		createTestNGXml(map, "parallelTests",listOfCases);
	}

	public static String[] readSuiteNames() {
		String[] allSuites = null;
		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("C:\\Automation\\Textura\\CPM\\src\\main\\config\\TestSuites.properties");

			// load a properties file
			prop.load(input);

			allSuites = prop.getProperty("TestSuites").split(",");

			for (int i = 0; i < allSuites.length; i++) {
				allSuites[i] = "com.textura.cpm.testsuites." + allSuites[i];
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return allSuites;
	}

	public static HashMap<String, List<String>> getClassNamesForGivenTestCases(String[] testCaseIds) throws ClassNotFoundException {
		String[] allClasses = readSuiteNames();
		HashMap<String, List<String>> listOfCasesBasedOnClassNames = new HashMap<String, List<String>>();

		List<String> testCasesList = null;
		// List<String> parallelTestcases = new ArrayList<String>();
		List<String> externalExecTestcases = new ArrayList<String>();
		List<String> productionIssuesTestcases = new ArrayList<String>();
		List<String> obsoleteTestcases = new ArrayList<String>();
		List<String> dateTimeChangeTestcases = new ArrayList<String>();
		List<String> notPresent = new ArrayList<String>();
		List<String> noTestAnnotaion = new ArrayList<String>();

		// System.out.println("Total Nuber of class names:" + allClasses.length);

		testCasesList = new ArrayList<String>();
		for (String testCaseId : testCaseIds) {

			ArrayList<String> classcount = new ArrayList<String>();
			boolean flag = false;
			for (String singleClassName : allClasses) {

				if (!classcount.contains(singleClassName)) {
					classcount.add(singleClassName);
				}

				Method[] allMethods1 = Class.forName(singleClassName).getMethods();

				String allMethods = Arrays.toString(Class.forName(singleClassName).getMethods());
				if (allMethods.contains(testCaseId.trim() + "()")) {
					flag = true;
					for (Method m : allMethods1) {
						if (m.getName().equals(testCaseId.trim())) {

							if (m.isAnnotationPresent(Test.class)) {
								Test testAnnotation = m.getAnnotation(Test.class);
								String[] groups = testAnnotation.groups();
								if (groups.length == 0) {
									testCasesList = new ArrayList<String>();
									testCasesList.add(testCaseId.trim());
									if (listOfCasesBasedOnClassNames.get(singleClassName) == null)
										listOfCasesBasedOnClassNames.put(singleClassName, testCasesList);
									else {
										List<String> tmpList = listOfCasesBasedOnClassNames.get(singleClassName);
										tmpList.add(testCaseId.trim());
										listOfCasesBasedOnClassNames.put(singleClassName, tmpList);
										break;
									}
								} else {

									for (String groupName : groups) {
										if (groupName.equals("ExternalExec.All")) {
											externalExecTestcases.add(testCaseId);
										}
										if (groupName.equals("ProductionIssues")) {
											productionIssuesTestcases.add(testCaseId);
										}
										if (groupName.equals("ObsoleteTestCases")) {
											obsoleteTestcases.add(testCaseId);
										}
										if (groupName.equals("DateTimeChange")) {
											dateTimeChangeTestcases.add(testCaseId);
										}
									}
								}

							} else {
								noTestAnnotaion.add(testCaseId);
							}
						}
					}

				}
			}

			if (!flag) {
				notPresent.add(testCaseId);
				// System.out.println(testCaseId + " is not present in any class. Please check the test case id.");
			}

		}

		if (obsoleteTestcases.size() > 0) {
			System.out.println("Below cases are excluded from getiing added into xml as these cases got Obsolete:");
			System.out.print("[");
			for (String caseId : obsoleteTestcases)
				System.out.print(caseId + ",");
			System.out.print("]");
			System.out.println();
		}

		if (noTestAnnotaion.size() > 0) {

			System.out.println("Below cases are excluded from getiing added into xml as these cases doesn't has Test Annotation:");
			System.out.print("[");
			for (String caseId : noTestAnnotaion) {
				System.out.print(caseId + ",");
				break;
			}
			System.out.print("]");
			System.out.println();
		}

		if (externalExecTestcases.size() > 0 || dateTimeChangeTestcases.size() > 0 || productionIssuesTestcases.size() > 0) {

			System.out.println("Below cases are excluded from getiing added into xml as these are not belongs to parallel execution:");
			System.out.print("[");
			for (String caseId : externalExecTestcases)
				System.out.print(caseId + ",");
			for (String caseId : dateTimeChangeTestcases)
				System.out.print(caseId + ",");
			for (String caseId : productionIssuesTestcases)
				System.out.print(caseId + ",");
			System.out.print("]");
			System.out.println();
		}
		if (notPresent.size() > 0) {
			System.out.println();
			System.out.println("Below cases have not been added in the XML due to one of the following reason.\n  1. Test case id is incorrect.  OR \n  2. test case id input format is incorrect. Test case ids just need to separated with comms ','. OR \n  3. These test cases are not present in any of the class listed in 'TestSuites.properties' .Please update the TestSuite file with the missing class names.");
			System.out.println();
			System.out.print("[");
			for (String caseId : notPresent)
				System.out.print(caseId + ",");
			System.out.print("]");
		}

		return listOfCasesBasedOnClassNames;
	}

	public static void createTestNGXml(HashMap<String, List<String>> suiteWithCasesMap, String xmlName,String[] inputTestCaseIds) throws Exception {

		if (suiteWithCasesMap.size() > 0) {
			int count = 0;
			XmlSuite suite = new XmlSuite();
			suite.setThreadCount(30);
			suite.setConfigFailurePolicy(FailurePolicy.CONTINUE);
			suite.setGuiceStage("DEVELOPMENT");
			suite.setVerbose(0);
			suite.setName("Failed suite [Failed suite [Automation Test Suite Custom]]");

			if (xmlName.equals("parallelTests")) {
				suite.setParallel(ParallelMode.METHODS);
			}

			suite.setJunit(false);
			suite.setSkipFailedInvocationCounts(false);
			suite.setDataProviderThreadCount(10);
			suite.setGroupByInstances(false);
			suite.setPreserveOrder(true);
			suite.setAllowReturnValues(false);

			XmlTest test = new XmlTest(suite);
			test.setName("Automation Test: Custom Test Suite");
			// .test.setPreserveOrder("true");
			if (xmlName.equals("parallelTests")) {
				test.setParallel(ParallelMode.METHODS);
			}

			test.setJunit(false);
			test.setSkipFailedInvocationCounts(false);
			test.setPreserveOrder(true);
			test.setGroupByInstances(false);
			test.setAllowReturnValues(false);

			XmlClass testClass = null;

			ArrayList<XmlClass> classes = new ArrayList<XmlClass>();
			ArrayList<XmlInclude> methodsToRun = null;// new ArrayList<XmlInclude>();

			Set<String> suitesSet2 = suiteWithCasesMap.keySet();
			int j = 0;

			for (String suiteNam : suitesSet2) {
				j++;
				testClass = new XmlClass();
				methodsToRun = new ArrayList<XmlInclude>();
				testClass.setName(suiteNam);

				List<String> allCasessInaSuite = suiteWithCasesMap.get(suiteNam);
				for (String s : allCasessInaSuite) {
					methodsToRun.add(new XmlInclude(s));
					count++;
				}
				testClass.setIncludedMethods(methodsToRun);
				classes.add(testClass);
			}

			test.setXmlClasses(classes);

			System.out.println();
			System.out.println();
			if (count<inputTestCaseIds.length) 
				System.out.println(" Total number of cases added into XML after excluding above cases:" + count);
			else
				System.out.println(" Total number of cases added into XML :" + count);
			System.out.println();
			System.out.println();
			System.out.println("======================================");
			System.out.println(suite.toXml());
			FileWriter writer = new FileWriter("C:\\Automation\\Textura\\CPM\\src\\main\\config\\testng-failed.xml");
			writer.write(suite.toXml());
			writer.close();
		}
	}

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

	
}