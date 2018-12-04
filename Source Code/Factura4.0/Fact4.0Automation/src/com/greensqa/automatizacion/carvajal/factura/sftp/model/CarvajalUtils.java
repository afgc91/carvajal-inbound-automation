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
	 * Leer archivo de configuración para realizar las peticiones WS de login,
	 * notificación y activacion.
	 * 
	 * @throws ParseException En caso de error al leer el JSON
	 * @throws IOException    En caso de error de Entrada/Salida
	 */
	public static WSStructureElement loadWSConfiFile(String filePath) throws IOException, ParseException {
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}

		try (FileReader fr = new FileReader(file)) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(fr);
			// Objeto LOGIN
			JSONObject login = (JSONObject) json.get("login");
			String loginUrl = (String) login.get("url");
			String user = (String) login.get("usuario");
			String password = (String) login.get("clave");
			// Objeto NOTIFICACION
			JSONObject notification = (JSONObject) json.get("notificacion");
			String notificationUrl = (String) notification.get("url");
			long companyId = Long.parseLong(json.get("numFinalRango") + "");
			String account = (String) notification.get("cuenta");
			String path = (String) notification.get("ruta");
			// Objeto Activation
			JSONObject activation = (JSONObject) json.get("activation");
			String activationUrl = (String) activation.get("url");
			int action = Integer.parseInt(activation.get("accion") + "");

			WSStructureElement wsData = new WSStructureElement(loginUrl, user, password, notificationUrl, companyId,
					account, path, activationUrl, action);
			return wsData;
		}
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
	protected static StandardFactStructureElement loadConfigFile(String configFilePath, SimpleDateFormat sdf)
			throws FileNotFoundException, IOException, ParseException, java.text.ParseException {
		File file = new File(configFilePath);
		if (!file.exists()) {
			return null;
		}

		try (FileReader fr = new FileReader(file)) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(fr);
			String factPrefix = "";
			if (json.get("factPref") != null) {
					factPrefix = !json.get("factPref").equals("") ? json.get("factPref") + "" : "";
			}
			long factStartNum = 0; 
			if(json.get("factNumInicial") != null) {
			factStartNum = !json.get("factNumInicial").equals("") ? Long.parseLong(json.get("factNumInicial") + "") : 0;}
			
			String nitSender = "";
			if (json.get("nitEmisor") != null) {
				nitSender = !json.get("nitEmisor").equals("") ? json.get("nitEmisor") + "" : "";
			}
			String nitReceiver = "";
			if (json.get("nitReceptor") != null) {
				nitReceiver = !json.get("nitReceptor").equals("") ? json.get("nitReceptor") + "" : "";
			}
			String strAuthNumber = "";
			if ((json.get("numAutorizacion") != null)) {
					strAuthNumber = !json.get("numAutorizacion").equals("") ? json.get("numAutorizacion") + "" : "";
				} 
			long authNumber = (!strAuthNumber.equals("")) ? Long.parseLong(strAuthNumber) : 0;
			// Modificar todos los campos que no estan en el archivo claro
			Date startingRangeDate = null;
			if ((json.get("fechaRangoInicial") != null)) {
				if (!json.get("fechaRangoInicial").equals("")) {
					startingRangeDate = new Date(sdf.parse((json.get("fechaRangoInicial") + "")).getTime());
				} else {
					startingRangeDate = new Date(sdf.parse("1990-01-01").getTime());
				}
			} else {
				startingRangeDate = new Date(sdf.parse("1990-01-01").getTime());
			}

			Date endingRangeDate = null;
			if ((json.get("fechaRangoFinal") != null)) {
				if (!json.get("fechaRangoFinal").equals("")) {
					endingRangeDate = new Date(sdf.parse(json.get("fechaRangoFinal") + "").getTime());
				} else {
					endingRangeDate = new Date(sdf.parse("1990-01-01").getTime());
				}
			} else {
				endingRangeDate = new Date(sdf.parse("1990-01-01").getTime());
			}

			long startingRangeNum = 0;
			if ((json.get("numInicioRango") != null)) {
					startingRangeNum = !json.get("numInicioRango").equals("") ? Long.parseLong(json.get("numInicioRango") + "") : 0;
			}

			long endingRangeNum = 0;
			if ((json.get("numFinalRango") != null)) {
					endingRangeNum = !json.get("numFinalRango").equals("") ? Long.parseLong(json.get("numFinalRango") + "") : 0;
				}
			String docTypeId = "";
			if (json.get("idTipoDoc") != null) {
				docTypeId = !json.get("idTipoDoc").equals("") ? json.get("idTipoDoc") + "" : "";
			}
			int docType = 0;
			if (json.get("tipoDoc") != null) {
				docType = !json.get("tipoDoc").equals("") ? Integer.parseInt(json.get("tipoDoc") + "") : 0;
			}
			Date factDate = null;
			if (json.get("fechaFactura") != null) {
				if (!json.get("fechaFactura").equals("")) {
					factDate = new Date(sdf.parse(json.get("fechaFactura") + "").getTime());
				} else {
					factDate = new Date(sdf.parse("1990-01-01").getTime());
				}
			} else {
				factDate = new Date(sdf.parse("1990-01-01").getTime());
			}
			String cufePath = "";
			if (json.get("rutaCufe") != null) {
				cufePath = (String) json.get("rutaCufe");
			} else {
				cufePath = "";
			}

			StandardFactStructureElement fact = new StandardFactStructureElement(factPrefix, factStartNum, nitSender,
					nitReceiver, authNumber, startingRangeDate, endingRangeDate, startingRangeNum, endingRangeNum,
					docTypeId, docType, factDate, cufePath);
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
	 * Precondición: El archivo es de extensión .fe. Verifica si el archivo de
	 * factura contiene la etiqueta "PRC" (Proceso)
	 * 
	 * @param filePath Ruta del Archivo
	 * @return true si Contiene la etiqueta PRC false si no contiene la etiqueta.
	 * @throws FileNotFoundException En caso de no encontrar el archivo.
	 * @throws IOException           En caso de un error de entrada/salida.
	 */
	protected static boolean isValidClaroFe(String filePath) throws FileNotFoundException, IOException {
		File file = new File(filePath);
		try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
			String line = br.readLine();
			if (line.contains("PRC")) {
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
	 * Concatena los elementos de un array separados por pipes (|).
	 * 
	 * @param lineArray Array de entrada.
	 * @return Línea resultante de la concatenación de los elementos del array.
	 */
	protected static String concatClaroFileLineArray(String[] lineArray) {
		String line = "";
		for (int j = 0; j < lineArray.length; j++) {
			line += lineArray[j];
			if (j != lineArray.length - 1) {
				line += "|";
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

	public static SftpAndDbDataElement loadConnectionsData(String filePath)
			throws FileNotFoundException, IOException, ParseException {
		File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}

		try (FileReader fr = new FileReader(file)) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(fr);
			// Objeto SFTP
			JSONObject channel = (JSONObject) json.get("canal");
			String userSftp = (String) channel.get("usuario");
			String passwordSftp = (String) channel.get("clave");
			String urlSftp = (String) channel.get("urlServidor");
			int portSftp = Integer.parseInt((String) channel.get("puerto"));
			String destSftp = (String) channel.get("destino");
			// Objeto Aurora (DB)
			JSONObject dataBase = (JSONObject) json.get("baseDeDatos");
			int tipoDb = !dataBase.get("tipoBD").equals("") ? Integer.parseInt((String) dataBase.get("tipoBD")) : 0;
			String userDb =!dataBase.get("usuario").equals("") ? (String) dataBase.get("usuario") : "";
			String passwordDb = !dataBase.get("clave").equals("") ? (String) dataBase.get("clave") : "";
			String urlDb = !dataBase.get("urlServidor").equals("") ? (String) dataBase.get("urlServidor") : "";
			int portDb = !dataBase.get("puerto").equals("") ? Integer.parseInt((String) dataBase.get("puerto")) : 0;
			// Objeto AWS
			JSONObject aws = (JSONObject) json.get("aws");
			String key = !aws.get("clave").equals("") ? (String) aws.get("clave") : "";
			String secretKey = !aws.get("claveSecreta").equals("") ? (String) aws.get("claveSecreta") : "";
			String nameBucket = !aws.get("nombreBucket").equals("") ? (String) aws.get("nombreBucket") : "";
			String region = !aws.get("nombreBucket").equals("") ? (String) aws.get("region") : "";
			// Objeto Archivo CUFE
			JSONObject cufe = (JSONObject) json.get("rutaArchivoConfigCufe");
			String cufePath = !cufe.get("ruta").equals("") ? (String) cufe.get("ruta") : "";

			SftpAndDbDataElement connectionsData = new SftpAndDbDataElement(userSftp, passwordSftp, urlSftp, portSftp,
					destSftp, tipoDb, userDb, passwordDb, urlDb, portDb, key, secretKey, nameBucket, region, cufePath);
			return connectionsData;
		}
	}

	public static String getCufeValuesByLocation(String filePath, String label, int position, int index)
			throws FileNotFoundException, IOException {
		ArrayList<String> fileLines = new ArrayList<>();
		String strWithLabelLines = "";
		String[] cufeItems = null;
		File f = new File(filePath);
		if (!f.exists()) {
			return null;
		}

		try (FileReader fr = new FileReader(f); BufferedReader br = new BufferedReader(fr)) {
			// Guardar líneas del archivo en el ArrayList.
			String line = "";
			while (true) {
				line = br.readLine();
				if (line == null) {
					break;
				}
				line = line.replaceAll("[^\\p{Graph}\n\r\t ]", "");
				fileLines.add(line);
			}
			for (int i = 0; i < fileLines.size(); i++) {
				line = fileLines.get(i);
				if (line.contains(label)) {
					strWithLabelLines += line + "\t";
				}
			}
			strWithLabelLines = strWithLabelLines.substring(0, strWithLabelLines.length() - 1);
			String[] labelLines = strWithLabelLines.split("\t");
			cufeItems = new String[labelLines.length];
			for (int i = 0; i < labelLines.length; i++) {
				cufeItems[i] = labelLines[i].split("\\|")[position];
			}
		}
		if (index < cufeItems.length) {
			return cufeItems[index];
		} else {
			return null;
		}
	}

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

	public static String getAuthorizationFromUblFile(String filePath)
			throws ParserConfigurationException, SAXException, IOException {
		File file = new File(filePath);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(file);
		doc.getDocumentElement().normalize();
		Node prefixNode = doc.getElementsByTagName("sts:InvoiceAuthorization").item(0);
		String prefix = prefixNode.getTextContent();
		return prefix;
	}
}
