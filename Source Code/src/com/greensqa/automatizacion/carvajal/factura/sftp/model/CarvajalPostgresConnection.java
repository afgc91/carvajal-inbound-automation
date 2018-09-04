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
	 * @param url Direcci�n a la BD 
	 * @param user usuario para conexi�n a la BD
	 * @param password contrase�a para conexi�n a la BD
	 */
	
	public CarvajalPostgresConnection(String url, String user, String password) {
		
		this.url = url; 
		this.user = user;
		this.password = password;
		
	}
	
	/**
	 * Permite la conexi�n a la BD 
	 * @return conexi�n a la BD 
	 * @throws SQLException Si se presenta  un error en la conexi�n a la BD
	 */
	
	public Connection getConnetion() throws SQLException {
		
		Connection con=DriverManager.getConnection(url, user, password);
		return con; 
		
	}	
	
}
