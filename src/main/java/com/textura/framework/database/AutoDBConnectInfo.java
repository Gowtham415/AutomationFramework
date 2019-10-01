package com.textura.framework.database;

import com.textura.framework.utils.DBParams;

public class AutoDBConnectInfo {

	public static DBParams getAutoDBConnectInfo(String productName) {
		DBParams params = new DBParams();
		params.dbName = "Automation_" + productName;
		params.dbServer = "dfwin7qaauto53";
		//params.dbServer = "localhost";
		params.user = "Automation";
		params.password = "QAautomation7";
		return params;
	}
}