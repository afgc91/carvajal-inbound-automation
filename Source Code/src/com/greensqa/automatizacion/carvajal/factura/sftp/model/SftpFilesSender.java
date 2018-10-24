package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
	static File file = null;
	static File[] files = null;
	static String nameFile = "";
	static String keyName = "";
	static List<String> listAccount = new ArrayList<String>();
	static List<String> listFiles = new ArrayList<String>();
	static List<String> listTestCase = new ArrayList<String>();
	static List<String> testCaseAws = new ArrayList<>();
	static List<String> pathKeyName = new ArrayList<String>();
	static List<String> pathFileTest = new ArrayList<String>();

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

	public void sendSftpFiles(int option) throws JSchException, SftpException, FileNotFoundException, IOException,
			ParseException, ParserConfigurationException, SAXException {

		if (option == 1) {

			File dir = new File(srcPath);
			File[] files = dir.listFiles();
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
			}

			sftpChannel.exit();
			session.disconnect();

		}
//Opción para enviar archivos según archivo de configuración de casos de prueba por SFTP - AS2 y AWS
		if (option == 2) {

			if (isTestCaseAWS()) {
				CarvajalFilesSenderAWSBucket filesAws = new CarvajalFilesSenderAWSBucket(key, secretKey, nameBucket,
						region);
				for (int j = 0; j < testCaseAws.size(); j++) {
					File fileSend = new File(pathFileTest.get(j));
					filesAws.moveFileToS3Bucket(pathKeyName.get(j), fileSend);
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
			System.out.println(listFiles.get(j) + " sfdsdf" + listAccount.get(j));
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

	public static List<String> getListFiles() {
		return listFiles;
	}

	public static void setListFiles(List<String> listFiles) {
		SftpFilesSender.listFiles = listFiles;
	}

	public String getDstPath() {
		return dstPath;
	}
}
