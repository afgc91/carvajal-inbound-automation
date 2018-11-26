package com.greensqa.automatizacion.carvajal.factura.sftp.model;

public class WSStructureElement {

	// Atributos petición Login
	private String loginUrl;
	private String user;
	private String password;

	// Atributos petición Notificación
	private String notificationUrl;
	private long companyId;
	private String account;
	private String path;

	// Atributos petición Activación
	private String activationUrl;
	private int action;

	/**
	 * Constructor para WSStructure
	 * 
	 * @param loginURL        URL para la petición de login para obtener el token
	 * @param user            Usuario parametrizado para la empresa
	 * @param password        Contraseña del usuario parametrizado
	 * @param notificationUrl URL para la petición de notificación, es decir envío a procesamiento.
	 * @param companyId       Número de identificación de la empresa
	 * @param account         Cuenta del Cliente
	 * @param path            Ruta donde se pondrán los archivos a procesar
	 * @param activationUrl   URL para la petición de activación de procesamiento
	 * @param action          Autorizar o Denegar el envío de los documentos
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
	 * Obtiene la URL de la petición Login
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
	 * Obtiene la contraseña para el login 
	 * @return password
	 */

	public String getPassword() {
		return password;
	}
	
	/**
	 * Obtiene la URL para la petición de notificación 
	 * @return Url para la notificación
	 */

	public String getNotificationUrl() {
		return notificationUrl;
	}
	/**
	 * Obtiene el Id o Nit de la compañia 
	 * @return Id de la compañia 
	 */

	public long getCompanyId() {
		return companyId;
	}
	/**
	 * Obtiene la cuenta de la compañia
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
	 * URL para la petición de activación 
	 * @return urlActivation
	 */
	public String getUrlActivation() {
		return activationUrl;
	}
	/**
	 * Valor Entero que indica si se confirma o no el envío del archivo. 
	 * @return action
	 */

	public int getAction() {
		return action;
	}
}