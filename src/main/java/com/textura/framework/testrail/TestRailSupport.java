package com.textura.framework.testrail;

import java.util.ArrayList;
import java.util.List;
import com.textura.framework.utils.JavaHelpers;

public class TestRailSupport {

	static String[] allTestClasses = null;

	public static String getTestSuiteNamesFromTestRunID(String testRunID) {

		if (allTestClasses == null) {
			allTestClasses = JavaHelpers.listFilesAsArray();
		}
		int suiteID = TestRailAPI.getSuiteIDfromRunID(testRunID);
		String suites = "";
		for (int j = 0; j < allTestClasses.length; j++) {
			if (allTestClasses[j].endsWith("_" + suiteID))
				suites = suites + allTestClasses[j] + ",";
		}
		return suites;
	}

	public static String getTestSuiteNamesFromTestSuiteID(String testSuiteID) {

		if (allTestClasses == null) {
			allTestClasses = JavaHelpers.listFilesAsArray();
		}
		String suites = "";
		for (int j = 0; j < allTestClasses.length; j++) {
			if (allTestClasses[j].endsWith("_" + testSuiteID))
				suites = suites + allTestClasses[j] + ",";
		}
		return suites;
	}

	public static String getTestSuiteNamesFromTestPlanID(String testPlanID) {

		String[] suiteIDs = TestRailAPI.getSuiteIDsInTestPlan(testPlanID);
		String suites = "";
		for (int i = 0; i < suiteIDs.length; i++)
			suites = suites + getTestSuiteNamesFromTestSuiteID(suiteIDs[i]);
		return suites;
	}

	public static String getTestSuiteNamesFromTestPlanIdOrRunId(String planOrRunID) {
		String testSuiteNames = "";
		testSuiteNames = getTestSuiteNamesFromTestRunID(planOrRunID);
		if (testSuiteNames.isEmpty())
			testSuiteNames = getTestSuiteNamesFromTestPlanID(planOrRunID);
		if (testSuiteNames.isEmpty())
			throw new RuntimeException("Provided ID is not either a Plan or Run ID.");
		return testSuiteNames;
	}

	public static String getTestRailAllTestRunIDsFromTestPlanID(String testPlanID) {
		if (!TestRailAPI.isTestPlanID(testPlanID) && TestRailAPI.isTestRunID(testPlanID))
			testPlanID = TestRailAPI.getPlanIDFromRun(testPlanID);
		String testRunIDs = "";
		String[] testRuns = TestRailAPI.getRunsInTestPlan(testPlanID);
		for (int i = 0; i < testRuns.length; i++)
			testRunIDs = testRunIDs + testRuns[i] + ",";
		return testRunIDs;
	}

	public static List<String> getFailedOrUntestedTestsInTestPlan(String planID) {
		List<String> failedTests = new ArrayList<String>();
		String[] runs = TestRailAPI.getRunsInTestPlan(planID);
		for (int i = 0; i < runs.length; i++) {
			List<String> testsList = TestRailAPI.getTestsV2(runs[i]);
			failedTests.addAll(testsList);
		}
		for(int i = 0; i < failedTests.size(); i++){
			failedTests.set(i, "c"+failedTests.get(i));
		}
		return failedTests;
	}

	// ===================================Test Runs=====================================================
	public static List<String> getFailedOrUntestedTestsInTestRun(String runIDID) {
		List<String> failedTests = new ArrayList<String>();
		List<String> testsList = TestRailAPI.getTests(runIDID);
		for (String test : testsList) {
			// Method below updated to use the TestRails API instead of directly calling the database
			String result = TestRailAPI.getTestResult(test);
			System.out.println(result);
			if (result.equals("5")) {
				// Method below updated to use the TestRails API instead of directly calling the database
				failedTests.add("c" + TestRailAPI.getCaseIdFromTestId(test));
			} else if (result.equals("3")) {
				String caseID = TestRailAPI.getCaseIdFromTestId(test);
				// Method below updated to use the TestRails API instead of directly calling the database
				if (TestRailAPI.isCaseAutomated(caseID) == 1) {
					failedTests.add("c" + caseID);
				}
			}
		}
		return failedTests;
	}

	public static List<String> getAllTestsInTestRun(String runIDID) {
		List<String> failedTests = new ArrayList<String>();
		List<String> testsList = TestRailAPI.getTests(runIDID);
		for (String test : testsList) {
			// Method below updated to use the TestRails API instead of directly calling the database
			failedTests.add("c" + TestRailAPI.getCaseIdFromTestId(test));
		}
		return failedTests;
	}

	public static List<String> getFailedTestsInTestRun(String runIDID) {
		List<String> failedTests = new ArrayList<String>();
		List<String> testsList = TestRailAPI.getTests(runIDID);
		for (String test : testsList) {
			// Method below updated to use the TestRails API instead of directly calling the database
			String result = TestRailAPI.getTestResult(test);
			if (result.equals("5")) {
				// Method below updated to use the TestRails API instead of directly calling the database
				failedTests.add("c" + TestRailAPI.getCaseIdFromTestId(test));
			}
		}
		return failedTests;
	}

	public static List<String> getUntestedTestsInTestPlan(String planID) {
		List<String> failedTests = new ArrayList<String>();
		String[] runs = TestRailAPI.getRunsInTestPlan(planID);
		for (int i = 0; i < runs.length; i++) {
			List<String> testsList = TestRailAPI.getTests(runs[i]);
			for (String test : testsList) {
				// Method below updated to use the TestRails API instead of directly calling the database
				String result = TestRailAPI.getTestResult(test);
				if (result.equals("3")) {
					// Method below updated to use the TestRails API instead of directly calling the database
					failedTests.add("c" + TestRailAPI.getCaseIdFromTestId(test));
				}
			}
		}
		return failedTests;
	}

}
