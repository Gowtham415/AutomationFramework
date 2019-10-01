package com.textura.framework.loganalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogAnalyzerUtils {

	public static final String LOG_TEST_SUITE = "Test suite:";
	public static final String LOG_RESULT_START = " start, com";
	public static final String LOG_RESULT_SUCCESS = " success, exe";
	public static final String LOG_RESULT_SKIPPED = " skipped, exe";
	public static final String LOG_RESULT_FAILURE = " failure, exe";
	public static final String LOG_DRIVER_TYPE_REMOTE = ", RemoteWebDriver";
	public static final String LOG_DRIVER_TYPE_LOCAL = ", WebDriver";

	protected static List<String> getMatchingTestCasesBetweenTwoJLogs(List<String> logOne, List<String> logTwo) {

		logOne.retainAll(logTwo);

		return logOne;
	}

	protected static List<String> getNewFailuresOnlyFromNewLog(List<String> logOne, List<String> logTwo) {

		logOne.removeAll(logTwo);

		return logOne;
	}

	protected static List<String> getTestIDsWithSuiteNamesFailedFromJLog(List<String> log) {

		String line;
		String suiteName;
		String testID;
		String newLine;
		List<String> finalOutput = new ArrayList<String>();
		log = LogAnalyzerUtils.getTestCasesFailedFromJLog(log);

		for (int i = 0; i < log.size(); i++) {
			line = log.get(i);
			testID = LogAnalyzerUtils.getTestIDFromJLog(line, LOG_RESULT_FAILURE);
			suiteName = LogAnalyzerUtils.getTestSuiteNameFromJLog(line);
			newLine = testID + " " + suiteName;
			finalOutput.add(newLine);
		}

		return finalOutput;
	}

	protected static List<String> getTestIDsWithSuiteNamesStartedFromJLog(List<String> log) {

		String line;
		String suiteName;
		String testID;
		String newLine;
		List<String> finalOutput = new ArrayList<String>();
		log = LogAnalyzerUtils.getTestCasesStartedFromJLog(log);

		for (int i = 0; i < log.size(); i++) {
			line = log.get(i);
			testID = LogAnalyzerUtils.getTestIDFromJLog(line, LOG_RESULT_START);
			suiteName = LogAnalyzerUtils.getTestSuiteNameFromJLog(line);
			newLine = testID + " " + suiteName;
			finalOutput.add(newLine);
		}

		return finalOutput;
	}

	protected static List<String> getTestIDsWithSuiteNamesSuccessfulFromJLog(List<String> log) {

		String line;
		String suiteName;
		String testID;
		String newLine;
		List<String> finalOutput = new ArrayList<String>();
		log = LogAnalyzerUtils.getTestCasesSuccessfulFromJLog(log);

		for (int i = 0; i < log.size(); i++) {
			line = log.get(i);
			testID = LogAnalyzerUtils.getTestIDFromJLog(line, LOG_RESULT_SUCCESS);
			suiteName = LogAnalyzerUtils.getTestSuiteNameFromJLog(line);
			newLine = testID + " " + suiteName;
			finalOutput.add(newLine);
		}

		return finalOutput;
	}

	protected static List<String> getTestIDsWithSuiteNamesSkippedFromJLog(List<String> log) {

		String line;
		String suiteName;
		String testID;
		String newLine;
		List<String> finalOutput = new ArrayList<String>();
		log = LogAnalyzerUtils.getTestCasesSkippedFromJLog(log);

		for (int i = 0; i < log.size(); i++) {
			line = log.get(i);
			testID = LogAnalyzerUtils.getTestIDFromJLog(line, LOG_RESULT_SKIPPED);
			suiteName = LogAnalyzerUtils.getTestSuiteNameFromJLog(line);
			newLine = testID + " " + suiteName;
			finalOutput.add(newLine);
		}

		return finalOutput;
	}

	protected static List<String> getTestSuiteNamesExecutedFromJLog(List<String> log) {

		String line;
		String suiteName;
		String newLine;
		List<String> finalOutput = new ArrayList<String>();
		log = LogAnalyzerUtils.getTestIDsWithSuiteNamesFailedFromJLog(log);

		for (int i = 0; i < log.size(); i++) {
			line = log.get(i);
			suiteName = LogAnalyzerUtils.getTestSuiteNameFromJLog(line);
			newLine = suiteName;
			if (!finalOutput.contains(suiteName))
				finalOutput.add(newLine);
		}

		return finalOutput;
	}

	protected static List<String> getTestCasesResultsInTwoLineFormatFromJLog(List<String> log, String result) {

		List<String> results = new ArrayList<String>();
		String line;

		for (int i = 0; i < log.size(); i++) {
			line = log.get(i);
			if (line.contains(result)) {
				results.add(line);
				if (i + 1 < log.size()) {
					results.add("  " + log.get(i + 1));
					results.add("");
				}
			}
		}

		return results;
	}

	protected static List<String> getTestCasesResultsInOneLineFormatFromJLog(List<String> log, String result) {

		List<String> results = new ArrayList<String>();
		String line;

		for (int i = 0; i < log.size(); i++) {
			line = log.get(i);
			if (line.contains(result)) {
				results.add(line);
				if (i + 1 < log.size()) {
					results.add("  " + log.get(i + 1));
				}
			}
		}

		return results;
	}

	protected static List<String> getTcTsInTwoLineFormatFromJLog(List<String> log, List<String> log2) {

		List<String> results = new ArrayList<String>();
		String line;
		String testID;

		for (int x = 0; x < log2.size(); x++) {
			testID = log2.get(x);
			testID = testID.substring(testID.indexOf("c"), testID.indexOf("com.textura")).trim();
			for (int i = 0; i < log.size(); i++) {
				line = log.get(i);
				if (line.contains(testID) && line.contains(LogAnalyzerUtils.LOG_RESULT_FAILURE)) {
					results.add(line);
					if (i + 1 < log.size()) {
						results.add("  " + log.get(i + 1));
						results.add("");
					}
				}
			}
		}

		return results;
	}

	protected static List<String> getTcTsInOneLineFormatFromJLog(List<String> log, List<String> log2) {

		List<String> results = new ArrayList<String>();
		String line;
		String testID;

		for (int x = 0; x < log2.size(); x++) {
			testID = log2.get(x);
			testID = testID.substring(testID.indexOf("c"), testID.indexOf("com.textura")).trim();
			for (int i = 0; i < log.size(); i++) {
				line = log.get(i);
				if (line.contains(testID) && line.contains(LogAnalyzerUtils.LOG_RESULT_FAILURE)) {
					results.add(line);
					if (i + 1 < log.size()) {
						results.add("  " + log.get(i + 1));
					}
				}
			}
		}

		return results;
	}

	protected static String getTestSuiteNameFromJLog(String line) {
		String suiteName = line.substring(line.lastIndexOf("com.textura")).trim();
		if (suiteName.contains(",")) {
			suiteName = suiteName.substring(0, suiteName.indexOf(','));
		}
		return suiteName.trim();
	}

	protected static String getTestIDFromJLog(String line, String result) {
		Pattern p = Pattern.compile(" c[\\d]+" + result);
		Matcher matcher = p.matcher(line);
		int index = 0;
		try {
			matcher.find();
			index = matcher.start();
		} catch (Exception x) {
			x.printStackTrace();
			throw new IllegalStateException("Unable to process line:" + line);
		}
		return line.substring(index, line.indexOf(result)).trim();
	}

	protected static String getTestIDFromLine(String line) {
		String[] test;
		test = line.split(" ");
		return test[0];
	}

	protected static String getTestSuiteNameFromLine(String line) {
		String[] suite;
		suite = line.split(" ");
		return suite[1];
	}

	protected static int getTestCasesCountByStartedFromJLog(List<String> log) {
		return getTestCasesByResultFromJLog(log, LOG_RESULT_START).size();
	}

	protected static int getTestCasesCountBySuccessfulFromJLog(List<String> log) {
		return getTestCasesByResultFromJLog(log, LOG_RESULT_SUCCESS).size();
	}

	protected static int getTestCasesCountBySkippedFromJLog(List<String> log) {
		return getTestCasesByResultFromJLog(log, LOG_RESULT_SKIPPED).size();
	}

	protected static int getTestCasesCountByFailedFromJLog(List<String> log) {
		return getTestCasesByResultFromJLog(log, LOG_RESULT_FAILURE).size();
	}

	protected static int getTestCasesCountByDriverRemoteFromJLog(List<String> log) {
		return getTestCasesByResultFromJLog(log, LOG_DRIVER_TYPE_REMOTE).size();
	}

	protected static int getTestCasesCountByDriverLocalFromJLog(List<String> log) {
		return getTestCasesByResultFromJLog(log, LOG_DRIVER_TYPE_LOCAL).size();
	}

	protected static int getTotalNumberOfLinesInJLog(List<String> log) {
		return log.size();
	}

	protected static List<String> getTestSuitesExecutedFromJLog(List<String> log) {
		return getTestCasesByResultFromJLog(log, LOG_TEST_SUITE);
	}

	protected static List<String> getTestCasesStartedFromJLog(List<String> log) {
		return getTestCasesByResultFromJLog(log, LOG_RESULT_START);
	}

	protected static List<String> getTestCasesSuccessfulFromJLog(List<String> log) {
		return getTestCasesByResultFromJLog(log, LOG_RESULT_SUCCESS);
	}

	protected static List<String> getTestCasesSkippedFromJLog(List<String> log) {
		return getTestCasesByResultFromJLog(log, LOG_RESULT_SKIPPED);
	}

	protected static List<String> getTestCasesFailedFromJLog(List<String> log) {
		return getTestCasesByResultFromJLog(log, LOG_RESULT_FAILURE);
	}

	protected static List<String> getTestCasesByDriverTypeRemoteFromJLog(List<String> log) {
		return getTestCasesByResultFromJLog(log, LOG_DRIVER_TYPE_REMOTE);
	}

	protected static List<String> getTestCasesByDriverTypeLocalFromJLog(List<String> log) {
		return getTestCasesByResultFromJLog(log, LOG_DRIVER_TYPE_LOCAL);
	}

	private static List<String> getTestCasesByResultFromJLog(List<String> log, String resultType) {

		List<String> results = new ArrayList<String>();
		String line;

		for (int i = 0; i < log.size(); i++) {
			line = log.get(i);
			if (!line.contains("SystemInfo start, com") && !line.contains("SystemInfo success, execution") && !line.contains(
					"SystemInfo failure, execution")) {
				if (line.contains(resultType))
					results.add(line);
			}
		}

		return results;
	}

	protected static List<String> getTextByStringFromJLog(List<String> log, List<String> buildInfo) {

		List<String> results = new ArrayList<String>();
		String line;

		for (int i = 0; i < log.size(); i++) {
			line = log.get(i);
			for (int j = 0; j < buildInfo.size(); j++)
				if (line.contains(buildInfo.get(j))) {
					results.add(line);
				}
		}

		return results;
	}

	protected static List<String> getTestCasesThatNeverRanFromNewLog(List<String> logOne, List<String> logTwo) {

		logOne.removeAll(logTwo);

		return logOne;
	}

}
