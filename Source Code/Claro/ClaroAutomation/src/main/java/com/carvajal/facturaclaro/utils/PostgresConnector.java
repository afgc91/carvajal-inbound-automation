package com.carvajal.facturaclaro.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import com.carvajal.facturaclaro.ral.dto.ActivationDTO;
import com.carvajal.facturaclaro.ral.dto.AuthorizationDTO;

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

	public static void getConnetion(String dbPath){
		try {
			Properties property = new Properties();
			InputStream inputStream = new FileInputStream(dbPath);
			property.load(inputStream);

			url = property.getProperty("url");
			port = property.getProperty("port");
			nameDB = property.getProperty("nameDB");
			user = property.getProperty("user");
			password = property.getProperty("password");

			String jdbc = "jdbc:postgresql://";
			con = DriverManager.getConnection(jdbc + url + ":" + port + "/" + nameDB, user, password);

			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("PostgreSQL DataSource unable to load PostgreSQL JDBC Driver");
		} catch (SQLException e) {
			System.out.println("El intento de conexión falló, revisar la conexión a la VPN o los datos de conexión");
		} catch (IOException e) {
			System.out.println("No se logró leer el archivo Properties de entrada, verique la ruta del archivo");
		}
	}
}