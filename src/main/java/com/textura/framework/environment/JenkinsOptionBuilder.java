package com.textura.framework.environment;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.textura.framework.utils.JavaHelpers;

public class JenkinsOptionBuilder {
	
	/**
	 * This will be a JAR file that will generate the following properties files:
	 * cpmEnvironments.properties
	 * cpmSuites.properties
	 * cpmTestCases.properties
	 * 
	 * It will rely on a partial local copy of the project on the jenkins server to 
	 * generate properies files used by Jenkins jobs  
	 * @param args
	 */
	public static void main(String[] args) {
		//Build cpmEnvironments.properties
		cpmEnvironments();
		// cpmSuites.properties
		cpmSuites();
		// cpmTestCases.properties
		//cpmTestCases();
	}
	
	/**
	 * Generates a list of all environments configured in environments.xml
	 */
	public static void cpmEnvironments() {
		
		Document document = null;
		SAXReader reader = new SAXReader();
		String s = Project.environment("environment.xml");
		String newLine = System.getProperty("line.separator");
		String environments = "";
		String fileHeader = "";
		String fileContent = "";
		
		try {
			File file = new File(s);
			document = reader.read(file);
		}
		catch (Exception e) {
	         e.printStackTrace();
	    }
		
		Element root = document.getRootElement();
		String serverHostname = null;		
		String environmentName = null;		
		
		for (Iterator<?> i = root.elementIterator(); i.hasNext();) {			
			Element serverIterator = (Element) i.next();			
			serverHostname = serverIterator.attribute("hostname").getText();			
			
			for (Iterator<?> j = serverIterator.elementIterator(); j.hasNext();) {
				Element environmentIterator = (Element) j.next();
				environmentName = environmentIterator.attribute("name").getText();
				environments += serverHostname + "." + environmentName + ",\\" + newLine;	
			}
		}		
		
		fileHeader = "#This file contains a list of available CPM environments based on 'evnironments.xml'" + newLine + "#It should be placed in " +
					"/var/lib/jenkins/jobs/CPM/Selenium/src/main/java/com/qa/cpm/environment/cpmEnvironments.properties" + 
					" on the Jenkins server" + newLine + "cpmEnvironments = \\" + newLine + "Select,\\" + newLine;
		
		fileContent = fileHeader + environments;
		
		System.out.println(fileContent);
		
		try {
			JavaHelpers.writeFile(Project.environment("cpmEnvironments.properties"), fileContent);
		} catch (Exception e) {
			System.out.println("JenkinsOptionBuilder.java - Error writing cpmEnvironments.properties file");
			e.printStackTrace();
		}
	}

	/**
	 * Generates a list of all TestRail Suites that are automated (currently it lists all Test Suite Classes)
	 */
	public static void cpmSuites() {
		
		String newLine = System.getProperty("line.separator");
		String[] allTestClasses = JavaHelpers.listFilesAsArray();
		String fileHeader = "";
		String testClasses = "";
		String fileContent = "";

		// Append all test classes
		for (int i = 0; i < allTestClasses.length; i++){
			testClasses = testClasses + allTestClasses[i] + ",\\" + newLine;
		}

		fileHeader = "#This file contains a list of available CPM TestRail test suites" + newLine + "#This file should be placed in " +
					"/var/lib/jenkins/jobs/CPM/Selenium/src/main/java/com/qa/cpm/environment/cpmSuites.properties" + 
					" on the Jenkins server" + 	newLine + "testClasses = \\" + newLine;
		
		fileContent = fileHeader + testClasses;
		
		System.out.println(fileContent);
		
		try {
			JavaHelpers.writeFile(Project.environment("cpmSuites.properties"), fileContent);
		} catch (Exception e) {
			System.out.println("JenkinsOptionBuilder.java - Error writing cpmSuites.properties file");
			e.printStackTrace();
		}		
	}
	
	/**
	 * Generates a list of all automated test cases in the following format: 'FunctionalArea.Feature.cTestCaseID'
	 */
	public static void cpmTestCases() {}
	
	
}
