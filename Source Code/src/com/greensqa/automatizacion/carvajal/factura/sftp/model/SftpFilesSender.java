package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SftpFilesSender {

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
	private List<String> listAccount = new ArrayList<String>();
	private List<String> listFiles = new ArrayList<String>();
	private List<String> listTestCase = new ArrayList<String>();
	private List<String> testCaseAws = new ArrayList<>();
	private List<String> pathKeyName = new ArrayList<String>();
	private List<String> pathFileTest = new ArrayList<String>();
	private int filesToSend;
	private int sentFiles;

	public SftpFilesSender(String srcPath, String dstPath, String user, String password, String url, int port,
			String key, String secretKey, String nameBucket, String region) {
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
			//Envío de archivos de acuerdo con lo parametrizado en el JSON de entrada.
			File dir = new File(srcPath);
			File[] files = dir.listFiles();
			filesToSend = files.length;
			JSch jsch = new JSch();
			Session session = jsch.getSession(user, url, port);
			session.setConfig("PreferredAuthentications", "password");
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);
			session.connect();

			Channel channel = session.openChannel("sftp");

			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.connect();

			for (int i = 0; i < files.length; i++) {
				sftpChannel.put(files[i].getAbsolutePath(), dstPath);
				sentFiles += 1;
			}

			sftpChannel.exit();
			session.disconnect();

		} else if (option == 2) {
			// Opción para enviar archivos según archivo de configuración de casos de prueba
			// por SFTP - AS2 y AWS

			if (isTestCaseAWS()) {
				sentFiles = testCaseAws.size() + listTestCase.size();
				CarvajalFilesSenderAWSBucket awsFilesSender = new CarvajalFilesSenderAWSBucket(key, secretKey, nameBucket,
						region);
				for (int j = 0; j < testCaseAws.size(); j++) {
					File fileToSend = new File(pathFileTest.get(j));
					awsFilesSender.moveFileToS3Bucket(pathKeyName.get(j), fileToSend);
				}

				for (int i = 0; i < listTestCase.size(); i++) {
					sendFilesSftp();
				}
			} else {
				sendFilesSftp();
			}
		}
	}

	private void sendFilesSftp() throws JSchException, SftpException {

		JSch jsch = new JSch();
		Session session = jsch.getSession(user, url, port);
		session.setConfig("PreferredAuthentications", "password");
		session.setConfig("StrictHostKeyChecking", "no");
		session.setPassword(password);
		session.connect();

		Channel channel = session.openChannel("sftp");

		ChannelSftp sftpChannel = (ChannelSftp) channel;
		sftpChannel.connect();

		for (int j = 0; j < listFiles.size(); j++) {
			sftpChannel.put(listFiles.get(j), listAccount.get(j));
		}
		sftpChannel.exit();
		session.disconnect();
	}

	private boolean isTestCaseAWS() throws IOException {

		listTestCase = ExcelReader.getValueFieldPosition(srcPath, 0);
		listAccount = ExcelReader.getValueFieldPosition(srcPath, 1);
		listFiles = ExcelReader.getValueFieldPosition(srcPath, 2);
		testCaseAws = new ArrayList<>();
		pathKeyName = new ArrayList<String>();
		pathFileTest = new ArrayList<String>();

		for (int i = 0; i < listTestCase.size(); i++) {
			if (listTestCase.get(i).equals("1.1") || listTestCase.get(i).equals("1.4")
					|| listTestCase.get(i).equals("1.5") || listTestCase.get(i).equals("1.6")
					|| listTestCase.get(i).equals("1.7")) {
				testCaseAws.add(listTestCase.get(i));
				pathFileTest.add(listFiles.get(i));
				file = new File(listFiles.get(i));
				nameFile = file.getName();
				keyName = listAccount.get(i) + "/" + nameFile;
				pathKeyName.add(keyName);
				listTestCase.remove(i);
				listFiles.remove(i);
				listAccount.remove(i);
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

	public List<String> getListFiles() {
		return listFiles;
	}

	public void setListFiles(List<String> listFiles) {
		this.listFiles = listFiles;
	}

	public String getDstPath() {
		return dstPath;
	}
}
