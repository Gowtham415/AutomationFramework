package com.textura.framework.tools;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.testng.annotations.Test;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlGroups;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlRun;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlSuite.FailurePolicy;
import org.testng.xml.XmlSuite.ParallelMode;
import org.testng.xml.XmlTest;
import com.textura.framework.annotations.Author;
import com.textura.framework.objects.main.Page;

/**
 * @author gvalaval
 *         Purpose:To automate the process of writing test cases into XML followed by Jenkins job picking up XML and trigger a run.
 */
public class AuthorXMLBuilder {

	public static void getTestcasesAndWriteIntoXml(String[] author) {
		Map<String, List<String>> suiteWithParallelTests = new HashMap<String, List<String>>();
		Map<String, List<String>> suiteWithExternalExecTests = new HashMap<String, List<String>>();
		Map<String, List<String>> suiteWithProductionIssuesTests = new HashMap<String, List<String>>();
		Map<String, List<String>> suiteWithObsoleteTests = new HashMap<String, List<String>>();
		Map<String, List<String>> suiteWithDateTimeChangeTests = new HashMap<String, List<String>>();
		String[] suites = null;
		try {
			suites = getAllSuites();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			getTestcases(author, suites, suiteWithParallelTests, suiteWithExternalExecTests, suiteWithProductionIssuesTests,
					suiteWithObsoleteTests,
					suiteWithDateTimeChangeTests);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			generateXml(suiteWithParallelTests, "parallelTests");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			generateXml(suiteWithDateTimeChangeTests, "dateTimeChangeTests");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			generateXml(suiteWithExternalExecTests, "externalExecTests");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			generateXml(suiteWithProductionIssuesTests, "productionIssuesTests");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			generateXml(suiteWithObsoleteTests, "obsoleteTests");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// To get all suite names from a properties file
	public static String[] getAllSuites() throws IOException {
		FileReader reader = new FileReader("C:\\Automation\\Textura\\CPM\\src\\main\\config\\TestSuites.properties");
		Properties p = new Properties();
		p.load(reader);
		String allClasses = p.getProperty("TestSuites");
		String[] classesArray = allClasses.split(",");
		return classesArray;
	}

	// To get all the test cases of mentioned authors
	public static void getTestcases(String[] selectedAuthor,String[] classes, Map<String, List<String>> suiteWithParallelTests,
			Map<String, List<String>> suiteWithExternalExecTests, Map<String, List<String>> suiteWithProductionIssuesTests,
			Map<String, List<String>> suiteWithObsoleteTests, Map<String, List<String>> suiteWithDateTimeChangeTests)
					throws ClassNotFoundException {
		Method[] methods = null;
		for (int j = 0; j < classes.length; j++) {
			Class eachClass = Class.forName("com.textura.cpm.testsuites." + classes[j]);
			methods = eachClass.getMethods();
			List<String> parallelTestcases = new ArrayList<String>();
			List<String> externalExecTestcases = new ArrayList<String>();
			List<String> productionIssuesTestcases = new ArrayList<String>();
			List<String> obsoleteTestcases = new ArrayList<String>();
			List<String> dateTimeChangeTestcases = new ArrayList<String>();

			for (Method m : methods) {

				if (m.isAnnotationPresent(Author.class)) {
					if (m.isAnnotationPresent(Test.class)) {
						Test testAnnotation = m.getAnnotation(Test.class);
						Author author = m.getAnnotation(Author.class);

						for (String auth : selectedAuthor) {
							if (author.name().equalsIgnoreCase(auth)) {
							String[] groups = testAnnotation.groups();
							if(groups.length==0){

								parallelTestcases.add(m.getName());
								suiteWithParallelTests.put(classes[j], parallelTestcases);
							} else {
								for (String groupName : groups) {
									if (groupName.equals("ExternalExec.All")){
										externalExecTestcases.add(m.getName());
										suiteWithExternalExecTests.put(classes[j], externalExecTestcases);
									}
									if (groupName.equals("ProductionIssues")){
										productionIssuesTestcases.add(m.getName());
										suiteWithProductionIssuesTests.put(classes[j], productionIssuesTestcases);
									}
									if (groupName.equals("ObsoleteTestCases")){
										obsoleteTestcases.add(m.getName());
										suiteWithObsoleteTests.put(classes[j], obsoleteTestcases);
									}
									if (groupName.equals("DateTimeChange")){
										dateTimeChangeTestcases.add(m.getName());
										suiteWithDateTimeChangeTests.put(classes[j], dateTimeChangeTestcases);
									}
								}
							}
						}
					}
					}
				}
			}
		}

		// Suite wise Test cases count and test case ID's
		Page.printFormattedMessage("Parallel Tests====================================");
		int parallelTestsCount = getTestcasesData(suiteWithParallelTests);

		Page.printFormattedMessage("External Exec Tests===============================");
		int externalExecTestsCount = getTestcasesData(suiteWithExternalExecTests);

		Page.printFormattedMessage("Production Issues ================================");
		int productionIssuesTestsCount = getTestcasesData(suiteWithProductionIssuesTests);

		Page.printFormattedMessage("Obsolete Tests====================================");
		int obsoleteTestsCount = getTestcasesData(suiteWithObsoleteTests);

		Page.printFormattedMessage("Date Time Change Tests============================");
		int dateTimeChangeTestsCount = getTestcasesData(suiteWithDateTimeChangeTests);


		// Overall Test cases count
		Page.printFormattedMessage("==================================================");
		int serialTestsCount = externalExecTestsCount + productionIssuesTestsCount + obsoleteTestsCount + dateTimeChangeTestsCount;
		int totalAutomatedCount = parallelTestsCount + serialTestsCount;
		Page.printFormattedMessage("Total Automated Tests Count :" + totalAutomatedCount);
		Page.printFormattedMessage("Parallel Tests Count :" + parallelTestsCount);
		Page.printFormattedMessage("Serial Tests Count :" + serialTestsCount);
		Page.printFormattedMessage("ExternalExec Tests Count :" + externalExecTestsCount);
		Page.printFormattedMessage("ProductionIssues Tests Count :" + productionIssuesTestsCount);
		Page.printFormattedMessage("Obsolete Tests Count :" + obsoleteTestsCount);
		Page.printFormattedMessage("DateTimeChange Tests Count :" + dateTimeChangeTestsCount);
		Page.printFormattedMessage("==================================================");
	}

	// Provides Suite names,test case ID's,test cases count
	public static int getTestcasesData(Map<String, List<String>> suiteTests) {
		int count = 0;
		Set<String> allkeys = suiteTests.keySet();
		for (String keyeach : allkeys) {
			Page.printFormattedMessage("Suite name:" + keyeach);
			Page.printFormattedMessage("Test cases count:" + suiteTests.get(keyeach).size());
			String value = "";
			for (String val : suiteTests.get(keyeach)) {
				value = value + val + ",";
				count++;
			}
			Page.printFormattedMessage("Test cases:" + value);
		}
		Page.printFormattedMessage("Total count:" + count);
		return count;
	}

	// To generate and write data into XML
	public static void generateXml(Map<String, List<String>> suiteTests, String xmlName) throws IOException {


		XmlSuite suite = new XmlSuite();
		suite.setThreadCount(25);
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
		suite.setAllowReturnValues(false);

		XmlTest test = new XmlTest(suite);
		test.setName("Automation Test: Custom Test Suite(failed)(failed)");

		if (xmlName.equals("parallelTests")) {
			test.setParallel(ParallelMode.METHODS);
		}

		test.setJunit(false);
		test.setSkipFailedInvocationCounts(false);
		test.setGroupByInstances(false);
		test.setAllowReturnValues(false);

		String[] groupNames = { "ExternalExec.All", "ProductionIssues", "ObsoleteTestCases", "DateTimeChange" };
		XmlGroups xmlGroups = new XmlGroups();
		XmlRun xmlRun = new XmlRun();
		if (xmlName.equals("parallelTests")) {
			for (String group : groupNames) {
				xmlRun.onExclude(group);
			}
			xmlGroups.setRun(xmlRun);
			for (String group : groupNames) {
				test.addExcludedGroup(group);
			}
		}
		if (xmlName.equals("externalExecTests")) {
			xmlRun.onInclude("ExternalExec.All");
			xmlGroups.setRun(xmlRun);
			test.addIncludedGroup("ExternalExec.All");
			}
		if (xmlName.equals("productionIssuesTests")) {
			xmlRun.onInclude("ProductionIssues");
			xmlGroups.setRun(xmlRun);
			test.addIncludedGroup("ProductionIssues");
		}
		if (xmlName.equals("obsoleteTests")) {
			xmlRun.onInclude("ObsoleteTestCases");
			xmlGroups.setRun(xmlRun);
			test.addIncludedGroup("ObsoleteTestCases");
		}
		if (xmlName.equals("dateTimeChangeTests")) {
			xmlRun.onInclude("DateTimeChange");
			xmlGroups.setRun(xmlRun);
			test.addIncludedGroup("DateTimeChange");
		}

		ArrayList<XmlClass> classes = new ArrayList<XmlClass>();
		XmlClass testClass = null;
		List<XmlInclude> methodsToRun = null;

		// iterating over classes
		for (String key : suiteTests.keySet()) {
			testClass = new XmlClass();
			methodsToRun = new ArrayList<XmlInclude>();
			testClass.setName("com.textura.cpm.testsuites." + key);

			// iterating over test cases
			List<String> allmethods = suiteTests.get(key);
			for (String method : allmethods) {
				methodsToRun.add(new XmlInclude(method));
			}
			testClass.setIncludedMethods(methodsToRun);
			classes.add(testClass);
		}
		test.setXmlClasses(classes);

		FileWriter writer = new FileWriter("C:\\Automation\\Textura\\CPM\\" + xmlName + "BasedOnAuthortestng-failed.xml");
		writer.write(suite.toXml());
		writer.close();
	}

}
