package com.textura.framework.testng;

public enum TestngProfilesSuiteFile {
	
	DEFAULT			("Automation Suites: Default    ", TestngFiles.EXTERNAL.getFileName() + "," +TestngFiles.LOCAL.getFileName()),
	EXTERNAL		("Automation Suites: Grid Only  ", TestngFiles.EXTERNAL.getFileName()),
	LOCAL			("Automation Suites: Local Only ", TestngFiles.LOCAL.getFileName()),
	FAILED			("Automation Suites: Failed     ", TestngFiles.FAILED.getFileName()),
	CUSTOM			("Automation Suites: Custom     ", TestngFiles.CUSTOM.getFileName());
	
	String suiteName;
	String files;
	
	private TestngProfilesSuiteFile (String suiteName, String files) {
		
		this.suiteName = suiteName;
		this.files = files;
	}
	
	public String getSuiteName() {
		return suiteName;
	}
	
	public String getFiles() {
		return files;
	}
}
