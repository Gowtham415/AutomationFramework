package com.textura.framework.objects.main;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import com.textura.framework.abstracttestsuite.AutoRunResults;
import com.textura.framework.annotations.Author;
import com.textura.framework.environment.Project;
import com.textura.framework.utils.DateHelpers;
import com.textura.framework.utils.JavaHelpers;

public class TestListener extends TestListenerAdapter {
	private static final Logger LOG = LogManager.getLogger(TestListener.class);
	public static final int WHITE_SPACE_ROWS = 20;
	private static List<String> failures = new ArrayList<String>();
	public static AutoRunResults autoRunResults = new AutoRunResults();
	public static List<String> finishedCases = new ArrayList<String>();

	@Override
	public void onTestFailure(ITestResult tr) {

		// include author in printout if present
		String testClassName = tr.getTestClass().getName();
		String methodName = tr.getName();
		String authorName = " ";
		try {
			Class<?> class_ = Class.forName(testClassName);
			Method m = class_.getMethod(methodName, null);
			Author author = m.getAnnotation(Author.class);
			if (author != null) {
				authorName = " " + author.name() + " ";
			}
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
			// do nothing
			e.printStackTrace();
		}

		LOG.info(DateHelpers.getCurrentDateAndTime() + authorName + methodName + " failure, execution time: " + ((tr
				.getEndMillis() - tr.getStartMillis()) / 1000.0) + " sec, suite: " + testClassName);
		tr.getThrowable().printStackTrace();
		failures.add(methodName);
		finishedCases.add(methodName);
		autoRunResults.setTestsFailed();
	}

	@Override
	public void onTestSkipped(ITestResult tr) {
		LOG.info(DateHelpers.getCurrentDateAndTime() + " " + tr.getName() + " skipped, execution time: " + ((tr.getEndMillis()
				- tr.getStartMillis()) / 1000.0) + " sec, suite: " + tr.getTestClass().getName());
		if (tr.getThrowable() != null) {
			tr.getThrowable().printStackTrace();
		}
		finishedCases.add(tr.getName());
		autoRunResults.setTestsSkipped();
	}

	@Override
	public void onTestSuccess(ITestResult tr) {
		LOG.info(DateHelpers.getCurrentDateAndTime() + " " + tr.getName() + " success, execution time: " + ((tr.getEndMillis()
				- tr.getStartMillis()) / 1000.0) + " sec, suite: " + tr.getTestClass().getName());
		finishedCases.add(tr.getName());
		autoRunResults.setTestsPassed();
	}

	@Override
	public void onTestStart(ITestResult tr) {
		LOG.info(DateHelpers.getCurrentDateAndTime() + " " + tr.getName() + " start, " + tr.getTestClass().getName());
	}

	public void onStart(ITestContext context) {
		LOG.info(DateHelpers.getCurrentDateAndTime() + " Test suite: " + context.getSuite().getName() + " started : " + context
				.getStartDate());
		autoRunResults.setStartDate(context.getStartDate());
	}

	public void onFinish(ITestContext context) {
		LOG.info(DateHelpers.getCurrentDateAndTime() + " Test suite: " + context.getSuite().getName() + " finished: " + context
				.getEndDate());
		autoRunResults.setEndDate(context.getEndDate());
		autoRunResults.setTotalExecutionTime();
		autoRunResults.setTestsToBeExecuted(context.getAllTestMethods().length);
	}

	public static void printReport() {
		String whiteSpace = "";
		for (int i = 0; i < WHITE_SPACE_ROWS; i++) {
			whiteSpace = whiteSpace.concat("\n");
		}
		LOG.info(whiteSpace);

		if (failures.size() == 0) {
			LOG.info(JavaHelpers.readFileAsString(Project.dataFiles("smoketest/alltestspassed.txt")));
		} else {
			LOG.info(JavaHelpers.readFileAsString(Project.dataFiles("smoketest/failure.txt")));
			LOG.info("The following tests failed:");
			for (int i = 0; i < failures.size(); i++) {
				LOG.info(failures.get(i));
				if (failures.get(i).equals("c739477")) {
					LOG.info("c739477 is an expected failure in AUS UAT");
				}
			}
			LOG.info("Scroll up to see details");
		}

		LOG.info(whiteSpace);

	}

	public static AutoRunResults getAutoRunResults() {
		return autoRunResults;
	}
}