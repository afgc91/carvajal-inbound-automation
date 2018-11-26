package com.greensqa.automatizacion.carvajal.factura.sftp.model;

public class WSStructureElement {

	// Atributos petici�n Login
	private String loginUrl;
	private String user;
	private String password;

	// Atributos petici�n Notificaci�n
	private String notificationUrl;
	private long companyId;
	private String account;
	private String path;

	// Atributos petici�n Activaci�n
	private String activationUrl;
	private int action;

	/**
	 * Constructor para WSStructure
	 * 
	 * @param loginURL        URL para la petici�n de login para obtener el token
	 * @param user            Usuario parametrizado para la empresa
	 * @param password        Contrase�a del usuario parametrizado
	 * @param notificationUrl URL para la petici�n de notificaci�n, es decir env�o a procesamiento.
	 * @param companyId       N�mero de identificaci�n de la empresa
	 * @param account         Cuenta del Cliente
	 * @param path            Ruta donde se pondr�n los archivos a procesar
	 * @param activationUrl   URL para la petici�n de activaci�n de procesamiento
	 * @param action          Autorizar o Denegar el env�o de los documentos
	 *                        procesados
	 */

	public WSStructureElement(String loginURL, String user, String password, String notificationUrl, long companyId,
			String account, String path, String activationUrl, int action) {
		super();
		this.loginUrl = loginURL;
		this.user = user;
		this.password = password;
		this.notificationUrl = notificationUrl;
		this.companyId = companyId;
		this.account = account;
		this.path = path;
		this.activationUrl = activationUrl;
	}

	/**
	 * Obtiene la URL de la petici�n Login
	 * 
	 * @return url del login
	 */

	public String getUrlLogin() {
		return loginUrl;
	}

	/**
	 * Obtiene el Usuario para el login 
	 * @return user
	 */
	public String getUser() {
		return user;
	}
	/**
	 * Obtiene la contrase�a para el login 
	 * @return password
	 */

	public String getPassword() {
		return password;
	}
	
	/**
	 * Obtiene la URL para la petici�n de notificaci�n 
	 * @return Url para la notificaci�n
	 */

	public String getNotificationUrl() {
		return notificationUrl;
	}
	/**
	 * Obtiene el Id o Nit de la compa�ia 
	 * @return Id de la compa�ia 
	 */

	public long getCompanyId() {
		return companyId;
	}
	/**
	 * Obtiene la cuenta de la compa�ia
	 * @return account 
	 */
	

	public String getAccount() {
		return account;
	}
	
	/**
	 * Ruta donde se dejaran los archivos para enviar a procesamiento 
	 * @return path 
	 */

	public String getPath() {
		return path;
	}

	/**
	 * URL para la petici�n de activaci�n 
	 * @return urlActivation
	 */
	public String getUrlActivation() {
		return activationUrl;
	}
	/**
	 * Valor Entero que indica si se confirma o no el env�o del archivo. 
	 * @return action
	 */

	public int getAction() {
		return action;
	}
}