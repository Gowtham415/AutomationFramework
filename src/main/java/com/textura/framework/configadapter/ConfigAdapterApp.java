package com.textura.framework.configadapter;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import com.textura.framework.environment.EnvironmentInfo;
import com.textura.framework.environment.EnvironmentReader;
import com.textura.framework.environment.Project;
import com.textura.framework.testng.TestSuitesBuilderApp;
import com.textura.framework.testng.TestngConfig;
import com.textura.framework.testng.TestngFiles;
import com.textura.framework.testng.TestngGroups;
import com.textura.framework.testng.TestngSuiteType;
import com.textura.framework.testrail.TestRailAPI;
import com.textura.framework.testrail.TestRailSupport;
import com.textura.framework.utils.JavaHelpers;
import com.textura.framework.utils.XmlFileBuilder;

public class ConfigAdapterApp extends ConfigAdapter {

	// TestNG related
	protected TestngSuiteType suiteType;

	protected EnvironmentInfo environmentInfo;
	public ConfigComponents component;

	public ConfigAdapterApp(ConfigComponents component, EnvironmentInfo environmentInfo) {

		testSuitesCreator = new TestSuitesBuilderApp();
		tng = new TestngConfig();

		appPath = component.pathProject();
		tng.component = component;

		Project.setProduct(component);
		this.environmentInfo = environmentInfo;
		this.component = component;
	}

	@Override
	public void readSettingsFromEnv() {

		populateFromEnvironmentVariables(environmentInfo);

		String path = Project.templates(configFileName);
		document = XmlFileBuilder.readXmlFile(path);

		environmentInfo.jenkinsBuild = "true";
		if (environmentInfo.gridPort == null) {
			environmentInfo.gridPort = document.selectSingleNode("//automation").valueOf("GridPort");
		}

		// environmentInfo.gridServer = "DFWIN7QAAUTO75";

		environmentInfo.gridURL = "http://" + environmentInfo.gridServer + ":" + environmentInfo.gridPort + "/wd/hub";
		// environmentInfo.gridURL = "http://DFWIN7QAAUTO22:" + environmentInfo.gridPort + "/wd/hub";
		// System.out.println("I am going through gridURL set up");
		// support windows and linux
		if (System.getProperty("os.name").contains("indows")) {
			environmentInfo.testFile = env.get(tng.component + "\\" + TestngFiles.FAILED.getFileName());
		} else {
			environmentInfo.testFile = env.get(tng.component + "/" + TestngFiles.FAILED.getFileName());
		}

		environmentInfo.product = tng.component.name();

		environmentInfo.testrailProjectID = TestRailAPI.getProjectIDfromProduct(tng.component);
		EnvironmentReader.readXMLIntoEnvironmentInfo(Project.environmentXML(), environmentInfo.testbed, environmentInfo);
	}

	@Override
	public void readSettingsFromEnvPQM() {
		populateFromEnvironmentVariables(environmentInfo);

		String path = Project.templates(configFileName);
		document = XmlFileBuilder.readXmlFile(path);

		environmentInfo.jenkinsBuild = "true";
		if (environmentInfo.gridPort == null) {
			environmentInfo.gridPort = document.selectSingleNode("//automation").valueOf("GridPort");
		}

		environmentInfo.gridURL = "http://" + environmentInfo.gridServer + ":" + environmentInfo.gridPort + "/wd/hub";
		// environmentInfo.gridURL = "http://DFWIN7QAAUTO22:" + environmentInfo.gridPort + "/wd/hub";
		// System.out.println("I am going through gridURL set up");
		environmentInfo.testFile = env.get(tng.component + "\\" + TestngFiles.FAILED.getFileName());
		environmentInfo.product = tng.component.name();

		environmentInfo.testrailProjectID = TestRailAPI.getProjectIDfromProduct(tng.component);
		EnvironmentReader.readXMLIntoEnvironmentInfo(Project.environmentXML(), environmentInfo.testbed, environmentInfo);
	}

	@Override
	public void readSettingsFromFile() {

		populateFromTestConfig(environmentInfo);

		environmentInfo.product = tng.component.name();
		EnvironmentReader.readXMLIntoEnvironmentInfo(Project.environmentXML(), environmentInfo.testbed, environmentInfo);
	}

	@Override
	public void validateSettings() {
		System.out.println(environmentInfo);

		if (environmentInfo.testbed == null || environmentInfo.testbed.equals("Select")) {
			throw new IllegalArgumentException("Test Environment cannot be blank!\n");
		}
		if (environmentInfo.testRunMode != null && environmentInfo.testRunMode.equals("true") && environmentInfo.testRunID == null) {
			throw new IllegalArgumentException("testRunID cannot be empty in TestRunMode!");
		}
		if (environmentInfo.testRunMode != null && environmentInfo.testRunMode.equals("false") && environmentInfo.testRunID != null
				&& environmentInfo.testRunID.length() != 0) {
			throw new IllegalArgumentException("TestRunMode needs to be checked in order to run testRunID!");
		}
		if (environmentInfo.testMilestoneMode != null
				&& (environmentInfo.testMilestoneMode.equals("true") && environmentInfo.testMilestoneID == null)) {
			throw new IllegalArgumentException("TestMilestoneID cannot be empty in TestMilestoneMode!");
		}
		if (environmentInfo.testMilestoneMode != null
				&& (environmentInfo.testMilestoneMode.equals("false") && environmentInfo.testMilestoneID != null
						&& environmentInfo.testMilestoneID
								.length() != 0)) {
			throw new IllegalArgumentException("TestMilestoneMode needs to be checked in order to run testMilestoneID!");
		}
		if (environmentInfo.testRerunMode != null && (environmentInfo.testRerunMode.equals("true")
				&& environmentInfo.testRerunID == null)) {
			throw new IllegalArgumentException("TestRerunID cannot be empty in TestRerunMode!");
		}
		if (environmentInfo.testRerunMode != null
				&& (environmentInfo.testRerunMode.equals("false") && environmentInfo.testRerunID != null && environmentInfo.testRerunID
						.length() != 0)) {
			throw new IllegalArgumentException("TestRerunMode needs to be checked in order to run testRerunID!");
		}
		if (environmentInfo.testFile != null && environmentInfo.testFile.length() != 0 && !environmentInfo.testFile.contains("testng-")) {
			throw new IllegalArgumentException("Invalid test file, attached file: " + environmentInfo.testFile
					+ " does not include failed test cases!");
		}
		if (environmentInfo.testCases != null && (environmentInfo.testFile != null || environmentInfo.testRerunID != null)) {
			throw new IllegalArgumentException("TestCases cannot be executed with TestFile or in TestRerunMode!");
		}
		if (environmentInfo.testFile != null && (environmentInfo.testCases != null || environmentInfo.testRerunID != null)) {
			throw new IllegalArgumentException("TestFile cannot be executed when TestCases or TestRerun is selected!");
		}
		if (environmentInfo.testRerunID != null && (environmentInfo.testCases != null || environmentInfo.testFile != null)) {
			throw new IllegalArgumentException("TestRerun cannot be executed when TestCases or TestFile is selected!");
		}
		if (environmentInfo.testMilestoneMode != null && (environmentInfo.testMilestoneMode.equals("true"))) {
			environmentInfo.testSuites = "testMilestoneMode";
		}
		if (environmentInfo.testRunMode != null && environmentInfo.testRunMode.equals("true")) {
			if (TestRailAPI.isIDATestPlanID(environmentInfo.testRunID) == true) {
				System.out.println("TestPlan Mode for ID " + environmentInfo.testRunID);
				environmentInfo.testSuites = TestRailSupport.getTestSuiteNamesFromTestPlanID(environmentInfo.testRunID);
				environmentInfo.testRunID = TestRailSupport.getTestRailAllTestRunIDsFromTestPlanID(environmentInfo.testRunID);
			} else if (TestRailAPI.isIDATestRunID(environmentInfo.testRunID) == true) {
				System.out.println("TestRun Mode for ID " + environmentInfo.testRunID);
				environmentInfo.testSuites = TestRailSupport.getTestSuiteNamesFromTestRunID(environmentInfo.testRunID);
			} else {
				throw new IllegalArgumentException("Invalid TestPlan or TestRun or Milestone ID!");
			}
		}
		if (environmentInfo.testRerunMode != null && (environmentInfo.testRerunMode.equals("true"))) {
			environmentInfo.testSuites = "testRerunMode";
		}
		if (environmentInfo.testCases != null) {
			environmentInfo.testSuites = "testCases";
		}
		suiteType = TestngSuiteType.DEFAULT_DOUBLE;
		if (environmentInfo.testFile == null && environmentInfo.testGroups != null
				&& environmentInfo.testGroups.equals(TestngGroups.DEFAULT.getName())) {

			if (environmentInfo.gridMode.equals("false")
					|| (environmentInfo.gridMode.equals("true") && environmentInfo.gridNodes.equals("1"))) {

				suiteType = TestngSuiteType.DEFAULT_DOUBLE; // DEFAULT_SINGLE

			} else if (environmentInfo.gridMode.equals("true") && !environmentInfo.gridNodes.equals("1")) {

				suiteType = TestngSuiteType.DEFAULT_DOUBLE;
			}

		} else if (environmentInfo.testFile != null) {

			suiteType = TestngSuiteType.FAILED;

		} else {
			suiteType = TestngSuiteType.CUSTOM;
		}

		if (environmentInfo.testFile == null && environmentInfo.testGroups != null
				&& (environmentInfo.testSuites.equals("application.performance.PerformanceTests_POSTS_1_2068")
						|| environmentInfo.testSuites.equals("application.performance.PerformanceTests_GETS_1_2068"))) {
			suiteType = TestngSuiteType.DEFAULT_SINGLE;
		}
		if (environmentInfo.testRerun != null && environmentInfo.testRerun.equals("true")) {
			suiteType = TestngSuiteType.RERUN;
		}
	}

	@Override
	public void createSettingsFile() {
		document = XmlFileBuilder.readXmlFile(Project.templates(configFileName));
		Field[] fields = EnvironmentInfo.class.getDeclaredFields();
		for (Field f : fields) {
			List<?> list = document.selectNodes("//" + StringUtils.capitalize(f.getName())); // find corresponding node in xml config
			if (list.size() < 1) {
				Node n = document.selectSingleNode("//environment"); // if config does not have the element, create one under environment element
				((Element) n).addElement(StringUtils.capitalize(f.getName()));
			} else {
				try {
					Object value = environmentInfo.getClass().getField(f.getName()).get(environmentInfo); // get current value of the attribute using reflection
					if (value != null) {
						((Element) list.get(0)).setText(value.toString()); // set the xml node text to the value if not null
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		XmlFileBuilder.writeXmlFile(appPath + configFileName, document);
		savePropertiesFile();
	}

	public void printSettings() {
		XmlFileBuilder.printPrettyXmlFile(document);
	}

	public void cleanEnv() {

	}

	@Override
	public TestngConfig getTngConfig() {
		tng.suiteType = suiteType;
		tng.output = appPath;
		tng.tngThreads = environmentInfo.gridNodes;
		tng.tngGroups = environmentInfo.testGroups;
		tng.tngSuites = environmentInfo.testSuites;
		return tng;
	}

	public void savePropertiesFile() {

		// save these settings temporarily before modifying testenv
		String newLine = System.getProperty("line.separator");
		String propFile = Project.pathFramework() + "selenium.properties";
		String propContent = "browser = Firefox" + newLine + "timeout = 30" + newLine + "testEnvironment = " + environmentInfo.testbed
				+ newLine + "userPassword = " + environmentInfo.userPassword + newLine + "automationGridMode = " + environmentInfo.gridMode
				+ newLine + "automationGridServer = " + environmentInfo.gridServer + newLine + "automationGridNumOfNodes = "
				+ environmentInfo.gridNodes + newLine + "automationGridPort = " + environmentInfo.gridPort + newLine
				+ "automationGridURL = " + environmentInfo.gridURL + newLine + "suitesInPlan = " + environmentInfo.testSuites + newLine
				+ "testRunMode = " + environmentInfo.testRunMode + newLine + "testRunIDs = " + environmentInfo.testRunID + newLine
				+ "testrailProjectID = " + "" + newLine + "codeBranch = " + environmentInfo.codeBranch + newLine + "codeRevision = "
				+ environmentInfo.codeRevision + newLine + "testNotes = " + environmentInfo.testNotes + newLine + "product = "
				+ environmentInfo.product + newLine + "sampleSize = " + environmentInfo.sampleSize + newLine + "logLevel = "
				+ environmentInfo.logLevel;
		try {
			JavaHelpers.writeFile(propFile, propContent);
		} catch (Exception e) {
			System.out.println("Error writing to " + propFile);
			e.printStackTrace();
		}
	}

	@Override
	public EnvironmentInfo getEnvironmentInfo() {
		return environmentInfo;
	}

	/**
	 * Reads data from the environment variables specified by the fieldMap and feeds it into the given EnvironmentInfo object
	 * 
	 * @param environmentInfo
	 */
	protected void populateFromEnvironmentVariables(EnvironmentInfo environmentInfo) {
		final Map<String, String> env = System.getenv();
		setFields(new FieldGetter() {

			@Override
			public String getField(String field) {
				return env.get(StringUtils.capitalize(field));
			}
		});
	}

	protected void populateFromTestConfig(EnvironmentInfo environmentInfo) {
		final String path = Project.path() + "testconfig.xml";
		final Document document = XmlFileBuilder.readXmlFile(path);
		setFields(new FieldGetter() {

			@Override
			public String getField(String field) { // given a field name, return the value in the xml config
				Node n = document.selectSingleNode("//" + StringUtils.capitalize(field));
				if (n == null) {
					return null;
				}
				return n.getText();
			}
		});
	}

	/**
	 * This sets the values in the environmentInfo object. For each attribute specified in fieldMap, use FieldGetter to retrieve the corresponding value. FieldGetter decides where
	 * the value comes from, whether an xml config file or environment variables.
	 * 
	 * @param valueGetter
	 *            An object that returns a value for a corresponding field name
	 */
	private void setFields(FieldGetter valueGetter) {

		Field[] fields = EnvironmentInfo.class.getDeclaredFields();
		for (Field environmentInfoField : fields) {
			String name = environmentInfoField.getName();
			if (name == null || name.equals("null") || name.length() == 0 || valueGetter.getField(name) == null
					|| valueGetter.getField(name).equals("")) {
				continue;
			}
			try {
				if (environmentInfoField.getType().equals(Long.class)) {
					environmentInfoField.set(environmentInfo, Long.parseLong(valueGetter.getField(name)));
				} else if (environmentInfoField.getType().equals(Integer.class)) {
					environmentInfoField.set(environmentInfo, Integer.parseInt(valueGetter.getField(name)));
				} else {
					environmentInfoField.set(environmentInfo, valueGetter.getField(name));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Implementor returns the value of a given field through getField
	 * 
	 */
	public interface FieldGetter {

		public String getField(String f);
	}
}
