package com.carvajal.facturaclaro.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import com.carvajal.facturaclaro.ral.dto.AuthorizationDTO;

public class WSPropertiesReader {

	// private static InputStream inputStream;
	public static String loginURL;
	public static String notificationURL;
	public static String activationURL;

	/**
	 * Permite extraer el path del WS a consultar
	 * 
	 * @return path Conexión WS
	 * @throws IOException
	 */

	public static void getWSPath(String wsPath) throws IOException {
		
		Properties property = new Properties();
		System.out.println("WS " + wsPath);
		InputStream inputStream = new FileInputStream(wsPath);
		property.load(inputStream);

		loginURL = property.getProperty("loginURL");
		notificationURL = property.getProperty("notificationURL");
		activationURL = property.getProperty("activationURL");
	}
}