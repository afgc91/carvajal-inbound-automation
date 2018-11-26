package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.Closeable;
import java.io.File;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * Esta clase permite establecer una conexión vía SFTP para enviar archivos.
 * 
 * @author Andrés Fernando Gasca Cruz
 *
 */
public class SftpFilesSender implements Closeable {

	private ChannelSftp channelSftp;
	private Session session;

	/**
	 * Constructor de la clase SftpFilesSender. Establece la conexión SFTP a partir
	 * de los parámetros enviados.
	 * 
	 * @param url      Ruta del host.
	 * @param user     Usuario SFTP.
	 * @param password Contraseña SFTP.
	 * @param port     Puerto del servidor por el cual escucha la conexión SFTP.
	 * @throws JSchException En caso de error en el establecimiento de la conexión.
	 */
	public SftpFilesSender(String url, String user, String password, int port) throws JSchException {
		JSch jsch = new JSch();
		this.session = jsch.getSession(user, url, port);
		session.setConfig("PreferredAuthentications", "password");
		session.setConfig("StrictHostKeyChecking", "no");
		session.setPassword(password);
		session.connect();

		Channel channel = session.openChannel("sftp");

		this.channelSftp = (ChannelSftp) channel;
		this.channelSftp.connect();
	}

	/**
	 * Envía un archivo vía SFTP a una ruta específica.
	 * 
	 * @param file    Archivo a enviar.
	 * @param dstPath Ruta de destino (donde debe quedar el archivo).
	 * @throws SftpException En caso de un error durante el envío del archivo.
	 */
	public void sendFileSftp(File file, String dstPath) throws SftpException {
		channelSftp.put(file.getAbsolutePath(), dstPath);
	}

	/**
	 * Finaliza la sesión SFTP (una vez se hayan enviado todos los archivos, dado
	 * que no se podrá seguir enviando archivos si se cierra la sesión).
	 */
	public void close() {
		channelSftp.exit();
		session.disconnect();
	}
}
