package com.serverless.products.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utilities {

	public static final String hostName = "<HOST_NAME>";
	public static final String username = "<USERNAME>";
	public static final String password = "<PASSWORD>";
	public static final String datasourceName = "<DATASOURCE>";
	public static final String accessKey = "<ACCESS_KEY>";
	public static final String secretKey = "<SECRET_KEY>";

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

	public static AmazonS3Client getS3Client() {
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		AmazonS3Client client = new AmazonS3Client(credentials);
		return client;
	}
	
	public static Gson getGson() {
		return new GsonBuilder().create();
	}
	
	public static <T> String asJson(Object source, Class<T> type) {
		return new GsonBuilder().create().toJson(source, type);
	}
}
