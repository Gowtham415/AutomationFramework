package com.textura.framework.testng;

public enum TestngAttributes {

	NAME 			("name"),
	PATH 			("path"),
	THREAD_COUNT	("thread-count"),
	PARALLEL 		("parallel");

	private String attr;
	
	private TestngAttributes(String attr) {
		this.attr = attr;
	}
	
	public String getName() {
		return attr;
	}
}
