package com.textura.framework.tools;

import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import com.textura.framework.testrail.TestRailAPI;
import com.textura.framework.testrail.TestRailAPI.ProjectID;

public class TestRailStatistics {

	public static void main(String args[]) {

		/*
		 * TestRail type ids with names:
		 * 
		 * Id: Name
		 * 1 : Automated
		 * 7 : Automated (Update Required)
		 * 2 : Functionality
		 * 6 : Manual
		 * 8 : Manual (To Be Written)
		 * 9 : Manual (Update Required)
		 * 11: Manual Only
		 * 3 : Performance
		 * 4 : Regression
		 * 5 : Usability
		 * 10: User Acceptance
		 */
		int automationUpdateRequiredTypeId = 7;
		int manualUpdateRequiredTypeId = 9;

		// Set project id
		ProjectID projectId = TestRailAPI.ProjectID.CPM;

		// Specify reference from TestRail, for example "ICPM-58"
		String reference = "ICPM-58";

		TestRailAPI api = TestRailAPI.setProject(projectId);

		getAutomationUpdateRequiredCasesByReference(api.getAllTestCasesByType(automationUpdateRequiredTypeId), reference);

		getManualUpdateRequiredCasesByReference(api.getAllTestCasesByType(manualUpdateRequiredTypeId), reference);

		getAutomationUpdateRequiredCases(api.getAllTestCasesByType(automationUpdateRequiredTypeId));

		getAutomationUpdateRequiredCases(api.getAllTestCasesByType(manualUpdateRequiredTypeId));

		getAutomatedCasesByReference(api.getAllTestCasesByReference(reference), reference);
	}

	private static void getAutomationUpdateRequiredCasesByReference(List<JSONObject> testCases, String reference) {
		System.out.println("Test Cases in Automation Update Required state with reference: " + reference);
		System.out.println("TestCaseId,Type,Reference,TestCaseTitle");
		printUpdateRequiredCasesByReference(testCases, reference);
	}

	private static void getManualUpdateRequiredCasesByReference(List<JSONObject> testCases, String reference) {
		System.out.println("\nTest Cases in Manual Update Required state with reference: " + reference);
		System.out.println("TestCaseId,Type,Reference,TestCaseTitle,AutomationComments");
		printUpdateRequiredCasesByReference(testCases, reference);
	}

	private static void getAutomationUpdateRequiredCases(List<JSONObject> testCases) {
		System.out.println("\nTest Cases in Automation Update Required state");
		System.out.println("TestCaseId,Type,Reference,TestCaseTitle,AutomationComments");
		printAutomationUpdateRequiredCases(testCases);
	}

	private static void getAutomatedCasesByReference(List<JSONObject> testCases, String reference) {
		System.out.println("\nTest Cases in Automation Update Required state");
		System.out.println("TestCaseId,Type,Reference,TestCaseTitle,AutomationComments");
		printAutomatedCasesByReference(testCases, reference);
	}

	private static void printAutomationUpdateRequiredCases(List<JSONObject> testCases) {
		for (JSONObject tc : testCases) {
			StringBuffer output = new StringBuffer();
			try {
				output.append("c");
				output.append(tc.getString("id")).append(",");
				output.append(tc.getString("refs")).append(",");
				output.append(tc.getString("type_id")).append(",");
				output.append(tc.getString("title")).append(",");
				output.append(tc.getString("custom_automation_comment"));
				System.out.println(output.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private static void printUpdateRequiredCasesByReference(List<JSONObject> testCases, String reference) {
		for (JSONObject tc : testCases) {
			StringBuffer output = new StringBuffer();
			try {
				if (tc.getString("refs").toLowerCase().contains(reference.toLowerCase())) {
					output.append("c");
					output.append(tc.getString("id")).append(",");
					output.append(tc.getString("refs")).append(",");
					output.append(tc.getString("type_id")).append(",");
					output.append(tc.getString("title"));
					System.out.println(output.toString());
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	private static void printAutomatedCasesByReference(List<JSONObject> testCases, String reference) {
		for (JSONObject tc : testCases) {
			StringBuffer output = new StringBuffer();
			try {
				// if ((tc.getString("title").toLowerCase().contains("OBSOLETE".toLowerCase()))) {
				output.append("c");
				output.append(tc.getString("id")).append(",");
				output.append(tc.getString("refs")).append(",");
				output.append(tc.getString("type_id")).append(",");
				output.append(tc.getString("title"));
				output.append(tc.getString("custom_automation_comment"));
				System.out.println(output.toString());
				// }
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}