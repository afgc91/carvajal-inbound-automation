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

import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

public class BusinessValidator implements Progressable {

	private Connection con;
	private String status;
	private String processName;
	private String message;
	private String directory;
	ArrayList<String> xmlPdfFiles = new ArrayList<String>();
	private boolean isLogged;
	private String logFilePath;
	private int filesOk;
	private int filesFailed;
	private String nameFileGovernment = "";
	private String cufe = "";
	private String namePDFFact = "";
	private String nameFileAccept = "";
	private String codeRtaDian = "";
	private String infoRtaDian = "";
	private int startProcess;
	private int total = 0;
	private int totalItems;
	private int processedItems;
	private boolean keepWorking;
	private String techicalKey = "";
	private boolean cufeOK = false;
	private String statusCufe = "";
	private String cufeFile = ""; 
	private boolean validateDocumentProcessedOk = false;
	private boolean validateDocumentProcessedNok = false;

	public BusinessValidator(Connection con, String directory) {
		this.setLogged(false);
		this.con = con;
		this.directory = directory;
		this.logFilePath = null;
		this.setFilesFailed(0);
		this.setFilesOk(0);
		this.keepWorking = true;
	}

	public void executeStatusQuery(File file, String cufePath) throws Exception {

		File log = null;

		if (!isLogged) {
			isLogged = true;
			FileLogger fileLogger = new FileLogger();
			log = fileLogger.getLogFile(directory);
			log.createNewFile();
			logFilePath = log.getAbsolutePath();
		} else {
			log = new File(logFilePath);
		}


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

		String techinicalKeyPrefixQuery = "select * from rangos_autorizacion " + "where id_autorizacion_gobierno = "
				+ "(select id from autorizaciones_gobierno where numero_autorizacion = ? )";

		if (con == null) {
			JOptionPane.showMessageDialog(null, "No se pudo realizar la conexi�n", "Conexi�n nula",
					JOptionPane.ERROR_MESSAGE);
		}
		try (PreparedStatement docStatusPs = con.prepareStatement(docStatusQuery);
				PreparedStatement docXMLNamePs = con.prepareStatement(docNameXMLQuery);
				PreparedStatement docPDFNamePs = con.prepareStatement(docNamePDFQuery);
				PreparedStatement docAcceptNamePs = con.prepareStatement(docNameAcceptQuery);
				PreparedStatement docRtaDianPs = con.prepareStatement(docNameRtaQuery);
				PreparedStatement docFactPs = con.prepareStatement(docFactQuery);
				PreparedStatement docNumAuthorizationPs = con.prepareStatement(techinicalKeyPrefixQuery)) {

			String factNum = CarvajalUtils.getFactNumber(file.getAbsolutePath());
			String typeDoc = CarvajalUtils.getTypeId(file.getAbsolutePath());
			String nitSender = CarvajalUtils.getNitSupplier(file.getAbsolutePath());
			String nitReceiver = CarvajalUtils.getNitCustomer(file.getAbsolutePath());
			String numAuthorizarion = CarvajalUtils.getAuthorizationFromUblFile(file.getAbsolutePath());

			docStatusPs.setString(1, file.getName());
			docXMLNamePs.setString(1, factNum);
			docPDFNamePs.setString(1, factNum);
			docAcceptNamePs.setString(1, factNum);
			docRtaDianPs.setString(1, factNum);
			docFactPs.setString(1, factNum);
			docNumAuthorizationPs.setString(1, numAuthorizarion);

			try (ResultSet docStatusRs = docStatusPs.executeQuery();
					ResultSet docXMLNameRs = docXMLNamePs.executeQuery();
					ResultSet docNamePDFRs = docPDFNamePs.executeQuery();
					ResultSet docAcceptNameRs = docAcceptNamePs.executeQuery();
					ResultSet docRtaDianRs = docRtaDianPs.executeQuery();
					ResultSet docFactExistRs = docFactPs.executeQuery();
					ResultSet docNumAuthorizationRs = docNumAuthorizationPs.executeQuery()) {

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

					if (docNumAuthorizationRs.next()) {
						techicalKey = docNumAuthorizationRs.getString(2);
						System.out.println(techicalKey);
					}

					isFailed = true;
				}
				
				if (isFailed) {
					System.out.println(file.getAbsolutePath());
					cufeFile = cufeFile(file,cufePath);
					if (cufeFile.equalsIgnoreCase(cufe)) {
						cufeOK = true;
						statusCufe = "OK"; 
					} else {
						statusCufe = "NOK"; 
					}}

				try (FileWriter fw = new FileWriter(log.getAbsoluteFile(), true);
						BufferedWriter bw = new BufferedWriter(fw)) {
					boolean failProcess = false;
					bw.write("N�mero de Documento: " + factNum + " Tipo: " + typeDoc + "\r\n");
					bw.write("Nit del Emisor: " + nitSender + "  Nit del Receptor:" + nitReceiver + "\r\n \r\n");
					if (docStatusRs != null) {

						while (docStatusRs.next()) {

							status = docStatusRs.getString(4);
							processName = docStatusRs.getString(3);
							message = docStatusRs.getString(8);

							if (("START").equals(processName)) {
								startProcess += 1;
							}

							if (status.equalsIgnoreCase("FAIL")) {
								bw.write("Los siguientes procesos fallaron: \r\n" + "Proceso: " + processName
										+ "\r\nEstado: " + status + " Mensaje: " + message + "\r\n");
								failProcess = true;
							}

							// Validar documento procesado OK.
							if (!validateDocumentProcessedOk) {								 
								if (("DOCUMENT_PROCESSED").equals(processName)) {
									if (("OK").equals(status)){ 
										System.out.println(cufeOK);
										if(cufeOK==true) {
										filesOk += 1;
										validateDocumentProcessedOk = true;
									}else {
										filesFailed += 1;
										validateDocumentProcessedNok = true;
									}
								}}
							}

							// Validar documento procesado NOK
							if (!validateDocumentProcessedNok) {
								if ("FAIL".equals(status)) {
									filesFailed += 1;
									validateDocumentProcessedNok = true;
								
							}}
						}
					} else {
						bw.write("El archivo no ha sido enviado a CEN Financiero. \r\n");
					}

					if (!failProcess) {
						bw.write("El procesamiento del documento no gener� errores. \r\n");
					}

					if (isFailed) {
		
						bw.write("\r\nNombre Archivo de Gobierno: " + nameFileGovernment + "\r\nNombre Archivo PDF: "
								+ namePDFFact + "\r\nCUFE : " + cufe + "\r\n" +"Cufe Calculado: "+cufeFile +"\r\nEstado Cufe:"+ statusCufe + "\r\n");
						if (isAccept) {
							bw.write("Nombre Archivo de Aceptaci�n Cliente: " + nameFileAccept + "\r\n");
						} else {
							bw.write("El Documento no ha sido Aceptado. \r\n");
						}

						if (isRtaDian) {
							bw.write("La DIAN recibio el archivo, la respuesta generada es :" + codeRtaDian + "  "
									+ infoRtaDian + "\r\n");
						} else {
							bw.write("La Dian no ha realizado acuse de recibido. \r\n");
						}
					}

					bw.write("---------------------------------------------------------------------------------------"
							+ "-------------------------------------------------------------\r\n");

				}
			}
		}
	}

	public int getStartProcess() {
		return startProcess;
	}

	private String cufeFile(File file, String pathConfigConnection) throws Exception {
		FecoCufeGenerator cufe = new FecoCufeGenerator(pathConfigConnection, file);
		String cufeFeco = cufe.getValuePathXmlUbl();
		return cufeFeco;
	}

	// Contar la cantidad de archivos en la carpeta
	public int getTotalFiles(File file) {
		String[] arrArchivos = file.list();
		total += arrArchivos.length;
		File tmpFile;
		for (int i = 0; i < arrArchivos.length; ++i) {
			tmpFile = new File(file.getPath() + "/" + arrArchivos[i]);
			if (tmpFile.isDirectory()) {
				total += getTotalFiles(tmpFile);
			}
		}
		System.out.println(total);
		return total;
	}

	public void getSummary() throws IOException {
		File file = new File(logFilePath);

		try (FileWriter fw = new FileWriter(file.getAbsoluteFile(), true); BufferedWriter bw = new BufferedWriter(fw)) {
			bw.write("Cantidad de archivos procesados correctamente: " + filesOk
					+ "\r\nCantidad de archivos procesados con errores: " + filesFailed);
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

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public boolean isLogged() {
		return isLogged;
	}

	public void setLogged(boolean isLogged) {
		this.isLogged = isLogged;
	}

	public String getLogFilePath() {
		return logFilePath;
	}

	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}

	public int getFilesOk() {
		return filesOk;
	}

	public void setFilesOk(int filesOk) {
		this.filesOk = filesOk;
	}

	public int getFilesFailed() {
		return filesFailed;
	}

	public void setFilesFailed(int filesFailed) {
		this.filesFailed = filesFailed;
	}

	@Override
	public int getTotalItems() {
		// TODO Auto-generated method stub
		return totalItems;
	}

	@Override
	public int getProcessedItems() {
		// TODO Auto-generated method stub
		return processedItems;
	}

	public void setProcessedItems(int processedItems) {
		this.processedItems = processedItems;
	}

	public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}

	@Override
	public boolean isKeepWorking() {
		return this.keepWorking;
	}

	public void setKeepWorking(boolean keepWorking) {
		this.keepWorking = keepWorking;
	}

}