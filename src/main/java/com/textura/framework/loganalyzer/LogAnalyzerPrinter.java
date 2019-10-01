package com.textura.framework.loganalyzer;

import java.util.ArrayList;
import java.util.List;

public class LogAnalyzerPrinter {

	protected static void printJLogContents(List<String> log) {

		for (int x = 0; x < log.size(); x++)
			System.out.println(" " + log.get(x));

		System.out.println();
	}

	protected static void printSearchStatisticsBetweenTwoLists(List<String> log, List<String> listOfStrings) {

		List<String> results = new ArrayList<String>();
		String line;
		int total = 0;

		for (int j = 0; j < listOfStrings.size(); j++) {

			System.out.println(" " + listOfStrings.get(j));

			for (int i = 0; i < log.size(); i++) {
				line = log.get(i);
				if (line.contains(listOfStrings.get(j))) {
					results.add(line);
				}
			}

			total = total + results.size();
			System.out.println("   Found " + results.size() + " time(s).");
			results.clear();
		}

		System.out.println(" Total number of strings found in log : " + total);
		System.out.println();
	}
}