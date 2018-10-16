package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class CarvajalUtils {

	String destPath = "";

	/**
	 * Carga las rutas de los directorios del archivo de entrada.
	 * 
	 * @throws IOException
	 */
	protected static void loadDirectoriesFromFile(String directoriesInOutFilePath, ArrayList<String> directoriesOut)
			throws IOException {
		try (FileReader fr = new FileReader(directoriesInOutFilePath); BufferedReader br = new BufferedReader(fr)) {
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
	 * 
	 * @param awsCredentialsFilePath Ruta del archivo cargado por el usuario.
	 * @return Array con las credenciales. Pos. 0: Access Key Pos. 1: Secret Access
	 *         Key Pos. 2: Bucket Name Pos. 3: Region
	 * @throws FileNotFoundException si no encuentra el archivo.
	 * @throws IOException           si hay un error de lectura/escritura.
	 */
	protected static String[] getCredentialsFromFile(String awsCredentialsFilePath)
			throws FileNotFoundException, IOException {
		String[] credentials = new String[4];
		try (FileReader fr = new FileReader(awsCredentialsFilePath); BufferedReader br = new BufferedReader(fr)) {
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
	 * 
	 * @param a Array 1
	 * @param b Array 2
	 * @return Array 1 + Array 2
	 */
	protected static <T> T[] concatArrays(T[] a, T[] b) {
		int aLen = a.length;
		int bLen = b.length;

		@SuppressWarnings("unchecked")
		T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}

	/**
	 * Lee el archivo con las configuraciones iniciales de los archivos de factura a
	 * generar.
	 * 
	 * @param configFilePath Ruta del archivo de configuración inicial de las
	 *                       facturas a generar.
	 * @return Datos de configuración de las facturas a generar.
	 * @throws FileNotFoundException En caso de que no exista el archivo.
	 * @throws IOException           En caso de un error de entrada/salida.
	 * @throws ParseException        En caso de un error al leer el JSON.
	 * @throws                       java.text.ParseException En caso de error al
	 *                               interpretar el JSON.
	 */
	protected static CarvajalStandardFactStructure loadConfigFile(String configFilePath, SimpleDateFormat sdf)
			throws FileNotFoundException, IOException, ParseException, java.text.ParseException {
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

			CarvajalStandardFactStructure fact = new CarvajalStandardFactStructure(factPrefix, factStartNum, nitSender,
					nitReceiver, authNumber, startingRangeDate, endingRangeDate, startingRangeNum, endingRangeNum,
					docTypeId, docType);
			return fact;
		}
	}

	/**
	 * Obtiene la extensión del archivo a partir del path del archivo.
	 * 
	 * @param filePath Ruta del archivo.
	 * @return Extensión del archivo.
	 */
	protected static String getFileExtension(String filePath) {
		String[] fileArray = filePath.split("\\.");
		String extension = fileArray[fileArray.length - 1];
		return extension;
	}

	/**
	 * Precondición: El archivo es de extensión .txt. Verifica si el archivo plano
	 * de factura contiene "ENC" (encabezado).
	 * 
	 * @param filePath Ruta del archivo.
	 * @return true Si contiene encabezado. false Si no contiene encabezado.
	 * @throws FileNotFoundException En caso de no encontrar el archivo.
	 * @throws IOException           En caso de un error de entrada/salida.
	 */
	protected static boolean isValidTxt(String filePath) throws FileNotFoundException, IOException {
		File file = new File(filePath);
		try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
			String line = br.readLine();
			if (line.contains("ENC")) {
				return true;
			}
			return false;
		}
	}

	/**
	 * Precondición: La extensión del archivo es .xml. Obtiene el tipo de archivo
	 * XML de factura.
	 * 
	 * @param filePath Ruta del archivo.
	 * @return 1 Si el archivo es un XML estándar. 2 Si el archivo es un XML UBL. 0
	 *         Si no es ninguno de los dos.
	 * @throws ParserConfigurationException En caso de error en la lectura del XML.
	 * @throws SAXException                 En caso de error en la lectura del XML.
	 * @throws IOException                  En caso de un error de entrada/salida.
	 */
	protected static int getXmlType(String filePath) throws ParserConfigurationException, SAXException, IOException {
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

	/**
	 * Concatena los elementos de un array separados por coma (,).
	 * 
	 * @param lineArray Array de entrada.
	 * @return Línea resultante de la concatenación de los elementos del array.
	 */
	protected static String concatTxtFileLineArray(String[] lineArray) {
		String line = "";
		for (int j = 0; j < lineArray.length; j++) {
			line += lineArray[j];
			if (j != lineArray.length - 1) {
				line += ",";
			}
		}
		return line;
	}

	/**
	 * Crea una copia de un archivo en la ruta especificada.
	 * 
	 * @param source Path absoluto del archivo a copiar.
	 * @param dest   Path absoluto con el que debe quedar el archivo copiado.
	 * @throws IOException En caso de un error de entrada/salida.
	 */
	protected static void copyFileUsingStream(File source, File dest) throws IOException {
		try (InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest)) {

			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		}
	}

	/**
	 * Modifica el primer tag del nodeList obtenido con el nombre de la etiqueta,
	 * con el valor enviado.
	 * 
	 * @param doc   Documento XML.
	 * @param tag   Nombre del NodeList a buscar.
	 * @param value Valorque se le quiere colocar al primer tag del NodeList.
	 */
	protected static void setXmlNode(Document doc, String tag, String value) {
		NodeList nodeList = doc.getElementsByTagName(tag);
		if (nodeList != null && nodeList.item(0) != null) {
			nodeList.item(0).setTextContent(value);
		}
	}

	public static SftpAndDbData loadConnectionsData(String filePath)
			throws FileNotFoundException, IOException, ParseException {
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}

		try (FileReader fr = new FileReader(file)) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(fr);
			//Objeto Configuración caso de Prueba 
			JSONObject confiTest = (JSONObject) json.get("test"); 
			String testCase = (String) confiTest.get("casoDePrueba");
			String channel = (String) confiTest.get("canal");
			String account = (String) confiTest.get("cuenta"); 
			// Objeto SFTP
			JSONObject sftp = (JSONObject) json.get("sftp");
			String userSftp = (String) sftp.get("usuario");
			String passwordSftp = (String) sftp.get("clave");
			String urlSftp = (String) sftp.get("urlServidor");
			int portSftp = Integer.parseInt((String) sftp.get("puerto"));
			String destSftp = (String) sftp.get("destino");
			// Objeto Aurora (DB)
			JSONObject aurora = (JSONObject) json.get("aurora");
			int tipoDb = Integer.parseInt((String) aurora.get("tipoBD"));
			String userDb = (String) aurora.get("usuario");
			String passwordDb = (String) aurora.get("clave");
			String urlDb = (String) aurora.get("urlServidor");
			int portDb = Integer.parseInt((String) aurora.get("puerto"));

			SftpAndDbData connectionsData = new SftpAndDbData(testCase, channel, account, userSftp, passwordSftp, urlSftp, portSftp, destSftp,
					tipoDb, userDb, passwordDb, urlDb, portDb);
			return connectionsData;
		}
	}
	

//	public static int getUrlSend(String filePath) throws FileNotFoundException, IOException, ParseException {
//		
//		//final String url = loadConnectionsData(filePath).getUrlSftp();
//		int channel = 0; 
//		
//		final String channelSftp = "\\Bsftp\\B"; 
//		final String channelAs2 = "\\Bas2\\B"; 
//
//		final Pattern patternSftp = Pattern.compile(channelSftp);
//		final Pattern patternAs2 = Pattern.compile(channelAs2); 
//		final Matcher matcherSftp = patternSftp.matcher(filePath);
//		final Matcher matcherAs2 = patternAs2.matcher(filePath); 
//		
//		if (matcherSftp.find()) { 
//			channel = 1; 
//		} else if (matcherAs2.find()){
//			channel = 2; 
//			}
//		return channel;
//  	}
//
//	public static Double getTestCase(String filePath)
//			throws FileNotFoundException, IOException, ParseException, ParserConfigurationException, SAXException {
//
//		String url = loadConnectionsData(filePath).getUrlSftp(); 
//		System.out.println(url);
//		File file = new File(filePath);
//		if (!file.exists()) {
//			return null;
//		}
//
//		Double idTestCase = null;
//		String typeCodeDoc = getTypeId(filePath);
//		String fileExt = getFileExtension(filePath);
//		int channel = getUrlSend(filePath);
////		int channel = 0;
//		
//		System.out.println(fileExt);
//		System.out.println(typeCodeDoc);
//		if (fileExt.equalsIgnoreCase("txt") && channel==1 && typeCodeDoc.equalsIgnoreCase("1")) {
//			idTestCase = 1.2;
//			System.out.println(1.2);
//		} else {
//			int docType = getXmlType(filePath);
//			if (docType == 0) {
//				return null;				
//			} else if (docType == 1 && channel==1 && typeCodeDoc.equalsIgnoreCase("4")) {
//				idTestCase = 1.17;
//			} else if (docType == 2 && channel==1 && typeCodeDoc.equalsIgnoreCase("3")) {
//				idTestCase = 1.14;
//			} else if (docType == 2 && channel==2 && typeCodeDoc.equalsIgnoreCase("3")) {
//				idTestCase = 1.11; 
//			}
//		}
//		return idTestCase;
//	}

	protected static String getFactNumber(String filePath)
			throws ParserConfigurationException, SAXException, IOException {
		File f = new File(filePath);
		if (!f.exists()) {
			return null;
		}

		String fileExt = getFileExtension(filePath);

		switch (fileExt) {
		case "txt": {
			// Obtener número de factura de archivo plano.
			return getFactNumFromTxtFile(f);
		}
		case "xml": {
			// Obtener nùmero de factura de archivo xml (idenificar si es UBL o XML
			// estándar).
			int docType = getXmlType(filePath);
			if (docType == 0) {
				return null;
			}

			if (docType == 1) {
				// Es XML estándar. Obtener número de factura.
				return getFactNumFromXmlFile(f);
			} else {
				// Es UBL Dian. Obtener número de factura.
				return getFactNumFromUblFile(f);
			}
		}
		}
		return null;
	}

	private static String getFactNumFromTxtFile(File file) throws IOException {
		// Pos 6 fila 1
		try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
			String line = br.readLine();
			if (line.contains("ENC")) {
				return line.split(",")[6];
			}
		}
		return null;
	}

	private static String getFactNumFromXmlFile(File file)
			throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(file);
		doc.getDocumentElement().normalize();
		return doc.getElementsByTagName("ENC_6").item(0).getTextContent();
	}

	private static String getFactNumFromUblFile(File file)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(file);
		doc.getDocumentElement().normalize();
		NodeList feInvoiceChildren = doc.getElementsByTagName("fe:Invoice").item(0).getChildNodes();
		String nodeName = "";
		Node item = null;
		for (int i = 0; i < feInvoiceChildren.getLength(); i++) {
			item = feInvoiceChildren.item(i);
			nodeName = item.getNodeName();
			if (nodeName.equalsIgnoreCase("cbc:ID")) {
				return item.getTextContent();
			}
		}
		return null;
	}

	protected static String getTypeId(String filePath) throws ParserConfigurationException, SAXException, IOException {
		File f = new File(filePath);
		if (!f.exists()) {
			return null;
		}

		String fileExt = getFileExtension(filePath);

		switch (fileExt) {
		case "txt": {
			// Obtener número de factura de archivo plano.
			return getTypeIdTxtFile(f);
		}
		case "xml": {
			// Obtener nùmero de factura de archivo xml (idenificar si es UBL o XML
			// estándar).
			int docType = getXmlType(filePath);
			if (docType == 0) {
				return null;
			}

			if (docType == 1) {
				// Es XML estándar. Obtener número de factura.
				return getTypeIdXmlFile(f);
			} else {
				// Es UBL Dian. Obtener número de factura.
				return getTypeIdUblFile(f);
			}
		}
		}
		return null;
	}

	private static String getTypeIdTxtFile(File file) throws FileNotFoundException, IOException {
		// Pos 9 fila 1
		try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
			String line = br.readLine();
			if (line.contains("ENC")) {
				return line.split(",")[9];
			}
		}
		return null;
	}

	private static String getTypeIdXmlFile(File file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(file);
		doc.getDocumentElement().normalize();
		return doc.getElementsByTagName("ENC_9").item(0).getTextContent();
	}

	private static String getTypeIdUblFile(File file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(file);
		doc.getDocumentElement().normalize();
		NodeList feInvoiceChildren = doc.getElementsByTagName("fe:Invoice").item(0).getChildNodes();
		String nodeName = "";
		Node item = null;
		for (int i = 0; i < feInvoiceChildren.getLength(); i++) {
			item = feInvoiceChildren.item(i);
			nodeName = item.getNodeName();
			if (nodeName.equalsIgnoreCase("cbc:InvoiceTypeCode")) {
				return item.getTextContent();
			}

		}
		return null;
	}

	protected static String getNitSupplier(String filePath)
			throws ParserConfigurationException, SAXException, IOException {
		File f = new File(filePath);
		if (!f.exists()) {
			return null;
		}

		String fileExt = getFileExtension(filePath);

		switch (fileExt) {
		case "txt": {
			// Obtener número de factura de archivo plano.
			return getSenderTxtFile(f);
		}
		case "xml": {
			// Obtener nùmero de factura de archivo xml (idenificar si es UBL o XML
			// estándar).
			int docType = getXmlType(filePath);
			if (docType == 0) {
				return null;
			}

			if (docType == 1) {
				// Es XML estándar. Obtener número de factura.
				return getSenderXmlFile(f);
			} else {
				// Es UBL Dian. Obtener número de factura.
				return getNitSupplierUblFile(f);
			}
		}
		}
		return null;
	}

	private static String getSenderTxtFile(File file) throws FileNotFoundException, IOException {
		// Pos 9 fila 1
		try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
			String line = br.readLine();
			if (line.contains("ENC")) {
				return line.split(",")[2];
			}
		}
		return null;
	}

	private static String getSenderXmlFile(File file) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(file);
		doc.getDocumentElement().normalize();
		return doc.getElementsByTagName("ENC_2").item(0).getTextContent();
	}

	private static String getNitSupplierUblFile(File file)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(file);
		doc.getDocumentElement().normalize();
		NodeList feInvoiceChildren = doc.getElementsByTagName("fe:Invoice").item(0).getChildNodes();

		String nodeName = "";
		Node item = null;
		for (int i = 0; i < feInvoiceChildren.getLength(); i++) {
			item = feInvoiceChildren.item(i);
			nodeName = item.getNodeName();
			NodeList feSupplierChildren = item.getChildNodes();
			if (nodeName.equalsIgnoreCase("fe:AccountingSupplierParty")) {
				for (int j = 0; j < feSupplierChildren.getLength(); j++) {
					item = feSupplierChildren.item(j);
					nodeName = item.getNodeName();
					NodeList fePartyChildren = item.getChildNodes();
					if (nodeName.equals("fe:Party")) {
						for (int k2 = 0; k2 < fePartyChildren.getLength(); k2++) {
							item = fePartyChildren.item(k2);
							nodeName = item.getNodeName();
							NodeList feIdSupplierChildren = item.getChildNodes();
							if (nodeName.equals("cac:PartyIdentification")) {
								for (int k3 = 0; k3 < feIdSupplierChildren.getLength(); k3++) {
									item = feIdSupplierChildren.item(k3);
									nodeName = item.getNodeName();
									if (nodeName.equals("cbc:ID")) {
										return item.getTextContent();
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	protected static String getNitCustomer(String filePath)
			throws ParserConfigurationException, SAXException, IOException {
		File f1 = new File(filePath);
		if (!f1.exists()) {
			return null;
		}

		String fileExt = getFileExtension(filePath);

		switch (fileExt) {
		case "txt": {
			// Obtener número de factura de archivo plano.
			return getNitReceiverTxtFile(f1);
		}
		case "xml": {
			// Obtener nùmero de factura de archivo xml (idenificar si es UBL o XML
			// estándar).
			int docType = getXmlType(filePath);
			if (docType == 0) {
				return null;
			}

			if (docType == 1) {
				// Es XML estándar. Obtener número de factura.
				return getNitReceiverXmlFile(f1);
			} else {
				// Es UBL Dian. Obtener número de factura.
				return getNitCustomerUblFile(f1);
			}
		}
		}
		return null;
	}

	private static String getNitReceiverTxtFile(File file) throws FileNotFoundException, IOException {
		// Pos 9 fila 1
		try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
			String line = br.readLine();
			if (line.contains("ENC")) {
				return line.split(",")[3];
			}
		}
		return null;
	}

	private static String getNitReceiverXmlFile(File file)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(file);
		doc.getDocumentElement().normalize();
		return doc.getElementsByTagName("ENC_3").item(0).getTextContent();
	}

	private static String getNitCustomerUblFile(File file)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(file);
		doc.getDocumentElement().normalize();
		NodeList feInvoiceChildren = doc.getElementsByTagName("fe:Invoice").item(0).getChildNodes();

		String nodeName1 = "";
		Node item1 = null;
		for (int l = 0; l < feInvoiceChildren.getLength(); l++) {
			item1 = feInvoiceChildren.item(l);
			nodeName1 = item1.getNodeName();
			// System.out.println(("fe:AccountingCustomerParty").equals(nodeName1));
			NodeList feCustomerChildren = item1.getChildNodes();
			if (nodeName1.equalsIgnoreCase("fe:AccountingCustomerParty")) {
				for (int h = 0; h < feCustomerChildren.getLength(); h++) {
					item1 = feCustomerChildren.item(h);
					nodeName1 = item1.getNodeName();
					// System.out.println(nodeName1);
					NodeList feCustPartyChildren = item1.getChildNodes();
					if (nodeName1.equals("fe:Party")) {
						for (int n = 0; n < feCustPartyChildren.getLength(); n++) {
							item1 = feCustPartyChildren.item(n);
							nodeName1 = item1.getNodeName();
							// System.out.println(nodeName1);
							NodeList feIdChildren = item1.getChildNodes();
							if (nodeName1.equals("cac:PartyIdentification")) {
								for (int i = 0; i < feIdChildren.getLength(); i++) {
									item1 = feIdChildren.item(i);
									nodeName1 = item1.getNodeName();
									if (nodeName1.equals("cbc:ID")) {
										return item1.getTextContent();
									}
								}
							}
						}
					}
				}
			}
		}

		return null;
	}

}
