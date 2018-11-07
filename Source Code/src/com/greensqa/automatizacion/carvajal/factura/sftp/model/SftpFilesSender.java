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
 * Esta clase permite establecer una conexi�n v�a SFTP para enviar archivos.
 * 
 * @author Andr�s Fernando Gasca Cruz
 *
 */
public class SftpFilesSender implements Closeable {

	private ChannelSftp channelSftp;
	private Session session;

	/**
	 * Constructor de la clase SftpFilesSender. Establece la conexi�n SFTP a partir
	 * de los par�metros enviados.
	 * 
	 * @param url      Ruta del host.
	 * @param user     Usuario SFTP.
	 * @param password Contrase�a SFTP.
	 * @param port     Puerto del servidor por el cual escucha la conexi�n SFTP.
	 * @throws JSchException En caso de error en el establecimiento de la conexi�n.
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
	 * Env�a un archivo v�a SFTP a una ruta espec�fica.
	 * 
	 * @param file    Archivo a enviar.
	 * @param dstPath Ruta de destino (donde debe quedar el archivo).
	 * @throws SftpException En caso de un error durante el env�o del archivo.
	 */
	public void sendFileSftp(File file, String dstPath) throws SftpException {
		channelSftp.put(file.getAbsolutePath(), dstPath);
	}

	/**
	 * Finaliza la sesi�n SFTP (una vez se hayan enviado todos los archivos, dado
	 * que no se podr� seguir enviando archivos si se cierra la sesi�n).
	 */
	public void close() {
		channelSftp.exit();
		session.disconnect();
	}
}
