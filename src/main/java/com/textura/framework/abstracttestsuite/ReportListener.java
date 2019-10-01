package com.textura.framework.abstracttestsuite;

/**
 * A callback to allow custom reporting for performance tests
 * 
 */
public interface ReportListener {

	/**
	 * Allows control of reporting test results.
	 * 
	 * @param testResults
	 */
	public void handleReport(TestResults testResults);

	/**
	 * Handle results of the entire test run.
	 * 
	 * @param runResults
	 */
	public void handleResults(RunResults runResults);

}