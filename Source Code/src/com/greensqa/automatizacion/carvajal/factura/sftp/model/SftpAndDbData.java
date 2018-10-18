package com.greensqa.automatizacion.carvajal.factura.sftp.model;

public class SftpAndDbData {
	//Atributos SFTP
	private String userSftp;
	private String passwordSftp;
	private String urlSftp;
	private int portSftp;
	private String destSftp;
	
	//Atributos conexión BD
	private int tipoBD; 
	private String userDb;
	private String passwordDb;
	private String urlDb;
	private int portDb;
	
	public SftpAndDbData(String userSftp, String passwordSftp, String urlSftp, int portSftp, String destSftp, int tipoBD,	String userDb, String passwordDb, String urlDb, int portDb) {

		this.userSftp = userSftp;
		this.passwordSftp = passwordSftp;
		this.urlSftp = urlSftp;
		this.portSftp = portSftp;
		this.destSftp = destSftp;
		this.tipoBD = tipoBD; 
		this.userDb = userDb;
		this.passwordDb = passwordDb;
		this.urlDb = urlDb;
		this.portDb = portDb;
	}

	public String getUserSftp() {
		return userSftp;
	}

	public void setUserSftp(String userSftp) {
		this.userSftp = userSftp;
	}

	public String getPasswordSftp() {
		return passwordSftp;
	}

	public void setPasswordSftp(String passwordSftp) {
		this.passwordSftp = passwordSftp;
	}

	public String getUrlSftp() {
		return urlSftp;
	}

	public void setUrlSftp(String urlSftp) {
		this.urlSftp = urlSftp;
	}

	public int getPortSftp() {
		return portSftp;
	}

	public void setPortSftp(int portSftp) {
		this.portSftp = portSftp;
	}

	public String getDestSftp() {
		return destSftp;
	}

	public void setDestSftp(String destSftp) {
		this.destSftp = destSftp;
	}

	public String getUserDb() {
		return userDb;
	}

	public void setUserDb(String userDb) {
		this.userDb = userDb;
	}

	public String getPasswordDb() {
		return passwordDb;
	}

	public void setPasswordDb(String passwordDb) {
		this.passwordDb = passwordDb;
	}

	public String getUrlDb() {
		return urlDb;
	}

	public void setUrlDb(String urlDb) {
		this.urlDb = urlDb;
	}

	public int getPortDb() {
		return portDb;
	}

	public void setPortDb(int portDb) {
		this.portDb = portDb;
	}

	public int getTipoBD() {
		return tipoBD;
	}

	public void setTipoBD(int tipoBD) {
		this.tipoBD = tipoBD;
	}
}
