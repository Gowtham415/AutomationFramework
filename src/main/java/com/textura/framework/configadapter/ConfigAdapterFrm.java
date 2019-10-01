package com.textura.framework.configadapter;

import com.textura.framework.environment.EnvironmentInfo;
import com.textura.framework.testng.TestSuitesBuilderApp;
import com.textura.framework.testng.TestngConfig;

public class ConfigAdapterFrm extends ConfigAdapter {

	//Framework related
	private String jenkinsJobUrl;
	private String jenkinsBuildUrl;
	private String jenkinsBuildNum;
	private String jenkinsBuildID;
	private String jenkinsJobName;
	private String svnUrl;
	private String svnRevision;
	private String sysOSName;
	private String javaHome;
	private String javaVersion;
	
	public ConfigAdapterFrm(ConfigComponents component) {
		
		testSuitesCreator = new TestSuitesBuilderApp();
		appPath = component.pathProject();
	}

	@Override
	public void readSettingsFromEnv() {

		jenkinsBuildUrl = env.get("BUILD_URL");
		jenkinsJobUrl = env.get("JOB_URL");
		jenkinsBuildNum = env.get("BUILD_NUMBER");
		jenkinsBuildID = env.get("BUILD_ID");
		jenkinsJobName = env.get("JOB_NAME");
		svnUrl = env.get("SVN_URL");
		svnRevision = env.get("SVN_REVISION");
		sysOSName = env.get("os.name");
		javaHome = env.get("JAVA_HOME");
		javaVersion = env.get("java.version");
	}
	
	@Override
	public void readSettingsFromEnvPQM() {
		
		jenkinsBuildUrl = env.get("BUILD_URL");
		jenkinsJobUrl = env.get("JOB_URL");
		jenkinsBuildNum = env.get("BUILD_NUMBER");
		jenkinsBuildID = env.get("BUILD_ID");
		jenkinsJobName = env.get("JOB_NAME");
		svnUrl = env.get("SVN_URL");
		svnRevision = env.get("SVN_REVISION");
		sysOSName = env.get("os.name");
		javaHome = env.get("JAVA_HOME");
		javaVersion = env.get("java.version");
		
	}
	
	@Override
	public void readSettingsFromFile() {
		
	}

	@Override
	public void validateSettings() {
		
	}

	@Override
	public void createSettingsFile() {
		
	}

	@Override
	public void printSettings() {

		System.out.println("Jenkins build information:");
		System.out.println(" Build Url : " + jenkinsBuildUrl );
		System.out.println(" Job Url   : " + jenkinsJobUrl);
		System.out.println(" Build Num : " + jenkinsBuildNum);
		System.out.println(" Build ID  : " + jenkinsBuildID);
		System.out.println(" Job Name  : " + jenkinsJobName);
		System.out.println(" SVN Url   : " + svnUrl);
		System.out.println(" SVN Rev.  : " + svnRevision);
		System.out.println(" OS Name   : " + sysOSName);
		System.out.println(" Java Home : " + javaHome);
		System.out.println(" Java Ver. : " + javaVersion);
	}
	
	@Override
	public void cleanEnv() {

	}

	@Override
	public TestngConfig getTngConfig() {
		return testngConfig;
	}

	@Override
	public EnvironmentInfo getEnvironmentInfo() {
		return null;
	}
}
