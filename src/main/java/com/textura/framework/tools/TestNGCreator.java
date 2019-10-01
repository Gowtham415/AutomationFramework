package com.textura.framework.tools;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

public class TestNGCreator {
	
	public static void main(String[] args) throws IOException {
		
		String a = FileUtils.readFileToString(new File("C:\\a.txt"));
		System.out.println(generateTestXML(parseOutMethods(a)));
	}
	/**
	 * Generates a TestNG runnable xml file from a list of methods. 
	 * @param inputMethods
	 * @return
	 */
	public static String generateTestXML(List<String> inputMethods) {
		removeDuplicates(inputMethods);
		List<String> addedInputMethods = new ArrayList<String>(); //another array for error checking
		Set<String> requiredClasses = new HashSet<String>(); //set of used classes
		
		StringBuilder xml = new StringBuilder();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
					"<!DOCTYPE suite SYSTEM \"http://testng.org/testng-1.0.dtd\">\n" + 
					"<suite thread-count=\"1\" name=\"Generated Suite\" parallel=\"methods\">\n" + 
					"  <test name=\"Default test\" parallel=\"methods\">\n" + 
					"    <classes>\n");
		Class<?>[] classes  = AllTestMethodNames.getClasses("com.textura.cpm.testsuites");
		//first pass over the data: find used classes. Don't add unused classes.
		for(Class<?> c : classes) {
			Method[] methods = c.getMethods();
			for(Method m : methods) {
				for(String s : inputMethods) {
					if(s.equals(m.getName())) {
						requiredClasses.add(c.getName());
						addedInputMethods.add(s);
					}
				}
			}
		}
		if(inputMethods.size() != addedInputMethods.size()) {
			System.out.println("Error: could not find at least one method's respective class");
			inputMethods.removeAll(addedInputMethods);
			System.out.println(inputMethods);
		}
		
		for(Class<?> c : classes) {
			for(String rclass : requiredClasses) {
				if(c.getName().equals(rclass)) {
					xml.append("      <class name=\"" + c.getName() + "\">\n");
					xml.append("        <methods>\n");
					Method[] methods = c.getMethods();
					for(Method m : methods) {
						for(String s : inputMethods) {
							if(s.equals(m.getName())) {
								xml.append("          <include name=\"" + s + "\"/>\n");
							}
						}
					}
					xml.append("        </methods>\n");
					xml.append("      </class>\n");
				}
			}
		}
		xml.append("    </classes>\n" + 
				   "  </test>\n" + 
				   "</suite>\n");

		return xml.toString();
	}
	
	/**
	 * Parses out a list of methods from an arbitrary string. 
	 * 
	 * Example:
	 *   c302789(com.textura.cpm.testsuites.interfaces.cmic.SubcontractorInvoice_2113): ER1: Expected csv does not match actual csv. actual:
  c302792(com.textura.cpm.testsuites.interfaces.cmic.SubcontractorInvoice_2113): java.io.IOException: Server returned HTTP response code: 500 for URL: http://dfdsvlp024.internal/qa1/InterfaceDownload.psp?fileType=content&actionType=export&_formID_=InterfaceDownload&downloadFile=true&jobID=15&login=True&username=cmicsub5&password=wert66
  c302795(com.textura.cpm.testsuites.interfaces.cmic.SubcontractorInvoice_2113): java.io.IOExc
  
  	gets parsed into a list of
  	
  	c302789, c302792, c302795
	 * @param str
	 * @return
	 */
	public static List<String> parseOutMethods(String str) {
		List<String> inputMethods = new ArrayList<String>();
		
		for(int i=0; i<str.length(); i++) {
			
			if(str.charAt(i) == 'c') {
				int begin = i;
				int j = 0;
				for(j=i+1; j<i+4; j++) {
					try{
						Integer.parseInt("" + str.charAt(j));
					}
					catch(Exception e) {
						j = i+7;
					}
				}
				if(j == i+4) {
					int end = 0;
					for(end = j; end<str.length(); end++) {
						try{
							Integer.parseInt("" + str.charAt(end));
						}
						catch(Exception e) {
							break;
						}
					}
					inputMethods.add(str.substring(begin, end));
				}
			}
		}
		return inputMethods;
	}
	
	public static void removeDuplicates(List<String> list) {
		ArrayList<String> temp = new ArrayList<String>();
		for(int i=0; i<list.size(); i++) {
			if(temp.contains(list.get(i))) {
				list.remove(i);
				i--;
			}
			else {
				temp.add(list.get(i));
			}
		}
	}
	
}
