package com.textura.framework.testrail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.textura.framework.abstracttestsuite.AbstractTestSuite.TestResult;
import com.textura.framework.configadapter.ConfigComponents;
import com.textura.framework.environment.EnvironmentInfo;
import com.textura.framework.tools.AllTestMethodNames;

//http://docs.gurock.com/testrail-api2/start

public class TestRailAPI {

	public static String TESTRAILAPIURL = "https://testrail.us.oracle.com/index.php?/api/v2/";
	public static String user = "qaautomation@oracle.com";
	public static String password = "KZ.Z0h4VbfvrUlxMs3au-1cXrM1e9iJpeasi8NIiB";
	
	public enum ProjectID {
		CPM {

			public String toString() {
				return "1";
			}
		},
		SE {

			public String toString() {
				return "12";
			}
		},
		PQM {

			public String toString() {
				return "2";
			}
		},
		GDB {

			public String toString() {
				return "7";
			}
		},
		PE {

			public String toString() {
				return "18";
			}
		},
		BO {

			public String toString() {
				return "8";
			}
		},
		POL {

			public String toString() {
				return "19";
			}
		},
		TCS {

			public String toString() {
				return "21";
			}
		},
		BS {

			public String toString() {
				return "22";
			}
		}
	}

	public static void main(String[] args) throws Exception {
		// new Assertion().assertTrue(getCase("1597541").contains("Test Case ID:
		// 1597541"));
		// new
		// Assertion().assertTrue(getMilestone("203").contains("PQM2013.3.1"));
		// new Assertion().assertTrue(isIDATestPlanID("11501"));
		// new Assertion().assertFalse(isIDATestPlanID("11458"));
		// new Assertion().assertTrue(isIDATestRunID("11458"));
		// new Assertion().assertFalse(isIDATestRunID("11501"));
		// new
		// Assertion().assertTrue(getProjectIDfromTestPlan("11501").equals("2"));
		// new Assertion().assertTrue(getRunsInTestPlan("10845").length > 0);
		// new
		// Assertion().assertTrue(getSuiteIDfromRunID("11458").equals("3564"));
		// new Assertion().assertTrue(getCasesInTestRun("11507").length > 0);
		// new Assertion().assertTrue(getTests("11507").size() > 0);
		// getSelectTestSuitePropertiesFromTestrailQuery("1");
		// getTestSuiteStatsFromTestrail();
		// getTestRailStats("34705");
		// System.out.println(getPlanStats2("34705"));
		// System.out.println(TestRailSupport.getTestSuiteNamesFromTestPlanIdOrRunId("34705"));
		// System.out.println(getPlanStats2("34164"));
		// resetAllPagesAffected();
		// System.out.println(TestRailSupport.getTestRailAllTestRunIDsFromTestPlanID("33856"));
		String aux = TestRailSupport.getTestRailAllTestRunIDsFromTestPlanID("66338");
		String[] split = aux.split(",");
		System.out.println("****************");
		for (int i = 0; i < split.length; i++) {
			System.out.println(TestRailSupport.getTestSuiteNamesFromTestRunID(split[i]));
		}
	}

	public String projectID;

	public static TestRailAPI setProject(ProjectID p) {
		return new TestRailAPI(p);
	}

	public TestRailAPI(ProjectID p) {
		projectID = p.toString();
	}

	/**
	 * Retrieves all projects from TestRail
	 * 
	 * @return A collection of all projects form TestRail
	 */
	public static JSONArray getProjects() {
		String URL = TESTRAILAPIURL + "get_projects";
		String getResult = HttpRequest.httpGet(URL, user, password);
		try {
			return new JSONArray(getResult);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Creates a Milestone and returns its new ID
	 * 
	 * @param projectID
	 * @param milestoneName
	 * @param milestoneDescription
	 * @param milestoneDate
	 * @return
	 */
	public static String addMilestone(String projectID, String milestoneName, String milestoneDescription,
			String milestoneDate) {

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", milestoneName);
		data.put("description", milestoneDescription);
		data.put("due_on", milestoneDate);
		String URL = TESTRAILAPIURL + "add_milestone/" + projectID;
		String postResult = HttpRequest.httpPost(URL, user, password, data);

		try {
			return new JSONObject(postResult).toString(3);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getMilestone(String milestoneID) {
		String URL = TESTRAILAPIURL + "get_milestone/" + milestoneID;
		String getResult = HttpRequest.httpGet(URL, user, password);
		try {
			return new JSONObject(getResult).toString(3);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Update a test result based on runID and caseID and returns ID of the test
	 * change
	 * 
	 * @param runID
	 * @param caseID
	 * @param statusID
	 * @param comment
	 * @param version
	 * @param elapsed
	 * @param defects
	 * @param assignedtoID
	 * @return
	 */
	public static String addResult(String runID, String caseID, String statusID, String comment, String version,
			String elapsed, String defects, String assignedtoID) {

		String URL = TESTRAILAPIURL + "add_result_for_case/" + runID + "/" + caseID;
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("status_id", statusID);
		data.put("comment", comment);
		data.put("version", version);
		data.put("elapsed", elapsed);
		data.put("defects", defects);
		data.put("assignedto_id", assignedtoID);
		String postResult = HttpRequest.httpPost(URL, user, password, data);
		try {
			return new JSONObject(postResult).toString(3);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	public static String addTestRunToPlan(String planID, String suiteID, List<String> caseIDs) {
		String URL = TESTRAILAPIURL + "add_plan_entry/" + planID;

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("suite_id", suiteID);
		data.put("include_all", Boolean.FALSE);
		String[] ar = new String[caseIDs.size()];
		caseIDs.toArray(ar);
		data.put("case_ids", ar);
		String postResult = HttpRequest.httpPost(URL, user, password, data);
		try {
			return new JSONObject(postResult).toString(3);
		} catch (Exception e) {
			throw new RuntimeException(data.toString(), e);
		}
	}

	/**
	 * Returns a test case details based on caseID as a single string
	 * 
	 * @param caseID
	 * @return
	 */
	public static String getCase(String caseID) {
		String URL = TESTRAILAPIURL + "get_case/" + caseID;
		String getResult = HttpRequest.httpGet(URL, user, password);

		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(getResult);
			String testcaseID = jsonObj.getString("id");
			String title = jsonObj.getString("title");
			String sectionID = jsonObj.getString("section_id");
			String typeID = jsonObj.getString("type_id");
			String priorityID = jsonObj.getString("priority_id");
			String references = jsonObj.getString("refs");
			String preconditions = jsonObj.getString("custom_preconds");
			String steps = jsonObj.getString("custom_steps");
			String expectedresults = jsonObj.getString("custom_expected");

			String combined = "Test Case ID: " + testcaseID + "\nTitle: " + title + "\nSection ID: " + sectionID
					+ "\nType ID: " + typeID + "\nPriority ID: " + priorityID + "\nReferences: " + references
					+ "\nPreconditions: " + preconditions + "\nExection Steps: " + steps + "\nExpected Results: "
					+ expectedresults;
			return combined;

		} catch (JSONException e) {
			try {
				System.out.println("Error parsing JSON string: " + jsonObj.toString(3));
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns 1 if a test case is marked as Automated in TestRail
	 * 
	 * @param caseID
	 * @return 1 if automated, 0 if not. -1 if caseID was not found
	 */
	public static int isCaseAutomated(String caseID) {
		String URL = TESTRAILAPIURL + "get_case/" + caseID;
		String getResult = HttpRequest.httpGet(URL, user, password);
		try {
			int type = new JSONObject(getResult).getInt("type_id");
			return (type == 1 || type == 7) ? 1 : 0;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean setCaseToAutomated(String caseID) {
		if (caseID.startsWith("c")) {
			caseID = caseID.substring(1);
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("type_id", 1);
		String URL = TESTRAILAPIURL + "update_case/" + caseID;
		String postResult = HttpRequest.httpPost(URL, user, password, data);
		return !postResult.isEmpty();
	}

	// Manual legacy type in TestRail, this type should not be used
	public static boolean setCaseToManual(String caseID) {
		if (caseID.startsWith("c")) {
			caseID = caseID.substring(1);
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("type_id", 6);
		String URL = TESTRAILAPIURL + "update_case/" + caseID;
		String postResult = HttpRequest.httpPost(URL, user, password, data);
		return !postResult.isEmpty();
	}

	public static boolean setCaseToManualToBeAutomated(String caseID) {
		if (caseID.startsWith("c")) {
			caseID = caseID.substring(1);
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("type_id", 14);
		String URL = TESTRAILAPIURL + "update_case/" + caseID;
		String postResult = HttpRequest.httpPost(URL, user, password, data);
		return !postResult.isEmpty();
	}

	/**
	 * Set type for caseID
	 * 
	 * @param caseID
	 * @param typeID
	 * @return
	 */
	public static boolean setCaseType(String caseID, String typeID) {
		if (caseID.startsWith("c")) {
			caseID = caseID.substring(1);
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("type_id", typeID);
		String URL = TESTRAILAPIURL + "update_case/" + caseID;
		String postResult = HttpRequest.httpPost(URL, user, password, data);
		return !postResult.isEmpty();
	}

	public List<String> getMismarkedAutomatedCases() {
		// Method below updated to use the TestRails API instead of directly
		// calling the database
		List<String> automatedCasesInTestRail = getAutomatedCases(projectID);
		List<String> automatedCasesInCode = AllTestMethodNames.getTestSuiteMethodNames();
		List<String> result = new ArrayList<String>();
		for (String s : automatedCasesInTestRail) {
			if (!automatedCasesInCode.contains(s)) {
				result.add(s);
			}
		}
		return result;
	}

	public List<String> getNotYetMarkedAutomatedCases() {
		// Method below updated to use the TestRails API instead of directly
		// calling the database
		List<String> automatedCasesInTestRail = getAutomatedCases(projectID);
		List<String> automatedCasesInCode = AllTestMethodNames.getTestSuiteMethodNames();
		List<String> result = new ArrayList<String>();
		for (String s : automatedCasesInCode) {
			if (!automatedCasesInTestRail.contains(s)) {
				result.add(s);
			}
		}
		return result;
	}

	// Benchmark: 13.5s
	public static List<String> getAutomatedCases(String projectID) {
		List<String> caseIDs = new ArrayList<String>();
		try {
			JSONArray suites = findAllSuitesForProject(projectID);
			String urlFindCases = TESTRAILAPIURL + "get_cases/" + projectID + "&&suite_id=";
			for (int i = 0; i < suites.length(); i++) {
				String getCasesID = HttpRequest
						.httpGet(urlFindCases + Integer.toString(suites.getJSONObject(i).getInt("id")), user, password);
				JSONArray cases = new JSONArray(getCasesID);
				for (int j = 0; j < cases.length(); j++) {
					if (cases.getJSONObject(j).getInt("type_id") == 1 || cases.getJSONObject(j).getInt("type_id") == 7)
						caseIDs.add("c" + Integer.toString(cases.getJSONObject(j).getInt("id")));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return caseIDs;
	}

	// Benchmark: 12.2s
	public static List<String> getAutomatedUpdateReqCases(String projectID) {
		List<String> caseIDs = new ArrayList<String>();
		try {
			JSONArray suites = findAllSuitesForProject(projectID);
			String urlFindCases = TESTRAILAPIURL + "get_cases/" + projectID + "&&suite_id=";
			for (int i = 0; i < suites.length(); i++) {
				String getCasesID = HttpRequest
						.httpGet(urlFindCases + Integer.toString(suites.getJSONObject(i).getInt("id")), user, password);
				JSONArray cases = new JSONArray(getCasesID);
				for (int j = 0; j < cases.length(); j++) {
					if (cases.getJSONObject(j).getInt("type_id") == 7)
						caseIDs.add("c" + Integer.toString(cases.getJSONObject(j).getInt("id")));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return caseIDs;
	}

	public static List<String> getNotAutomatedorAutomatedUpdateReqCases(String projectID) {
		List<String> caseIDs = new ArrayList<String>();
		try {
			JSONArray suites = findAllSuitesForProject(projectID);
			String urlFindCases = TESTRAILAPIURL + "get_cases/" + projectID + "&&suite_id=";
			for (int i = 0; i < suites.length(); i++) {
				String getCasesID = HttpRequest
						.httpGet(urlFindCases + Integer.toString(suites.getJSONObject(i).getInt("id")), user, password);
				JSONArray cases = new JSONArray(getCasesID);
				for (int j = 0; j < cases.length(); j++) {
					if (cases.getJSONObject(j).getInt("type_id") != 1 && cases.getJSONObject(j).getInt("type_id") != 7)
						caseIDs.add("c" + Integer.toString(cases.getJSONObject(j).getInt("id")));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return caseIDs;
	}

	/**
	 * Returns an array with all test case IDs for a given runID
	 * 
	 * @param runID
	 * @return
	 */
	public static String[] getCasesInTestRun(String runID) {
		String URL = TESTRAILAPIURL + "get_tests/" + runID;
		String getResult = HttpRequest.httpGet(URL, user, password);
		String[] casesList;
		try {
			JSONArray tests = new JSONArray(getResult);
			casesList = new String[tests.length()];
			for (int i = 0; i < tests.length(); i++) {
				casesList[i] = Integer.toString(tests.getJSONObject(i).getInt("case_id"));
			}
			return casesList;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns an array with all test case IDs for a given runID
	 * 
	 * @param runID
	 * @return
	 */
	public static List<String> getTests(String runID) {
		String URL = TESTRAILAPIURL + "get_tests/" + runID;
		String getResult = HttpRequest.httpGet(URL, user, password);
		try {
			JSONArray tests = new JSONArray(getResult);
			List<String> testsList = new ArrayList<String>();
			for (int i = 0; i < tests.length(); i++) {
				testsList.add(Integer.toString(tests.getJSONObject(i).getInt("id")));
			}
			return testsList;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static List<String> getTestsV2(String runID) {
		String URL = TESTRAILAPIURL + "get_tests/" + runID + "&status_id=3,4,5";
		String getResult = HttpRequest.httpGet(URL, user, password);
		try {
			JSONArray tests = new JSONArray(getResult);
			List<String> testsList = new ArrayList<String>();
			for (int i = 0; i < tests.length(); i++) {
				testsList.add(Integer.toString(tests.getJSONObject(i).getInt("case_id")));
			}
			return testsList;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// Benchmark: 0.12s
	public static String getCaseIdFromTestId(String testID) {
		String URL = TESTRAILAPIURL + "get_test/" + testID;
		String getResult = HttpRequest.httpGet(URL, user, password);
		try {
			return Integer.toString(new JSONObject(getResult).getInt("case_id"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// Benchmark: 0.1s
	public static String getTestResult(String testID) {
		String URL = TESTRAILAPIURL + "get_test/" + testID;
		String getResult = HttpRequest.httpGet(URL, user, password);
		try {
			return Integer.toString(new JSONObject(getResult).getInt("status_id"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns an array with all run IDs for a given testplan
	 * 
	 * @param projectID
	 * @param testPlanID
	 * @return
	 */
	public static String[] getRunsInTestPlan(String testPlanID) {

		String URL = TESTRAILAPIURL + "get_plan/" + testPlanID;
		String getResult = HttpRequest.httpGet(URL, user, password);

		try {
			JSONObject jsonObj = new JSONObject(getResult);
			JSONArray runs = jsonObj.getJSONArray("entries");
			String[] runsListArray = new String[runs.length()];
			for (int i = 0; i < runs.length(); i++) {
				runsListArray[i] = String
						.valueOf(runs.getJSONObject(i).getJSONArray("runs").getJSONObject(0).getInt("id"));
			}
			return runsListArray;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns an array with all run IDs for a given testplan
	 * 
	 * @param projectID
	 * @param testPlanID
	 * @return
	 */
	public static String[] getSuiteIDsInTestPlan(String testPlanID) {

		String URL = TESTRAILAPIURL + "get_plan/" + testPlanID;
		String getResult = HttpRequest.httpGet(URL, user, password);
		try {
			JSONObject jsonObj = new JSONObject(getResult);
			JSONArray runs = jsonObj.getJSONArray("entries");
			String[] runsListArray = new String[runs.length()];
			for (int i = 0; i < runs.length(); i++) {
				runsListArray[i] = String
						.valueOf(runs.getJSONObject(i).getJSONArray("runs").getJSONObject(0).getInt("suite_id"));
			}
			return runsListArray;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Prints TestRail case Type Ids with matching Names
	 */
	public static void printCaseTypeIdsWithNames() {
		String URL = TESTRAILAPIURL + "get_case_types/";
		String getResult = HttpRequest.httpGet(URL, user, password);
		try {
			JSONArray types = new JSONArray(getResult);
			System.out.println("Id: Name");
			for (int i = 0; i < types.length(); i++) {
				String typeId = Integer.toString(types.getJSONObject(i).getInt("id"));
				String typeName = types.getJSONObject(i).getString("name");
				System.out.println(typeId + ": " + typeName);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns true if test case is part of the specified project/runID
	 * combination, false otherwise
	 * 
	 * @param projectID
	 * @param runID
	 * @param caseID
	 * @return
	 */
	public static boolean isTestCaseInRun(String projectID, String runID, String caseID) {
		String[] casesInRun = getCasesInTestRun(runID);

		if (ArrayUtils.contains(casesInRun, caseID))
			return true;

		return false;
	}

	/**
	 * This may take a while depending on number of test cases in test plan
	 * 
	 * @param projectID
	 * @param testPlanID
	 * @param caseID
	 * @return true if test case is part of the specified project/testplan
	 *         combination, false otherwise
	 */
	public static boolean isTestCaseInTestPlan(String testPlanID, String caseID) {
		String[] runIDs = getRunsInTestPlan(testPlanID);
		String[] casesInRun;

		for (int i = 0; i < runIDs.length; i++) {
			casesInRun = getCasesInTestRun(runIDs[i]);

			if (ArrayUtils.contains(casesInRun, caseID))
				return true;
		}

		return false;
	}

	/**
	 * This may take a while depending on number of test cases in test plan
	 * 
	 * @param projectID
	 * @param testPlanID
	 * @param caseID
	 * @return runID of a test case is test plan, if test case does not exist in
	 *         any test plan, returns empty string
	 */
	public static String runIDofCaseInPlan(String projectID, String testPlanID, String caseID) {
		String[] runIDs = getRunsInTestPlan(testPlanID);
		String[] casesInRun;

		for (int i = 0; i < runIDs.length; i++) {
			casesInRun = getCasesInTestRun(runIDs[i]);

			if (ArrayUtils.contains(casesInRun, caseID))
				return runIDs[i];
		}

		return "";
	}

	/**
	 * Returns the SuiteID given a RunID
	 * 
	 * @param runID
	 * @return
	 */
	public static int getSuiteIDfromRunID(String runID) {

		String URL = TESTRAILAPIURL + "get_run/" + runID;
		String getResult = HttpRequest.httpGet(URL, user, password);

		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(getResult);
			return jsonObj.getInt("suite_id");
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Returns TestRail ProjectID given TestPlanID
	 * 
	 * @param testPlanID
	 * @return
	 */
	public static String getProjectIDfromTestPlan(String testPlanID) {
		String URL = TESTRAILAPIURL + "get_plan/" + testPlanID;
		try {
			JSONObject jsonObj = new JSONObject(HttpRequest.httpGet(URL, user, password));
			return jsonObj.getString("project_id");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns TestRail ProjectID given TestRunID
	 * 
	 * @param runID
	 * @return
	 */
	public static String getProjectIDfromTestRun(String runID) {
		String URL = TESTRAILAPIURL + "get_run/" + runID;
		String getResult = HttpRequest.httpGet(URL, user, password);

		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(getResult);
			return jsonObj.getString("project_id");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Determines if provided ID is a TestRun ID
	 * 
	 * @param TestRail
	 *            ID
	 * @return
	 */
	public static boolean isIDATestRunID(String id) {
		String URL = TESTRAILAPIURL + "get_run/" + id;
		try {
			String body = HttpRequest.httpGet(URL, user, password);
			return !body.contains("run is not a valid test run");
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Determines if provided ID is a TestPlan ID
	 * 
	 * @param TestRail
	 *            ID
	 * @return
	 */
	public static boolean isIDATestPlanID(String id) {
		String URL = TESTRAILAPIURL + "get_plan/" + id;
		try {
			String body = HttpRequest.httpGet(URL, user, password);
			return !body.contains("plan_id is not a valid test plan");
		} catch (Exception ex2) {
			return false;
		}
	}

	public static String mapTestResultValue(TestResult result) {
		if (result.equals(TestResult.FAIL)) {
			return "5";
		} else if (result.equals(TestResult.PASS)) {

			return "1";
		} else if (result.equals(TestResult.SKIP)) {

			return "4";
		}
		return null;
	}

	public static void sendTestResult(String testName, TestResult result, String message,
			EnvironmentInfo environmentInfo) {
		String[] runIDs = environmentInfo.testRunIDs.split(",");
		for (int i = 0; i < runIDs.length; i++) {
			if (TestRailAPI.isTestCaseInRun(environmentInfo.testrailProjectID, runIDs[i], testName)) {
				TestRailAPI.addResult(runIDs[i], testName, mapTestResultValue(result), message, "", "", "", "");
				// Method below updated to use the TestRails API instead of
				// directly calling the database
				if (TestRailAPI.isCaseAutomated(testName) == 0)
					// Method below updated to use the TestRails API instead of
					// directly calling the database
					TestRailAPI.setCaseToAutomated(testName);
			}
		}
	}

	public static String getProjectIDfromProduct(ConfigComponents component) {
		return ProjectID.valueOf(component.toString()).toString();
	}

	/**
	 * Returns cases that should be marked as automated. Eligible cases are
	 * those marked as 'Manual'.
	 * 
	 * @return
	 */
	public List<String> getToBeMarkedAutomatedCases() {
		// Method updated to use the TestRails API instead of directly calling
		// the database
		List<String> automatedCasesInTestRail = getAllAutomatedCases();
		List<String> automatedCasesInCode = AllTestMethodNames.getTestSuiteMethodNames();
		List<String> result = new ArrayList<String>();
		for (String s : automatedCasesInCode) {
			if (!automatedCasesInTestRail.contains(s)) {
				// Method updated to use the TestRails API instead of directly
				// calling the database
				if (isCaseManual(s)) {
					result.add(s);
				}
			}
		}
		return result;
	}

	// benchmark: 13.5s
	public List<String> getAllAutomatedCases() {
		List<String> caseIDs = new ArrayList<String>();
		try {
			JSONArray suites = findAllSuitesForProject(projectID);

			String urlFindCases = TESTRAILAPIURL + "get_cases/" + projectID + "&&suite_id=";

			for (int i = 0; i < suites.length(); i++) {
				String getCasesID = HttpRequest
						.httpGet(urlFindCases + Integer.toString(suites.getJSONObject(i).getInt("id")), user, password);
				JSONArray cases = new JSONArray(getCasesID);
				for (int j = 0; j < cases.length(); j++) {
					if (cases.getJSONObject(j).getInt("type_id") == 1 || cases.getJSONObject(j).getInt("type_id") == 7)
						caseIDs.add("c" + Integer.toString(cases.getJSONObject(j).getInt("id")));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return caseIDs;
	}

	// Benchmark: 13s
	public static List<String> getAllCases(String projectID) {
		List<String> caseIDs = new ArrayList<String>();
		try {
			JSONArray suites = findAllSuitesForProject(projectID);
			String urlFindCases = TESTRAILAPIURL + "get_cases/" + projectID + "&&suite_id=";
			for (int i = 0; i < suites.length(); i++) {
				String getCasesID = HttpRequest
						.httpGet(urlFindCases + Integer.toString(suites.getJSONObject(i).getInt("id")), user, password);
				JSONArray cases = new JSONArray(getCasesID);
				for (int j = 0; j < cases.length(); j++) {
					caseIDs.add("c" + Integer.toString(cases.getJSONObject(j).getInt("id")));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return caseIDs;
	}

	public static List<JSONObject> getAllCasesObjects(String projectID) {
		List<JSONObject> cases = new ArrayList<JSONObject>();
		try {
			JSONArray suites = findAllSuitesForProject(projectID);
			String urlFindCases = TESTRAILAPIURL + "get_cases/" + projectID + "&&suite_id=";
			for (int i = 0; i < suites.length(); i++) {
				JSONArray casesArray = new JSONArray(HttpRequest.httpGet(
						urlFindCases + Integer.toString(suites.getJSONObject(i).getInt("id")), user, password));
				for (int j = 0; j < casesArray.length(); j++) {
					cases.add(casesArray.getJSONObject(j));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return cases;
	}

	/**
	 * Returns true if test case is in Manual status type = 6 or
	 * Manual (ToBeAutomated) status type = 14
	 * 
	 * @param caseID
	 * @return
	 */
	public boolean isCaseManual(String caseID) {
		if (caseID.startsWith("c")) {
			caseID = caseID.substring(1);
		}
		String URL = TESTRAILAPIURL + "get_case/" + caseID;
		String getResult = HttpRequest.httpGet(URL, user, password);
		try {
			int type = new JSONObject(getResult).getInt("type_id");
			if (type == 6 || type == 14)
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Splits on the new line and appends 'c' to each test case id
	 * 
	 * @param cases
	 * @return
	 */
	public static List<String> splitIDs(String cases) {
		String[] temp = cases.split("\n");
		List<String> result = new ArrayList<String>();
		for (String s : temp) {
			if (s.length() > 0)
				result.add("c" + s);
		}
		return result;
	}

	// Benchmark: 12.2s
	/**
	 * Retrieves all test cases which are in type_id=9 (automation update
	 * required) state. Note type_id=9 (manual update required) test cases are
	 * handled by manual testers.
	 */
	public List<String> getUpdateRequiredCases() {
		List<String> caseIDs = new ArrayList<String>();
		try {
			JSONArray suites = findAllSuitesForProject(projectID);
			String urlFindCases = TESTRAILAPIURL + "get_cases/" + projectID + "&&suite_id=";
			for (int i = 0; i < suites.length(); i++) {
				String getCasesID = HttpRequest
						.httpGet(urlFindCases + Integer.toString(suites.getJSONObject(i).getInt("id")), user, password);
				JSONArray cases = new JSONArray(getCasesID);
				for (int j = 0; j < cases.length(); j++) {
					if (cases.getJSONObject(j).getInt("type_id") == 7)
						caseIDs.add("c" + Integer.toString(cases.getJSONObject(j).getInt("id")));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return caseIDs;
	}

	/**
	 * Retrieves test cases with from TestRail with detailed information by
	 * type, for example type_id=7 (automated update required), type_id=9
	 * (manual update required)
	 * 
	 */
	public List<JSONObject> getAllTestCasesByType(int typeId) {
		List<JSONObject> updateRequiredCases = new ArrayList<JSONObject>();
		try {
			JSONArray suites = findAllSuitesForProject(projectID);
			String urlFindCases = TESTRAILAPIURL + "get_cases/" + projectID + "&&suite_id=";
			for (int i = 0; i < suites.length(); i++) {
				String getCasesID = HttpRequest
						.httpGet(urlFindCases + Integer.toString(suites.getJSONObject(i).getInt("id")), user, password);
				JSONArray cases = new JSONArray(getCasesID);
				for (int j = 0; j < cases.length(); j++) {
					if (cases.getJSONObject(j).getInt("type_id") == typeId)
						updateRequiredCases.add(cases.getJSONObject(j));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return updateRequiredCases;
	}

	/**
	 * Retrieves all update required test cases with detailed information from
	 * TestRail by reference where reference for example would be a string like
	 * "ICPM-58"
	 * 
	 */
	public List<JSONObject> getAllTestCasesByReference(String reference) {
		List<JSONObject> updateRequiredCases = new ArrayList<JSONObject>();
		try {
			JSONArray suites = findAllSuitesForProject(projectID);
			String urlFindCases = TESTRAILAPIURL + "get_cases/" + projectID + "&&suite_id=";
			for (int i = 0; i < suites.length(); i++) {
				String getCasesID = HttpRequest
						.httpGet(urlFindCases + Integer.toString(suites.getJSONObject(i).getInt("id")), user, password);
				JSONArray cases = new JSONArray(getCasesID);
				for (int j = 0; j < cases.length(); j++) {
					if (cases.getJSONObject(j).getString("refs").toLowerCase().contains(reference.toLowerCase()))
						updateRequiredCases.add(cases.getJSONObject(j));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return updateRequiredCases;
	}

	public static void clearCaseLink(String caseID) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("custom_automated_case_link", "");
		String URL = TESTRAILAPIURL + "update_case/" + caseID;
		System.out.println(URL);
		String postResult = HttpRequest.httpPost(URL, user, password, data);

		try {
			if (new JSONObject(postResult).has("created_on")) {
				System.out.println("Clearing link for: " + caseID);
			} else {
				System.out.println("Cleating link failed for cases: " + caseID + "with message: "
						+ new JSONObject(postResult).toString(3));
			}
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setCaseLink(String caseID, String link) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("custom_automated_case_link", link);
		String URL = TESTRAILAPIURL + "update_case/" + caseID;
		String postResult = HttpRequest.httpPost(URL, user, password, data);

		try {
			if (new JSONObject(postResult).has("created_on")) {
				System.out.println("Updated link for: " + caseID);
			} else {
				System.out.println("Updated link failed for cases: " + caseID + "with message: "
						+ new JSONObject(postResult).toString(3));
			}
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Cases marked as manual or manual update required and have an automated
	 * case link value set
	 * 
	 * @return
	 */
	public List<String> getManualWithCaseLinkCases() {
		List<String> caseIDs = new ArrayList<String>();
		try {
			JSONArray suites = findAllSuitesForProject(projectID);
			String urlFindCases = TESTRAILAPIURL + "get_cases/" + projectID + "&&suite_id=";
			for (int i = 0; i < suites.length(); i++) {
				String getCasesID = HttpRequest
						.httpGet(urlFindCases + Integer.toString(suites.getJSONObject(i).getInt("id")), user, password);
				JSONArray cases = new JSONArray(getCasesID);
				for (int j = 0; j < cases.length(); j++) {
					if ((cases.getJSONObject(j).getInt("type_id") == 6 || cases.getJSONObject(j).getInt("type_id") == 9 || cases
							.getJSONObject(j).getInt("type_id") == 14)
							&& !cases.getJSONObject(j).isNull("custom_automated_case_link"))
						caseIDs.add("c" + Integer.toString(cases.getJSONObject(j).getInt("id")));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return caseIDs;
	}

	public static JSONArray findAllSuitesForProject(String projectID) {
		String urlFindSuites = TESTRAILAPIURL + "get_suites/" + projectID;
		try {
			return new JSONArray(HttpRequest.httpGet(urlFindSuites, user, password));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> getAllAutomatedCasesWithoutCustomAutomatedLink(String projectID) {
		List<String> caseIDs = new ArrayList<String>();
		String urlFindCases = TESTRAILAPIURL + "get_cases/" + projectID + "&&type_id=1&&suite_id=";
		try {
			JSONArray suites = findAllSuitesForProject(projectID);
			for (int i = 0; i < suites.length(); i++) {
				String getCasesID = HttpRequest
						.httpGet(urlFindCases + Integer.toString(suites.getJSONObject(i).getInt("id")), user, password);
				JSONArray cases = new JSONArray(getCasesID);
				for (int j = 0; j < cases.length(); j++) {
					if (cases.getJSONObject(j).isNull("custom_automated_case_link"))
						caseIDs.add("c" + Integer.toString(cases.getJSONObject(j).getInt("id")));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return caseIDs;
	}

	public static JSONArray getAllCasesPerSuite(String projectID, String suiteID) {
		try {
			String urlFindCases = TESTRAILAPIURL + "get_cases/" + projectID + "&&suite_id=" + suiteID;
			JSONArray allCases = new JSONArray(HttpRequest.httpGet(urlFindCases, user, password));
			return allCases;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Map<String, Integer> getAllCases(String projectID, String suiteID) {
		Map<String, Integer> returnData = new HashMap<String, Integer>();
		int emptyCases = 0;
		int automatedCases = 0;
		int manualCases = 0;
		try {
			String urlFindCases = TESTRAILAPIURL + "get_cases/" + projectID + "&&suite_id=" + suiteID;
			JSONArray allCases = new JSONArray(HttpRequest.httpGet(urlFindCases, user, password));
			for (int i = 0; i < allCases.length(); i++) {

				if (allCases.getJSONObject(i).isNull("custom_preconds")
						|| allCases.getJSONObject(i).isNull("custom_steps")
						|| allCases.getJSONObject(i).isNull("custom_expected")) {
					emptyCases++;
				}
				if (allCases.getJSONObject(i).getInt("type_id") == 1
						|| allCases.getJSONObject(i).getInt("type_id") == 7) {
					automatedCases++;
				}
				if (allCases.getJSONObject(i).getInt("type_id") == 6 || allCases.getJSONObject(i).getInt("type_id") == 8
						|| allCases.getJSONObject(i).getInt("type_id") == 9
						|| allCases.getJSONObject(i).getInt("type_id") == 11
						|| allCases.getJSONObject(i).getInt("type_id") == 14) {
					manualCases++;
				}
			}
			returnData.put("casesTotal", allCases.length());
			returnData.put("emptyCasesCount", emptyCases);
			returnData.put("automatedCasesCount", automatedCases);
			returnData.put("manualCasesCount", manualCases);

			return returnData;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Map<String, Integer> getRunData(String runID) {
		Map<String, Integer> returnData = new HashMap<String, Integer>();
		int passedCasesCount = 0;
		int failedCasesCount = 0;
		int untestedCasesCount = 0;
		int passedAutomated = 0;
		int passedManual = 0;
		int countTestsInPlan = 0;

		try {
			JSONArray results = new JSONArray(
					HttpRequest.httpGet(TESTRAILAPIURL + "get_results_for_run/" + runID, user, password));
			countTestsInPlan += results.length();
			for (int i = 0; i < results.length(); i++) {
				JSONObject result = (JSONObject) results.get(i);

				if (!result.getString("status_id").equals("null")) {

					if (result.getInt("status_id") == 1) {
						passedCasesCount++;
						JSONObject test = getTest(result.getString("test_id"));
						if (test.get("type_id") != null && (test.getInt("type_id") == 1 || test.getInt("type_id") == 7))
							passedAutomated++;
						else
							passedManual++;
					}

					if (result.getInt("status_id") == 3 || result.getInt("status_id") == 0
							|| result.getInt("status_id") == 6)
						untestedCasesCount++;

					if (result.getInt("status_id") == 5)
						failedCasesCount++;
				}
			}
			returnData.put("passed_count", passedCasesCount);
			returnData.put("failed_count", failedCasesCount);
			returnData.put("untested_count", untestedCasesCount);
			returnData.put("passedAutomated", passedAutomated);
			returnData.put("passedManual", passedManual);
			returnData.put("countTestsInPlan", countTestsInPlan);

			return returnData;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static JSONObject getTest(String testID) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(HttpRequest.httpGet(TESTRAILAPIURL + "get_test/" + testID, user, password));
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		return jsonObject;
	}

	public static JSONObject getCaseTitle(String testID) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(HttpRequest.httpGet(TESTRAILAPIURL + "get_case/" + testID, user, password));
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		return jsonObject;
	}

	public static String getSelectTestSuitePropertiesFromTestrailQuery(String projectID) {
		List<Integer> excludedIDs = Arrays.asList(43, 2014, 2071, 1360, 9027, 12285);
		Map<Integer, String> suiteProperties = new HashMap<Integer, String>();
		try {
			JSONArray suites = findAllSuitesForProject(projectID);
			for (int i = 0; i < suites.length(); i++) {
				int id = suites.getJSONObject(i).getInt("id");
				if (!excludedIDs.contains(id)) {
					suiteProperties.put(id, suites.getJSONObject(i).getString("name"));
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return sortByValue(suiteProperties).toString().replace(", ", "").replace("=", ",").replace("{", "").replace("}",
				"");
	}

	public static String getTestSuiteStatsFromTestrail() {
		List<String> excludedIDs = Arrays.asList("43", "2014", "2071", "1360", "9027", "12285");
		List<Map<String, String>> listSuiteStats = new ArrayList<Map<String, String>>();
		try {
			JSONArray suites = findAllSuitesForProject(ProjectID.CPM.toString());
			for (int i = 0; i < suites.length(); i++) {
				String id = String.valueOf(suites.getJSONObject(i).getInt("id"));
				if (!excludedIDs.contains(id)) {
					System.out.print(".");
					Map<String, String> suiteStats = new LinkedHashMap<String, String>();
					suiteStats.put("id", id);
					suiteStats.put("name", suites.getJSONObject(i).getString("name"));

					Map<String, Integer> casesData = getAllCases(ProjectID.CPM.toString(), id);
					suiteStats.put("casesTotal", String.valueOf(casesData.get("casesTotal")));
					suiteStats.put("emptyCasesTotal", String.valueOf(casesData.get("emptyCasesCount")));
					suiteStats.put("automated", String.valueOf(casesData.get("automatedCasesCount")));
					listSuiteStats.add(suiteStats);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		Collections.sort(listSuiteStats, new Comparator<Map<String, String>>() {

			public int compare(final Map<String, String> o1, final Map<String, String> o2) {
				return o1.get("name").compareTo(o2.get("name"));
			}
		});

		System.out.println("\n" + formatResult(listSuiteStats));
		return formatResult(listSuiteStats);
	}

	public static String getTestCaseTitle(String testCaseID) {
		try {
			System.out.println("TEST CASES ID: " + testCaseID);
			return getCaseTitle(testCaseID).getString("title");
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getPlanIDFromRun(String runID) {
		String URL = TESTRAILAPIURL + "get_run/" + runID;
		String getResult = HttpRequest.httpGet(URL, user, password);

		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(getResult);
			return jsonObj.getString("plan_id");
		} catch (Exception e) {
			throw new RuntimeException("Could not find Run for run id " + runID);
		}
	}

	/**
	 * Determines if provided ID is a TestRun ID
	 * 
	 * @param TestRail
	 *            ID
	 * @return
	 */
	public static boolean isTestRunID(String id) {
		String URL = TESTRAILAPIURL + "get_run/" + id;
		JSONObject json = null;
		try {
			json = new JSONObject(HttpRequest.httpGet(URL, user, password));
		} catch (JSONException e) {
			return false;
		}
		if (json.has("error"))
			return false;
		return true;
	}

	/**
	 * Determines if provided ID is a TestPlan ID
	 * 
	 * @param TestRail
	 *            ID
	 * @return
	 */
	public static boolean isTestPlanID(String id) {
		String URL = TESTRAILAPIURL + "get_plan/" + id;

		JSONObject json = null;
		try {
			json = new JSONObject(HttpRequest.httpGet(URL, user, password));
		} catch (JSONException e) {
			return false;
		}
		if (json.has("error"))
			return false;
		return true;
	}

	public static List<Map<String, String>> getTestRailStats(String testPlanID) {
		JSONObject plan = null;
		List<Map<String, String>> resultList = new LinkedList<Map<String, String>>();
		try {
			plan = new JSONObject(HttpRequest.httpGet(TESTRAILAPIURL + "get_plan/" + testPlanID, user, password));
			JSONArray entries = (JSONArray) plan.get("entries");
			for (int i = 0; i < entries.length(); i++) {
				JSONObject entry = (JSONObject) entries.get(i);
				JSONArray runs = (JSONArray) entry.get("runs");
				for (int j = 0; j < runs.length(); j++) {
					JSONObject run = (JSONObject) runs.get(j);
					JSONArray tests = new JSONArray(
							HttpRequest.httpGet(TESTRAILAPIURL + "get_tests/" + run.getString("id"), user, password));
					for (int k = 0; k < tests.length(); k++) {
						Map<String, String> resultSet = new LinkedHashMap<String, String>();
						JSONObject test = (JSONObject) tests.get(k);
						resultSet.put("id", test.getString("id"));
						resultSet.put("title", test.getString("title"));
						resultSet.put("reference", test.getString("refs"));
						resultSet.put("statusID", test.getString("status_id"));

						JSONArray results = new JSONArray(HttpRequest
								.httpGet(TESTRAILAPIURL + "get_results/" + test.getString("id"), user, password));
						if (results.length() > 0) {
							JSONObject lastResult = results.getJSONObject(results.length() - 1);
							resultSet.put("user", getUserNameByID(lastResult.getString("created_by")));
							resultSet.put("updateTimeStamp", lastResult.getString("created_on"));
						}
						resultSet.put("testRun", run.getString("name"));
						resultList.add(resultSet);
						System.out.print(".");
					}
				}
			}
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		return resultList;
	}

	public static Map<String, Object> getPlanStats2(String testPlanID) {
		Map<String, Object> resultSet = new LinkedHashMap<String, Object>();
		Map<String, Map<String, Integer>> dataByDate = new LinkedHashMap<String, Map<String, Integer>>();
		Map<String, Integer> dailyData = null;
		try {
			JSONObject plan = new JSONObject(
					HttpRequest.httpGet(TESTRAILAPIURL + "get_plan/" + testPlanID, user, password));
			resultSet.put("testPlanName", plan.getString("name"));
			resultSet.put("passed_count", plan.getInt("passed_count"));
			resultSet.put("blocked_count", plan.getInt("blocked_count"));
			resultSet.put("untested_count", plan.getInt("untested_count"));
			resultSet.put("retest_count", plan.getInt("retest_count"));
			resultSet.put("failed_count", plan.getInt("failed_count"));
			resultSet.put("custom_statusSum",
					plan.getInt("custom_status1_count") + plan.getInt("custom_status2_count")
							+ plan.getInt("custom_status3_count") + plan.getInt("custom_status4_count")
							+ plan.getInt("custom_status5_count") + plan.getInt("custom_status6_count")
							+ plan.getInt("custom_status7_count"));

			JSONArray entries = (JSONArray) plan.get("entries");
			for (int i = 0; i < entries.length(); i++) {
				JSONObject entry = (JSONObject) entries.get(i);
				for (int j = 0; j < entry.getJSONArray("runs").length(); j++) {
					JSONObject run = (JSONObject) entry.getJSONArray("runs").get(j);
					JSONArray resultsFromRun = new JSONArray(HttpRequest
							.httpGet(TESTRAILAPIURL + "get_results_for_run/" + run.getString("id"), user, password));
					// results from run: each record is the testResult
					for (int l = 0; l < resultsFromRun.length(); l++) {
						JSONObject testResult = resultsFromRun.getJSONObject(l);
						if (!testResult.isNull("status_id")) {

							String testDate = getDateFromSeconds(testResult.getLong("created_on"));
							String userName = getUserNameByID(testResult.getString("created_by"));
							int testType = TestRailAPI.getTest(testResult.getString("test_id")).getInt("type_id");
							// add data to existing date
							if (dataByDate.containsKey(testDate)) {
								if (testResult.getInt("status_id") == 1) {
									if (testType == 1 || testType == 2)
										dataByDate.get(testDate).put("passedAutomated",
												dailyData.get("passedAutomated") + 1);
									if (testType == 6 || testType == 8 || testType == 9 || testType == 11)
										dataByDate.get(testDate).put("passedmanual", dailyData.get("passedmanual") + 1);
								} else {
									if (testResult.getInt("status_id") == 2)
										dataByDate.get(testDate).put("blocked", dailyData.get("blocked") + 1);
									if (testResult.getInt("status_id") == 3)
										dataByDate.get(testDate).put("untested", dailyData.get("untested") + 1);
									if (testResult.getInt("status_id") == 4)
										dataByDate.get(testDate).put("retest", dailyData.get("retest") + 1);
									if (testResult.getInt("status_id") == 5)
										dataByDate.get(testDate).put("failed", dailyData.get("failed") + 1);
									if (testResult.getInt("status_id") == 6)
										dataByDate.get(testDate).put("N/A", dailyData.get("N/A") + 1);
									if (testResult.getInt("status_id") == 7)
										dataByDate.get(testDate).put("onHold", dailyData.get("onHold") + 1);
								}
								if (dailyData.get(userName) == null)
									dailyData.put(userName, 0);
								dataByDate.get(testDate).put(userName, dailyData.get(userName) + 1);
							}

							// insert a new record into the data dataSet
							else {
								dailyData = new HashMap<String, Integer>();
								dailyData.put("passedAutomated", 0);
								dailyData.put("passedmanual", 0);
								dailyData.put("blocked", 0);
								dailyData.put("untested", 0);
								dailyData.put("retest", 0);
								dailyData.put("failed", 0);
								dailyData.put("N/A", 0);
								dailyData.put("onHold", 0);
								dailyData.put(userName, 0);

								if (testResult.getInt("status_id") == 1) {
									if (testType == 1 || testType == 2)
										dailyData.put("passedAutomated", dailyData.get("passedAutomated") + 1);
									if (testType == 6 || testType == 8 || testType == 9 || testType == 11)
										dailyData.put("passedmanual", dailyData.get("passedmanual") + 1);
								} else {
									if (testResult.getInt("status_id") == 2)
										dailyData.put("blocked", dailyData.get("blocked") + 1);
									if (testResult.getInt("status_id") == 3)
										dailyData.put("untested", dailyData.get("untested") + 1);
									if (testResult.getInt("status_id") == 4)
										dailyData.put("retest", dailyData.get("retest") + 1);
									if (testResult.getInt("status_id") == 5)
										dailyData.put("failed", dailyData.get("failed") + 1);
									if (testResult.getInt("status_id") == 6)
										dailyData.put("N/A", dailyData.get("N/A") + 1);
									if (testResult.getInt("status_id") == 7)
										dailyData.put("onHold", dailyData.get("onHold") + 1);
								}
								dailyData.put(userName, dailyData.get(userName) + 1);

							}
							dataByDate.put(testDate, dailyData);
							System.out.print(".");
						}
					}
				}
			}
			System.out.println(".");
		} catch (Exception e) {
			e.printStackTrace();
		}
		resultSet.put("dates", dataByDate);
		return resultSet;
	}

	/**
	 * Get all TestRail Users
	 * 
	 * @return
	 */
	public static JSONArray getUsers() {

		String URL = TESTRAILAPIURL + "get_users";
		String getResult = HttpRequest.httpGet(URL, user, password);
		try {
			return new JSONArray(getResult);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getUserNameByID(String userID) {
		try {
			return (new JSONObject(HttpRequest.httpGet(TESTRAILAPIURL + "get_user/" + userID, user, password)))
					.getString("name");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void resetAllPagesAffected() {
		Map<String, Object> data = new HashMap<String, Object>();

		List<JSONObject> allCases = getAllCasesObjects(ProjectID.CPM.toString());
		data.put("custom_pages", new ArrayList<String>());
		int count = 0;
		for (JSONObject testCase : allCases) {
			try {
				if (testCase.has("custom_pages") && !testCase.isNull("custom_pages")
						&& testCase.getString("custom_pages").matches(".*\\d+.*")) {
					HttpRequest.httpPost(TESTRAILAPIURL + "update_case/" + testCase.getString("id"), user, password,
							data);
					count++;
				}
			} catch (JSONException e) {
			}
		}
		System.out.println("'Pages Affected' field removed from " + count + " test cases.");
	}

	public static String findRunForPlanAndViceVersa(String runOrPlanID) {
		String URL = TESTRAILAPIURL + "get_run/" + runOrPlanID;
		String getResult = HttpRequest.httpGet(URL, user, password);
		return getResult;
	}

	private static String getDateFromSeconds(Long seconds) {
		DateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		Date aux = new Date(seconds * 1000);
		return df.format(aux);
	}

	private static String formatResult(List<?> resultList) {
		StringBuffer result = new StringBuffer();
		for (Object resultLine : resultList) {
			result.append(resultLine.toString()
					.replace(", ", "")
					.replace("=", ",")
					.replace("{", "")
					.replace("}", "")
					.replace("id,", "")
					.replace("name", "")
					.replace("casesTotal", "")
					.replace("emptyCasesTotal", "")
					.replace("automated", ""));
			result.append("\n");
		}
		return result.toString();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Map<?, ?> sortByValue(Map<?, ?> unsortMap) {
		List<?> list = new LinkedList(unsortMap.entrySet());

		Collections.sort(list, new Comparator() {

			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue()).compareTo(((Map.Entry) (o2)).getValue());
			}
		});
		Map<Object, Object> sortedMap = new LinkedHashMap();
		for (Iterator<?> it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue() + "\n");
		}
		return sortedMap;
	}

	public static List<String> getTestsResult(String runID) {
		String URL = TESTRAILAPIURL + "get_results_for_run/" + runID + "&status_id=3";
		String getResult = HttpRequest.httpGet(URL, user, password);
		try {
			JSONArray tests = new JSONArray(getResult);
			List<String> testsList = new ArrayList<String>();
			for (int i = 0; i < tests.length(); i++) {
				System.out.println(tests.getJSONObject(i));
				testsList.add(Integer.toString(tests.getJSONObject(i).getInt("test_id")));
			}
			return testsList;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
