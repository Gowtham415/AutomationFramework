package com.textura.framework.testng;

public enum TestngElements {

	SUITE		("suite"),
	SUITE_FILE	("suite-file"),
	SUITE_FILES	("suite-files"),
	TEST		("test"),
	GROUPS		("groups"),
	RUN			("run"),
	INCLUDE		("include"),
	EXCLUDE		("exclude"),
	CLASSES		("classes"),
	CLASS		("class"),
	METHODS		("methods");

	private String element;
	
	private TestngElements(String element) {
		this.element = element;
	}
	
	public String getName() {
		return element;
	}
	
}
