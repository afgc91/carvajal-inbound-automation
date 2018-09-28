package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CarvajalPostgresConnection {

	private String url;
	private String user;
	private String password;

	/**
	 * Constructor de objeto para conectarse a una BD Postgres
	 * 
	 * @param url      Direcci�n a la BD
	 * @param user     usuario para conexi�n a la BD
	 * @param password contrase�a para conexi�n a la BD
	 */

	public CarvajalPostgresConnection(String url, String user, String password) {

		this.url = url;
		this.user = user;
		this.password = password;

	}

	/**
	 * Permite la conexi�n a la BD
	 * 
	 * @param dbType Tipo de base de datos a la cual se desea conectar. 1, Postgres;
	 *               2, Oracle
	 * @return conexi�n a la BD
	 * @throws SQLException Si se presenta un error en la conexi�n a la BD
	 * @throws ClassNotFoundException 
	 */

	public Connection getConnetion(int dbType) throws SQLException, ClassNotFoundException {
		if (dbType == 1) {
			String jdbc = "jdbc:postgres://";
			Connection con = DriverManager.getConnection(jdbc + url, user, password);
			return con;
		} else if (dbType == 2) {
			
			String jdbc = "jdbc:oracle:thin:@";
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection con = DriverManager.getConnection(jdbc + url, user, password);
			return con;
		}
		return null;
	}

}
