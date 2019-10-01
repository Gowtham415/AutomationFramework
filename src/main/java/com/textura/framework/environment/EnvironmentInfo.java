package com.textura.framework.environment;

import java.lang.reflect.Field;
import com.textura.framework.utils.DBParams;

/**
 * This class represents variables common to any Product. For each Product, there will be a class that inherits this one.
 * 
 */
public class EnvironmentInfo {

	public String product;
	public String server;
	public String url;
	public String browser;
	public String testbed;
	public String environment;
	public String userPassword;
	public Long timeout = new Long(15);
	public String gridMode;
	public String gridURL;
	public String gridNodes;
	public String gridServer;
	public String gridPort;
	public String userIntervention = "false";
	public String jenkinsBuild;
	public String sampleSize;
	public String codeBranch;
	public String codeRevision;
	public String testClient;
	public String testNotes;
	public String runPreconditions = "false";
	public String testRunIDs;
	public String testSuites;
	public String testRerunID;
	public String testGroups;
	public String testMilestoneID;
	public String testRunMode;
	public String testRerunMode;
	public String testFile;
	public String testRunID;
	public String testMilestoneMode;
	public String testCases;
	public String testPlanMode;
	public String testrailProjectID;
	public String email;
	public String proxyMode;
	public String proxy;
	public String testRerun;
	public String portalNode;
	public Integer testsCurrent = 0;
	public Integer testsExecuted = 0;
	public String logLevel = "DEBUG";
	public String activeDirectoryUsername;
	public String activeDirectoryPassword;

	public String toString() {
		StringBuilder result = new StringBuilder();
		Field[] fields = this.getClass().getFields();
		for (Field f : fields) {
			try {
				if (f.getType().equals(String.class)) {
					result.append(f.getName() + ": " + f.get(this) + "\n");
				} else if (f.getType().equals(DBParams.class)) {
					result.append(f.getName() + ": " + f.get(this) + "\n");
				} else if (f.getType().equals(Integer.class)) {
					result.append(f.getName() + ": " + f.get(this) + "\n");
				} else {
					result.append(f.getType().getSimpleName() + " " + f.getName() + " " + f.get(this).toString() + "\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result.toString();
	}
}