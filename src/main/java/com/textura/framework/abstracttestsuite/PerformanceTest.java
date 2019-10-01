package com.textura.framework.abstracttestsuite;

public interface PerformanceTest {

	/**
	 * Executes a Performance Test. Implementor is expected to use 3 methods inherited from AbstractTestSuite:
	 * startTimer();
	 * stopTimer();
	 * reportTestResults();
	 * 
	 * Code before startTimer() executes preconditions, code between start and stop execute the page under test and is expected to block until page completes rendering.
	 * reportTestResults() sends results to the ReportListeners
	 */
	public void performanceTestImpl();
}