package com.textura.framework.loganalyzer;

import java.io.File;
import java.io.IOException;
import java.util.List;
import com.textura.framework.environment.Project;
import com.textura.framework.testng.TestngConfig;
import com.textura.framework.testng.TestngSuiteType;
import com.textura.framework.tools.AllTestMethodNames;

public class LogAnalyzerTool {

	static final String LOG_ANALYZER_OUTPUT_PATH = Project.pathAutomationArtifacts("LogAnalyzer/");
	static final String LOG_ANALYZER_LOG_NAME_1 = "LogAnalyzer-LogNew.log";
	static final String LOG_ANALYZER_LOG_NAME_2 = "LogAnalyzer-LogOld.log";
	static final String LOG_ANALYZER_LOG_NEW = LOG_ANALYZER_OUTPUT_PATH + LOG_ANALYZER_LOG_NAME_1;
	static final String LOG_ANALYZER_LOG_OLD = LOG_ANALYZER_OUTPUT_PATH + LOG_ANALYZER_LOG_NAME_2;
	static final String product = "cpm"; // change to your product name

	public static void main(String[] args) {
		List<String> productionIssues = AllTestMethodNames.getTestSuiteProductionIssuesMethods(product);
		List<String> obsolete = AllTestMethodNames.getTestSuiteObsoleteMethods(product);
		productionIssues.addAll(obsolete);
		List<String> productionIssuesAndObsoleteTestCases = productionIssues;

		List<String> logNew;
		List<String> logOld;
		LogAnalyzer tool;

		initializeToolPaths();
		logNew = LogAnalyzerReader.readJenkinsLogFromFile(LOG_ANALYZER_LOG_NEW);
		logOld = LogAnalyzerReader.readJenkinsLogFromFile(LOG_ANALYZER_LOG_OLD);
		tool = new LogAnalyzer(logNew);
		createFullReport(tool, logOld, productionIssuesAndObsoleteTestCases);

		// generate xml file with test cases which were started during an automation run but never finished
		// createXMLWithUnfinishedTestCases(tool, productionIssuesAndObsoleteTestCases);

		// generate xml file with test cases which were not started at all because for instance test run failed before finishing. (eg Java Heap)
		// Place log contents of complete full recent run into C:\Automation_artifacts\LogAnalyzer\LogAnalyzer-LogOld.log
		// createXMLWithTestCasesThatNeverStartedFromLog(tool, logOld, productionIssuesAndObsoleteTestCases);

		// Change to which group test cases belong to.
		// Options below will add additional group to the @Test(groups = {""}) the test cases belong to or if non exist then it will add a new one. This will make the change in the
		// source code.
		// In the test case file each test case is separated by a new line, 2 test case file would be ex:
		// c158643
		// c256486
		// Uncomment the option you want below. Place testCaseList.txt file containing test cases needing update in C:\Automation_artifacts\LogAnalyzer\ directory.
		// Update the product variable value above if its different than cpm

		// AllTestMethodNames.addTestCaseToObsoleteTestCasesGroup(LOG_ANALYZER_OUTPUT_PATH + "testCaseList.txt", product);
		// AllTestMethodNames.addTestCaseToProductionIssuesGroup(LOG_ANALYZER_OUTPUT_PATH + "testCaseList.txt", product);
		// AllTestMethodNames.addTestCaseToExternalExecGroup(LOG_ANALYZER_OUTPUT_PATH + "testCaseList.txt", product);
		// AllTestMethodNames.addTestCaseToDateTimeChangeGroup(LOG_ANALYZER_OUTPUT_PATH + "testCaseList.txt",product);
		// AllTestMethodNames.addTestCaseToCustomGroup(LOG_ANALYZER_OUTPUT_PATH + "testCaseList.txt", "EnterCustomGroupNameHere", product); // enter your custom group name as the 2nd parameter.

		// Remove to which group test cases belong to.
		// AllTestMethodNames.removeTestCaseFromObsoleteTestCasesGroup(LOG_ANALYZER_OUTPUT_PATH + "testCaseList.txt", product);
		// AllTestMethodNames.removeTestCaseFromProductionIssuesGroup(LOG_ANALYZER_OUTPUT_PATH + "testCaseList.txt", product);
		// AllTestMethodNames.removeTestCaseFromExternalExecGroup(LOG_ANALYZER_OUTPUT_PATH + "testCaseList.txt", product);
		// AllTestMethodNames.removeTestCaseFromDateTimeChangeGroup(LOG_ANALYZER_OUTPUT_PATH + "testCaseList.txt", product);
		// AllTestMethodNames.removeTestCaseFromCustomGroup(LOG_ANALYZER_OUTPUT_PATH + "testCaseList.txt","EnterCustomGroupNameHere", product ); // enter your custom group name as the 2nd parameter.

	}

	private static void createFullReport(LogAnalyzer tool, List<String> logOld, List<String> productionIssuesAndObsoleteTestCases) {

		createTestSuitesFromLog(tool, "testng-suite-custom.xml", productionIssuesAndObsoleteTestCases);

		showBuildInfo(tool);

		showTestSuitesExecuted(tool);

		showTestNGSuitesRunTime(tool);

		showKnownFailures(tool);

		showTestCaseFailures(tool);

		showMatchingFailuresBetweenTwoLogs(tool, logOld);

		showNewFailuresFromLatestLog(tool, logOld, "minus_automation_failures_testng-suite-custom.xml", productionIssuesAndObsoleteTestCases);

		printFileStatisticsBetweenTwoLogs(tool, logOld);

	}

	/**
	 * This method is used to create TestNG XML file with test cases that were started during automation
	 * run but never finished - which means were not reported as skipped, success or failure. Usage:
	 * Save Jenkins log in C:\Automation_artifacts\LogAnalyzer\LogAnalyzer-LogNew.log and execute the command,
	 * all test cases that were started but never finished will be included in testng-suite-custom.xml file
	 * which can be used for rerun.
	 * 
	 * @param tool
	 */

	@SuppressWarnings("unused")
	private static void createXMLWithUnfinishedTestCases(LogAnalyzer tool, List<String> productionIssuesAndObsoleteTestCases) {

		createTestSuitesWithUnfinishedTestCasesFromLog(tool, productionIssuesAndObsoleteTestCases);
	}

	private static void showBuildInfo(LogAnalyzer tool) {

		System.out.println("Build Information " + LOG_ANALYZER_LOG_NAME_1 + ":");

		tool.showBuildInfo();
	}

	private static void showTestSuitesExecuted(LogAnalyzer tool) {

		System.out.println("Test suites executed in this run " + LOG_ANALYZER_LOG_NAME_1 + ":");

		tool.showTestSuitesExecutedFromJLog();
	}

	private static void showTestNGSuitesRunTime(LogAnalyzer tool) {

		System.out.println("Test suites execution run time " + LOG_ANALYZER_LOG_NAME_1 + ":");

		tool.showTestNGSuitesRunTime();
	}

	private static void showKnownFailures(LogAnalyzer tool) {

		System.out.println("List of Known Failures " + LOG_ANALYZER_LOG_NAME_1 + ":");

		tool.showListOfUsusalFailures();
	}

	private static void showTestCaseFailures(LogAnalyzer tool) {

		System.out.println("List of Failed test cases " + LOG_ANALYZER_LOG_NAME_1 + ":");

		tool.showTestCaseFailuresFromJLog();
	}

	private static void showMatchingFailuresBetweenTwoLogs(LogAnalyzer tool, List<String> logOld) {

		System.out.println("Matching failures between two logs:");

		LogAnalyzer tool2 = new LogAnalyzer(logOld);
		tool.showMatchingFailuresBetweenTwoLogs(tool2.log);

		System.out.println("Matching failures total count: " + tool.getCountOfMatchingFailuresBetweenTwoLogs(tool2.log));
		System.out.println();
	}

	private static void showNewFailuresFromLatestLog(LogAnalyzer tool, List<String> logOld, String xmlFullFileName,
			List<String> productionIssuesAndObsoleteTestCases) {
		System.out.println("Failures introduced in the latest run " + LOG_ANALYZER_LOG_NAME_1 + ":");
		LogAnalyzer tool2 = new LogAnalyzer(logOld);
		List<String> results = tool.showNewFailuresOnlyFromNewLog(tool2.log);
		tool.createTestSuitesFromLog("1", results, "_" + xmlFullFileName, productionIssuesAndObsoleteTestCases);
	}

	private static void printFileStatisticsBetweenTwoLogs(LogAnalyzer tool, List<String> logOld) {

		LogAnalyzer tool2 = new LogAnalyzer(logOld);
		System.out.println("Log file statistics " + LOG_ANALYZER_LOG_NAME_1 + ":");

		tool.printLogStatistics();
		System.out.println("Log file statistics " + LOG_ANALYZER_LOG_NAME_2 + ":");

		tool2.printLogStatistics();
		System.out.println();
	}

	private static void createTestSuitesFromLog(LogAnalyzer tool, String xmlFullFileName,
			List<String> productionIssuesAndObsoleteTestCases) {
		TestngConfig tng = new TestngConfig();
		tng.output = LOG_ANALYZER_OUTPUT_PATH;
		tng.suiteType = TestngSuiteType.CUSTOM;
		tng.tngThreads = "1";
		tool.createTestSuitesFromLog(tng.tngThreads, tool.log, "_" + xmlFullFileName, productionIssuesAndObsoleteTestCases);
		System.out.println("\n\nSaving file in: " + LOG_ANALYZER_OUTPUT_PATH);
	}

	private static void createTestSuitesWithUnfinishedTestCasesFromLog(LogAnalyzer tool,
			List<String> productionIssuesAndObsoleteTestCases) {

		System.out.println("Unfinished test cases:");
		tool.createTestSuitesWithUnfinishedTestCases(productionIssuesAndObsoleteTestCases);
		System.out.println();
	}

	private static void initializeToolPaths() {

		File LogAnalyzerOutput = new File(LOG_ANALYZER_OUTPUT_PATH);
		File LogAnalyzerLogNew = new File(LOG_ANALYZER_LOG_NEW);
		File LogAnalyzerLogOld = new File(LOG_ANALYZER_LOG_OLD);

		try {
			if (!LogAnalyzerOutput.exists())
				LogAnalyzerOutput.mkdir();
			if (!LogAnalyzerLogNew.exists())
				LogAnalyzerLogNew.createNewFile();
			if (!LogAnalyzerLogOld.exists())
				LogAnalyzerLogOld.createNewFile();
		} catch (IOException e) {
			System.out.println("Unable to initialize output paths:");
			e.printStackTrace();
		}

		System.out.println("Log Analyzer Paths:");
		System.out.println(" Ouput folder: " + LOG_ANALYZER_OUTPUT_PATH);
		System.out.println(" Log new: " + LOG_ANALYZER_LOG_NEW);
		System.out.println(" Log old: " + LOG_ANALYZER_LOG_OLD);
		System.out.println();
	}

	public static void createXMLWithTestCasesThatNeverStartedFromLog(LogAnalyzer tool, List<String> logOld,
			List<String> productionIssuesAndObsoleteTestCases) {
		LogAnalyzer tool2 = new LogAnalyzer(logOld);

		System.out.println("NEVER started test cases:");

		tool.createTestSuitesWithTestCasesThatNeverRan(tool2.log, productionIssuesAndObsoleteTestCases);

		System.out.println();

	}

}
