package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.File;

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
	
	public SftpFilesSender(String srcPath, String dstPath, String user, String password, String url, int port) {
		this.srcPath = srcPath;
		this.dstPath = dstPath;
		this.user = user;
		this.password = password;
		this.url = url;
		this.port = port;
	}
	
	public void sendSftpFiles() throws JSchException, SftpException {
		File dir = new File(srcPath);
		File[] files = dir.listFiles();
		JSch jsch = new JSch();
		Session session = jsch.getSession(user, url, port);
		session.setConfig("StrictHostKeyChecking", "no");
		session.setPassword(password);
		session.connect();
		
		Channel channel = session.openChannel("sftp");
		channel.connect();
		ChannelSftp sftpChannel = (ChannelSftp) channel;
		
		for (int i = 0; i < files.length; i++) {
			sftpChannel.put(files[i].getAbsolutePath(), dstPath);
		}
		
		sftpChannel.exit();
		session.disconnect();
	}

	public String getSrcPath() {
		return srcPath;
	}

	public void setSrcPath(String srcPath) {
		this.srcPath = srcPath;
	}

	public String getDstPath() {
		return dstPath;
	}

	public void setDstPath(String dstPath) {
		this.dstPath = dstPath;
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
