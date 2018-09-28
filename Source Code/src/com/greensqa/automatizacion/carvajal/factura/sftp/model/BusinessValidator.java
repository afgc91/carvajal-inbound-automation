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
	private String nameFilegovernment; 
	private String cufe; 
	
	public BusinessValidator(Connection con) {

		this.con = con;
	}

	public void executeStatusQuery(File file) throws SQLException, IOException, ParserConfigurationException, SAXException {

		String logPath = "C:\\Users\\dvalencia\\Documents\\Test FECO\\Log.txt";
		File log = new File(logPath);

		String docStatusQuery = "select * from estados_procesamiento where id_transaccion = " + "(select * from (select id "
				+ "	from transacciones " + "	where nombre_archivo_original = ? "
				+ "	order by fecha_creacion desc) where rownum = 1)" + "order by fecha_creacion";

		String docNumQuery = "select * from datos_documentos "
				+ "where id_documento = (select id from documentos "
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
			try (ResultSet docStatusRs = docStatusPs.executeQuery();
					ResultSet docNumRs = docNumPs.executeQuery()) {
				
				String numberFileGovernment =""; 
				String cufe="";
				
				if (docNumRs.next()) {
					numberFileGovernment=docNumRs.getString(13); 
					cufe=docNumRs.getString(14); 
					
				}
				try (FileWriter fw = new FileWriter(log.getAbsoluteFile(), true);
						BufferedWriter bw = new BufferedWriter(fw)) {
					
					while (docStatusRs.next()) {

						status = docStatusRs.getString(4);
						processName = docStatusRs.getString(3);
						message = docStatusRs.getString(8);
						if (!log.exists()) {
							log.createNewFile();
						}
						
						bw.write("Proceso: " + processName + "\r\nEstado: " + status + " Mensaje: " + message +"\r\n");	
					}
					bw.write("Nombre Archivo de Gobierno: " +numberFileGovernment + "\r\nCUFE: "+ cufe +"\r\n");
				}
				
				
//				ResultSet interno = nd.executeQuery();
//				while (interno.next() == true) {					
//				nameFilegovernment = interno.getString(13); 
//				cufe = interno.getString(14); 
//				
//				bw.newLine();
//				if (log.exists()) {
//					bw.write("Nombre Archivo de Gobierno: " + nameFilegovernment + "\n"+ cufe +"\n"); 
//					bw.newLine();
//				} else {
//					bw = new BufferedWriter(fw);
//					bw.write("Nombre Archivo de Gobierno: " + nameFilegovernment + "\n"+ cufe +"\n"); 
//					bw.newLine();
//				}
//				if (bw != null) {
//					bw.close();
//				}
//				if (fw != null) {
//					fw.close();
//				}
//				}
			}
		}
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

}
