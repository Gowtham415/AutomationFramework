package com.textura.framework.testng;

public enum TestngFiles {

	MAIN				("tng-suites.xml", 			"tng-suites.template.xml"),

	EXTERNAL			("tng-suite-1.xml", 		"tng-suite-1.template.xml"),
	LOCAL				("tng-suite-2.xml", 		"tng-suite-2.template.xml"),
	FAILED				("tng-suite-failed.xml", 	"tng-suite-failed.template.xml"),
	CUSTOM				("tng-suite-custom.xml", 	"tng-suite-custom.template.xml");


	private String fileName;
	private String templateName;
	
	private TestngFiles(String fileName, String templateName) {
		
		this.fileName = fileName;
		this.templateName = templateName;
	}

	public String getFileName() {
		return fileName;
	}
	
	public String getTemplateName() {
		return templateName;
	}
}
