package com.textura.framework.environment.productenvironments;

import com.textura.framework.environment.EnvironmentInfo;

public class TCSEnvironmentInfo extends EnvironmentInfo {

	public String tcs_url;
	public String tcs_appserver;
	//public String tcs_computername = System.getenv().get("COMPUTERNAME");
	public String tcs_databaseserver;
	public String tcs_databaseport;
	public String tcs_databaseext;
	public String tcs_databaselogin;
	public String tcs_databasepassword;
	
	public String getAppServer() {
		return this.tcs_appserver;
	}
	
	public String getDatabaseExt() {
		return this.tcs_databaseext;
	}

	public String getDatabaseLogin() {
		return this.tcs_databaselogin;
	}

	public String getDatabasePassword() {
		return this.tcs_databasepassword;
	}

	public String getDatabasePort() {
		return this.tcs_databaseport;
	}

	public String getDatabaseServer() {
		return this.tcs_databaseserver;
	}

	public String getURL() {
		return this.tcs_url;
	}
}