package com.textura.framework.utils;

import java.util.HashMap;

import com.jamonapi.Monitor;
import com.textura.framework.environment.EnvironmentInfo;
import com.textura.framework.environment.productenvironments.BSEnvironmentInfo;
import com.textura.framework.environment.productenvironments.CPMEnvironmentInfo;
import com.textura.framework.objects.main.Obj;

public class PerfDetailsQuery {

	public static String getQ() {
		StringBuffer testQ = new StringBuffer();
		testQ.append("INSERT INTO tests (test_id) VALUES ('1234567890');");
		return testQ.toString();
	}


	public static String getTestResultsQuery2(String testId, String runId, String jenkinsRunId, String masterID, Monitor mon,
			int time, String testcaseID, String page) {
		StringBuffer testResults = new StringBuffer();
		testResults.append("INSERT INTO tests (");
		testResults.append("test_id, ");
		testResults.append("run, ");
		testResults.append("test_case_id, ");
		testResults.append("page_name,");
		testResults.append("hits, ");
		testResults.append("last_value, ");
		testResults.append("min, ");
		testResults.append("max, ");
		testResults.append("avg, ");
		testResults.append("std_dev, ");
		testResults.append("total, ");
		testResults.append("first_access, ");
		testResults.append("last_access, ");
		testResults.append("active, ");
		testResults.append("max_active, ");
		testResults.append("avg_active, ");
		testResults.append("avg_global_active, ");
		testResults.append("avg_primary_active, ");
		testResults.append("jenkinsrunid, ");
		testResults.append("jenkinsmasterkey)");
		testResults.append("VALUES ('");
		testResults.append(testId).append("', '");
		testResults.append(runId).append("', '");
		testResults.append(testcaseID).append("', '");
		testResults.append(page).append("', '");
		testResults.append(mon.getHits()).append("', '");
		testResults.append(time).append("', '");
		testResults.append(time).append("', '");
		testResults.append(time).append("', '");
		testResults.append(time).append("', '");
		testResults.append(mon.getStdDev()).append("', '");
		testResults.append(time).append("', '");
		testResults.append(mon.getFirstAccess()).append("', '");
		testResults.append(mon.getLastAccess()).append("', '");
		testResults.append(mon.getActive()).append("', '");
		testResults.append(mon.getMaxActive()).append("', '");
		testResults.append(mon.getAvgActive()).append("', '");
		testResults.append(mon.getAvgGlobalActive()).append("', '");
		testResults.append(mon.getAvgPrimaryActive()).append("', '");
		testResults.append(jenkinsRunId).append("', '");
		testResults.append(masterID).append("');");
		return testResults.toString();
	}
	
	
	public static String getTestResultsQueryWithJenkinsID(String testId, String runId, String jenkinsRunId,
			Monitor mon, String page) {
		StringBuffer testResults = new StringBuffer();

		testResults.append("INSERT INTO tests (");
		testResults.append("test_id, ");
		testResults.append("run, ");
		testResults.append("test_case_id, ");
		testResults.append("page_name,");
		testResults.append("hits, ");
		testResults.append("last_value, ");
		testResults.append("min, ");
		testResults.append("max, ");
		testResults.append("avg, ");
		testResults.append("std_dev, ");
		testResults.append("total, ");
		testResults.append("first_access, ");
		testResults.append("last_access, ");
		testResults.append("active, ");
		testResults.append("max_active, ");
		testResults.append("avg_active, ");
		testResults.append("avg_global_active, ");
		testResults.append("avg_primary_active, ");
		testResults.append("jenkinsrunid)");
		testResults.append("VALUES ('");
		testResults.append(testId).append("', '");
		testResults.append(runId).append("', '");
		testResults.append(JavaHelpers.getTestCaseMethodName()).append("', '");
		testResults.append(page).append("', '");
		testResults.append(mon.getHits()).append("', '");
		testResults.append(mon.getLastValue()).append("', '");
		testResults.append(mon.getMin()).append("', '");
		testResults.append(mon.getMax()).append("', '");
		testResults.append(mon.getAvg()).append("', '");
		testResults.append(mon.getStdDev()).append("', '");
		testResults.append(mon.getTotal()).append("', '");
		testResults.append(mon.getFirstAccess()).append("', '");
		testResults.append(mon.getLastAccess()).append("', '");
		testResults.append(mon.getActive()).append("', '");
		testResults.append(mon.getMaxActive()).append("', '");
		testResults.append(mon.getAvgActive()).append("', '");
		testResults.append(mon.getAvgGlobalActive()).append("', '");
		testResults.append(mon.getAvgPrimaryActive()).append("', '");
		testResults.append(jenkinsRunId).append("');");
		
		return testResults.toString();
	}
	

	
	public static String writeTestResultsQueryWithMasterID(String testId, String runId, String jenkinsRunId, String masterID,
			Monitor mon, String page) {
		StringBuffer testResults = new StringBuffer();

		testResults.append("INSERT INTO tests (");
		testResults.append("test_id, ");
		testResults.append("run, ");
		testResults.append("test_case_id, ");
		testResults.append("page_name,");
		testResults.append("hits, ");
		testResults.append("last_value, ");
		testResults.append("min, ");
		testResults.append("max, ");
		testResults.append("avg, ");
		testResults.append("std_dev, ");
		testResults.append("total, ");
		testResults.append("first_access, ");
		testResults.append("last_access, ");
		testResults.append("active, ");
		testResults.append("max_active, ");
		testResults.append("avg_active, ");
		testResults.append("avg_global_active, ");
		testResults.append("avg_primary_active, ");
		testResults.append("jenkinsrunid, ");
		testResults.append("jenkinsmasterkey)");
		testResults.append("VALUES ('");
		testResults.append(testId).append("', '");
		testResults.append(runId).append("', '");
		testResults.append(JavaHelpers.getTestCaseMethodName()).append("', '");
		testResults.append(page).append("', '");
		testResults.append(mon.getHits()).append("', '");
		testResults.append(mon.getLastValue()).append("', '");
		testResults.append(mon.getMin()).append("', '");
		testResults.append(mon.getMax()).append("', '");
		testResults.append(mon.getAvg()).append("', '");
		testResults.append(mon.getStdDev()).append("', '");
		testResults.append(mon.getTotal()).append("', '");
		testResults.append(mon.getFirstAccess()).append("', '");
		testResults.append(mon.getLastAccess()).append("', '");
		testResults.append(mon.getActive()).append("', '");
		testResults.append(mon.getMaxActive()).append("', '");
		testResults.append(mon.getAvgActive()).append("', '");
		testResults.append(mon.getAvgGlobalActive()).append("', '");
		testResults.append(mon.getAvgPrimaryActive()).append("', '");
		testResults.append(jenkinsRunId).append("', '");
		testResults.append(masterID).append("');");
		
		return testResults.toString();
	}
	
	public static String getTestResultsQuery(String testId, String runId,
			Monitor mon, String page) {
		StringBuffer testResults = new StringBuffer();

		testResults.append("INSERT INTO tests (");
		testResults.append("test_id, ");
		testResults.append("run, ");
		testResults.append("test_case_id, ");
		testResults.append("page_name,");
		testResults.append("hits, ");
		testResults.append("last_value, ");
		testResults.append("min, ");
		testResults.append("max, ");
		testResults.append("avg, ");
		testResults.append("std_dev, ");
		testResults.append("total, ");
		testResults.append("first_access, ");
		testResults.append("last_access, ");
		testResults.append("active, ");
		testResults.append("max_active, ");
		testResults.append("avg_active, ");
		testResults.append("avg_global_active, ");
		testResults.append("avg_primary_active)");
		testResults.append("VALUES ('");
		testResults.append(testId).append("', '");
		testResults.append(runId).append("', '");
		testResults.append(JavaHelpers.getTestCaseMethodName()).append("', '");
		testResults.append(page).append("', '");
		testResults.append(mon.getHits()).append("', '");
		testResults.append(mon.getLastValue()).append("', '");
		testResults.append(mon.getMin()).append("', '");
		testResults.append(mon.getMax()).append("', '");
		testResults.append(mon.getAvg()).append("', '");
		testResults.append(mon.getStdDev()).append("', '");
		testResults.append(mon.getTotal()).append("', '");
		testResults.append(mon.getFirstAccess()).append("', '");
		testResults.append(mon.getLastAccess()).append("', '");
		testResults.append(mon.getActive()).append("', '");
		testResults.append(mon.getMaxActive()).append("', '");
		testResults.append(mon.getAvgActive()).append("', '");
		testResults.append(mon.getAvgGlobalActive()).append("', '");
		testResults.append(mon.getAvgPrimaryActive()).append("');");
		
		return testResults.toString();
	}

	public static String getRunResultsQuery(String runId, CPMEnvironmentInfo env, Monitor mon, Obj obj) {
		StringBuffer runResults = new StringBuffer();
		runResults.append("INSERT INTO test_runs (");
		runResults.append("run_id, ");
		runResults.append("start_time, ");
		runResults.append("end_time, ");
		runResults.append("env, ");
		runResults.append("branch, ");
		runResults.append("revision, ");
		runResults.append("browser, ");
		runResults.append("client, ");
		runResults.append("notes) " );
		runResults.append("VALUES ('");
		runResults.append(runId).append("', '");
		runResults.append(mon.getFirstAccess()).append("', '");
		runResults.append(mon.getLastAccess() + "', '");
		runResults.append(env.testServer).append("', '"); 
		runResults.append(env.codeBranch).append("', '");  
		runResults.append(env.codeRevision).append("', '");
		runResults.append(ClientInfo.getBrowserVersion(obj,env)).append("', '");
		runResults.append(env.testClient).append("', '"); 
		runResults.append(env.testNotes).append("');");

		return runResults.toString();
	}
	
	public static String getRunResultsQuery(String runId, BSEnvironmentInfo env, Monitor mon, String browserVersion) {
		StringBuffer runResults = new StringBuffer();
		runResults.append("INSERT INTO test_runs (");
		runResults.append("run_id, ");
		runResults.append("start_time, ");
		runResults.append("end_time, ");
		runResults.append("env, ");
		runResults.append("branch, ");
		runResults.append("revision, ");
		runResults.append("browser, ");
		runResults.append("client, ");
		runResults.append("notes) ");
		runResults.append("VALUES ('");
		runResults.append(runId).append("', '");
		runResults.append(mon.getFirstAccess()).append("', '");
		runResults.append(mon.getLastAccess() + "', '");
		runResults.append(env.testServer).append("', '");
		runResults.append(env.codeBranch).append("', '");
		runResults.append(env.codeRevision).append("', '");
		runResults.append(browserVersion).append("', '");
		// runResults.append(ClientInfo.getBrowserVersion(obj)).append("', '");
		runResults.append(env.testClient).append("', '");
		runResults.append(env.testNotes).append("');");

		return runResults.toString();
	}

	public static String getRunResultsQuery(String runId, EnvironmentInfo env, Monitor mon, String browserVersion) {
		StringBuffer runResults = new StringBuffer();
		runResults.append("INSERT INTO test_runs (");
		runResults.append("run_id, ");
		runResults.append("start_time, ");
		runResults.append("end_time, ");
		runResults.append("env, ");
		runResults.append("branch, ");
		runResults.append("revision, ");
		runResults.append("browser, ");
		runResults.append("client, ");
		runResults.append("notes) " );
		runResults.append("VALUES ('");
		runResults.append(runId).append("', '");
		runResults.append(mon.getFirstAccess()).append("', '");
		runResults.append(mon.getLastAccess() + "', '");
		runResults.append(env.url).append("', '"); 
		runResults.append(env.codeBranch).append("', '");  
		runResults.append(env.codeRevision).append("', '");
		runResults.append(browserVersion).append("', '");
		runResults.append(env.testClient).append("', '"); 
		runResults.append(env.testNotes).append("');");
		return runResults.toString();
	}
	
	public static String getRunEndTimeQuery(String runId, Monitor mon) {
		StringBuffer runResults = new StringBuffer();

		runResults.append("UPDATE test_runs ");
		runResults.append("SET end_time = '");
		runResults.append(mon.getLastAccess()).append("' ");
		runResults.append("WHERE run_id = '"); 
		runResults.append(runId).append("';");  

		return runResults.toString();
	}

	

}