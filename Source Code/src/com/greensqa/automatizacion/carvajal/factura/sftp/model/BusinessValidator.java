package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class BusinessValidator {

	private Connection con;
	private String status;
	private String processName;
	private String message;
	private String directory;
	ArrayList<String> xmlPdfFiles = new ArrayList<String>();

	public BusinessValidator(Connection con, String directory) {

		this.con = con;
		this.directory = directory;
	}

	public void executeStatusQuery(File file)
			throws SQLException, IOException, ParserConfigurationException, SAXException {

		File log = getLogFile();
		log.createNewFile();

		String docStatusQuery = "select * from estados_procesamiento where id_transaccion = "
				+ "(select * from (select id " + "	from transacciones " + "	where nombre_archivo_original = ? "
				+ "	order by fecha_creacion desc) where rownum = 1)" + "order by fecha_creacion";

		String docNameXMLQuery = "select * from datos_documentos " + "where id_documento = (select id from documentos "
				+ "where numero_documento = ?)";

		String docNamePDFQuery = "select * from archivos_procesamiento "
				+ "where  id_document_storage = (select id_storage_pdf from datos_documentos "
				+ "where id_documento = (select id from documentos " + "where numero_documento= ? )) ";

		String docNameAcceptQuery = "select * from archivos_procesamiento "
				+ "where  id_document_storage = (select id_storage_aceptacion from datos_documentos "
				+ "where id_documento = (select id from documentos \" + \"where numero_documento= ? )) ";

		String docNameRtaQuery = "select * from auditoria_envios_rta_gob where  id_storage_rta = "
				+ "(select id_storage_rta_entidad from datos_documentos "
				+ "where id_documento = (select id from documentos where numero_documento= ? ))";

		String docFactQuery = "select * from documentos where numero_documento = ? ";

		if (con == null) {
			JOptionPane.showMessageDialog(null, "No se pudo realizar la conexión", "Conexión nula",
					JOptionPane.ERROR_MESSAGE);
		}
		try (PreparedStatement docStatusPs = con.prepareStatement(docStatusQuery);
				PreparedStatement docXMLNamePs = con.prepareStatement(docNameXMLQuery);
				PreparedStatement docPDFNamePs = con.prepareStatement(docNamePDFQuery);
				PreparedStatement docAcceptNamePs = con.prepareStatement(docNameAcceptQuery);
				PreparedStatement docRtaDianPs = con.prepareStatement(docNameRtaQuery);
				PreparedStatement docFactPs = con.prepareStatement(docFactQuery)) {

			String factNum = CarvajalUtils.getFactNumber(file.getAbsolutePath());
			String nitSender = CarvajalUtils.getNitSender(file.getAbsolutePath());
			// String typeId = CarvajalUtils.getTypeId(file.getAbsolutePath());

			docStatusPs.setString(1, file.getName());
			docXMLNamePs.setString(1, factNum);
			docPDFNamePs.setString(1, factNum);
			docAcceptNamePs.setString(1, factNum);
			docRtaDianPs.setString(1, factNum);
			docFactPs.setString(1, factNum);

			try (ResultSet docStatusRs = docStatusPs.executeQuery();
					ResultSet docXMLNameRs = docXMLNamePs.executeQuery();
					ResultSet docNamePDFRs = docPDFNamePs.executeQuery();
					ResultSet docAcceptNameRs = docAcceptNamePs.executeQuery();
					ResultSet docRtaDianRs = docRtaDianPs.executeQuery();
					ResultSet docFactExistRs = docFactPs.executeQuery()) {

				String nameFileGovernment = "";
				String cufe = "";
				String namePDFFact = "";
				String nameFileAccept = "";
				String codeRtaDian = "";
				String infoRtaDian = "";
				int factOk = 0;
				boolean isAccept = false;
				boolean isRtaDian = false;
				boolean isFailed = false;

				if (docFactExistRs.next()) {

					if (docXMLNameRs.next()) {
						nameFileGovernment = docXMLNameRs.getString(13);
						cufe = docXMLNameRs.getString(14);
					}

					if (docNamePDFRs.next()) {
						namePDFFact = docNamePDFRs.getString(5);
					}

					if (docAcceptNameRs.next()) {
						nameFileAccept = docAcceptNameRs.getString(25);
						if (docAcceptNameRs != null) {
							isAccept = true;
						}
					}

					if (docRtaDianRs.next()) {
						codeRtaDian = docRtaDianRs.getString(4);
						infoRtaDian = docRtaDianRs.getString(5);
						if (docRtaDianRs != null) {
							isRtaDian = true;
						}
					}

					isFailed = true;
				}

				try (FileWriter fw = new FileWriter(log.getAbsoluteFile(), true);
						BufferedWriter bw = new BufferedWriter(fw)) {
					int filesOk = 0;
					int filesNok = 0;

					bw.write("Número de Factura: " + factNum + "\r\n");
					bw.write("Nit del Emisor: " + nitSender + "\r\n");

					if (docStatusRs != null && docStatusRs.next()) {
						while (docStatusRs.next()) {

							status = docStatusRs.getString(4);
							processName = docStatusRs.getString(3);
							message = docStatusRs.getString(8);


							if (status.equalsIgnoreCase("FAIL")) {
								bw.write("Proceso: " + processName + "\r\nEstado: " + status + " Mensaje: " + message
										+ "\r\n");
							}

							if (processName.equalsIgnoreCase("DOCUMENT_PROCESSED") && status.equalsIgnoreCase("OK")) {
								filesOk++;
							} else {
								filesNok++;
							}
						}
					} else {
						bw.write("El archivo no ha sido enviado a CEN Financiero \r\n");
					}

					if (isFailed) {
						bw.write("\r\nNombre Archivo de Gobierno: " + nameFileGovernment + "\r\nNombre Archivo PDF: "
								+ namePDFFact + "\r\nCUFE : " + cufe + "\r\n");
						if (isAccept) {
							bw.write("Nombre Archivo de Aceptación Cliente: " + nameFileAccept + "\r\n");
						} else {
							bw.write("El Documento no ha sido Aceptado \r\n");
						}

						if (isRtaDian) {
							bw.write("La DIAN recibio el archivo, la respuesta generada es :" + codeRtaDian + "  "
									+ infoRtaDian + "\r\n");
						} else {
							bw.write("La Dian no ha realizado acuse de recibido \r\n");
						}

					}

					bw.write("---------------------------------------------------------------------------------------"
							+ "-------------------------------------------------------------\r\n");
					bw.write("Cantidad de Archivos exitosos: " + filesOk + "\r\nCantidad de Archivos no exitosos: " + filesNok + "\r\n");
				}
			}
		}
	}

	@SuppressWarnings("null")
	private File getLogFile() throws IOException {
		String dirPath = directory + "\\Logs";
		File dir = new File(dirPath);
		dir.mkdir();
		File[] files = dir.listFiles();
		String logFilePath = "";
		if (files != null) {
			logFilePath = dirPath + "\\log" + (files.length + 1) + ".txt";
		} else {
			logFilePath = dirPath + "\\log1.txt";
		}
		File log = new File(logFilePath);
		log.canWrite();
		return log;
	}

	public Connection getCon() {
		return con;
	}

	public void setCon(Connection con) {
		this.con = con;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

}
