package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class BusinessValidator {

	private Connection con;
	private String status;
	private String processName;
	private String message;
	private String directory; 

	public BusinessValidator(Connection con, String directory) {

		this.con = con;
		this.directory=directory; 
	}

	public void executeStatusQuery(File file)
			throws SQLException, IOException, ParserConfigurationException, SAXException {

		File log = getLogFile();

		String docStatusQuery = "select * from estados_procesamiento where id_transaccion = "
				+ "(select * from (select id " + "	from transacciones " + "	where nombre_archivo_original = ? "
				+ "	order by fecha_creacion desc) where rownum = 1)" + "order by fecha_creacion";

		String docNumQuery = "select * from datos_documentos " + "where id_documento = (select id from documentos "
				+ "where numero_documento = ?)";

		if (con == null) {
			JOptionPane.showMessageDialog(null, "No se pudo realizar la conexión", "Conexión nula",
					JOptionPane.ERROR_MESSAGE);
		}
		try (PreparedStatement docStatusPs = con.prepareStatement(docStatusQuery);
				PreparedStatement docNumPs = con.prepareStatement(docNumQuery)) {
			String factNum = CarvajalUtils.getFactNumber(file.getAbsolutePath());
			docStatusPs.setString(1, file.getName());
			docNumPs.setString(1, factNum);
			try (ResultSet docStatusRs = docStatusPs.executeQuery(); ResultSet docNumRs = docNumPs.executeQuery()) {

				String numberFileGovernment = "";
				String cufe = "";

				if (docNumRs.next()) {
					numberFileGovernment = docNumRs.getString(13);
					cufe = docNumRs.getString(14);
				}
				try (FileWriter fw = new FileWriter(log.getAbsoluteFile(), true);
						BufferedWriter bw = new BufferedWriter(fw)) {

					bw.write(factNum + "\r\n");
					boolean isFailed = false;
					while (docStatusRs.next()) {

						status = docStatusRs.getString(4);
						processName = docStatusRs.getString(3);
						message = docStatusRs.getString(8);

						if (processName.equalsIgnoreCase("DOCUMENT_PROCESSED") && status.equalsIgnoreCase("FAIL")) {
							isFailed = true;
						}

						bw.write("Proceso: " + processName + "\r\nEstado: " + status + " Mensaje: " + message + "\r\n");
					}
					if (!isFailed) {
						bw.write("Nombre Archivo de Gobierno: " + numberFileGovernment + "\r\nCUFE : " + cufe + "\r\n");
					}
					bw.write("---------------------------------------------------------------------------------------"
							+ "-------------------------------------------------------------\r\n");
				}
			}
		}
	}
	
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
		log.createNewFile();
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
