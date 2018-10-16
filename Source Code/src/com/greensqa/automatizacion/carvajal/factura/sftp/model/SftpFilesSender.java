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
	static List<String> listAccount = new ArrayList<String>();
	static List<String> listFiles = new ArrayList<String>();

	public SftpFilesSender(String srcPath, String dstPath, String user, String password, String url, int port) {
		this.srcPath = srcPath;
		this.dstPath = dstPath;
		this.user = user;
		this.password = password;
		this.url = url;
		this.port = port;
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

		if (option == 2) {

			listAccount = ReadExcel.getValueFieldPosition(srcPath, 1);
			listFiles = ReadExcel.getValueFieldPosition(srcPath, 2);

			JSch jsch = new JSch();
			Session session = jsch.getSession(user, url, port);
			session.setConfig("PreferredAuthentications", "password");
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);
			session.connect();

			Channel channel = session.openChannel("sftp");

			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.connect();

			for (int i = 0; i < listFiles.size(); i++) {
				sftpChannel.put(listFiles.get(i), listAccount.get(i));
			}

			sftpChannel.exit();
			session.disconnect();
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

}
