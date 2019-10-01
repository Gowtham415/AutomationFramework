package com.textura.framework.testrail;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.textura.framework.environment.Project;


public class ResultXMLParser {
	
	/*Parses a test result XML file and returns relevant data in an array of ResultData.
	An example format read is as follows:
	<testsuites>
	  <testsuite>
	    <testcase classname="" name="" time="" />
	      <error type="" </error>
	    <testcase classname="" name="" time="" />
	      <failure type="" </failure>
	    <testcase classname="" name="" time="" />
	    <testcase classname="" name="" time="" />
	    
	If there is no error or failure, the error code is 0.
    */
	public static ResultsData[] parseResults() throws IOException {
		Document document = null;
		SAXReader reader = new SAXReader();
		String s = Project.path() + "target/surefire-reports/TEST-TestSuite.xml";
		try {
			File file = new File(s);
			document = reader.read(file);
		}
		catch (Exception e) {
	         e.printStackTrace();
	    }
		Element root = document.getRootElement();
		
		//count size of array
		int testCaseCount = 0;
		for (Iterator<?> i = root.elementIterator(); i.hasNext();) {
			Element testsuite = (Element) i.next();
			if(testsuite.getName().startsWith("testcase")) {
	            testCaseCount++;
	        }
		}
		ResultsData[] testResults = new ResultsData[testCaseCount];
		
		String errorDetails = "";
		String errorCode = "";
		int resultsIterator = 0;
		//iterate through list of suites
		for (Iterator<?> i = root.elementIterator(); i.hasNext();) {
			Element testsuite = (Element) i.next();
			//through each testcase
			if(testsuite.getName().startsWith("testcase")) {
				ResultsData result = new ResultsData();
				if(testsuite.elementText("failure") != null) {
					errorDetails = testsuite.elementText("failure").toString();
					errorCode = "2";
				}
				else if(testsuite.elementText("error") != null) {
					errorDetails = testsuite.elementText("error").toString();
					errorCode = "1";
				}
				else {
					errorDetails = "";
					errorCode = "0";
				}
				result.className = testsuite.attributeValue("classname");
				result.testCaseID = testsuite.attributeValue("name");
				result.timeElapsed = testsuite.attributeValue("time");
				result.errorCode = errorCode;
				result.errorDetails = errorDetails;
				testResults[resultsIterator] = result;
				resultsIterator++;
			}
		}
		return testResults;
	}
	
}
