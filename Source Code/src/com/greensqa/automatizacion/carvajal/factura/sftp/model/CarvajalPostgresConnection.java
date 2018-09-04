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
	 * @param url Dirección a la BD 
	 * @param user usuario para conexión a la BD
	 * @param password contraseña para conexión a la BD
	 */
	
	public CarvajalPostgresConnection(String url, String user, String password) {
		
		this.url = url; 
		this.user = user;
		this.password = password;
		
	}
	
	/**
	 * Permite la conexión a la BD 
	 * @return conexión a la BD 
	 * @throws SQLException Si se presenta  un error en la conexiòn a la BD
	 */
	
	public Connection getConnetion() throws SQLException {
		
		Connection con=DriverManager.getConnection(url, user, password);
		return con; 
		
	}	
	
}
