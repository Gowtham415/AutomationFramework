package com.textura.framework.testng;

import com.textura.framework.testng.TestngConfig;

public class TestSuitesBuilderFrm implements TestSuitesBuilder {

	protected TestngConfig tng;
	
	@Override
	public void createTestSuites(TestngConfig cfg) {
		
		tng = cfg;
		System.out.println("Create Test suites type " + tng.suiteType);
		
	}
	
	@Override
	public void createTestSuites(String threadNumber, String classes, String xmlFullFileName) {

	}
}
