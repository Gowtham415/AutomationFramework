package com.textura.framework.loganalyzer;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.textura.framework.testng.TestSuitesBuilderApp;

public class LogAnalyzer {

	public static final String TEST_SUITE = "Test suite:";
	public static final String RESULT_START = "start,";
	public static final String RESULT_SUCCESS = "success,";
	public static final String RESULT_SKIPPED = "skipped,";
	public static final String RESULT_FAILURE = "failure,";
	public static final String DRIVER_TYPE_REMOTE = ", RemoteWebDriver";
	public static final String DRIVER_TYPE_LOCAL = ", WebDriver";

	com.textura.framework.testng.TestSuitesBuilder testSuitesCreator;
	String classes;
	List<String> log;

	int testCasesStartedTotal;
	int testCasesFailedTotal;
	int testCasesPassedTotal;
	int testCasesSkippedTotal;
	int testCasesDriverLocalTotal;
	int testCasesDriverRemoteTotal;

	public LogAnalyzer(List<String> logNew) {

		log = logNew;
		testCasesStartedTotal = LogAnalyzerUtils.getTestCasesCountByStartedFromJLog(log);
		testCasesFailedTotal = LogAnalyzerUtils.getTestCasesCountByFailedFromJLog(log);
		testCasesPassedTotal = LogAnalyzerUtils.getTestCasesCountBySuccessfulFromJLog(log);
		testCasesSkippedTotal = LogAnalyzerUtils.getTestCasesCountBySkippedFromJLog(log);
		testCasesDriverLocalTotal = LogAnalyzerUtils.getTestCasesCountByDriverLocalFromJLog(log);
		testCasesDriverRemoteTotal = LogAnalyzerUtils.getTestCasesCountByDriverRemoteFromJLog(log);
		testSuitesCreator = new TestSuitesBuilderApp();
	}

	public void printLogStatistics() {

		System.out.print(" Executed: " + testCasesStartedTotal + ",");
		System.out.print(" Failed: " + testCasesFailedTotal + ",");
		System.out.print(" Passed: " + testCasesPassedTotal + ",");
		System.out.print(" Skipped: " + testCasesSkippedTotal + ".");
		System.out.print(" Execution:");
		System.out.print(" External: " + testCasesDriverRemoteTotal + ",");
		System.out.println(" Local: " + testCasesDriverLocalTotal);
	}

	public void showBuildInfo() {

		List<String> buildInfo = new ArrayList<String>();
		buildInfo.add("'TestEnvironment'");
		buildInfo.add("'AutomationGridMode'");
		buildInfo.add("'AutomationGridServer'");
		buildInfo.add("'AutomationGridNumOfNodes'");
		buildInfo.add("'TestPlanMode'");
		buildInfo.add("'TestPlanID'");
		buildInfo.add("'TestRunMode'");
		buildInfo.add("'TestRunID'");
		buildInfo.add("'TestSuitesFailed'");
		buildInfo.add("'TestNotes'");

		LogAnalyzerPrinter.printJLogContents(LogAnalyzerUtils.getTextByStringFromJLog(log, buildInfo));
	}

	public void showTestSuitesExecutedFromJLog() {

		LogAnalyzerPrinter.printJLogContents(LogAnalyzerUtils.getTestSuiteNamesExecutedFromJLog(log));
	}

	public void showTestNGSuitesRunTime() {

		List<String> results = LogAnalyzerUtils.getTestSuitesExecutedFromJLog(log);
		LogAnalyzerPrinter.printJLogContents(results);
	}

	public void showTestCaseFailuresFromJLog() {

		List<String> results = LogAnalyzerUtils.getTestCasesResultsInTwoLineFormatFromJLog(log, LogAnalyzerUtils.LOG_RESULT_FAILURE);
		LogAnalyzerPrinter.printJLogContents(results);
	}

	public void showMatchingFailuresBetweenTwoLogs(List<String> logOld) {

		List<String> compared = LogAnalyzerUtils.getMatchingTestCasesBetweenTwoJLogs(LogAnalyzerUtils
				.getTestIDsWithSuiteNamesFailedFromJLog(this.log), LogAnalyzerUtils.getTestIDsWithSuiteNamesFailedFromJLog(logOld));
		List<String> results = LogAnalyzerUtils.getTcTsInOneLineFormatFromJLog(log, compared);

		LogAnalyzerPrinter.printJLogContents(results);
	}

	public int getCountOfMatchingFailuresBetweenTwoLogs(List<String> logOld) {

		List<String> compared = LogAnalyzerUtils.getMatchingTestCasesBetweenTwoJLogs(LogAnalyzerUtils
				.getTestIDsWithSuiteNamesFailedFromJLog(this.log), LogAnalyzerUtils.getTestIDsWithSuiteNamesFailedFromJLog(logOld));

		return compared.size();
	}

	public List<String> showNewFailuresOnlyFromNewLog(List<String> logOld) {

		List<String> compared = LogAnalyzerUtils.getNewFailuresOnlyFromNewLog(LogAnalyzerUtils
				.getTestIDsWithSuiteNamesFailedFromJLog(this.log), LogAnalyzerUtils.getTestIDsWithSuiteNamesFailedFromJLog(logOld));
		List<String> results = LogAnalyzerUtils.getTcTsInOneLineFormatFromJLog(log, compared);

		LogAnalyzerPrinter.printJLogContents(results);
		
		return results;
	}

	public List<String> getListOfUsusalFailures() {

		List<String> listOfErrors = new ArrayList<String>();
		listOfErrors.add("org.openqa.selenium.UnsupportedCommandException:");
		listOfErrors.add("org.openqa.selenium.WebDriverException: Error communicating with the remote browser. It may have died.");
		listOfErrors
				.add("org.openqa.selenium.remote.UnreachableBrowserException: Error communicating with the remote browser. It may have died.");
		listOfErrors.add("Caused by: org.openqa.selenium.StaleElementReferenceException:");
		listOfErrors.add("Caused by: org.openqa.selenium.NoSuchElementException: Unable to locate element:");
		listOfErrors.add("org.openqa.selenium.WebDriverException: Java heap space");
		listOfErrors.add("org.openqa.selenium.WebDriverException: <html>");
		listOfErrors.add("java.lang.AssertionError: Timeout waiting for link:");

		return listOfErrors;
	}

	public void showListOfUsusalFailures() {

		LogAnalyzerPrinter.printSearchStatisticsBetweenTwoLists(log, getListOfUsusalFailures());
	}

	public void createTestSuitesFromLog(String threads, List<String> log, String xmlFullFileName, List<String> productionIssuesAndObsoleteTestCases) {
		String tngSuites = LogAnalyzerXmlBuilder.createXMLWithMethodsFromJenkinsOutput(log, productionIssuesAndObsoleteTestCases);
		int tests = countOccuranceOfTests(tngSuites);
		testSuitesCreator.createTestSuites(threads, tngSuites, tests + xmlFullFileName);
	}

	public void createTestSuitesWithMatchingTestCases(List<String> logOld, List<String> productionIssuesAndObsoleteTestCases) {

		List<String> compared = LogAnalyzerUtils.getMatchingTestCasesBetweenTwoJLogs(LogAnalyzerUtils
				.getTestIDsWithSuiteNamesFailedFromJLog(this.log), LogAnalyzerUtils.getTestIDsWithSuiteNamesFailedFromJLog(logOld));

		System.out.println("Test Cases which failed in both logs: ");

		classes = LogAnalyzerXmlBuilder.createXMLWithMethodsFromJenkinsOutput(log, productionIssuesAndObsoleteTestCases);

		System.out.println("Total number of matching test cases: " + compared.size());
	}

	public void createTestSuitesWithUnfinishedTestCases(List<String> productionIssuesAndObsoleteTestCases) {
		List<String> unfinished;
		List<String> removeList = new ArrayList<>();
		int finished = 0;
		List<String> started = LogAnalyzerUtils.getTestIDsWithSuiteNamesStartedFromJLog(this.log);
		List<String> failed = LogAnalyzerUtils.getTestIDsWithSuiteNamesFailedFromJLog(this.log);
		List<String> skipped = LogAnalyzerUtils.getTestIDsWithSuiteNamesSkippedFromJLog(this.log);
		List<String> passed = LogAnalyzerUtils.getTestIDsWithSuiteNamesSuccessfulFromJLog(this.log);

		finished = failed.size() + skipped.size() + passed.size();
		System.out.println("Started test cases : " + started.size() + " (started)");
		System.out.println("Finished test cases: " + failed.size() + " (failed) +  " + skipped.size() + " (skipped) + " + passed.size()
				+ " (passed) = " + finished);
		System.out.println("Run status:");

		unfinished = LogAnalyzerUtils.getNewFailuresOnlyFromNewLog(started, failed);
		unfinished = LogAnalyzerUtils.getNewFailuresOnlyFromNewLog(unfinished, skipped);
		unfinished = LogAnalyzerUtils.getNewFailuresOnlyFromNewLog(unfinished, passed);
		
		for(String current : unfinished){
			if(productionIssuesAndObsoleteTestCases.contains(current.replaceAll(" com.*", ""))){
				removeList.add(current);
			}
		}
		unfinished.removeAll(removeList);
		
		classes = LogAnalyzerXmlBuilder.createXMLWithMethods(unfinished);
		testSuitesCreator.createTestSuites("1", classes, "unfinished_testcases_testng-suite-custom.xml");

		System.out.println("Total number of unfinished test cases: " + unfinished.size());
	}
	
	public void createTestSuitesWithTestCasesThatNeverRan(List<String> fullRunLog, List<String> productionIssuesAndObsoleteTestCases) {

		List<String> neverRan;
		List<String> removeList = new ArrayList<>();
		int finished = 0;
		List<String> started = LogAnalyzerUtils.getTestIDsWithSuiteNamesStartedFromJLog(this.log);
		List<String> failed = LogAnalyzerUtils.getTestIDsWithSuiteNamesFailedFromJLog(this.log);
		List<String> skipped = LogAnalyzerUtils.getTestIDsWithSuiteNamesSkippedFromJLog(this.log);
		List<String> passed = LogAnalyzerUtils.getTestIDsWithSuiteNamesSuccessfulFromJLog(this.log);
		List<String> allTestCasesInFullRun = LogAnalyzerUtils.getTestIDsWithSuiteNamesStartedFromJLog(fullRunLog);

		finished = failed.size() + skipped.size() + passed.size();
		neverRan = LogAnalyzerUtils.getTestCasesThatNeverRanFromNewLog(allTestCasesInFullRun, started);
		
		System.out.println("NEVER started cases: " + neverRan.size());
		System.out.println("Started test cases : " + started.size() + " (started)");
		System.out.println("Finished test cases: " + failed.size() + " (failed) +  " + skipped.size() + " (skipped) + " + passed.size()
				+ " (passed) = " + finished);
		System.out.println("Run status:");

		for(String current : neverRan){
			if(productionIssuesAndObsoleteTestCases.contains(current.replaceAll(" com.*", ""))){
				removeList.add(current);
			}
		}
		neverRan.removeAll(removeList);
		
		classes = LogAnalyzerXmlBuilder.createXMLWithMethods(neverRan);
		testSuitesCreator.createTestSuites("1", classes, "never_ran_testcases_testng-suite-custom.xml");

		System.out.println("Total number of test cases that never got started: " + neverRan.size());
	}
	
	public int countOccuranceOfTests(String string){
		return StringUtils.countMatches(string, "<include name=");
	}
}
