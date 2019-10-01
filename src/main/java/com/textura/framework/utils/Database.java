package com.textura.framework.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.textura.framework.environment.Project;
import com.textura.framework.environment.productenvironments.CPMEnvironmentInfo;
import com.textura.framework.objects.main.Obj;

public class Database {

	public static CPMEnvironmentInfo environment;

	public static void setCPMEnvironment(CPMEnvironmentInfo environmentInfo) {
		environment = environmentInfo;
	}

	@SuppressWarnings("resource")
	public static String queryResultPSQL(DBParams params, String query) {
		ResultSet rid = null;
		Connection conn = null;
		String result = null;
		String url = "jdbc:postgresql://" + params.dbServer + "/";
		String driver = "org.postgresql.Driver";

		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url + params.dbName, params.user, params.password);
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			conn.setAutoCommit(false);
			String method = Thread.currentThread().getStackTrace()[2].getMethodName();
			if (method.matches("c[0-9]*")) {
				if (!query.contains("desc") && !query.contains("DESC") && !query.contains("asc") && !query.contains("ASC"))
					query = query.replaceAll(";", "") + " ORDER BY 1 ASC;";
				rid = stmt.executeQuery(query);
			} else {
				rid = stmt.executeQuery(query);
			}
			for (int x = 1; x <= 5; x++) {
				Thread.sleep(1000);
				if (rid.first() == false) {
					rid = stmt.executeQuery(query);
				} else {
					break;
				}
			}
			ResultSetMetaData rsmd = rid.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();
			String columnValue = "";

			if (rid.first() && !(rid.next())) {
				rid.first();
				for (int i = 1; i <= numberOfColumns; i++) {
					if (i > 1)
						columnValue = columnValue + ",";

					columnValue = columnValue + rid.getString(i);
				}
				result = columnValue;
			} else {
				rid.beforeFirst();
				while (rid.next()) {
					for (int i = 1; i <= numberOfColumns; i++) {
						if (i > 1)
							columnValue = columnValue + ",";

						columnValue = columnValue + rid.getString(i);
					}
					columnValue = columnValue + "\n";
				}
				result = columnValue;
			}
			conn.commit();
			conn.close();
			return result.trim();

		} catch (Exception e) {
			System.err.println("Error running a PSQL query");
			System.err.println(params);
			e.printStackTrace();
			return null;
		}
	}

	// This method is used to check queries that are not supposed to successfully execute
	public static String failureUpdateResultPSQL(DBParams params, String update) {
		Connection conn = null;
		String url = "jdbc:postgresql://" + params.dbServer + "/";
		String driver = "org.postgresql.Driver";
		String message = "";
		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url + params.dbName, params.user, params.password);
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			conn.setAutoCommit(true);
			stmt.executeUpdate(update);
			message = "Query executed without failure.";
			conn.commit();
			conn.close();
		} catch (Exception e) {
			message = e.getMessage();

		}

		return message;

	}
	public static String queryResultMySQL(DBParams params, String query) {
		Connection conn = null;
		String result = null;
		String url = "jdbc:mysql://" + params.dbServer + ":3306/";
		String driver = "com.mysql.jdbc.Driver";

		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url + params.dbName, params.user, params.password);
			Statement stmt = conn.createStatement();
			conn.setAutoCommit(false);
			ResultSet rid = stmt.executeQuery(query);
			ResultSetMetaData rsmd = rid.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();
			String columnValue = "";

			if (rid.first() && !(rid.next())) {
				rid.first();
				for (int i = 1; i <= numberOfColumns; i++) {
					if (i > 1)
						columnValue = columnValue + ",";

					columnValue = columnValue + rid.getString(i);
				}
				result = columnValue;
			} else {
				rid.beforeFirst();
				while (rid.next()) {
					for (int i = 1; i <= numberOfColumns; i++) {
						if (i > 1)
							columnValue = columnValue + ",";

						columnValue = columnValue + rid.getString(i);
					}
					columnValue = columnValue + "\n";
				}
				result = columnValue;
			}

			conn.commit();
			conn.close();
			return result;
		} catch (Exception e) {
			System.err.println("Error running a MySQL query");
			System.err.println(params);
			e.printStackTrace();
			return null;
		}
	}

	public static boolean executeUpdatePSQL(DBParams params, String query) {
		Connection conn = null;
		String url = "jdbc:postgresql://" + params.dbServer + "/";
		String driver = "org.postgresql.Driver";

		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url + params.dbName, params.user, params.password);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
			stmt.close();
			conn.close();
			return true;
		} catch (Exception e) {
			System.err.println("Error running a Postgres update");
			System.err.println(params);
			System.err.println(query);
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * No longer works after psql upgrade
	 * @param params
	 * @param query
	 * @return
	 */
	@Deprecated
	public static int executeReturningInsertPSQL(DBParams params, String query) {
		Connection conn = null;
		String url = "jdbc:postgresql://" + params.dbServer + "/";
		String driver = "org.postgresql.Driver";
		int value = -1;
		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url + params.dbName, params.user, params.password);
			Statement stmt = conn.createStatement();
			stmt.execute(query, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			rs.next();
			value = rs.getInt(1);
			stmt.close();
			conn.close();
		} catch (Exception e) {
			System.err.println("Error running a Postgres update");
			System.err.println(params);
			System.err.println(query);
			e.printStackTrace();
		}
		return value;
	}

	public static boolean executeUpdateMySQL(DBParams params, String query) {
		Connection conn = null;
		String url = "jdbc:mysql://" + params.dbServer + ":3306/";
		String driver = "com.mysql.jdbc.Driver";

		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url + params.dbName, params.user, params.password);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
			stmt.close();
			conn.close();
			return true;
		} catch (Exception e) {
			System.err.println("Error running a MySQL update: " + query);
			e.printStackTrace();
			return false;
		}
	}

	public static String executeSqlLiteQuery(String query) {

		if (environment == null) {
			System.out.println(DateHelpers.getCurrentDateAndTime() + " " + JavaHelpers.getTestCaseMethodName() + " "
					+ "WARNING! Environment was NULL " + environment.pdq_databasename);
			environment = new CPMEnvironmentInfo();
		}
		String output = null;
		String sqlFile = environment.csvus_databasename;
		String cmd = "sqlite3 " + sqlFile + " \"" + query + "\"";

		for (int attempt = 1; attempt < Obj.TIMEOUT_30; attempt++) {
			output = SSHClient.execute(environment.sshUser, environment.sshPassword, environment.pdq_databaseserver, cmd);
			if (output.contains("Error: database is locked")) {
				System.out.println(DateHelpers.getCurrentDateAndTime() + " " + JavaHelpers.getTestCaseMethodName() + " "
						+ "executeSqlLiteQuery Error: database is locked " + environment.pdq_databasename);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				return output;
			}
		}
		return output;
	}

	public static boolean setOrgBankServicePDQ(String name, String job) {
		DBParams params = new DBParams();
		params.dbServer = environment.testDatabase;
		params.dbName = environment.pdq_databasename;
		params.user = "postgres";
		params.password = "";
		String verificationQuery1 = "select name from organization where name='" + name + "';";
		if (queryResultPSQL(params, verificationQuery1).length() < 1) {
			System.err.println(DateHelpers.getCurrentDateAndTime() + " " + JavaHelpers.getTestCaseMethodName() + " PDQ Organization "
					+ name + " not found in database.");
		}
		String query = "UPDATE organization SET create_unbalanced_ach = false, job_id =" + job + " WHERE name= '" + name + "';";
		return executeUpdatePSQL(params, query);
	}

	public static String getOrgBankServicePDQ(String name) {
		String query = "select job_id from organization where name = '" + name + "' and create_unbalanced_ach=false;";

		DBParams params = new DBParams();
		params.dbServer = environment.pdq_databaseserver;
		params.dbName = environment.pdq_databasename;
		params.user = "postgres";
		params.password = "";
		String result = queryResultPSQL(params, query).trim();
		try {
			if (!result.isEmpty())
				Integer.parseInt(result);
		} catch (Exception e) {
			throw new UncheckedExecutionException("PDQ bank service job query did not return an integer. Returned '" + result
					+ "'. Query used was " + query, null);
		}
		return result;
	}

	public static void getOrgBankServicesFromPDQ() {
		String query = "select name, job_id from organization where not(job_id isnull);";
		DBParams params = new DBParams();
		params.dbServer = environment.pdq_databaseserver;
		params.dbName = environment.pdq_databasename;
		params.user = "postgres";
		params.password = "";
		String result = queryResultPSQL(params, query).trim();

		try {
			if (!result.isEmpty()) {
				System.out.println("Create a list of bank services from PDQ database");
				JavaHelpers.writeFile(Project.downloads("PDQBankServices.log"), result);
			} else {
				System.out.println("Create PDQ database - query did not return any bank services");
			}
		} catch (Exception e) {
			throw new UncheckedExecutionException("PDQ bank service query did not return value. Returned '" + result + "'. Query used was "
					+ query, null);
		}				
	}

	public static String getOrgBankServiceFromFile(String organization) {
		List<String> list = JavaHelpers.readFileAsList(Project.downloads("PDQBankServices.log"));
		for (String pair : list) {
			if (pair.substring(0, pair.indexOf(",") + 1).equals(organization + ",")) {
				return pair.substring(pair.indexOf(",") + 1);
			}
		}
		System.out.println(DateHelpers.getCurrentDateAndTime() + " " + JavaHelpers.getTestCaseMethodName()
		+ " PDQ Bank Service NOT FOUND for org " + organization + " list: \n" + list.toString());
		return null;
	}

	public static String getXthResult(String query, DBParams params, int xth) {
		return Database.queryResultPSQL(params, query).split("\n")[xth - 1].trim();
	}

	public static boolean isColumnPresentInTable(DBParams params, String column, String table) {
		String result = Database.queryResultPSQL(params, "select column_name from information_schema.columns where table_name='" + table
				+ "';");
		List<String> columns = Arrays.asList(result.split("\n"));

		if (result == null || !columns.contains(column))
			return false;

		return true;
	}

	public static int numberOfRowsReturnedByQuery(DBParams params, String query) {		
		String resultQuery = Database.queryResultPSQL(params, query);
		return resultQuery.isEmpty()?0:resultQuery.split("\n").length;
	}

	public static int executeUpdateSQLServer(DBParams params, String query) {
		Connection conn = null;
		Statement stmt = null;
		String url = "jdbc:sqlserver://" + params.dbServer + ":1433;databaseName=" + params.dbName;
		String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		int rowsUpdated = 0;
		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url, params.user, params.password);
			stmt = conn.createStatement();
			rowsUpdated = stmt.executeUpdate(query);

			return rowsUpdated;
		} catch (Exception e) {
			System.err.println("Error running a SQL Server update");
			System.err.println(params);
			System.err.println(query);
			e.printStackTrace();
			return rowsUpdated;
		}
		finally{
			try {
				if(stmt != null)
					stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if(conn != null)
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * //Sample Usage:
	 * public static void main(String[] args) {
	 * DBParams params = new DBParams();
	 * String query = "select count(*) from project;";
	 * params.dbServer = "dfqacpm10.texturallc.net";
	 * params.dbName = environment.pdq_databasename;
	 * params.user = "postgres";
	 * params.password = "";
	 * String output = queryResultPSQL(params, query);
	 * System.out.println(output);
	 * }
	 */

	public static String executeCSVusSqlLiteQuery(String query) {

		if (environment == null) {
			System.out.println(DateHelpers.getCurrentDateAndTime() + " " + JavaHelpers.getTestCaseMethodName() + " "
					+ "WARNING! Environment was NULL " + environment.csvus_databasename);
			environment = new CPMEnvironmentInfo();
		}
		String output = null;
		String sqlFile = environment.csvus_databasename;
		String cmd = "sqlite3 " + sqlFile + " \"" + query + "\"";

		for (int attempt = 1; attempt < Obj.TIMEOUT_30; attempt++) {
			output = SSHClient.execute(environment.sshUser, environment.sshPassword, environment.csvus_databaseserver, cmd);
			if (output.contains("Error: database is locked")) {
				System.out.println(DateHelpers.getCurrentDateAndTime() + " " + JavaHelpers.getTestCaseMethodName() + " "
						+ "executeSqlLiteQuery Error: database is locked " + environment.csvus_databasename);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				return output;
			}
		}
		return output;
	}

	public static void setCSVusJobStatusToError(int invoiceNumber) {
		String query = "update item_csvus_importexport_job_v2 SET status = 'error', contentFilePath = null where rowid = " + invoiceNumber;
		if (environment == null) {
			System.out.println(DateHelpers.getCurrentDateAndTime() + " " + JavaHelpers.getTestCaseMethodName() + " "
					+ "WARNING! Environment was NULL " + environment.pdq_databasename);
			environment = new CPMEnvironmentInfo();
		}
		String output = null;
		String sqlFile = environment.csvus_databasename;
		String cmd = "sudo " + "sqlite3 " + sqlFile + " \"" + query + "\"";

		for (int attempt = 1; attempt < Obj.TIMEOUT_30; attempt++) {
			output = SSHClient.execute(environment.sshUser, environment.sshPassword, environment.csvus_databaseserver, cmd);
			if (output.contains("Error: database is locked")) {
				System.out.println(DateHelpers.getCurrentDateAndTime() + " " + JavaHelpers.getTestCaseMethodName() + " "
						+ "executeSqlLiteQuery Error: database is locked " + environment.csvus_databasename);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				return;
			}
		}
	}

	public static void setCSVusJobStatusToPending(int invoiceNumber) {
		String query = "update item_csvus_importexport_job_v2 SET status = 'pending', contentFilePath = null where rowid = "
				+ invoiceNumber;
		if (environment == null) {
			System.out.println(DateHelpers.getCurrentDateAndTime() + " " + JavaHelpers.getTestCaseMethodName() + " "
					+ "WARNING! Environment was NULL " + environment.pdq_databasename);
			environment = new CPMEnvironmentInfo();
		}
		String output = null;
		String sqlFile = environment.csvus_databasename;
		String cmd = "sudo " + "sqlite3 " + sqlFile + " \"" + query + "\"";

		for (int attempt = 1; attempt < Obj.TIMEOUT_30; attempt++) {
			output = SSHClient.execute(environment.sshUser, environment.sshPassword, environment.csvus_databaseserver, cmd);
			if (output.contains("Error: database is locked")) {
				System.out.println(DateHelpers.getCurrentDateAndTime() + " " + JavaHelpers.getTestCaseMethodName() + " "
						+ "executeSqlLiteQuery Error: database is locked " + environment.csvus_databasename);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				return;
			}
		}
	}

	public static boolean executeUpdateOrganizationTransactionFeeScheduleIdPSQL(DBParams params, String transactionFeeScheduleId, String organizationName){
		String query = "update organization set transactionfeescheduleid = '"+transactionFeeScheduleId+"' where name = '"+organizationName+"'";
		return Database.executeUpdatePSQL(params, query);	
	}

	public static boolean executeUpdateProjectTransactionFeeScheduleIdPSQL(DBParams params, String transactionFeeScheduleId, String projectID){
		String query = "update project set transactionfeescheduleid = '"+transactionFeeScheduleId+"' where projectid = '"+projectID+"'";
		return Database.executeUpdatePSQL(params, query);	
	}

	public static String queryContractTransactionFeeScheduleIdPSQL(DBParams params, String contractID){
		return Database.queryResultPSQL(params, "select transactionfeescheduleid from contract where contractid ='"+contractID+"'");
	}

	public static String queryProjectTransactionFeeScheduleIdPSQL(DBParams params, String projectID){
		return Database.queryResultPSQL(params, "select transactionfeescheduleid from project where projectid ='"+projectID+"'");
	}

	public static String queryOrganizationTransactionFeeScheduleIdPSQL(DBParams params, String organizationName){
		return Database.queryResultPSQL(params, "select transactionfeescheduleid from organization where name ='"+organizationName+"'");
	}
	
	public static void executeUpdateCsvus2PSQL(DBParams params, String query) {
		// Changing of params may not be needed once csvus2 is activated and used by application.
		DBParams tempParams = new DBParams();
		String dbServer = environment.testServer;
		String dbName = environment.cpm_databasename;
		
		tempParams.dbServer = dbServer;
		tempParams.dbName = dbName.replace("cpm_", "csvus2_");
		tempParams.user = "csvus2";
		tempParams.password = "";
		Database.executeUpdatePSQL(tempParams, query);
	}
	
	public static String queryResultPSQLWaitForResult(DBParams params, String query, String expectedResult) {
		String result = "";
		for (int i = 0; i < 10; i++) {
			result = queryResultPSQL(params, query);
			if (result.equals(expectedResult)) {
				return result;
			}
			try {
				Thread.sleep(2); // Delay for expected value to appear in db.
			} catch (Exception e) {
				System.out.println("Failure sleeping thread.");
			}
		}
		return result;
	}
	
	public static boolean executeEnableAnalyticsForOrg(DBParams params, String orgId) {
		Connection conn = null;
		String url = "jdbc:postgresql://" + params.dbServer + "/";
		String driver = "org.postgresql.Driver";
		int value = -1;
		String query = "insert into obisettings(organizationid) values ("+orgId+");";
		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url + params.dbName, params.user, params.password);
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(query);
			stmt.close();
			conn.close();
			return true;
		} catch (Exception e) {
			System.err.println("Error running a Postgres update");
			System.err.println(params);
			System.err.println(query);
			e.printStackTrace();
			return false;
		}
	}
}
