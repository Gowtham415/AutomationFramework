package com.textura.framework.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.textura.framework.environment.Project;
import com.textura.framework.testrail.TestRailAPI;
import com.textura.framework.testrail.TestRailAPI.ProjectID;
import com.textura.framework.utils.JavaHelpers;

public class TestCaseStats {

	/**
	 * 
	 * @benchmark 50s
	 */
	public static void main(String[] args) {
		long begin = new Date().getTime();
		System.out.println("Started generating report...");
		String fileName = Project.tools("TestCaseStats2.csv");
		String header = "Id,Suite Name,Cases Total,Empty Cases,Automated Cases, Status, Test Developer\n";
		String fileContent = "";

		TestCaseStats test = new TestCaseStats();
		String output = test.getSuiteData();

		fileContent = header + output;
		System.out.println(fileContent);
		JavaHelpers.writeFile(fileName, fileContent);
		System.out.println("\nRun time: " + ((new Date().getTime() - begin)) / 1000 + "s");
	}

	public String getSuiteData() {
		List<Integer> undesirableIDs = new ArrayList<Integer>(Arrays.asList(43, 2014, 2071, 1360, 12285));
		JSONArray suites = TestRailAPI.findAllSuitesForProject(ProjectID.CPM.toString());
		String validSuitesString = "";
		try {
			for (int i = 0; i < suites.length(); i++) {
				JSONObject suite = suites.getJSONObject(i);
				if (!undesirableIDs.contains(suite.get("id"))) {
					System.out.print(".");
					suite.accumulate("reportData", TestRailAPI.getAllCases(ProjectID.CPM.toString(), suite.getString("id")));
					suite.remove("is_master");
					suite.remove("is_baseline");
					suite.remove("completed_on");
					suite.remove("description");
					suite.remove("is_completed");
					suite.remove("url");
					suite.remove("project_id");
					suite.remove("manualCasesCount");
					validSuitesString += suite.get("id") + "," + suite.get("name") + "," + extractReportData(suite.get("reportData"))
							+ "\n";
				}
			}
			System.out.println(".");
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}
		return validSuitesString;
	}

	@SuppressWarnings("unchecked")
	private String extractReportData(Object data) {
		return ((Map<String, Integer>) data).get("casesTotal") + "," + ((Map<String, Integer>) data).get("emptyCasesCount") + ","
				+ ((Map<String, Integer>) data).get("automatedCasesCount");

	}
}