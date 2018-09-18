package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CarvajalUtils {
	
	/**
	 * Carga las rutas de los directorios del archivo de entrada.
	 * @throws IOException
	 */
	public static void loadDirectoriesFromFile(String directoriesInOutFilePath, ArrayList<String> directoriesOut) throws IOException {
		try (FileReader fr = new FileReader(directoriesInOutFilePath);
				BufferedReader br = new BufferedReader(fr)) {
			String str = "";
			
			while (true) {
				str = br.readLine();
				if (str == null || str.equals("")) {
					break;
				}
				directoriesOut.add(str);
			}
		}
	}
	
	/**
	 * Retorna las credenciales a partir del archivo cargado por el usuario.
	 * @param awsCredentialsFilePath Ruta del archivo cargado por el usuario.
	 * @return Array con las credenciales.
	 * Pos. 0: Access Key
	 * Pos. 1: Secret Access Key
	 * Pos. 2: Bucket Name
	 * Pos. 3: Region
	 * @throws FileNotFoundException si no encuentra el archivo.
	 * @throws IOException si hay un error de lectura/escritura.
	 */
	public static String[] getCredentialsFromFile(String awsCredentialsFilePath) throws FileNotFoundException, IOException {
		String[] credentials = new String[4];
		try (FileReader fr = new FileReader(awsCredentialsFilePath);
				BufferedReader br = new BufferedReader(fr)) {
			String str = "";
			
			for (int i = 0; i < credentials.length; i++) {
				str = br.readLine();
				credentials[i] = str;
			}
			
			return credentials;
		}
	}
	
	/**
	 * Concatena dos Arrays
	 * @param a Array 1
	 * @param b Array 2
	 * @return Array 1 + Array 2
	 */
	public static <T> T[] concatArrays(T[] a, T[] b) {
	    int aLen = a.length;
	    int bLen = b.length;

	    @SuppressWarnings("unchecked")
	    T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
	    System.arraycopy(a, 0, c, 0, aLen);
	    System.arraycopy(b, 0, c, aLen, bLen);

	    return c;
	}
	
	/**
	 * Lee el archivo con las configuraciones iniciales de los archivos de factura a generar.
	 * @param configFilePath Ruta del archivo de configuración inicial de las facturas a generar.
	 * @return Datos de configuración de las facturas a generar.
	 * @throws FileNotFoundException En caso de que no exista el archivo.
	 * @throws IOException En caso de un error de entrada/salida.
	 * @throws ParseException En caso de un error al leer el JSON.
	 * @throws java.text.ParseException  En caso de error al interpretar el JSON.
	 */
	public static CarvajalStandardFactStructure loadConfigFile(String configFilePath, SimpleDateFormat sdf) throws FileNotFoundException, IOException, ParseException, java.text.ParseException {
		File file = new File(configFilePath);
		if (!file.exists()) {
			return null;
		}
		
		try (FileReader fr = new FileReader(file)) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(fr);
			String factPrefix = (String) json.get("factPref");
			long factStartNum = Long.parseLong(json.get("factNumInicial") + "");
			String nitSender = (String) json.get("nitEmisor");
			String nitReceiver = (String) json.get("nitReceptor");
			long authNumber = Long.parseLong(json.get("numAutorizacion") + "");
			Date startingRangeDate = new Date(sdf.parse((json.get("fechaRangoInicial") + "")).getTime());
			Date endingRangeDate = new Date(sdf.parse((json.get("fechaRangoFinal") + "")).getTime());
			long startingRangeNum = Long.parseLong(json.get("numInicioRango") + "");
			long endingRangeNum = Long.parseLong(json.get("numFinalRango") + "");
			String docTypeId = (String) json.get("idTipoDoc");
			int docType = Integer.parseInt(json.get("tipoDoc") + "");
			
			CarvajalStandardFactStructure fact = new CarvajalStandardFactStructure(factPrefix, factStartNum, nitSender, nitReceiver, authNumber, startingRangeDate, endingRangeDate, startingRangeNum, endingRangeNum, docTypeId, docType);
			return fact;
		}
	}
	
	/**
	 * Obtiene la extensión del archivo a partir del path del archivo.
	 * @param filePath Ruta del archivo.
	 * @return Extensión del archivo.
	 */
	public static String getFileExtension(String filePath) {
		String[] fileArray = filePath.split("\\.");
		String extension = fileArray[fileArray.length - 1];
		return extension;
	}
	
	/**
	 * Precondición: El archivo es de extensión .txt.
	 * Verifica si el archivo plano de factura contiene "ENC" (encabezado).
	 * @param filePath Ruta del archivo.
	 * @return true Si contiene encabezado. false Si no contiene encabezado.
	 * @throws FileNotFoundException En caso de no encontrar el archivo.
	 * @throws IOException En caso de un error de entrada/salida.
	 */
	public static boolean isValidTxt(String filePath) throws FileNotFoundException, IOException {
		File file = new File(filePath);
		try (FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr)) {
			String line = br.readLine();
			if (line.contains("ENC")) {
				return true;
			}
			return false;
		}
	}
	
	/**
	 * Precondición: La extensión del archivo es .xml.
	 * Obtiene el tipo de archivo XML de factura.
	 * @param filePath Ruta del archivo.
	 * @return 1 Si el archivo es un XML estándar. 2 Si el archivo es un XML UBL. 0 Si no es ninguno de los dos.
	 * @throws ParserConfigurationException En caso de error en la lectura del XML.
	 * @throws SAXException En caso de error en la lectura del XML.
	 * @throws IOException En caso de un error de entrada/salida.
	 */
	public static int getXmlType(String filePath) throws ParserConfigurationException, SAXException, IOException {
		File file = new File(filePath);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(file);
		doc.getDocumentElement().normalize();
		NodeList xmlList = doc.getElementsByTagName("ENC");
		if (xmlList.getLength() > 0) {
			return 1;
		}
		NodeList ublList = doc.getElementsByTagName("ext:UBLExtensions");
		if (ublList.getLength() > 0) {
			return 2;
		}
		return 0;
	}
	
	public static String concatTxtFileLineArray(String[] lineArray) {
		String line = "";
		for (int j = 0; j < lineArray.length; j++) {
			line += lineArray[j];
			if (j != lineArray.length - 1) {
				line += ",";
			}
		}
		return line;
	}
}
