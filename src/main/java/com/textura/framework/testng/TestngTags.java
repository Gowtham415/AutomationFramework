package com.textura.framework.testng;

public class TestngTags {

	public static final String XML_TMPL_SUITES_FILE_NAME = "testng-suite.template.xml";
	public static final String XML_TMPL_SUITE_FILE_NAME_1 = "testng-suite-1-1.xml.template";
	public static final String XML_TMPL_SUITE_FILE_NAME_2 = "testng-suite-1-2.xml.template";
	public static final String XML_TMPL_SUITE_FILE_NAME_CUSTOM = "testng-suite-custom.xml.template";

	public static final String XML_GROUP_EXTERNAL_EXEC_ALL = "ExternalExec.All";
	public static final String XML_GROUP_EXTERNAL_EXEC_SETUP = "ExternalExec.Setup";
	public static final String XML_GROUP_PRODUCTION_ISSUES = "ProductionIssues";
	public static final String XML_GROUP_DATE_TIME_CHANGE = "DateTimeChange";
	public static final String XML_GROUP_OBSOLETE_TEST_CASES = "ObsoleteTestCases";
	
	public static final String XML_PARALLEL_NONE = "none";
	public static final String XML_PARALLEL_METHODS = "methods";
	public static final String XML_PARALLEL_CLASSES = "classes";
	
	
	public static String setXMLSuiteThreadCount(String suiteXMLFile, String threadCount){
		return suiteXMLFile.replace("[THREAD_COUNT]", threadCount);
	}
	public static String setXMLSuiteName(String suiteXMLFile, String suiteName){
		return suiteXMLFile.replace("[SUITE_NAME]", suiteName);
	}
	public static String setXMLSuiteParallel(String suiteXMLFile, String parallel){
		return suiteXMLFile.replace("[PARALLEL]", parallel);
	}
	public static String setXMLSuiteTestName(String suiteXMLFile, String testName){
		return suiteXMLFile.replace("[TEST_NAME]", testName);
	}
	public static String setXMLSuiteGroups(String suiteXMLFile, String testClasses){
		return suiteXMLFile.replace("[TEST_GROUPS]", testClasses);
	}
	public static String setXMLSuiteClasses(String suiteXMLFile, String testName){
		return suiteXMLFile.replace("[TEST_CLASSES]", testName);
	}
	public static String setXMLSuiteFileNames(String suiteXMLFile, String fileNames){
		return suiteXMLFile.replace("[TEST_SUITE_FILE]", fileNames);
	}
	public static String addXMLGroupRunTagStart(){
		return "      <run>";
	}
	public static String addXMLGroupRunTagEnd(){
		return "      </run>";
	}
	public static String addXMLGroupIncludeTagName(String groupName){
		return "         <include name=\"" + groupName + "\"/>";
	}
	public static String addXMLGroupExcludeTagName(String groupName){
		return "         <exclude name=\"" + groupName + "\"/>";
	}
	public static String addXMLMethodsTagStart(){
		return "        <methods>";
	}
	public static String addXMLMethodsTagEnd(){
		return "        </methods>";
	}
	public static String addXMLMethodTagName(String testID){
		return "         <include name=\"" + testID + "\"/>";
	}
	public static String addXMLClassTagStart(String className){
		return "      <class name=\"" + className + "\">";
	}
	public static String addXMLClassTagEnd(String className){
		return "      </class> <!-- " + className + " -->";
	}
	public static String addXMLClassTag(String className){
		return "      <class name=\"" + className + "\"/";
	}
	public static String addXMLSuiteFileNameTagName(String suiteFile){
		return "    <suite-file path=\"" + suiteFile + "\"/>";
	}
}
