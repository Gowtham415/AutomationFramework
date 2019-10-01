package com.textura.automation.tce;

import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class to handle different comparisons of lines: contains and regex
 *
 */
enum CompareMode {

	contains(CompareMode::doesStringContain),
	regex(CompareMode::doesStringMatchRegex),
	contains_regex(CompareMode::doesStringContainRegex);

	BiFunction<String, String, Boolean> comparisonFunction;

	CompareMode(BiFunction<String, String, Boolean> comparisonFunction) {
		this.comparisonFunction = comparisonFunction;
	}

	private static boolean doesStringContain(String statementString, String containsString) {
		return statementString.contains(containsString);
	}

	private static boolean doesStringMatchRegex(String statementString, String regex) {
		return statementString.matches(regex);
	}

	private static boolean doesStringContainRegex(String statementString, String regex) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(statementString);
		return m.find();
	}

	public boolean compare(String statementString, String compareString) {
		return comparisonFunction.apply(statementString, compareString);
	}
}
