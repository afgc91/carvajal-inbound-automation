package com.carvajal.facturaclaro.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.carvajal.facturaclaro.ral.dto.AuthorizationDTO;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class FilesSender {

	private ChannelSftp channelSftp;
	private Session session;
	/**
	 * Clase que permite enviar por sftp los archivos relacionados en el archivo de
	 * configuración de la prueba
	 * 
	 * @param aut Objeto de tipo AuthorizationDTO que permite obtener la iformación
	 *            de la ruta del archivo y ruta sftp a la cual se enviaran los
	 *            archivos
	 * @throws JSchException
	 * @throws SftpException
	 * @throws IOException   Excepción de Entrada y salida
	 */

	public FilesSender(String sftpPath) throws JSchException, SftpException, IOException {

		Properties property = new Properties();
		InputStream inputStream = new FileInputStream(sftpPath);
		property.load(inputStream);

		String user = property.getProperty("user").trim();
		String url = property.getProperty("url").trim();
		int port = Integer.parseInt(property.getProperty("port").trim());
		String password = property.getProperty("password").trim();

		JSch jsch = new JSch();
		this.session = jsch.getSession(user, url, port);
		session.setConfig("StrictHostKeyChecking", "no");
		session.setConfig("PreferredAuthentications", "password");
		session.setPassword(password);
		session.connect();
		Channel channel = session.openChannel("sftp");
		this.channelSftp = (ChannelSftp) channel;
		this.channelSftp.connect();

	}

	public void sendFiles(AuthorizationDTO aut) throws SftpException {
		channelSftp.put(aut.getPathFile(), aut.getPathSftp());
	}
	
	public void close() {
		channelSftp.exit();
		session.disconnect();
	}
}
