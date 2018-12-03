package com.carvajal.facturaclaro.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import com.carvajal.facturaclaro.ral.dto.AuthorizationDTO;

public class WSPropertiesReader {

	/**
	 * Permite extraer el path del WS a consultar
	 * 
	 * @return path Conexión WS
	 * @throws IOException
	 */

	public static void getWSPath(String wsPath) {

		try {
			Properties property = new Properties();
			System.out.println("WS " + wsPath);
			InputStream inputStream = new FileInputStream(wsPath);
			property.load(inputStream);

			PATH.LOGINURL = property.getProperty("loginURL").trim();
			PATH.NOTIFICATIONURL = property.getProperty("notificationURL").trim();
			PATH.ACTIVATIONIONURL = property.getProperty("activationURL").trim();
		} catch (IOException e) {
			System.out.println("No se logró leer el archivo Properties, verique la ruta del archivo");
		}
	}
}