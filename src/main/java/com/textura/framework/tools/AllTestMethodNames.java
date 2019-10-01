package com.textura.framework.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import org.testng.annotations.Test;

public class AllTestMethodNames {

	/**
	 * Driver for getTestSuiteMethodNames
	 * 
	 * @param args
	 */
	public static void main(String[] args) {	
		List<String> tests = getTestSuiteMethodNames();
		for (int i = 0; i < tests.size(); i++) {
			// System.out.println(tests.get(i));
		}
		System.out.println("\n" + tests.size());

		ArrayList<String> duplicateChecker = new ArrayList<String>();
		for (String s : tests) {
			if (duplicateChecker.contains(s)) {
				System.out.println("\nDuplicate test: " + s);
			} else {
				duplicateChecker.add(s);
			}
		}
	}

	public static List<String> getTestSuiteMethodNames() {
		List<String> allMethods = getTestSuiteMethodNames("com");
		return allMethods;
	}
	
	public static List<String>getTestSuiteObsoleteMethods(String product){
		List<String> allMethods = getTestSuiteObsoleteMethodNames("com.textura." + product + ".testsuites");
		return allMethods;
	}
	
	public static List<String>getTestSuiteObsoleteMethodNames(){
		List<String> allMethods = getTestSuiteObsoleteMethodNames("com");
		return allMethods;
	}
	
	public static List<String>getTestSuiteProductionIssuesMethods(String product){
		List<String> allMethods = getTestSuiteProductionIssuesMethodNames("com.textura." + product + ".testsuites");
		return allMethods;
	}
	
	public static List<String>getTestSuiteProductionIssuesMethodNames(){
		List<String> allMethods = getTestSuiteProductionIssuesMethodNames("com");
		return allMethods;
	}
	
	public static List<String>getTestSuiteDateTimeChangeMethods(String product){
		List<String> allMethods = getTestSuiteDateTimeChangeMethodNames("com.textura." + product + ".testsuites");
		return allMethods;
	}
	
	public static List<String>getTestSuiteDateTimeChangeMethodNames(){
		List<String> allMethods = getTestSuiteDateTimeChangeMethodNames("com");
		return allMethods;
	}

	public static List<String> getDuplicateTestCases() {
		List<String> tests = getTestSuiteMethodNames();
		List<String> duplicateChecker = new ArrayList<String>();
		List<String> results = new ArrayList<String>();
		for (String s : tests) {
			if (duplicateChecker.contains(s)) {
				results.add(s);
			} else {
				duplicateChecker.add(s);
			}
		}
		return results;
	}

	/**
	 * Returns all of the test suite method names.
	 * 
	 * @return
	 */
	public static List<String> getTestSuiteMethodNames(String packageDirectory) {
		Class<?>[] classes = getClasses(packageDirectory);
		ArrayList<String> result = new ArrayList<String>();
		for (Class<?> c : classes) {
			if (c.getName().contains("_")) {
				Method[] methods = c.getMethods();
				for (Method m : methods) {
					for (Annotation a : m.getAnnotations()) {
						if (a.annotationType().getName().contains("annotations.Test") ) {
							String name = m.getName();
							if (name.startsWith("c")) {
								try {
									Integer.parseInt(name.substring(1));
									result.add(name);
								} catch (Exception e) {
								}
							}
						}
					}
				}
			}
		}
		Collections.sort(result);
		return result;
	}

	public static List<String> getTestMethodsFromClass(Class<?> c) {
		ArrayList<String> result = new ArrayList<String>();
		Method[] methods = c.getMethods();
		for (Method m : methods) {
			for (Annotation a : m.getAnnotations()) {
				if (a.annotationType().getName().contains("annotations.Test")) {
					String name = m.getName();
					if (name.startsWith("c")) {
						try {
							Integer.parseInt(name.substring(1));
							result.add(name);
						} catch (Exception e) {
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Scans all classes accessible from the context class loader which belong
	 * to the given package and subpackages.
	 * 
	 * @param packageName
	 *            The base package
	 * @return The classes
	 */
	@SuppressWarnings("rawtypes")
	public static Class<?>[] getClasses(String packageName) {
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			assert classLoader != null;
			String path = packageName.replace('.', '/');
			Enumeration<URL> resources = classLoader.getResources(path);
			List<File> dirs = new ArrayList<File>();
			while (resources.hasMoreElements()) {
				URL resource = resources.nextElement();
				dirs.add(new File(resource.getFile()));
			}
			ArrayList<Class> classes = new ArrayList<Class>();
			for (File directory : dirs) {
				classes.addAll(findClasses(directory, packageName));
			}
			return classes.toArray(new Class[classes.size()]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Recursive method used to find all classes in a given directory and
	 * subdirs.
	 * 
	 * @param directory
	 *            The base directory
	 * @param packageName
	 *            The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("rawtypes")
	private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
			}
		}
		return classes;
	}
	
	public static List<String> getTestSuiteObsoleteMethodNames(String packageDirectory) {
		Class<?>[] classes = getClasses(packageDirectory);
		ArrayList<String> result = new ArrayList<String>();
		for (Class<?> c : classes) {
			if (c.getName().contains("_")) {
				Method[] methods = c.getMethods();
				for (Method m : methods) {
					Test t = m.getAnnotation(Test.class);
					if (t != null && t.groups().length > 0) {
						if (Arrays.asList(t.groups()).contains("ObsoleteTestCases")) {
							for (Annotation a : m.getAnnotations()) {
								if (a.annotationType().getName().contains("annotations.Test")) {
									String name = m.getName();
									if (name.startsWith("c")) {
										try {
											Integer.parseInt(name.substring(1));
											result.add(name);
										} catch (Exception e) {
										}
									}
								}
							}
						}
					}
				}
			}
		}
		Collections.sort(result);
		return result;
	}
	
	public static List<String> getTestSuiteDateTimeChangeMethodNames(String packageDirectory) {
		Class<?>[] classes = getClasses(packageDirectory);
		ArrayList<String> result = new ArrayList<String>();
		for (Class<?> c : classes) {
			if (c.getName().contains("_")) {
				Method[] methods = c.getMethods();
				for (Method m : methods) {
					Test t = m.getAnnotation(Test.class);
					if (t != null && t.groups().length > 0) {
						if (Arrays.asList(t.groups()).contains("DateTimeChange")) {
							for (Annotation a : m.getAnnotations()) {
								if (a.annotationType().getName().contains("annotations.Test")) {
									String name = m.getName();
									if (name.startsWith("c")) {
										try {
											Integer.parseInt(name.substring(1));
											result.add(name);
										} catch (Exception e) {
										}
									}
								}
							}
						}
					}
				}
			}
		}
		Collections.sort(result);
		return result;
	}
	
	public static List<String> getTestSuiteProductionIssuesMethodNames(String packageDirectory) {
		Class<?>[] classes = getClasses(packageDirectory);
		ArrayList<String> result = new ArrayList<String>();
		for (Class<?> c : classes) {
			if (c.getName().contains("_")) {
				Method[] methods = c.getMethods();
				for (Method m : methods) {
					Test t = m.getAnnotation(Test.class);
					if (t != null && t.groups().length > 0) {
						if (Arrays.asList(t.groups()).contains("ProductionIssues")) {
							for (Annotation a : m.getAnnotations()) {
								if (a.annotationType().getName().contains("annotations.Test")) {
									String name = m.getName();
									if (name.startsWith("c")) {
										try {
											Integer.parseInt(name.substring(1));
											result.add(name);
										} catch (Exception e) {
										}
									}
								}
							}
						}
					}
				}
			}
		}
		Collections.sort(result);
		return result;
	}
	
	public static void addTestCaseToObsoleteTestCasesGroup(String filePath, String product){
		 setGroupForTestCaseInProject(filePath, "ObsoleteTestCases", "add", product);
	}
	
	public static void addTestCaseToProductionIssuesGroup(String filePath, String product){
		 setGroupForTestCaseInProject(filePath, "ProductionIssues", "add", product);
	}
	
	public static void addTestCaseToDateTimeChangeGroup(String filePath, String product){
		 setGroupForTestCaseInProject(filePath, "DateTimeChange", "add", product);
	}
	
	public static void addTestCaseToExternalExecGroup(String filePath, String product){
		 setGroupForTestCaseInProject(filePath, "ExternalExec.All", "add", product);
	}
	
	public static void addTestCaseToCustomGroup(String filePath, String groupName, String product){
		 setGroupForTestCaseInProject(filePath, groupName, "add", product);
	}
	
	public static void removeTestCaseFromObsoleteTestCasesGroup(String filePath, String product){
		 setGroupForTestCaseInProject(filePath, "ObsoleteTestCases", "remove", product);
	}
	
	public static void removeTestCaseFromProductionIssuesGroup(String filePath, String product){
		 setGroupForTestCaseInProject(filePath, "ProductionIssues", "remove", product);
	}
	
	public static void removeTestCaseFromDateTimeChangeGroup(String filePath, String product){
		 setGroupForTestCaseInProject(filePath, "DateTimeChange", "remove", product);
	}
	
	public static void removeTestCaseFromExternalExecGroup(String filePath, String product){
		 setGroupForTestCaseInProject(filePath, "ExternalExec.All", "remove", product);
	}
	
	public static void removeTestCaseFromCustomGroup(String filePath, String groupName, String product){
		 setGroupForTestCaseInProject(filePath, groupName, "remove", product);
	}
	
	public static void setGroupForTestCaseInProject(String filePath, String groupName, String AddOrRemove, String product) {
		String path = "";
		BufferedWriter writer = null;
		List<String> testCases = null;
		List<String> current = null;
		List<String> notFoundCases = new ArrayList<>();
		List<String> failedToReadCases = new ArrayList<>();
		boolean hit = false;
		boolean found = false;
		boolean failedToRead = false;
		try {
			testCases = new ArrayList<>(Files.readAllLines(Paths.get(filePath)));
		} catch (IOException e) {
			System.out.println(
					"Failed to read file. Make sure to place the file containing test cases you want to update in directory:\n"
							+ e.getMessage());
			throw new RuntimeException();
		}
		Class<?>[] classes = getClasses("com.textura." + product + ".testsuites");
		for (String testCase : testCases) {
			classLoop: for (Class<?> c : classes) {
				if (c.getName().contains("_")) {
					Method[] methods = c.getMethods();
					for (Method m : methods) {
						Test t = m.getAnnotation(Test.class);
						if (t != null) {
							for (Annotation a : m.getAnnotations()) {
								if (a.annotationType().getName().contains("annotations.Test")) {
									String name = m.getName();
									if (name.equals(testCase)) {
										found = true;
										try {
											path = "C://Automation//Textura//CPM//src//test//java//"
													+ c.getName().replaceAll("\\.", "//") + ".java";

											try {
												current = new ArrayList<>(
														Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8));
											} catch (Exception e) {
												current = new ArrayList<>(Files.readAllLines(Paths.get(path),
														StandardCharsets.ISO_8859_1));
											}

											int index = 0;
											StringBuilder s = new StringBuilder();
											for (String line : current) {
												index++;
												s.append(line + "\n");
												if (hit == false) {
													if (current.get(index + 2).contains(testCase + "()")) {
														if (current.get(index + 1).contains("@Author")) {
															if (current.get(index).contains("@Test(groups")) {
																if (AddOrRemove.equals("add")) {
																	if(!current.get(index).contains(groupName)){
																	current.set(index, current.get(index).replaceAll(
																			"\"(?!.*\")", "\", \"" + groupName + "\""));
																	System.out.println(testCase + " marked as: "
																			+ current.get(index));
																}else{
																	System.out.println(testCase + " stays as: "
																			+ current.get(index + 1) + "\n already part of group trying to be added.");
																}
																}else {
																	current.set(index, current.get(index).replaceAll(
																			",? \"" + groupName + "\",?", ""));
																	if (current.get(index)
																			.contains("@Test(groups = { })")) {
																		current.set(index, current.get(index)
																				.replaceAll("@Test.*", "@Test"));
																	}
																	System.out.println(testCase + " marked as: "
																			+ current.get(index));
																}
															} else {
																if (AddOrRemove.equals("add")) {
																	if(!current.get(index).contains(groupName)){
																current.set(index, current.get(index).replaceAll(
																		"@Test.*",
																		"@Test(groups = { \"" + groupName + "\" })"));
																System.out.println(
																		testCase + " marked as: " + current.get(index));
																}else{
																	System.out.println(testCase + " stays as: "
																			+ current.get(index + 1) + "\n already part of group trying to be added.");
																}
																}else{
																	System.out.println(testCase + " stays as: "
																			+ current.get(index) + "\n(Does not belong to any Groups already)");
																}
															}
															hit = true;
														} else {
															if (current.get(index + 1).contains("@Test(groups")) {
																if (AddOrRemove.equals("add")) {
																	if(!current.get(index + 1).contains(groupName)){
																	current.set(index + 1,
																			current.get(index + 1).replaceAll(
																					"\"(?!.*\")", "\", \"" + groupName
																							+ "\""));
																	System.out.println(testCase + " marked as: "
																			+ current.get(index + 1));
																	}else{
																		System.out.println(testCase + " stays as: "
																				+ current.get(index + 1) + "\n already part of group trying to be added.");
																	}
																}else {
																	current.set(index + 1,
																			current.get(index + 1).replaceAll(
																					",? \"" + groupName + "\",?", ""));
																	if (current.get(index + 1)
																			.contains("@Test(groups = { })")) {
																		current.set(index + 1, current.get(index + 1)
																				.replaceAll("@Test.*", "@Test"));
																		System.out.println(testCase + " marked as: "
																				+ current.get(index + 1));
																	}
																	System.out.println(testCase + " stays as: "
																			+ current.get(index + 1) + "\n(Group format not matching regex, confirm spaces.)");
																}
															} else {
																if (AddOrRemove.equals("add")) {
																	if(!current.get(index + 1).contains(groupName)){
																current.set(index + 1,
																		current.get(index + 1).replaceAll("@Test.*",
																				"@Test(groups = { \"" + groupName
																						+ "\" })"));
																System.out.println(testCase + " marked as: "
																		+ current.get(index + 1));
																}else{
																	System.out.println(testCase + " stays as: "
																			+ current.get(index + 1) + "\n already part of group trying to be added.");
																}
																}else{
																	System.out.println(testCase + " stays as: "
																			+ current.get(index + 1) + "\n(Does not belong to any Groups already)");
																}
															}
															hit = true;
														}
													}
												}
											}
											try {
												writer = new BufferedWriter(new FileWriter(path), s.length() + 100);
												writer.write(s.toString());
												writer.close();
												break classLoop;
											} catch (Exception e) {
												writer.close();
												System.out.println(e.getMessage());
											}

										} catch (MalformedInputException e) {
											failedToRead = true;
										} catch (Exception e) {
											System.out.println(e.getMessage());
										}
									}
								}
							}
						}
					}
				}
			}
			if (hit == false && found == false) {
				notFoundCases.add(
						testCase + " not found in your project. Test case was not changed, are you sure it exists?");
			}
			if (failedToRead == true) {
				failedToReadCases.add(testCase + " failed to read file: " + path + ". Update Manually.");
			}
			hit = false;
			found = false;
			failedToRead = false;
		}
		for (String test : notFoundCases) {
			System.out.println(test);
		}
		for (String test : failedToReadCases) {
			System.out.println(test);
		}
	}
}
