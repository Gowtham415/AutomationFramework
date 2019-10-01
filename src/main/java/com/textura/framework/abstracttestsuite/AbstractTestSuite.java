package com.textura.framework.abstracttestsuite;

import java.io.File;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import com.textura.framework.annotations.LocalOnly;
import com.textura.framework.automationcontrollers.AutoRemote;
import com.textura.framework.automationcontrollers.AutomationController;
import com.textura.framework.configadapter.ConfigAdapter;
import com.textura.framework.configadapter.ConfigAdapterInit;
import com.textura.framework.environment.EnvironmentInfo;
import com.textura.framework.environment.Project;
import com.textura.framework.erutils.ERManager;
import com.textura.framework.objects.main.Assertions;
import com.textura.framework.objects.main.Obj;
import com.textura.framework.utils.DBParams;
import com.textura.framework.utils.Database;
import com.textura.framework.utils.DateHelpers;
import com.textura.framework.utils.JavaHelpers;
import com.textura.framework.utils.WindowsOperations;

@Listeners({ com.textura.framework.objects.main.TestListener.class })
public abstract class AbstractTestSuite {

	protected static EnvironmentInfo environmentInfo;
	protected static Obj staticSelenium;
	protected Assertions asrt = new Assertions();
	protected ERManager er= new ERManager(asrt);
	protected static boolean quitStatic = false;
	public static boolean wasFailure = false;
	protected static boolean jenkinsBuild = false;
	protected static boolean gridMode;
	protected static boolean runPreconditions;
	protected Map<String, Obj> drivers;
	protected static AutomationController automationController;
	protected static AutoRemote remote;
	protected boolean failedToFindHubNode = false;

	// Performance data fields
	protected static String testRunId;
	protected static int sampleSize;
	protected Monitor testCaseMonitor;
	protected static Monitor testRunMonitor;
	protected String testId;
	protected String testDescription;
	protected String browserVersion;
	protected static boolean debug = true;
	protected Map<String, Monitor> monitors = new HashMap<String, Monitor>();
	protected static boolean performanceTest = false;
	protected static List<ReportListener> reporters = new ArrayList<ReportListener>();
	protected static DBParams dbParams;

	@BeforeSuite(alwaysRun = true)
	public void abstractBeforeSuite() {
		WindowsOperations.runCommandQuiet("C:/", "cmd", "/C", "taskkill", "/F", "/IM", "chromedriver.exe");
		setupEnvironment();
		automationController = new AutomationController();
		automationController.start();

		sampleSize = toInteger(environmentInfo.sampleSize);
		testRunId = UUID.randomUUID().toString();
		testRunMonitor = MonitorFactory.start(testRunId).stop().start();
	}

	public static int toInteger(String s) {
		if (s != null && s.length() > 0) {
			try {
				return Integer.valueOf(s);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 1;
	}

	@BeforeClass(alwaysRun = true)
	public void abstractBeforeClass() {
	}

	@AfterClass(alwaysRun = true)
	public void abstractAfterClass() {
	}

	public Obj staticSelenium() {
		return staticSelenium;
	}

	public AbstractTestSuite() {
		drivers = new ConcurrentHashMap<String, Obj>();
	}

	/**
	 * This executes before configuration file is loaded. Could use this method to ensure a config file is present.
	 */
	public abstract void setupEnvironmentBeforeConfigLoad();

	/**
	 * Returns a String representing the Component.
	 * 
	 * @return
	 */
	public abstract String getConfigComponent();

	/**
	 * Executes immediately after configuration has loaded but before a browser has started. Can set last minute configs or set up objects that require config parameters
	 */
	public abstract void afterConfigLoad();

	/**
	 * Method must start a browser and return a class extending Obj
	 * 
	 * @param seleniumRunTime
	 * @param testCaseId
	 * @param capabilities
	 * @return
	 */
	public abstract <T extends Obj> Obj startBrowser(String seleniumRunTime, String testCaseId, Map<String, Object> capabilities);

	/**
	 * Save the parameter to a static variable
	 * 
	 * @param s
	 */
	public abstract void setStaticSelenium(Obj s);

	/**
	 * Save the environmentInfo object to an instance variable
	 * 
	 * @param environmentInfo
	 */
	public abstract void setEnvironment(EnvironmentInfo environmentInfo);

	/**
	 * Sends test result to a reporting entity. Runs after each test case.
	 * 
	 * @param testCaseId
	 * @param result
	 * @param message
	 * @param environmentInfo
	 */
	public abstract void sendTestResult(String testCaseId, TestResult result, String message, EnvironmentInfo environmentInfo);

	public enum TestResult {
		FAIL {
		},
		PASS {
		},
		SKIP {
		}
	}

	@BeforeMethod(alwaysRun = true)
	public void abstractBeforeTest(Method m) {
		String testMethodName = m.getName();

		if (automationController.shouldPause()) { // automationController is a server on it's own thread. Listens for commands. See AutomationController for details.
			System.out.println("Automation paused...");
			while (automationController.shouldPause()) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Automation resumed");
		}

		if (gridMode && staticSelenium != null) {
			synchronized (staticSelenium) {
				if (!quitStatic) {
					quitStatic = true;
					staticSelenium.quit();
				}
			}
		}
		Obj selenium;
		if (gridMode) {
			boolean hubOnly = m.isAnnotationPresent(LocalOnly.class);
			selenium = startBrowser(Obj.SELENIUM_RUN_TIME_MODE_GRID, testMethodName, !failedToFindHubNode && hubOnly);

			drivers.put(testMethodName, selenium);
		}
	}

	private void setupEnvironment() {
		setupEnvironmentBeforeConfigLoad();
		environmentInfo = loadConfiguration();
		setEnvironment(environmentInfo);
		setRootLoggerLevel();
		afterConfigLoad();
		quitStatic = false;
		jenkinsBuild = environmentInfo.jenkinsBuild != null && environmentInfo.jenkinsBuild.equals("true");
		remote = new AutoRemote();
		if (!jenkinsBuild) {
			remote.start();
		}
		if (environmentInfo.testRunMode != null && environmentInfo.testRunMode.equals("true")) {
			environmentInfo.testRunIDs = environmentInfo.testRunID;
		}

		System.out.println(environmentInfo);
		runPreconditions = environmentInfo.runPreconditions.toLowerCase().equals("true");
		gridMode = environmentInfo.gridMode.toLowerCase().equals("true");
		if (gridMode) {
			try {
				Obj s = startBrowser(Obj.SELENIUM_RUN_TIME_MODE_GRID, "testHub", true);
				s.quit();
			} catch (Exception e) {
				e.printStackTrace();
				String exampleJSON = "{\n  \"capabilities\":\n\t  [\n\t\t{\n\t\t  \"browserName\": \"firefox\",\n\t\t  \"version\": \"24\",\n\t\t  \"platform\": \"WINDOWS\",\n\t\t  \"seleniumRunMode\": \"Hub\",\n\t\t  \"seleniumProtocol\": \"WebDriver\",\n\t\t  \"maxInstances\": 1,\n\t\t  \"applicationName\": \"hub\"\n\t\t},\n\t\t{\n\t\t  \"browserName\": \"chrome\",\n\t\t  \"maxInstances\": 1,\n\t\t  \"seleniumProtocol\": \"WebDriver\",\n\t\t  \"applicationName\": \"hub\"\n\t\t},\n\t\t{\n\t\t  \"platform\": \"WINDOWS\",\n\t\t  \"browserName\": \"internet explorer\",\n\t\t  \"maxInstances\": 1,\n\t\t  \"seleniumProtocol\": \"WebDriver\",\n\t\t  \"applicationName\": \"hub\"\n\t\t}\n\t  ],\n  \"configuration\":\n  {\n\t\"proxy\": \"org.openqa.grid.selenium.proxy.DefaultRemoteProxy\",\n\t\"maxSession\": 1,\n\t\"port\": 5555,\n\t\"host\": ip,\n\t\"register\": true,\n\t\"registerCycle\": 5000,\n\t\"hubPort\": 4444,\n\t\"nodeTimeout\": 20\n  }\n}";
				System.err
						.println("Error finding the hub node. The hub node must be configured with capability \"applicationName\":\"hub\" in the SeleniumNodeConfigHub.json config file.\nExample:\n"
								+ exampleJSON + "\nDisabling local only tests. @LocalOnly tests will run on any node.");
				failedToFindHubNode = true;
			}
		} else {
			staticSelenium = startBrowser(Obj.SELENIUM_RUN_TIME_MODE_LOCAL, "preconditions", false);
			setStaticSelenium(staticSelenium);
		}
	}

	protected Obj getSelenium() {
		if (automationController.throwExceptions()) {
			throw new RuntimeException("Fail test cases requested");
		}

		String testMethodName = JavaHelpers.getTestCaseMethodName();

		if (gridMode) {
			Obj selenium = drivers.get(testMethodName);
			if (selenium == null) {
				throw new NullPointerException("Could not find Obj for test case: " + testMethodName);
			}
			return selenium;

		} else {
			return staticSelenium;
		}
	}

	private EnvironmentInfo loadConfiguration() {
		ConfigAdapter config = ConfigAdapterInit.getConfig(getConfigComponent());
		config.readSettingsFromFile();
		environmentInfo = config.getEnvironmentInfo();
		return environmentInfo;
	}

	private Obj startBrowser(String seleniumRunTime, String testCaseId, boolean hubOnly) {
		Map<String, Object> capabilities = new HashMap<String, Object>();
		if (hubOnly) {
			capabilities.put("applicationName", "hub");
		}
		Obj s = startBrowser(seleniumRunTime, testCaseId, capabilities);
		if (environmentInfo.userIntervention.equals("true")) {
			s.driver.allowUserIntervention();
		}
		s.Page.openURL(environmentInfo.url);
		s.driver.setRemote(remote);
		return s;
	}

	@AfterMethod(alwaysRun = true)
	public synchronized void abstractTearDownAfterTest(ITestResult result) {
		if (result.getStatus() == ITestResult.SKIP) {
			String message = result.toString();
			if (result.getThrowable() != null) {
				message = JavaHelpers.getStackTrace(result.getThrowable());
			}
			throw new RuntimeException("Skipped, " + message);
		}
		TestResult testResult;
		String message = "";
		Obj selenium = drivers.get(result.getName());

		if (!gridMode) {
			selenium = staticSelenium;
		}
		if (!result.isSuccess()) {
			wasFailure = true;
			if (selenium != null) {
				try {
					System.out.println(DateHelpers.getCurrentDateAndTime() + " " + result.getName() + " failure screenshot taken");
					selenium.Page.captureScreenshot(Project.screenshots("") + result.getName() + ".png");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			testResult = TestResult.FAIL;
			if (result.getThrowable() != null) {
				message = JavaHelpers.getStackTrace(result.getThrowable());
			}
		} else {
			testResult = TestResult.PASS;
			message = "***Automation Test Result***";
		}
		if (selenium == null) {
			System.err.println("Could not find driver matching test case " + result.getName());
		}
		if (gridMode && selenium != null) {
			try {
				selenium.Page.quitDriver();
			} catch (Exception e) {
				System.out.println("Failed closing browser for test case: " + result.getName() + "\n" + JavaHelpers.getStackTrace(e));
			}
		}
		if (environmentInfo.testRunMode.equals("true")) {
			// append a url to the screenshot of failure
			// requires FileServer.jar to be running on the machine
			// M:\QualityAssurance\Automation\AutomationServers\fileserver
			if (testResult.equals(TestResult.FAIL)) {
				try {
					String artifacts = "Automation_artifacts/" + System.getenv("BUILD_ID") + "_" + System.getenv("JOB_NAME") + "_"
							+ System.getenv("BUILD_NUMBER");
					message += "\nhttp://" + InetAddress.getLocalHost().getHostName() + ":8080/" + artifacts + "/test-screenshots/"
							+ result.getName() + ".png";
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			sendTestResult(result.getName(), testResult, message, environmentInfo);
		}

	}

	@AfterSuite(alwaysRun = true)
	public void abstractTearDownAfterSuiteCompletes() {
		if ((!wasFailure || jenkinsBuild)) {
			Obj.quitAllBrowsers();
		}
		if (automationController != null) {
			automationController.stopServer();
		}
		if (remote != null) {
			remote.stop = true;
		}

		if (performanceTest) {
			testRunMonitor.stop();
			RunResults runResults = new RunResults();
			runResults.runId = testRunId;
			runResults.start = testRunMonitor.getFirstAccess().toString();
			runResults.end = testRunMonitor.getLastAccess().toString();
			runResults.environment = environmentInfo.url;
			runResults.codeBranch = environmentInfo.codeBranch;
			runResults.codeRevision = environmentInfo.codeRevision;
			runResults.browser = environmentInfo.browser + ": " + browserVersion;
			runResults.testClient = environmentInfo.testClient;
			runResults.testNotes = environmentInfo.testNotes;

			if (!debug) {
				for (ReportListener reporter : reporters) {
					reporter.handleResults(runResults);
				}
			} else {
				System.out.println(runResults);
			}
		}
	}

	protected void openLoginPage() {
		getSelenium().Page.openURL(environmentInfo.url);
	}

	/**
	 * Runs a performance test sampleSize number of times. If any of the of runs throws an exception, the test will fail.
	 * 
	 * @param test
	 */
	public void runPerformanceTest(String testDescription, PerformanceTest test) {
		performanceTest = true;
		browserVersion = getSelenium().driver.getVersion();
		List<Throwable> exceptions = new ArrayList<Throwable>();
		List<Integer> iteration = new ArrayList<Integer>();
		this.testDescription = testDescription;
		for (int i = 0; i < sampleSize; i++) {
			try {
				test.performanceTestImpl();
			} catch (Exception e) {
				exceptions.add(e);
				iteration.add(i);
				getSelenium().Page.captureScreenshot(Project.screenshots(JavaHelpers.getTestCaseMethodName() + "." + i + ".png"));
			} finally {
				getSelenium().Page.openURL(environmentInfo.url);
			}
		}
		if (exceptions.size() > 0) {
			for (int i = 0; i < exceptions.size(); i++) {
				System.err.println("Exception " + iteration.get(i));
				exceptions.get(i).printStackTrace();
			}
			if (exceptions.size() >= sampleSize) {
				throw new RuntimeException(exceptions.get(0));
			}
		}
	}

	public void startTimer() {
		testId = UUID.randomUUID().toString();
		testCaseMonitor = monitors.get(JavaHelpers.getTestCaseMethodName());
		if (testCaseMonitor == null) {
			testCaseMonitor = MonitorFactory.start(testId + "-" + testDescription);
			monitors.put(JavaHelpers.getTestCaseMethodName(), testCaseMonitor);
		} else {
			testCaseMonitor.start();
		}
		if (debug) {
			System.out.println(JavaHelpers.getTestCaseMethodName() + " Start");
		}
	}

	public void stopTimer() {
		testCaseMonitor.stop();
		if (debug) {
			System.out.println(JavaHelpers.getTestCaseMethodName() + " Stop");
		}
	}

	public void reportTestResults() {

		TestResults testResults = new TestResults();
		testResults.testId = testId;
		testResults.runId = testRunId;
		testResults.testCase = JavaHelpers.getTestCaseMethodName();
		testResults.testDescription = this.testDescription;
		testResults.hits = "" + testCaseMonitor.getHits();
		testResults.lastValue = "" + testCaseMonitor.getLastValue();
		testResults.min = "" + testCaseMonitor.getMin();
		testResults.max = "" + testCaseMonitor.getMax();
		testResults.avg = "" + testCaseMonitor.getAvg();
		testResults.std_dev = "" + testCaseMonitor.getStdDev();
		testResults.total = "" + testCaseMonitor.getTotal();
		testResults.start = "" + testCaseMonitor.getFirstAccess();
		testResults.end = "" + testCaseMonitor.getLastAccess();
		if (!debug) {
			for (ReportListener reporter : reporters) {
				reporter.handleReport(testResults);
			}
		} else {
			System.out.println(testResults);
		}
		testCaseMonitor = null;
		testId = null;
	}

	static {
		dbParams = new DBParams();
		dbParams.dbName = "cpmPerformance";
		dbParams.dbServer = "DFQACPM10";
		dbParams.user = "postgres";
		dbParams.password = "";

		// add default reporter
		addPerformanceReporter(new ReportListener() {

			@Override
			public void handleResults(RunResults runResults) {

				StringBuilder query = new StringBuilder();
				query.append("INSERT INTO test_runs (");
				query.append("run_id, ");
				query.append("start_time, ");
				query.append("end_time, ");
				query.append("env, ");
				query.append("branch, ");
				query.append("revision, ");
				query.append("browser, ");
				query.append("client, ");
				query.append("notes) ");
				query.append("VALUES ('");
				query.append(runResults.runId).append("', '");
				query.append(runResults.start).append("', '");
				query.append(runResults.end).append("', '");
				query.append(runResults.environment).append("', '");
				query.append(runResults.codeBranch).append("', '");
				query.append(runResults.codeRevision).append("', '");
				query.append(runResults.browser).append("', '");
				query.append(runResults.testClient).append("', '");
				query.append(runResults.testNotes).append("');");
				Database.executeUpdatePSQL(dbParams, query.toString());
			}

			@Override
			public void handleReport(TestResults testResults) {

				StringBuilder query = new StringBuilder();
				query.append("INSERT INTO tests (");
				query.append("test_id, ");
				query.append("run, ");
				query.append("test_case_id, ");
				query.append("page_name,");
				query.append("hits, ");
				query.append("last_value, ");
				query.append("min, ");
				query.append("max, ");
				query.append("avg, ");
				query.append("std_dev, ");
				query.append("total, ");
				query.append("first_access, ");
				query.append("last_access, ");
				query.append("active, ");
				query.append("max_active, ");
				query.append("avg_active, ");
				query.append("avg_global_active, ");
				query.append("avg_primary_active)");
				query.append("VALUES ('");
				query.append(testResults.testId).append("', '");
				query.append(testResults.runId).append("', '");
				query.append(testResults.testCase).append("', '");
				query.append(testResults.testDescription.replaceAll("'", "")).append("', '");
				query.append(testResults.hits).append("', '");
				query.append(testResults.lastValue).append("', '");
				query.append(testResults.min).append("', '");
				query.append(testResults.max).append("', '");
				query.append(testResults.avg).append("', '");
				query.append(testResults.std_dev).append("', '");
				query.append(testResults.total).append("', '");
				query.append(testResults.start).append("', '");
				query.append(testResults.end).append("',0,0,0,0,0);");
				Database.executeUpdatePSQL(dbParams, query.toString());
			}

		});
		addPerformanceReporter(new ReportListener() {

			@Override
			public void handleResults(RunResults runResults) {
				File runReport = new File(Project.downloads("performanceTest.csv"));

				StringBuilder query = new StringBuilder();
				query.append("run_id,start_time,end_time,env,branch,revision,browser,client,notes\n");
				query.append(runResults.runId).append(",");
				query.append(runResults.start).append(",");
				query.append(runResults.end).append(",");
				query.append(runResults.environment).append(",");
				query.append(runResults.codeBranch).append(",");
				query.append(runResults.codeRevision).append(",");
				query.append(runResults.browser).append(",");
				query.append(runResults.testClient).append(",");
				query.append(runResults.testNotes + "\n");

				try {
					FileUtils.write(runReport, query.toString(), true);
					System.out.println("Saved run results in " + runReport.getAbsolutePath());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void handleReport(TestResults testResults) {
				File runReport = new File(Project.downloads("performanceTest.csv"));

				StringBuilder query = new StringBuilder();
				query.append("test_id,run,test_case_id,page_name,hits,last_value,min,max,avg,std_dev,total,first_access,last_access\n");
				query.append(testResults.testId).append(",");
				query.append(testResults.runId).append(",");
				query.append(testResults.testCase).append(",");
				query.append(testResults.testDescription).append(",");
				query.append(testResults.hits).append(",");
				query.append(testResults.lastValue).append(",");
				query.append(testResults.min).append(",");
				query.append(testResults.max).append(",");
				query.append(testResults.avg).append(",");
				query.append(testResults.std_dev).append(",");
				query.append(testResults.total).append(",");
				query.append(testResults.start).append(",");
				query.append(testResults.end + "\n");
				try {
					FileUtils.write(runReport, query.toString(), true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
	}

	public static void setPerformanceDatabaseCredentials(DBParams d) {
		dbParams = d;
	}

	/**
	 * In debug mode, starts and stops are printed, measurements are printed instead of reporting
	 * 
	 * @param d
	 */
	public static void setPerformanceDebug(boolean d) {
		debug = d;
	}

	/**
	 * Add a callback to report results when a test is completed
	 * 
	 * @param r
	 */
	public static void addPerformanceReporter(ReportListener r) {
		reporters.add(r);
	}
	
	private void setRootLoggerLevel(){
		String logLevel = environmentInfo.logLevel;
		if(!logLevel.equals("DEBUG")){
			LogManager.getRootLogger().setLevel(Level.toLevel(logLevel));
		}
	}
}
