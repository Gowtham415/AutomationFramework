package com.textura.framework.testng;

import com.textura.framework.testng.TestngConfig;

public interface TestSuitesBuilder {
	
	public void createTestSuites(String threadNumber, String classes, String xmlFullFileName);

	public void createTestSuites(TestngConfig config);
	
}
