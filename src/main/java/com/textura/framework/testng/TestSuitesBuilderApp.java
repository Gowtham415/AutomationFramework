package com.textura.framework.testng;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import com.textura.framework.environment.Project;
import com.textura.framework.utils.JavaHelpers;
import com.textura.framework.utils.XmlFileBuilder;

public class TestSuitesBuilderApp implements TestSuitesBuilder {

	protected TestngConfig tng;

	@Override
	public void createTestSuites(TestngConfig cfg) {

		tng = cfg;
		System.out.println("Create Test suites type " + tng.suiteType);

		switch (cfg.suiteType) {

		case DEFAULT_DOUBLE:
			createSuiteDefaultDouble();
			break;
		case DEFAULT_SINGLE:
			createSuiteDefaultSingle();
			break;
		case FAILED:
			createSuiteFailed();
			break;
		case CUSTOM:
			createSuiteCustom();
			break;
		case RERUN:
			createRerunSuite();
			break;
		}
	}

	public File[] sortFiles(File[] files) {
		Arrays.sort(files, new Comparator<File>() {

			public int compare(File f1, File f2) {
				long result = f2.lastModified() - f1.lastModified();
				if (result > 0) {
					return 1;
				} else if (result < 0) {
					return -1;
				} else {
					return 0;
				}
			}
		});
		return files;
	}

	public void createRerunSuite() {
		File[] builds = sortFiles(new File(Project.pathAutomationArtifacts()).listFiles());
		if (builds.length < 1) {
			throw new RuntimeException("No previous builds found in " + Project.pathAutomationArtifacts());
		}
		File failedXML = new File(builds[0], "surefire-reports/testng-failed.xml");
		if (!failedXML.exists()) {
			throw new RuntimeException("Previous run xml could not be found: " + failedXML.getAbsolutePath());
		}
		try {
			String s = FileUtils.readFileToString(failedXML);
			if (s.contains("thread-count=")) {
				s = s.replaceAll("thread-count=\"\\d*\"", "thread-count=\"" + tng.tngThreads + "\"");
			} else {
				System.out.println("Rerun xml did not have thread-count specified: " + failedXML.getAbsolutePath());
			}
			File projectPath = new File(tng.output);
			File output = new File(projectPath, TestngFiles.MAIN.getFileName());
			FileUtils.write(output, s, false);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void createSuiteDefaultDouble() {

		createTSMain(TestngProfilesSuiteFile.DEFAULT);
		createTSExternal(TestngProfilesClassFiles.DEFAULT_EXTERNAL);
		createTSLocal(TestngProfilesClassFiles.DEFAULT_LOCAL);
	}

	public void createSuiteDefaultSingle() {

		createTSMain(TestngProfilesSuiteFile.EXTERNAL);
		createTSExternal(TestngProfilesClassFiles.DEFAULT_EXTERNAL);
	}

	public void createSuiteFailed() {
		try {
			// update thread-count with variable passed from environment
			File failedXML = new File(tng.output + "/" + TestngProfilesSuiteFile.FAILED.getFiles());
			String s = FileUtils.readFileToString(failedXML);
			if (s.contains(" thread-count=")) {
				s = s.replaceAll(" thread-count=\"\\d*\"", " thread-count=\"" + tng.tngThreads + "\" ");
			} else {
				System.out.println("Failed xml did not have thread-count specified, setting thread-count to: " + tng.tngThreads);
				s = s.replaceAll("<suite ", "<suite thread-count=\"" + tng.tngThreads + "\" ");
				}
			FileUtils.write(failedXML, s, false);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		createTSMain(TestngProfilesSuiteFile.FAILED);
	}

	public void createSuiteCustom() {

		createTSMain(TestngProfilesSuiteFile.CUSTOM);
		createTSCustom(TestngProfilesClassFiles.CUSTOM);
	}

	public void createTSMain(TestngProfilesSuiteFile profile) {

		Document document = XmlFileBuilder.readXmlFile(Project.templates(TestngFiles.MAIN.getTemplateName()));

		Element root = document.getRootElement();
		root.addAttribute(TestngAttributes.NAME.getName(), profile.getSuiteName());

		setSuiteFiles(document, profile.getFiles());

		XmlFileBuilder.printPrettyXmlFile(document);
		XmlFileBuilder.writeXmlFile(tng.output + TestngFiles.MAIN.getFileName(), document);

	}

	public void createTSExternal(TestngProfilesClassFiles profile) {

		Document document = XmlFileBuilder.readXmlFile(Project.templates(TestngFiles.EXTERNAL.getTemplateName()));

		Element root = document.getRootElement();
		root.addAttribute(TestngAttributes.NAME.getName(), profile.getSuiteName());
		root.addAttribute(TestngAttributes.THREAD_COUNT.getName(), tng.tngThreads);
		root.addAttribute(TestngAttributes.PARALLEL.getName(), TestngParallelRunMode.METHODS.getRunMode());

		setClasses(document, tng.tngSuites);

		XmlFileBuilder.printPrettyXmlFile(document);
		XmlFileBuilder.writeXmlFile(tng.output + TestngFiles.EXTERNAL.getFileName(), document);
	}

	public void createTSLocal(TestngProfilesClassFiles profile) {

		Document document = XmlFileBuilder.readXmlFile(Project.templates(TestngFiles.LOCAL.getTemplateName()));

		Element root = document.getRootElement();
		root.addAttribute(TestngAttributes.NAME.getName(), profile.getSuiteName());
		root.addAttribute(TestngAttributes.THREAD_COUNT.getName(), "1");
		root.addAttribute(TestngAttributes.PARALLEL.getName(), TestngParallelRunMode.FALSE.getRunMode());

		setClasses(document, tng.tngSuites);

		XmlFileBuilder.printPrettyXmlFile(document);
		XmlFileBuilder.writeXmlFile(tng.output + TestngFiles.LOCAL.getFileName(), document);
	}

	public void createTSCustom(TestngProfilesClassFiles profile) {

		Document document = XmlFileBuilder.readXmlFile(Project.templates(TestngFiles.CUSTOM.getTemplateName()));

		Element root = document.getRootElement();
		root.addAttribute(TestngAttributes.NAME.getName(), profile.getSuiteName());
		root.addAttribute(TestngAttributes.THREAD_COUNT.getName(), "1");
		root.addAttribute(TestngAttributes.PARALLEL.getName(), TestngParallelRunMode.FALSE.getRunMode());

		setClasses(document, tng.tngSuites);

		if (tng.tngGroups != null)
			setGroups(document, tng.tngGroups);

		XmlFileBuilder.printPrettyXmlFile(document);
		XmlFileBuilder.writeXmlFile(tng.output + TestngFiles.CUSTOM.getFileName(), document);
	}

	public Document setSuiteFiles(Document document, String files) {

		List<?> list = document.selectNodes("//suite-files");
		List<String> entity = convertCsvToList(files);
		Element element;

		for (int i = 0; i < entity.size(); i++) {
			element = ((Element) list.get(0)).addElement(TestngElements.SUITE_FILE.getName());
			element.addAttribute(TestngAttributes.PATH.getName(), entity.get(i));
		}
		return document;
	}

	public Document setGroups(Document document, String groups) {

		List<?> list = document.selectNodes("//run");
		List<String> entity = convertCsvToList(groups);
		Element element;

		for (int i = 0; i < entity.size(); i++) {
			element = ((Element) list.get(0)).addElement(TestngElements.INCLUDE.getName());
			element.addAttribute(TestngAttributes.NAME.getName(), entity.get(i));
		}
		return document;
	}

	public Document setClasses(Document document, String suites) {

		List<?> list = document.selectNodes("//classes");
		List<String> entity = convertCsvToList(suites);
		Element element;

		for (int i = 0; i < entity.size(); i++) {
			element = ((Element) list.get(0)).addElement(TestngElements.CLASS.getName());
			if (entity.get(i).startsWith("textura.")) {
				element.addAttribute(TestngAttributes.NAME.getName(), "com." + entity.get(i));
			} else {
				element.addAttribute(TestngAttributes.NAME.getName(), "com.textura." + tng.component.getComponent().toLowerCase()
						+ ".testsuites." + entity.get(i));
			}
		}
		return document;
	}

	public static List<String> convertCsvToList(String csv) {

		return Arrays.asList(csv.split(","));
	}

	@Override
	public void createTestSuites(String threadNumber, String classes, String xmlFullFileName) {

		String path = Project.pathAutomationArtifacts("LogAnalyzer");
		createSuiteFileCustom(threadNumber, classes, path, xmlFullFileName);
	}

	public void createTestSuiteMain(String suiteFileName, String outputPath) {

		String suiteXmlName = TestngSuiteFiles.MAIN.getFileName();
		String suiteXmlFile = JavaHelpers.readFileAsString(Project.templates(TestngSuiteFiles.MAIN_TEMPLATE.getFileName()));
		String suiteFiles;

		suiteFiles = createXMLSuiteFileNames();
		suiteXmlFile = TestngTags.setXMLSuiteName(suiteXmlFile, suiteXmlName);
		suiteXmlFile = TestngTags.setXMLSuiteFileNames(suiteXmlFile, suiteFiles);

		System.out.println("Automation Suites xml file:\n\n" + suiteXmlFile);
		JavaHelpers.writeFile(outputPath + File.separator + "testng-suite.xml", suiteXmlFile);
	}

	public void createCPMSuiteFileExternal(String threadCount, String classes, String outputPath) {

		String suiteName = TestngProfilesClassFiles.DEFAULT_EXTERNAL.getSuiteName();
		String suiteTestName = TestngProfilesClassFiles.DEFAULT_EXTERNAL.getTestName();
		String suite = JavaHelpers.readFileAsString(Project.templates(TestngSuiteFiles.EXTERNAL_TEMPLATE.getFileName()));
		String groups;

		groups = createXMLGroupsExternal();

		suite = TestngTags.setXMLSuiteThreadCount(suite, threadCount);
		suite = TestngTags.setXMLSuiteName(suite, suiteName);
		suite = TestngTags.setXMLSuiteTestName(suite, suiteTestName);
		suite = TestngTags.setXMLSuiteParallel(suite, TestngTags.XML_PARALLEL_METHODS);
		suite = TestngTags.setXMLSuiteGroups(suite, groups);
		suite = TestngTags.setXMLSuiteClasses(suite, classes);

		System.out.println("Automation Suite External Test Cases:\n\n" + suite);
		JavaHelpers.writeFile(outputPath + File.separator + "testng-failed.xml", suite);
	}

	public void createSuiteFileLocal(String threadCount, String classes, String outputPath) {

		String suiteName = "Automation Test Suite 2";
		String XmlTestNameLocal = "Automation Test Part 2: Execute test cases locally";
		String suite = JavaHelpers.readFileAsString(Project.templates(TestngTags.XML_TMPL_SUITE_FILE_NAME_2));
		String groups;

		groups = createXMLGroupsLocal();

		suite = TestngTags.setXMLSuiteThreadCount(suite, threadCount);
		suite = TestngTags.setXMLSuiteName(suite, suiteName);
		suite = TestngTags.setXMLSuiteTestName(suite, XmlTestNameLocal);
		suite = TestngTags.setXMLSuiteParallel(suite, TestngTags.XML_PARALLEL_METHODS);
		suite = TestngTags.setXMLSuiteGroups(suite, groups);
		suite = TestngTags.setXMLSuiteClasses(suite, classes);

		System.out.println("Automation Suite Local Test Cases:\n\n" + suite);
		JavaHelpers.writeFile(outputPath + File.separator + "testng-suite-1-2.xml", suite);
	}

	public void createSuiteFileCustom(String threadCount, String classes, String outputPath, String xmlFullFileName) {
		String suiteName = "Automation Test Suite Custom";
		String XmlTestName = "Automation Test: Custom Test Suite";
		String suite = JavaHelpers.readFileAsString(Project.templates(TestngTags.XML_TMPL_SUITE_FILE_NAME_CUSTOM));
		String groups;

		groups = createXMLGroupsExternal();

		suite = TestngTags.setXMLSuiteThreadCount(suite, threadCount);
		suite = TestngTags.setXMLSuiteName(suite, suiteName);
		suite = TestngTags.setXMLSuiteTestName(suite, XmlTestName);
		suite = TestngTags.setXMLSuiteParallel(suite, TestngTags.XML_PARALLEL_METHODS);
		suite = TestngTags.setXMLSuiteGroups(suite, groups);
		suite = TestngTags.setXMLSuiteClasses(suite, classes);
		System.out.println("Automation Suite Custom Test Cases:\n\n" + suite);
		JavaHelpers.writeFile(outputPath + File.separator + xmlFullFileName, suite);
	}

	private String createXMLGroupsExternal() {

		String groups;
		groups = TestngTags.addXMLGroupRunTagStart() + "\n";
		groups = groups + TestngTags.addXMLGroupExcludeTagName(TestngTags.XML_GROUP_EXTERNAL_EXEC_ALL) + "\n";
		groups = groups + TestngTags.addXMLGroupExcludeTagName(TestngTags.XML_GROUP_PRODUCTION_ISSUES) + "\n";
		groups = groups + TestngTags.addXMLGroupExcludeTagName(TestngTags.XML_GROUP_OBSOLETE_TEST_CASES) + "\n";
		groups = groups + TestngTags.addXMLGroupExcludeTagName(TestngTags.XML_GROUP_DATE_TIME_CHANGE) + "\n";
		groups = groups + TestngTags.addXMLGroupRunTagEnd() + "\n";

		return groups;
	}

	private String createXMLGroupsLocal() {

		String groups;
		groups = TestngTags.addXMLGroupRunTagStart() + "\n";
		groups = groups + TestngTags.addXMLGroupIncludeTagName(TestngTags.XML_GROUP_EXTERNAL_EXEC_ALL) + "\n";
		groups = groups + TestngTags.addXMLGroupExcludeTagName(TestngTags.XML_GROUP_PRODUCTION_ISSUES) + "\n";
		groups = groups + TestngTags.addXMLGroupExcludeTagName(TestngTags.XML_GROUP_OBSOLETE_TEST_CASES) + "\n";
		groups = groups + TestngTags.addXMLGroupExcludeTagName(TestngTags.XML_GROUP_DATE_TIME_CHANGE) + "\n";
		groups = groups + TestngTags.addXMLGroupRunTagEnd() + "\n";

		return groups;
	}

	private String createXMLGroupsCustom() {

		String groups;
		groups = TestngTags.addXMLGroupRunTagStart() + "\n";
		groups = groups + TestngTags.addXMLGroupRunTagEnd() + "\n";

		return groups;
	}

	private String createXMLSuiteFileNames() {

		String fileNames;
		fileNames = TestngTags.addXMLSuiteFileNameTagName(TestngTags.XML_TMPL_SUITE_FILE_NAME_1) + "\n";
		fileNames = fileNames + TestngTags.addXMLSuiteFileNameTagName(TestngTags.XML_TMPL_SUITE_FILE_NAME_2) + "\n";

		return fileNames;
	}
}
