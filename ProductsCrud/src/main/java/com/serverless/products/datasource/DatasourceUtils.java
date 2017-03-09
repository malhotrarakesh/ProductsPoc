package com.serverless.products.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

public class DatasourceUtils {

	public static final String hostName = "testdatabase.cnywwrxz3rfd.us-east-1.rds.amazonaws.com";
	public static final String username = "TestUsername";
	public static final String password = "TestPassword";
	public static final String datasourceName = "ProductsPocDB";

	public static Connection getConnnection() throws Exception {
		String dbURL = "jdbc:mysql://" + hostName + "/" + datasourceName + "?user=" + username + "&password=" + password;
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection(dbURL);
	}

	public static void closeResources(Connection connection, ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (Exception e) {
				// Do nothing
			}
		}

		if (connection != null) {
			try {
				connection.close();
			} catch (Exception e) {
				// Do nothing
			}

		}

	}
}
