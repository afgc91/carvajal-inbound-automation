package com.carvajal.facturaclaro.ral.dto;

public class AuthorizationDTO {
	
 private LoginDTO login; 
 private ActivationDTO activation;
 private NotificationDTO notificacion;
 private String testCase; 
 private String pathFile;
 private String pathSftp; 
 private String confiDB; 
 private String confiSftp;
 

public String getConfiDB() {
	return confiDB;
}
public void setConfiDB(String confiDB) {
	this.confiDB = confiDB;
}
public String getConfiSftp() {
	return confiSftp;
}
public void setConfiSftp(String confiSftp) {
	this.confiSftp = confiSftp;
}
public LoginDTO getLogin() {
	return login;
}
public void setLogin(LoginDTO login) {
	this.login = login;
}
public ActivationDTO getActivation() {
	return activation;
}
public void setActivation(ActivationDTO activation) {
	this.activation = activation;
}
public NotificationDTO getNotificacion() {
	return notificacion;
}
public void setNotificacion(NotificationDTO notificacion) {
	this.notificacion = notificacion;
}
public String getTestCase() {
	return testCase;
}
public void setTestCase(String testCase) {
	this.testCase = testCase;
}
public String getPathFile() {
	return pathFile;
}
public void setPathFile(String pathFile) {
	this.pathFile = pathFile;
}
public String getPathSftp() {
	return pathSftp;
}
public void setPathSftp(String pathSftp) {
	this.pathSftp = pathSftp;
} 
}
