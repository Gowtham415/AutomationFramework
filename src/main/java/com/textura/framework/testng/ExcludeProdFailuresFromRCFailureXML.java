package com.textura.framework.testng;

import org.testng.annotations.Test;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.testng.xml.Parser;
import org.testng.xml.SuiteXmlParser;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlSuite.FailurePolicy;
import org.testng.xml.XmlSuite.ParallelMode;
import org.testng.xml.XmlTest;

/*
 * This class will exclude the failed test cases of prod.xml from rcfail.xml
 *
 *Inputs are
 *	-	RC failure XML
 *	-  	Prod failure XML that is obtained after executing the RC failure.xml on current prod
 * 
 * This will exclude the Prod failure XML test cases from RC failure XML cases and provide us with new XML.
 * 
 * Excluding all groups
 * 
 */
public class ExcludeProdFailuresFromRCFailureXML {

	XmlSuite rcxmlSuite = new XmlSuite();
	XmlSuite prodxmlSuite = new XmlSuite();
	SuiteXmlParser sl = new SuiteXmlParser();
	InputStream rcinputStream;
	InputStream prodinputStream;
	Parser parser;
	String rcFailureFile = "C:\\Automation\\Textura\\Framework\\154failures_testng-failed.xml";
	List<XmlTest> rctestList;
	List<XmlTest> prodtestList;
	List<XmlClass> rcxmlclasses;
	List<XmlClass> prodxmlclasses;
	Map<String, String> suiteParameters = new HashMap<String, String>();
	List<String> suiteExcludeGroup = new ArrayList<String>();
	int count=0;
	String prodFailureFile="C:\\Automation\\Textura\\Framework\\prod_testng-failed.xml";
	List<String> prodFileMethods=new ArrayList<>();

	@Test
	public void rcFileData() {
		try {
			rcinputStream = new FileInputStream(new File(rcFailureFile));
			rcxmlSuite = sl.parse(rcFailureFile, rcinputStream, false);
			System.out.println(rcxmlSuite.getAllParameters());
			rctestList = rcxmlSuite.getTests();
			
			rcxmlclasses = rctestList.get(0).getClasses();
			rcxmlSuite.setExcludedGroups(rcxmlSuite.getExcludedGroups());
			for (XmlClass classes : rcxmlclasses) {
				System.out.println(classes.getName());
			}
			prodFileData();
			createTestNgFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void prodFileData() {
		try {
			prodinputStream = new FileInputStream(prodFailureFile);
			prodxmlSuite = sl.parse(prodFailureFile, prodinputStream, false);
			System.out.println(prodxmlSuite.getAllParameters());
			prodtestList = prodxmlSuite.getTests();
			
			prodxmlclasses = prodtestList.get(0).getClasses();
			prodxmlSuite.setExcludedGroups(prodxmlSuite.getExcludedGroups());
			for (XmlClass classes : prodxmlclasses) {
				System.out.println(classes.getName());
				for (XmlInclude include : classes.getIncludedMethods()) {
					prodFileMethods.add(include.getName());
				}
			}
			createTestNgFile();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}
	
	public void createTestNgFile() {
		rcxmlSuite.getName();
		XmlSuite writeXmlSuite=new XmlSuite();
		writeXmlSuite.setName("Failed suite [Failed suite [Failed suite [Failed suite [Failed suite [Failed suite [Failed suite [Automation Suite 1]]]]]]]");
		writeXmlSuite.setParallel(ParallelMode.METHODS);
		writeXmlSuite.setThreadCount(20);
		writeXmlSuite.setConfigFailurePolicy(FailurePolicy.CONTINUE);
		writeXmlSuite.setVerbose(0);
		writeXmlSuite.setGuiceStage("DEVELOPMENT");
		XmlTest writeXmlTest=new XmlTest(writeXmlSuite);
		writeXmlTest.setName("Automation Test Part 1: Execute test cases externally.(failed)(failed)(failed)(failed)(failed)(failed)");
		writeXmlTest.setParallel(ParallelMode.METHODS);
		writeXmlTest.setExcludedGroups(groupsToExclude());
		
		List<XmlClass> classList=new ArrayList<XmlClass>();
		for (XmlClass classes : rcxmlclasses) {
			count=0;
			List<XmlInclude> includeList=new ArrayList<XmlInclude>();
			for (XmlInclude include : classes.getIncludedMethods()) {
				if(!prodFileMethods.contains(include.getName()))
				{
					count++;
				includeList.add(include);
				}
}
			if(count>0)
			classList.add(classes);
			classes.setIncludedMethods(includeList);
		}
		writeXmlTest.setXmlClasses(classList);
		//wr
		FileWriter writer;
		try {
			writer = new FileWriter(new File("C:\\Automation\\Textura\\Framework\\onlyRcFailure_testng-failed.xml"));
			writer.write(writeXmlSuite.toXml());
			writer.flush();
			writer.close();
			System.out.println(new File("C:\\Automation\\Textura\\Framework\\onlyRcFailure_testng-failed.xml").getAbsolutePath());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> groupsToExclude()
	{
		List<String> excludeGroups=new ArrayList<>();
		excludeGroups.add("ExternalExec.All");
		excludeGroups.add("ProductionIssues");
		excludeGroups.add("ObsoleteTestCases");
		excludeGroups.add("DateTimeChange");

		return excludeGroups;
	}
}
