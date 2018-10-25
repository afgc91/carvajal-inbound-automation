package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class FilesSender {

	private String srcPath;
	private String dstPath;
	private String user;
	private String password;
	private String url;
	private int port;
	private String key;
	private String secretKey;
	private String nameBucket;
	private String region;
	private File file = null;
	private String nameFile = "";
	private String keyName = "";
	private ArrayList<String> accountsList = new ArrayList<String>();
	private ArrayList<String> filesFromXlsList = new ArrayList<String>();
	private ArrayList<String> listTestCase = new ArrayList<String>();
	private ArrayList<String> testCaseAws = new ArrayList<>();
	private ArrayList<String> pathKeyName = new ArrayList<String>();
	private ArrayList<String> pathFileTest = new ArrayList<String>();
	private int filesToSend;
	private int sentFiles;

	public FilesSender(String srcPath, String dstPath, String user, String password, String url, int port, String key,
			String secretKey, String nameBucket, String region) {
		this.srcPath = srcPath;
		this.dstPath = dstPath;
		this.user = user;
		this.password = password;
		this.url = url;
		this.port = port;
		this.key = key;
		this.secretKey = secretKey;
		this.nameBucket = nameBucket;
		this.region = region;
	}

	/**
	 * Método que maneja el envío de los archivos.
	 * 
	 * @param option 1 para enviar los archivos por SFTP, según lo parametrizado en
	 *               el archivo JSON de entrada. 2 para enviar los archivos según un
	 *               archivo Excel con la configuración de los casos de prueba.
	 * @throws JSchException                En caso de error en el establecimiento
	 *                                      de la conexión SFTP.
	 * @throws SftpException                En caso de error en el envío SFTP.
	 * @throws FileNotFoundException        En caso de no encontrar el archivo.
	 * @throws IOException                  En cas de error de entrada/salida.
	 * @throws ParseException               En caso de error conviertiendo el
	 *                                      archivo json.
	 * @throws ParserConfigurationException En caso de error convirtiendo el archivo
	 *                                      XML.
	 * @throws SAXException                 En caso de error con el archivo XML.
	 */
	public void manageFilesSending(int option) throws JSchException, SftpException, FileNotFoundException, IOException,
			ParseException, ParserConfigurationException, SAXException {
		sentFiles = 0;
		if (option == 1) {
			// Envío de archivos de acuerdo con lo parametrizado en el JSON de entrada.
			File dir = new File(srcPath);
			File[] filesFromJson = dir.listFiles();
			filesToSend = filesFromJson.length;
			SftpFilesSender sftpFilesSender = new SftpFilesSender(url, user, password, port);
			for (int i = 0; i < filesFromJson.length; i++) {
				sftpFilesSender.sendFileSftp(filesFromJson[i], dstPath);
				sentFiles += 1;
			}
			sftpFilesSender.endSftpSession();
		} else if (option == 2) {
			// Opción para enviar archivos según archivo de configuración de casos de prueba
			// por SFTP - AS2 y AWS
			filesToSend = filesFromXlsList.size();
			if (isTestCaseAWS()) {
				filesToSend += testCaseAws.size();
				FilesSenderAWSBucket awsFilesSender = new FilesSenderAWSBucket(key, secretKey, nameBucket, region);
				for (int j = 0; j < testCaseAws.size(); j++) {
					File fileToSend = new File(pathFileTest.get(j));
					awsFilesSender.moveFileToS3Bucket(pathKeyName.get(j), fileToSend);
					sentFiles += 1;
				}
			}
			SftpFilesSender sftpFilesSender = new SftpFilesSender(url, user, password, port);
			for (int j = 0; j < filesFromXlsList.size(); j++) {
				sftpFilesSender.sendFileSftp(new File(filesFromXlsList.get(j)), accountsList.get(j));
				sentFiles += 1;
			}
			sftpFilesSender.endSftpSession();
		}
	}

	private boolean isTestCaseAWS() throws IOException {

		listTestCase = ExcelReader.getValueFieldPosition(srcPath, 0);
		accountsList = ExcelReader.getValueFieldPosition(srcPath, 1);
		filesFromXlsList = ExcelReader.getValueFieldPosition(srcPath, 2);
		testCaseAws = new ArrayList<>();
		pathKeyName = new ArrayList<String>();
		pathFileTest = new ArrayList<String>();

		for (int i = 0; i < listTestCase.size(); i++) {
			if (listTestCase.get(i).equals("1.1") || listTestCase.get(i).equals("1.4")
					|| listTestCase.get(i).equals("1.5") || listTestCase.get(i).equals("1.6")
					|| listTestCase.get(i).equals("1.7")) {
				testCaseAws.add(listTestCase.get(i));
				pathFileTest.add(filesFromXlsList.get(i));
				file = new File(filesFromXlsList.get(i));
				nameFile = file.getName();
				keyName = accountsList.get(i) + "/" + nameFile;
				pathKeyName.add(keyName);
				listTestCase.remove(i);
				filesFromXlsList.remove(i);
				accountsList.remove(i);
			}
		}

		if (!testCaseAws.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	public void setDstPath(String dstPath) {
		this.dstPath = dstPath;
	}

	public String getSrcPath() {
		return srcPath;
	}

	public void setSrcPath(String srcPath) {
		this.srcPath = srcPath;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getNameBucket() {
		return nameBucket;
	}

	public void setNameBucket(String nameBucket) {
		this.nameBucket = nameBucket;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public List<String> getFilesFromXls() {
		return filesFromXlsList;
	}

	public void setFilesFromXls(ArrayList<String> listFiles) {
		this.filesFromXlsList = listFiles;
	}

	public String getDstPath() {
		return dstPath;
	}
	
	public int getSentFiles() {
		return this.sentFiles;
	}
	
	public int getFilesToSend() {
		return this.filesToSend;
	}
}
