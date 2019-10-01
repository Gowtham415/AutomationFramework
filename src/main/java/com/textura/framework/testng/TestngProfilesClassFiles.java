package com.textura.framework.testng;

public enum TestngProfilesClassFiles {

	DEFAULT_EXTERNAL	("Automation Suite 1", 			"Automation Test Part 1: Execute test cases externally.", 
							"tng-suite-1.template.xml", "tng-suite-1.template.xml"),
							
	DEFAULT_LOCAL		("Automation Suite 2", 			"Automation Test Part 2: Execute test cases locally.", 
							"tng-suite-1.template.xml", "tng-suite-1.template.xml"),
							
	FAILED				("Automation Failed Suite", 	"Automation Test: Execute (failed) test cases", 
							"tng-suite-1.template.xml", "tng-suite-1.template.xml"),
							
	CUSTOM				("Automation Suite Custom", 	"Automation Test Custom: Execute test cases", 
							"tng-suite-custom.xml", "tng-suite-1.template.xml");

	private String suiteName;
	private String testName;
	private String fileName;
	private String templateName;

	private TestngProfilesClassFiles(String suiteName, String testName,	String fileName, String templateName) {
		
		this.suiteName = suiteName;
		this.testName = testName;
		this.fileName = fileName;
		this.templateName = templateName;
	}
	
	public String getSuiteName() {
		return suiteName;
	}
	
	public String getTestName() {
		return testName;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getTemplateName() {
		return templateName;
	}
}
