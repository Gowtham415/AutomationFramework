package com.textura.framework.testng;

public enum TestngSuiteFiles {
	
	MAIN				("testng.xml"),
	MAIN_TEMPLATE		("testng-suite.template.xml"),

	DEFAULT				("testng-suite-1-1.xml,testng-suite-1-2.xml"),

	LOCAL				("testng-suite-1-1.xml"),
	LOCAL_TEMPLATE		("testng-suite-1-1.template.xml"),
	EXTERNAL			("testng-suite-1-2.xml"),
	EXTERNAL_TEMPLATE	("testng-suite-custom.xml.template"),
	FAILED				("testng-failed.xml"),
	FAILED_TEMPLATE		("testng-failed.template.xml");

	private String fileName;
	
	private TestngSuiteFiles(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

}
