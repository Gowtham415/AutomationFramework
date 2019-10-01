package com.textura.framework.database;

import java.util.Map;

public abstract class AutoDBProductSupport {

	public String projectID;
	public String excludedTestSuites;
	public Map<Integer, Integer> labelsWithSuitesAssignment;
	public String[] autoDBTestRailTestSuiteLabels;
}