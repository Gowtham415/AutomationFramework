package com.textura.framework.utils;

public class DBParams {

	public String dbServer = "";
	public String dbName = "";
	public String user = "";
	public String password = "";

	public String toString() {
		String result = "dbServer: " + dbServer + "\ndbName: " + dbName + "\nuser: " + user + "\npassword: " + password;
		return result;
	}
}