package com.textura.framework.testng;

public enum TestngParallelRunMode {

	SUITES	("suites"),
	CLASSES	("classes"),
	METHODS	("methods"),
	FALSE	("false");
	
	private String mode;
	
	
	private TestngParallelRunMode(String mode) {
		this.mode = mode;
	}
	
	public String getRunMode() {
		return mode;	
	}
}
