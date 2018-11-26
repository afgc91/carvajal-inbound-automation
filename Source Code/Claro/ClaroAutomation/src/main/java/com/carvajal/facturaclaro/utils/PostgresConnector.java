package com.carvajal.facturaclaro.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgresConnector {

	// private static InputStream inputStream;
	private static String url;
	private static String user;
	private static String port;
	private static String nameDB;
	private static String password;
	public static Connection con;

	/**
	 * Permite la conexión a la BD
	 * 
	 * @return conexión a la BD
	 * @throws SQLException           Si se presenta un error en la conexión a la BD
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */

	public static void getConnetion() throws SQLException, ClassNotFoundException, IOException {

		Properties property = new Properties();
		String properyFileName = "config.properties.txt";
		// InputStream inputStream =
		// getClass.getClassLoader().getResourceAsStream(properyFileName);
		InputStream inputStream = new FileInputStream(properyFileName);
		property.load(inputStream);

		url = property.getProperty("url");
		port = property.getProperty("port");
		nameDB = property.getProperty("nameDB");
		user = property.getProperty("user");
		password = property.getProperty("password");

		String jdbc = "jdbc:postgresql://";
		con = DriverManager.getConnection(jdbc+url+":"+port+"/"+nameDB, user, password);

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.err.println("PostgreSQL DataSource unable to load PostgreSQL JDBC Driver");
		}

	}
}
