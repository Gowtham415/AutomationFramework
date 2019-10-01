package com.textura.framework.abstracttestsuite;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AutoRunResults {

	private String name = "";
	private Date start;
	private Date end;
	private double totalTime = 0;
	private int testsExecuted = 0;
	private int testsToBeExecuted = 0;
	private int testsPassed = 0;
	private int testsFailed = 0;
	private int testsSkipped = 0;

	public void setTestsPassed() {
		testsPassed++;
	}

	public void setTestsFailed() {
		testsFailed++;
	}

	public void setTestsSkipped() {
		testsSkipped++;
	}

	public void setTestsToBeExecuted(int toBeExecuted) {
		testsToBeExecuted = toBeExecuted;
	}

	public void setStartDate(Date startDate) {
		start = startDate;
	}

	public void setEndDate(Date endDate) {
		end = endDate;
	}

	public void setTotalExecutionTime() {
		totalTime = ((end.getTime() - start.getTime()));
	}

	public int getTestsPassed() {
		return testsPassed;
	}

	public int getTestsFailed() {
		return testsFailed;
	}

	public int getTestsSkipped() {
		return testsSkipped;
	}

	public int getTestsExecuted() {
		testsExecuted = testsPassed + testsFailed + testsSkipped;
		return testsExecuted;
	}

	public int getTestsToBeExecuted() {
		return testsToBeExecuted;
	}

	public String getStartDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:sss");
		if (start == null)
			try {
				start = sdf.parse("01/01/1000 01:01:001");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		return sdf.format(start);
	}

	public String getEndDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:sss");
		if (end == null)
			try {
				end = sdf.parse("01/01/1000 01:01:001");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		return sdf.format(end);
	}

	public double getTotalExecutionTimeInSeconds() {
		return Math.round(((totalTime) / 1000.0));
	}

	public double getTotalExecutionTimeInMinutes() {
		return Math.round((((totalTime) / 1000.0) / 60.0));
	}

	public String toString() {

		StringBuffer result = new StringBuffer();

		result.append("\nAutomation Run Name: ").append(name).append("\n");
		result.append("Automation Run Start:  ").append(start).append("\n");
		result.append("Automation Run End:    ").append(end).append("\n");
		result.append("Total execution time:  ").append(getTotalExecutionTimeInSeconds()).append(" sec, ");
		result.append(getTotalExecutionTimeInMinutes()).append(" min\n");
		result.append("Total tests executed:       ").append(getTestsExecuted()).append("\n");
		result.append("Total tests to be executed: ").append(testsToBeExecuted).append("\n");
		result.append("Total tests passed:         ").append(testsPassed).append("\n");
		result.append("Total tests failed:         ").append(testsFailed).append("\n");
		result.append("Total tests skipped:        ").append(testsSkipped).append("\n");

		return result.toString();
	}
}
