package com.textura.framework.loganalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.textura.framework.testng.TestngTags;

public class LogAnalyzerXmlBuilder {


	public static String createXMLWithMethodsFromJenkinsOutput(List<String> log, List<String> productionIssuesAndObsoleteTestCases) {

		Map<String, List<String>> map = new HashMap<String, List<String>>();
		String className = null;
		String testId = null;
		for (String line : log) {
			if ((line.contains(LogAnalyzerUtils.LOG_RESULT_FAILURE) || line.contains(LogAnalyzerUtils.LOG_RESULT_SKIPPED)) && !line
					.contains("SystemInfo")) {
				className = LogAnalyzerUtils.getTestSuiteNameFromJLog(line);
				if (!map.containsKey(className)) {
					map.put(className, new ArrayList<String>());
				}

				if (line.contains(LogAnalyzerUtils.LOG_RESULT_FAILURE)) {
					testId = LogAnalyzerUtils.getTestIDFromJLog(line, LogAnalyzerUtils.LOG_RESULT_FAILURE);
				} else {
					testId = LogAnalyzerUtils.getTestIDFromJLog(line, LogAnalyzerUtils.LOG_RESULT_SKIPPED);
				}
				if(!productionIssuesAndObsoleteTestCases.contains(testId) && !map.get(className).contains(testId)){
				map.get(className).add(testId);
				}

			}
		}


		StringBuilder xml = new StringBuilder();
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			className = entry.getKey();
			List<String> cases = entry.getValue();
			xml.append(TestngTags.addXMLClassTagStart(className) + "\n");
			xml.append(TestngTags.addXMLMethodsTagStart() + "\n");

			for (String case_ : cases) {
				xml.append(TestngTags.addXMLMethodTagName(case_) + "\n");
			}
			xml.append(TestngTags.addXMLMethodsTagEnd() + "\n");
			xml.append(TestngTags.addXMLClassTagEnd(className) + "\n");
		}

		return xml.toString();

	}

	public static String createXMLWithMethodsFromJenkinsOutputWithPrintout(List<String> log) {

		String testID;
		String suiteName;
		String suiteNameold = null;
		String xmlMethodsAsString = "";
		String line;
		List<String> xmlMethods = new ArrayList<String>();
		List<String> failures = LogAnalyzerUtils.getTestCasesFailedFromJLog(log);
		Map<String, List<String>> methods = new HashMap<String, List<String>>();

		for (int i = 0; i < failures.size(); i++) {

			line = failures.get(i);
			suiteName = LogAnalyzerUtils.getTestSuiteNameFromJLog(line);
			testID = LogAnalyzerUtils.getTestIDFromJLog(line, LogAnalyzerUtils.LOG_RESULT_FAILURE);

			if (xmlMethods.size() == 0 || !suiteName.equals(suiteNameold)) {
				if ((xmlMethods.size() != 0 && !suiteName.equals(suiteNameold))) {
					methods.put(suiteName, xmlMethods);
					xmlMethods.clear();
				}
				suiteNameold = suiteName;
				xmlMethods.add(testID);
			}
		}
		for (int i = 0; i < methods.size(); i++) {
			System.out.println("Map" + methods.toString());
		}

		return xmlMethodsAsString;
	}

	public static String createXMLWithMethods(List<String> log) {

		String testID;
		String suiteName;
		String suiteNameold = null;
		String xmlMethodsAsString = "";
		String line;
		List<String> xmlMethods = new ArrayList<String>();

		for (int i = 0; i < log.size(); i++) {

			line = log.get(i);
			suiteName = LogAnalyzerUtils.getTestSuiteNameFromLine(line);
			testID = LogAnalyzerUtils.getTestIDFromLine(line);

			if (xmlMethods.size() == 0 || !suiteName.equals(suiteNameold)) {
				if ((xmlMethods.size() != 0 && !suiteName.equals(suiteNameold))) {
					xmlMethods.add(TestngTags.addXMLMethodsTagEnd());
					xmlMethods.add(TestngTags.addXMLClassTagEnd(suiteNameold));
				}
				suiteNameold = suiteName;
				xmlMethods.add(TestngTags.addXMLClassTagStart(suiteName));
				xmlMethods.add(TestngTags.addXMLMethodsTagStart());
			}
			xmlMethods.add(TestngTags.addXMLMethodTagName(testID));
		}
		xmlMethods.add(TestngTags.addXMLMethodsTagEnd());
		xmlMethods.add(TestngTags.addXMLClassTagEnd(suiteNameold));

		for (int i = 0; i < xmlMethods.size(); i++) {
			xmlMethodsAsString = xmlMethodsAsString + xmlMethods.get(i) + "\n";
		}
		return xmlMethodsAsString;
	}
}
