package com.textura.framework.testng;

public enum TestngGroups {
	
	DEFAULT					("Default"),
	GRID					("GridOnly"),
	PRODUCTION_ISSUES		("ProductionIssues"),
	OBSOLETE_TEST_CASES		("ObsoleteTestCases"),
	DATE_TIME_CHANGE		("DateTimeChange"), 
	POST_DEPLOYMENT_CLEANUP	("PostDeploymentCleanup");

	private String name;
	
	private TestngGroups (String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
