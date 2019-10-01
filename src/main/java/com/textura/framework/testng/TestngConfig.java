package com.textura.framework.testng;

import com.textura.framework.configadapter.ConfigComponents;

public class TestngConfig {

	public ConfigComponents component;
	public TestngSuiteType suiteType;
	public String output;
	
	public String tngThreads;
	public String tngParallel;
	public String tngSuites;
	public String tngGroups;
	
	public TestngConfig() {

		component = null;
		suiteType = null;
		output = null;
		
		tngThreads = null;
		tngParallel = null;
		tngSuites = null;
		tngGroups = null;
	}
}
