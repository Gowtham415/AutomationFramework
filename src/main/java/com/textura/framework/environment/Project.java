package com.textura.framework.environment;

import java.io.File;
import java.io.IOException;
import com.textura.framework.configadapter.ConfigComponents;
import com.textura.framework.objects.main.Page;

public class Project {

	private static String fileSeperator = System.getProperty("file.separator");
	private static ConfigComponents product;
	private static String root;

	public static void setProduct(ConfigComponents product_) {
		product = product_;
		root = pathWorkspace() + transformPath(product.name() + "/");
	}

	public static String environmentXML() {
		return path() + transformPath("environment.xml");
	}

	/**
	 * @return Absolute path to Automation folder
	 */
	private static final String pathAutomation() {
		String automationPath = "";
		String OSName = System.getProperty("os.name");
		try {
			if (OSName.startsWith("Windows"))
				automationPath = "C:/Automation/";
			else if (OSName.startsWith("Linux"))
				automationPath = "/Automation/";
			else if (OSName.startsWith("Mac"))
				automationPath = "/Automation/";
			else
				throw new IOException();
		} catch (IOException e) {
			System.err.println("Error getting Automation Path!");
			e.printStackTrace();
			automationPath = null;
		}
		return automationPath;
	}

	/**
	 * @return Absolute path to Automation Workspace
	 */
	public static final String pathWorkspace() {
		return transformPath(pathAutomation() + "Textura/");
	}

	/**
	 * @return Absolute path to Automation Repository
	 */
	public static final String pathRepository(String filePath) {
		return transformPath(pathAutomation() + "repository/" + filePath);
	}

	/**
	 * Transforms the subpath to an OS independent format
	 */
	public static String transformPath(String subPath) {
		if (subPath.contains("\\")) {
			Page.printFormattedMessage("Backslash is not allowed in path!");
			return null;
		} else {
			String convertedSubPath = subPath.replace("/", fileSeperator);
			return convertedSubPath;
		}
	}

	public static String pathFramework() {
		return pathWorkspace() + transformPath("Framework/");
	}

	public static String testResources(String filePath) {
		String testResources = "src/test/resources/" + filePath;
		return path() + transformPath(testResources);
	}

	public static String dataFiles(String filePath) {
		String dataFiles = "src/test/resources/datafiles/";
		return path() + transformPath(dataFiles + filePath);
	}

	public static String notifications(String filePath) {
		String notifications = "src/test/resources/notifications/";
		return path() + transformPath(notifications + filePath);
	}

	public static String documents(String filePath) {
		String documents = "src/test/resources/documents/";
		return path() + transformPath(documents + filePath);
	}

	public static String queryresults(String filePath) {
		String queryresults = "src/test/resources/queryresults/";
		return path() + transformPath(queryresults + filePath);
	}

	public static String templates(String filePath) {
		String templates = "src/main/java/com/textura/framework/templates/";
		return pathFramework() + transformPath(templates + filePath);
	}

	public static String tools(String filePath) {
		String tools = "src/main/java/com/textura/framework/tools/";
		return pathFramework() + transformPath(tools + filePath);
	}

	public static String testsuites(String filePath) {
		String testsuites = "src/test/java/com/textura/" + product + "/testsuites/";
		return path() + transformPath(testsuites + filePath);
	}

	public static String scripts(String filePath) {
		String scripts = "src/main/java/com/textura/framework/utils/scripts/";
		return pathFramework() + transformPath(scripts + filePath);
	}

	public static String environment(String filePath) {
		String environment = "src/main/java/com/textura/framework/environment/";
		return pathFramework() + transformPath(environment + filePath);
	}

	public static String downloads(String filePath) {
		String downloads = "target/test-downloads/";
		File f = new File(path() + transformPath("target/test-downloads"));
		f.mkdirs();
		return path() + transformPath(downloads + filePath);
	}

	public static String screenshots(String filePath) {
		String screenshots = "target/test-screenshots/";
		File f = new File(path() + transformPath("target/test-screenshots"));
		f.mkdirs();
		return path() + transformPath(screenshots + filePath);
	}

	public static String dataFiles() {
		return path() + transformPath("src/test/resources/datafiles/");
	}

	public static String dataGeneration() {
		return dataFiles() + transformPath("datagenerationfiles/");
	}

	public static String manualverification() {
		return path() + transformPath("manualverification/");
	}

	public static String path() {
		return root;
	}

	public static String screenShots() {
		return path() + transformPath("target/test-screenshots");
	}

	public static String executableExtension() {
		String ext = "";
		String OSName = System.getProperty("os.name");

		if (OSName.startsWith("Windows"))
			ext = ".exe";

		return ext;
	}

	public static String pathAutomationArtifacts(String relativeFilePath) {
		return pathAutomationArtifacts() + transformPath(relativeFilePath);
	}

	public static String pathAutomationArtifacts() {
		String OSName = System.getProperty("os.name");

		if (OSName.startsWith("Windows"))
			return transformPath("C:/Automation_artifacts/");
		else
			return transformPath("/Automation_artifacts/");
	}
}
