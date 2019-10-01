package com.textura.framework.tools;

import java.util.ArrayList;
import java.util.List;
import com.textura.framework.testrail.TestRailAPI;
import com.textura.framework.testrail.TestRailAPI.ProjectID;

public class MarkCasesAutomated {

	/**
	 * 
	 * @param testrailproject
	 */
	public static void markAutomated(ProjectID testrailproject) {
		List<String> duplicate = new ArrayList();
		List<String> toBeAutomated = new ArrayList();
		List<String> mismarked = new ArrayList();
		List<String> updateRequired = new ArrayList();
		List<String> obsolete = new ArrayList();
		int updated = 0;

		System.out.println("Analyzing TestRail Data");

		TestRailAPI api = TestRailAPI.setProject(testrailproject);

		// mark manual as automated
		toBeAutomated = api.getToBeMarkedAutomatedCases();

		// list Automated(Update Required) && Method below updated to use the TestRails API instead of directly calling the database
		updateRequired = api.getUpdateRequiredCases();

		// mark automated as manual
		mismarked = api.getMismarkedAutomatedCases();

		// find duplicates in code
		duplicate = AllTestMethodNames.getDuplicateTestCases();

		// List Obsolete test cases in actual code, not in testrail
		obsolete = AllTestMethodNames.getTestSuiteObsoleteMethodNames();

		System.out.println("Number of cases TOTAL:           " + AllTestMethodNames.getTestSuiteMethodNames().size());
		System.out.println("Number of cases AUTOMATED:       " + toBeAutomated.size()); // cases set to automated from manual
		System.out.println("Number of cases UPDATE REQUIRED: " + updateRequired.size());
		System.out.println("Number of cases OBSOLETE:        " + obsolete.size());
		System.out.println("Number of cases DUPLICATE:       " + duplicate.size());
		System.out.println("Number of cases MISMARKED:       " + mismarked.size()); // cases set to manual in test rail

		if (toBeAutomated.size() > 0) {
			for (String s : toBeAutomated) {
				// Method below updated to use the TestRails API instead of directly calling the database
				TestRailAPI.setCaseToAutomated(s);
				updated++;
			}
			System.out.println("Set " + updated + " test cases to AUTOMATED in TestRail: \n" + toBeAutomated.toString()
					.replace("[", "")
					.replace("]", ""));
		}

		if (mismarked.size() > 0) {
			for (String s : mismarked) {
				// Method below updated to use the TestRails API instead of directly calling the database
				TestRailAPI.setCaseToManualToBeAutomated(s);
			}
			System.out.println("Set to MANUAL in TestRail: \n" + mismarked.toString().replace("[", "").replace("]", ""));
		}

		if (updateRequired.size() > 0 && !updateRequired.get(0).equals("c")) {
			System.out.println("UPDATE REQUIRED cases: \n" + updateRequired.toString().replace("[", "").replace("]", ""));
		}

		if (obsolete.size() > 0) {
			System.out.println("OBSOLETE cases: \n" + obsolete.toString().replace("[", "").replace("]", ""));
		}

		if (duplicate.size() > 0) {
			System.out.println("DUPLICATE cases: \n" + duplicate.toString().replace("[", "").replace("]", ""));
		}

		if (mismarked.size() > 0) {
			System.out.println("MISMARKED cases: \n" + mismarked.toString().replace("[", "").replace("]", ""));
		}
	}

	/**
	 * Updates only case links that are null
	 * 
	 * @param testrailproject
	 * @param testCodeServiceURL
	 *            http://dfwin7qaauto53:8080/Automation/
	 */
	public static void updateCaseLink(ProjectID testrailproject, String testCodeServiceURL) {
		TestRailAPI api = TestRailAPI.setProject(testrailproject);
		List<String> toUpdate = api.getAllAutomatedCasesWithoutCustomAutomatedLink(testrailproject.toString());
		System.out.println(toUpdate);
		System.out.println("Updating case links");
		for (int i = 0; i < toUpdate.size(); i++) {
			if (toUpdate.size() > 9 && i % (toUpdate.size() / 10) == 0) {
				System.out.print("."); // progress indicator
			}
			String s = toUpdate.get(i).replace("c", "");
			// Method below updated to use the TestRails API instead of directly calling the database
			TestRailAPI.setCaseLink(s, testCodeServiceURL + testrailproject.name() + "/" + s);
		}
		// Method below updated to use the TestRails API instead of directly calling the database
		List<String> cases = api.getManualWithCaseLinkCases();
		int count = 0;
		System.out.println(cases);
		for (int i = 0; i < cases.size(); i++) {
			// Method below updated to use the TestRails API instead of directly calling the database
			TestRailAPI.clearCaseLink(cases.get(i).replace("c", ""));
			count++;
		}
		System.out.println("\nCompleted. " + count + " test cases updated.");
	}

	/**
	 * Updates all cases marked as automated. Can be used if the case link has changed
	 * 
	 * @param testrailproject
	 * @param testCodeServiceURL
	 *            http://dfwin7qaauto53:8080/Automation/
	 */
	public static void updateAllCaseLink(ProjectID testrailproject, String testCodeServiceURL) {
		TestRailAPI api = TestRailAPI.setProject(testrailproject);
		// update the test code url -- // Method below updated to use the TestRails API instead of directly calling the database
		List<String> automated = api.getAllAutomatedCases();
		System.out.println("Updating case links");
		for (int i = 0; i < automated.size(); i++) {
			if (automated.size() > 9 && i % (automated.size() / 10) == 0) {
				System.out.print("."); // progress indicator
			}
			String s = automated.get(i);
			// Method below updated to use the TestRails API instead of directly calling the database
			TestRailAPI.setCaseLink(s, testCodeServiceURL + testrailproject.name() + "/" + s);
		}
		// Method below updated to use the TestRails API instead of directly calling the database
		List<String> cases = api.getManualWithCaseLinkCases();
		for (int i = 0; i < cases.size(); i++) {
			// Method below updated to use the TestRails API instead of directly calling the database
			TestRailAPI.clearCaseLink(cases.get(i));
		}
		System.out.println("\nCompleted");
	}

}
