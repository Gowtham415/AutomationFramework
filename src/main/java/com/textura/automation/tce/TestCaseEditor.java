package com.textura.automation.tce;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ArrayUtils;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.textura.framework.environment.Project;
import com.textura.framework.utils.JavaHelpers;

public class TestCaseEditor {

	static final String LOG_ANALYZER_OUTPUT_PATH = Project.pathAutomationArtifacts("LogAnalyzer/");
	static final String LOG_ANALYZER_CASES_PATH = LOG_ANALYZER_OUTPUT_PATH + "testCaseList.txt";
	static final String LOG_ANALYZER_TCG_LOG = LOG_ANALYZER_OUTPUT_PATH + "tcglog.log";
	static List<String> testCasesToEdit = new ArrayList<String>();
	static TreeSet<String> filesToSave = new TreeSet<String>();

	public static void main(String[] args) throws Exception {
		// get the test cases that need to be edited
		testCasesToEdit = getCasesFromFile(LOG_ANALYZER_CASES_PATH);

		// Read testsuites
		File folder = new File("C:/Automation/Textura/CPM/src/test/java/com/textura/cpm/testsuites");
				// "C:/Automation/Textura/CPM/src/test/java/com/textura/cpm/testsuites/administration/messages"
				// "C:/Automation/Textura/CPM/src/test/java/com/textura/cpm/testsuites/invoice/systemgeneratedinvoicenumbers"
				ArrayList<File> files = getFilesFromDir(folder);

		// parse files of the cases that need to be edited
		CompilationUnit cu = new CompilationUnit();
		for (File in : files) {
			try {
				System.out.println("Parsing File " + in.getName() + "...");
				replaceLinebreaks(in);
				cu = JavaParser.parse(in, Charset.defaultCharset());
				// use MethodVisitor to edit cases
				cu.accept(new MethodVisitor(), null);
			} finally {
				// save the files that were added to the list of files to save
				if (filesToSave.contains(in.getName())) {
					System.out.println("Saving... " + in.getName());
					Files.write(in.toPath(), cu.toString().getBytes());
				}
				replaceLinebreakComments(in);
			}

			System.out.println("Done parsing " + in.getName());
		}
	}

	static MethodDeclaration addStep(MethodDeclaration n, String containingStmt, String addStmtString) {
		return addStep(n, containingStmt, addStmtString, CompareMode.contains);
	}

	/**
	 * Replaces $ markers with original values of regex matched string.
	 * For example:
	 * match = "selenium().ProjectHomePage.clickLink("asdf");
	 * regex = "selenium\(\)\.(.*)\.clickMenuLink\((.*));
	 * replacementString = "selenium().$1.clickMenuLink($2);
	 * 
	 * This method will return
	 * selenium().ProjectHomePage.clickMenuLink("asdf");
	 *
	 * 
	 * @param match
	 *            The string that matched the regex
	 * @param regex
	 *            The regex used to search
	 * @param replacementString
	 *            The string with $ markers to be replaced by the original values
	 * @return replacementString with $ markers replaced with original values from string
	 */
	private static String insertCapturedGroups(String match, String regex, String replacementString) {
		Matcher matcher = Pattern.compile(regex).matcher(match);
		int groups = matcher.groupCount();
		String regexedStmtString = replacementString;
		if (matcher.find()) {
			for (int i = 1; i <= groups; i++) {
				try {
					String group = matcher.group(i);
					regexedStmtString = regexedStmtString.replaceAll("\\$" + i, group);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		else {
			System.out.println("insertCapturedGroupsToString did not find a match... this is suspicious... String: '" + match
					+ "' contain: '" + regex + "' add: '" + replacementString);
		}
		return regexedStmtString;
	}

	/*
	 * adds addStmtString to method in the line after containingStmt if the result of compareMode.compare is true.
	 * Comparemode can be contains or regex
	 */
	static MethodDeclaration addStep(MethodDeclaration n, String containingStmt, String addStmtString, CompareMode compareMode) {
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		indexes = getIndexWhereContains(n, containingStmt, compareMode);

		if (compareMode.equals(CompareMode.regex) && indexes.size() > 0 && addStmtString.contains("$")) { // capture group support
			for (int i = 0; i < indexes.size(); i++) {
				int index = indexes.get(i);
				String line = n.getBody().get().getStatement(index - 1).toString();
				String regexedStmtString = insertCapturedGroups(line, containingStmt, addStmtString);
				Statement regexedStmt = JavaParser.parseStatement(regexedStmtString);
				n = addStepsAtIndexes(n, new ArrayList<Integer>(Arrays.asList(index)), regexedStmt);
				indexes = incrementCounts(i, indexes);
			}
		} else {
			Statement addStmt = JavaParser.parseStatement(addStmtString);
			n = addStepsAtIndexes(n, indexes, addStmt);
		}
		return n;
	}
	
	static MethodDeclaration addSteps(MethodDeclaration n, String containingStmt, String... addStmtStrings) {
		ArrayUtils.reverse(addStmtStrings);
		for (String addStmtString : addStmtStrings) {
			n = addStep(n, containingStmt, addStmtString);
		}
		return n;
	}

	static MethodDeclaration changeStep(MethodDeclaration n, String ogStmt, String changeStmtString) {
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		Statement changeStmt = JavaParser.parseStatement(changeStmtString);

		indexes = getIndexWhereContains(n, ogStmt);
		n = addStepsAtIndexes(n, indexes, changeStmt);

		return n;
	}
	
	static MethodDeclaration removeStep(MethodDeclaration n, String removeStepString) {
		n = removeStmts(n, removeStepString, CompareMode.contains);
		return n;
	}

	/*
	 * Removes removeStepString from method n if comparemode.compare returns true.
	 * CompareMode can be contains or regex
	 */
	static MethodDeclaration removeStep(MethodDeclaration n, String removeStepString, CompareMode compareMode) {
		n = removeStmts(n, removeStepString, compareMode);
		return n;
	}

	/*
	 * adds addStmt to the method where
	 */
	private static MethodDeclaration addStepsAtIndexes(MethodDeclaration n, ArrayList<Integer> indexes, Statement addStmt) {
		BlockStmt body = n.getBody().get();

		// go through the list of indexes and add the statement we want after all of the instances in indexes
		for (int i = 0; i < indexes.size(); i++) {
			body.addStatement(indexes.get(i), addStmt);
			// hella janky but it git's it dun
			indexes = incrementCounts(i, indexes);
		}

		return n;
	}

	private static ArrayList<Integer> getIndexWhereContains(MethodDeclaration n, String containingStmt) {
		return getIndexWhereContains(n, containingStmt, CompareMode.contains);
	}

	/*
	 * gets list of indexes where it finds containingStmt
	 */
	private static ArrayList<Integer> getIndexWhereContains(MethodDeclaration n, String containingStmt, CompareMode comparer) {
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		BlockStmt body = n.getBody().get();
		int index = 1; // starting at one so it inserts in the correct spot

		if (testCasesToEdit.contains(n.getNameAsString())) {
			// go through the lines of code in the method and see if they contain the text we want to change
			for (Statement s : body.getStatements()) {
				if (comparer.compare(s.toString(), containingStmt)) {
					// if the line does, add that line index to the indexes list
					indexes.add(index);

					// add the file that it came from to the list of files to save
					Node parent = n.getParentNode().get();
					filesToSave.add(parent.getChildNodes().get(0).toString() + ".java");
				}
				index++;
			}
		}
		return indexes;
	}
	
	private static MethodDeclaration removeStmts(MethodDeclaration n, String containingStmt) {
		return removeStmts(n, containingStmt, CompareMode.contains);
	}

	private static MethodDeclaration removeStmts(MethodDeclaration n, String containingStmt, CompareMode compareMode) {
		BlockStmt body = n.getBody().get();


		if (testCasesToEdit.contains(n.getNameAsString())) {
			// go through the lines of code in the method and see if they contain the text we want to change
			for (int i = 0; i < body.getStatements().size(); i++) {
				Statement s = body.getStatement(i);
				if (compareMode.compare(s.toString(), containingStmt)) {
					s.remove();
					i--;

					// add the file that it came from to the list of files to save
					Node parent = n.getParentNode().get();
					filesToSave.add(parent.getChildNodes().get(0).toString() + ".java");
				}
			}
		}
		return n;
	}

	/*
	 * increments the counts of the all the nums in the array starting from int i
	 */
	private static ArrayList<Integer> incrementCounts(int i, ArrayList<Integer> incArray) {
		ArrayList<Integer> copyArray = (ArrayList<Integer>) incArray.clone();
		for (int j = i; j < incArray.size(); j++) {
			copyArray.set(j, incArray.get(j) + 1);
		}
		return copyArray;
	}

	/*
	 * replaces empty lines with a comment so that we can save them when they get parsed
	 */
	private static void replaceLinebreaks(File in) {
		String editFile = JavaHelpers.readFileAsString(in.toString())
				.replaceAll("(?m)^\\s*$", "// this is actually a line break");
		JavaHelpers.writeFile(in.toString(), editFile.toString());
	}

	/*
	 * replaces the comments put in by replaceLinebreaks(File in) with a blank line
	 */
	private static void replaceLinebreakComments(File in) {
		String editFile = JavaHelpers.readFileAsString(in.toString())
				.replaceAll("// this is actually a line break", "");
		JavaHelpers.writeFile(in.toString(), editFile.toString());
	}

	/*
	 * gets all file from a given directory, and all the files in that dir, recursively
	 */
	private static ArrayList<File> getFilesFromDir(File folder) {
		ArrayList<File> allFiles = new ArrayList<File>();
		for (File file : folder.listFiles()) {
			// can edit which suites to exclude
			boolean exclude = !file.getName().equals("abstracttestsuite") && !file.getName().equals("datageneration");
			if (file.isDirectory() && exclude) {
				allFiles.addAll(getFilesFromDir(file));
			} else if (exclude) {
				allFiles.add(file);
			}
		}
		return allFiles;
	}

	private static ArrayList<File> getFilesFromDirectories(File... folders) {
		ArrayList<File> allFiles = new ArrayList<File>();

		for (File folder : folders) {
			allFiles.addAll(getFilesFromDir(folder));
		}

		return allFiles;
	}

	private static ArrayList<String> getCasesFromFile(String filePath) {
		ArrayList<String> testCasesFromFile = null;
		try {
			testCasesFromFile = new ArrayList<>(Files.readAllLines(Paths.get(filePath)));
		} catch (IOException e) {
			System.out.println("Failed to read file. Make sure to place the file containing test cases you want to update in directory:\n"
					+ e.getMessage());
			throw new RuntimeException();
		}
		return testCasesFromFile;
	}

	/**
	 * 
	 * Returns the name of the project being used in the testcase
	 */
	public static String getProjectName(MethodDeclaration n) {
		String containingStmt = "selenium().HomePage.clickLink(\"";
		BlockStmt body = n.getBody().get();
		
		for (Statement s : body.getStatements()) {
			String statementString = s.toString();
			if (statementString.contains(containingStmt)) {
				statementString = statementString.substring(statementString.indexOf(containingStmt));
				int begin = statementString.indexOf("\"") + 1;
				int end = statementString.indexOf("\"", begin);
				return statementString.substring(begin, end);
			}
		}
		return "";
	}

	/**
	 * 
	 * @param n
	 *            method declaration
	 * @param s
	 *            String to be searched for within method
	 * @param comparer
	 *            CompareMode to be used to determine if method contains string
	 * @return
	 */
	public static boolean doesMethodContainString(MethodDeclaration n, String s, CompareMode comparer) {
		String body = n.getBody().toString();
		return comparer.compare(body, s);
	}
}