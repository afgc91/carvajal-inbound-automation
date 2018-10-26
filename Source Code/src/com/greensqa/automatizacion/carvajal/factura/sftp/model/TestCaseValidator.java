package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class TestCaseValidator {

	private Connection con;
	private String docStatusQuery;
	private String docNameXMLQuery;
	private String docNamePDFQuery;
	private String docNameAcceptQuery;// TODO
	private String docNameRtaQuery;
	private String docFactQuery;// TODO
	private static String status;
	private static String processName;
	private String message;
	private String factNum;
	private String nameFileGovernment = "";
	private String cufe = "";// TODO
	private String namePDFFact = "";
	private String nameFileAccept = "";// TODO
	private String codeRtaDian = "";// TODO
	private String infoRtaDian = "";// TODO
	private boolean failProcess = false;// TODO
	private String resultados;
	private String logErrorQuery;
	private String logError;
	private boolean validateDocumentProcessedOk = false;
	private final String patternXml = "face_[f,c,n,d][0-9]{10,11}[0-9A-Fa-f]{10}\\.xml"; 
	private final String patternPdf = "face_[f,c,n,d][0-9]{10,11}[0-9A-Fa-f]{10}\\.pdf"; 
	private static String typeDoc = "";
	private static String nitSender = "";
	private static String nitReceiver = "";


	public TestCaseValidator(Connection con) {

		this.con = con;

	}

	public void executeQuery(File file) throws SQLException, Exception {

		docStatusQuery = "select * from estados_procesamiento where id_transaccion = " + "(select * from (select id "
				+ "	from transacciones " + "	where nombre_archivo_original = ? "
				+ "	order by fecha_creacion desc) where rownum = 1)" + "order by fecha_creacion";

		docNameXMLQuery = "select * from archivos_procesamiento " 
				+ "where  id_document_storage = (select id_storage_firmado from datos_documentos "
				+ "where id_documento = (select id from documentos " + "where numero_documento= ? ))";

		docNamePDFQuery = "select * from archivos_procesamiento "
				+ "where  id_document_storage = (select id_storage_pdf from datos_documentos "
				+ "where id_documento = (select id from documentos " + "where numero_documento= ? )) ";

		docNameAcceptQuery = "select * from archivos_procesamiento "
				+ "where  id_document_storage = (select id_storage_aceptacion from datos_documentos "
				+ "where id_documento = (select id from documentos \" + \"where numero_documento= ? )) ";

		docNameRtaQuery = "select * from auditoria_envios_rta_gob where  id_storage_rta = "
				+ "(select id_storage_rta_entidad from datos_documentos "
				+ "where id_documento = (select id from documentos where numero_documento= ? ))";

		docFactQuery = "select * from documentos where numero_documento = ? ";

		logErrorQuery = "select * from log_errores where id_transaccion = " + "(select * from (select id "
				+ "	from transacciones " + "	where nombre_archivo_original = ? "
				+ "	order by fecha_creacion desc) where rownum = 1)" + "order by fecha_creacion";

		if (con == null) {
			JOptionPane.showMessageDialog(null, "No se pudo realizar la conexión", "Conexión nula",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void testCase(String pathFileTest) throws SQLException, Exception {

		int statusOk = 0;
		int statusNOk = 0;		

		File fileFhater = new File(pathFileTest);
		String directory = fileFhater.getParent();
		FileLogger fileLogger = new FileLogger();
		File log = fileLogger.getLogFile(directory);

		try (FileWriter fw = new FileWriter(log.getAbsoluteFile(), true); BufferedWriter bw = new BufferedWriter(fw)) {
			
			ArrayList<String> testCase = ExcelReader.getValueFieldPosition(pathFileTest, 0);
			ArrayList<String> documents = ExcelReader.getValueFieldPosition(pathFileTest, 2);

			for (int i = 0; i < testCase.size(); i++) {
				File file = new File(documents.get(i));
				factNum = CarvajalUtils.getFactNumber(file.getAbsolutePath());
				String nameDocument = ((file.getName()).split("\\.")[0]);
	
				typeDoc = CarvajalUtils.getTypeId(file.getAbsolutePath());
				nitSender = CarvajalUtils.getNitSupplier(file.getAbsolutePath());
				nitReceiver = CarvajalUtils.getNitCustomer(file.getAbsolutePath());
				bw.write("Caso de prueba: " + testCase.get(i) + "\r\n");
				
				if ((testCase.get(i)).equals("1.2") || (testCase.get(i)).equals("1.11")) {
					bw.write( "Tipo: " + typeDoc +"  " +"Nit del Emisor: " + nitSender + "  Nit del Receptor:" + nitReceiver + "\r\n \r\n");
					generateProcessFail(documents.get(i), log);
					filesGenerates(documents.get(i), log);
					if (validateDocumentProcessedOk) {
						if (nameFileGovernment.contains(nameDocument) && namePDFFact.contains(nameDocument)) {
							statusOk++;							
							bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum + "\r\nPDF: "
									+ namePDFFact + "\r\nArchivo de gobierno: " + nameFileGovernment
									+ "\r\nEstado : OK \r\n");
							bw.write(resultados);
						} else {
							statusNOk++;
							bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum + "\r\nPDF: "
									+ namePDFFact + "\r\nArchivo de gobierno: " + nameFileGovernment
									+ "\r\nEstado : NOK \r\n");
							bw.write(
									"El nombre de los documentos generados no contiene el nombre del documento Original \r\n");
						}
					} else {
						bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum
								+ " Estado : NOK \r\n");
						statusNOk++;
						bw.write(resultados);
					}
				} else if ((testCase.get(i).equals("1.1")) || (testCase.get(i).equals("1.5"))
						|| (testCase.get(i).equals("1.6")) || (testCase.get(i).equals("1.7"))) {
					File fileSend = new File(documents.get(i));
					String nameFileSend = fileSend.getName();
					bw.write( "Tipo: " + typeDoc +"  " +"Nit del Emisor: " + nitSender + "  Nit del Receptor:" + nitReceiver + "\r\n \r\n");

					if (validateDocumentProcessedOk) {
						if (nameFileGovernment.matches("face_[f,c,n,d][0-9]{10,11}[0-9A-Fa-f]{10}")
								&& namePDFFact.matches("face_[f,c,n,d][0-9]{10,11}[0-9A-Fa-f]{10}")) {
							bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum
									+ "\r\nNombre Comprobante en Zip: " + nameFileSend + " \r\nDocumento PDF : "
									+ namePDFFact + "\r\nArchivo de gobierno: " + nameFileGovernment
									+ " Estado : OK \r\n");
							bw.write(resultados);
							statusOk++;
						} else {
							bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum
									+ "\r\nNombre Comprobante en Zip: " + nameFileSend + " \r\nDocumento PDF : "
									+ namePDFFact + "\r\nArchivo de gobierno: " + nameFileGovernment
									+ " Estado : NOK \r\n");
							statusNOk++;
						}
					} else {
						bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum
								+ "\r\nNombre Comprobante en Zip: " + nameFileSend + "\r\n Estado : NOK \r\n");
						statusNOk++;
						bw.write(resultados);
					}
				} else if (testCase.get(i).equals("1.9")) {
					generateProcessFail(documents.get(i), log);
					filesGenerates(documents.get(i), log);
					bw.write( "Tipo: " + typeDoc +"  " +"Nit del Emisor: " + nitSender + "  Nit del Receptor:" + nitReceiver + "\r\n \r\n");
					if (validateDocumentProcessedOk) {
						if (nameFileGovernment.equalsIgnoreCase(file.getName() + ".xml")
								&& namePDFFact.equalsIgnoreCase(file.getName() + ".pdf")) {
							bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum
									+ " \r\nDocumento PDF : " + namePDFFact + "\r\nArchivo de gobierno: "
									+ nameFileGovernment + "\r\nEstado : OK \r\n");
							bw.write(resultados);
							statusOk++;
						} else {
							statusNOk++;
							bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum
									+ " \r\nDocumento PDF : " + namePDFFact + "\r\nArchivo de gobierno: "
									+ nameFileGovernment + "\r\nEstado : NOK \r\n");
							bw.write(
									"El nombre del archivo generado no corresponde al nombre del archivo original enviado \r\n");
						}
					} else {
						bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum
								+ "\r\nEstado : NOK \r\n");
						statusNOk++;
						bw.write(resultados);
					}
				} else if ((testCase.get(i).equals("1.8"))) {
					generateLogError(documents.get(i), log);
					bw.write( "Tipo: " + typeDoc +"  " +"Nit del Emisor: " + nitSender + "  Nit del Receptor:" + nitReceiver + "\r\n \r\n");
					if (logError != null) {
						if (logError.contains("[ERROR-VA3]:")) {
							bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum + "\r\n"
									+ "Estado : OK \r\n");
							bw.write("Contenido del Log de Errores: \r\n" + logError  + "\r\n");
							statusOk++;
						} else {
							bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum 
									+ "\r\nEstado : NOK \r\n"
									+ "No se genero el Log de errores correspondiente");
							bw.write(logError + "\r\n");
							statusNOk++;
						}
					} else {
						bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum
								+ "\r\nEstado : NOK \r\n");
						statusNOk++;
						bw.write(resultados);
					}
				} else if ((testCase.get(i).equals("1.3"))) {

					String pathFile = FilesCompressor.unZip(documents.get(i));
					factNum = CarvajalUtils.getFactNumber(pathFile);
					File fileInZip = new File(pathFile);
					typeDoc = CarvajalUtils.getTypeId(fileInZip.getAbsolutePath());
					nitSender = CarvajalUtils.getNitSupplier(fileInZip.getAbsolutePath());
					nitReceiver = CarvajalUtils.getNitCustomer(fileInZip.getAbsolutePath());
					bw.write( "Tipo: " + typeDoc +"  " +"Nit del Emisor: " + nitSender + "  Nit del Receptor:" + nitReceiver + "\r\n \r\n");
					String nameFileSend = fileInZip.getName();
					generateProcessFail(documents.get(i), log);
					filesGenerates(documents.get(i), log);
					
					if (validateDocumentProcessedOk) {						
						if (nameFileGovernment.matches(patternXml)
								&& namePDFFact.matches(patternPdf)) {
							bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum
									+ "\r\nNombre Comprobante en Zip: " + nameFileSend + " \r\nDocumento PDF : "
									+ namePDFFact + "\r\nArchivo de gobierno: " + nameFileGovernment
									+ "\r\nEstado : OK \r\n \r\n");
							bw.write(resultados);
							statusOk++;
						} else {
							bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum
									+ "\r\nNombre Comprobante en Zip: " + nameFileSend + " \r\nDocumento PDF : "
									+ namePDFFact + "\r\nArchivo de gobierno: " + nameFileGovernment
									+ "\r\nEstado : NOK \r\n");
							bw.write("El nombre de los archivos generados no corresponde a la estructura face_Tipodecomprobantennnnnnnnnnhhhhhhhhhh \r\n");
							statusNOk++;
						}
					} else {
						bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum
								+ "\r\nNombre Comprobante en Zip: " + nameFileSend + "\r\nEstado : NOK \r\n");
						statusNOk++;
						bw.write(resultados);
					}
				} else if ((testCase.get(i).equals("1.14")) || (testCase.get(i).equals("1.15"))
						|| (testCase.get(i).equals("1.17"))) {
					generateProcessFail(documents.get(i), log);
					filesGenerates(documents.get(i), log);
					bw.write( "Tipo: " + typeDoc +"  " +"Nit del Emisor: " + nitSender + "  Nit del Receptor:" + nitReceiver + "\r\n \r\n");
					if (validateDocumentProcessedOk) {
						bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum + " \r\nDocumento PDF : "
								+ namePDFFact + "\r\nArchivo de gobierno: " + nameFileGovernment
								+ "\r\nEstado : OK \r\n");
						bw.write(resultados);
						statusOk++;
					} else {
						bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum
								+ "\n Estado : NOK \r\n");
					statusNOk++;
					bw.write(resultados);
				}}
				else if ((testCase.get(i).equals("1.18"))){
					generateProcessFail(documents.get(i), log);
					filesGenerates(documents.get(i), log);
					bw.write( "Tipo: " + typeDoc +"  " +"Nit del Emisor: " + nitSender + "  Nit del Receptor:" + nitReceiver + "\r\n \r\n");
					if(validateDocumentProcessedOk){
						bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum
								+ "\n Estado : OK \r\n");
						bw.write("El error generado es: \r\n");
						bw.write(resultados + "\r\n");
						statusOk++;						
					} else { bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum
							+ "\n Estado : NOK \r\n");
					statusNOk++;					
					bw.write(resultados + "\r\n");}
					}
				bw.write("------------------------------------------------------------------------ \r\n");
			}
			fw.flush();
			bw.write("Cantidad de archivos procesados correctamente: " + statusOk
					+ "\r\nCantidad de archivos procesados con errores: " + statusNOk);
		}
	}

	private void generateProcessFail(String filePathTest, File log) throws SQLException, Exception {

		try (PreparedStatement docStatusPs = con.prepareStatement(docStatusQuery);
				FileWriter fw = new FileWriter(log.getAbsoluteFile(), true);
				BufferedWriter bw = new BufferedWriter(fw)) {

			File fileSend = new File(filePathTest);
			docStatusPs.setString(1, fileSend.getName());

			try (ResultSet docStatusRs = docStatusPs.executeQuery()) {

				if (docStatusRs != null) {
					while (docStatusRs.next()) {
						status = docStatusRs.getString(4);
						processName = docStatusRs.getString(3);
						message = docStatusRs.getString(8);

						if (!validateDocumentProcessedOk) {
							if (("DOCUMENT_PROCESSED").equals(processName)) {
								if (("OK").equals(status)) {
									validateDocumentProcessedOk = true;
								}
							}
						}

						if (status.equalsIgnoreCase("FAIL")) {
							resultados = ("Los siguientes procesos fallaron: \r\n" + "Proceso: " + processName
									+ "\r\nEstado: " + status + " Mensaje: " + message + "\r\n");
							failProcess = true;
						}
					}
				}
				if (!failProcess) {
					resultados = ("El procesamiento del documento no generó errores. \r\n");
				}
			}
		}
	}

	private void filesGenerates(String filePathTest, File log) throws SQLException, Exception {

		try (PreparedStatement docStatusPs = con.prepareStatement(docStatusQuery);
				PreparedStatement docXMLNamePs = con.prepareStatement(docNameXMLQuery);
				PreparedStatement docPDFNamePs = con.prepareStatement(docNamePDFQuery);
				PreparedStatement docRtaDianPs = con.prepareStatement(docNameRtaQuery)) {

			docXMLNamePs.setString(1, factNum);
			docPDFNamePs.setString(1, factNum);
			docRtaDianPs.setString(1, factNum);

			try (ResultSet docXMLNameRs = docXMLNamePs.executeQuery();
					ResultSet docNamePDFRs = docPDFNamePs.executeQuery();
					ResultSet docRtaDianRs = docRtaDianPs.executeQuery()) {

				if ((docXMLNameRs != null)) {
					while (docNamePDFRs.next()) {
						namePDFFact = docNamePDFRs.getString(5);

					}
				}

				if ((docXMLNameRs != null)) {
					while (docRtaDianRs.next()) {
						codeRtaDian = docRtaDianRs.getString(4);
						infoRtaDian = docRtaDianRs.getString(5);
					}
				}

				if (docXMLNameRs != null) {
					while (docXMLNameRs.next()) {
						nameFileGovernment = docXMLNameRs.getString(5);
						// cufe = docXMLNameRs.getString(14);
					}
				}
			}
		}
	}

	private void generateLogError(String filePathTest, File log) throws SQLException, Exception {

		try (PreparedStatement logErrorPs = con.prepareStatement(logErrorQuery)) {

			File fileSend = new File(filePathTest);
			logErrorPs.setString(1, fileSend.getName());

			try (ResultSet logErrorRs = logErrorPs.executeQuery()) {

				if (logErrorRs != null) {
					while (logErrorRs.next()) {
						logError = logErrorRs.getString(5);
					}
				}
			}
		}
	}

}