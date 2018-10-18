package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.util.SystemOutLogger;
import org.jaxen.pattern.PatternHandler;
import org.xml.sax.SAXException;

import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;

public class TestCaseValidator {

	private Connection con;
	private String docStatusQuery;
	private String docNameXMLQuery;
	private String docNamePDFQuery;
	private String docNameAcceptQuery;//TODO
	private String docNameRtaQuery;
	private String docFactQuery;//TODO
	private String status;
	private String processName;
	private String message;
	private String factNum;
	private String nameFileGovernment = "";
	private String cufe = "";//TODO
	private String namePDFFact = "";
	private String nameFileAccept = "";//TODO
	private String codeRtaDian = "";//TODO
	private String infoRtaDian = "";//TODO
	private boolean failProcess = false;//TODO
	private String resultados;
	private String logErrorQuery;
	private String logError;

	public TestCaseValidator(Connection con) {

		this.con = con;

	}

	public void executeQuery(File file) throws SQLException, Exception {

		docStatusQuery = "select * from estados_procesamiento where id_transaccion = " + "(select * from (select id "
				+ "	from transacciones " + "	where nombre_archivo_original = ? "
				+ "	order by fecha_creacion desc) where rownum = 1)" + "order by fecha_creacion";

		docNameXMLQuery = "select * from datos_documentos " + "where id_documento = (select id from documentos "
				+ "where numero_documento = ?)";

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

		String factNum = "";
		String status = "";
		int statusOk = 0;
		int statusNOk = 0;

		File fileFhater = new File(pathFileTest);
		String directory = fileFhater.getParent();
		CarvajalFileLogger fileLogger = new CarvajalFileLogger();
		File log = fileLogger.getLogFile(directory);

		try (FileWriter fw = new FileWriter(log.getAbsoluteFile(), true); BufferedWriter bw = new BufferedWriter(fw)) {

			ArrayList<String> testCase = ExcelReader.getValueFieldPosition(pathFileTest, 0);
			ArrayList<String> documents = ExcelReader.getValueFieldPosition(pathFileTest, 2);

			for (int i = 0; i < testCase.size(); i++) {
				File file = new File(documents.get(i));
				factNum = CarvajalUtils.getFactNumber(file.getAbsolutePath());
				String nameDocument = ((file.getName()).split("\\.")[0]);
				bw.write("Caso de prueba: " + testCase.get(i) + "\r\n \r\n");
				if ((testCase.get(i)).equals("1.2") || (testCase.get(i)).equals("1.11")) {
					if (nameFileGovernment.contains(nameDocument) && namePDFFact.contains(nameDocument)
							&& ("DOCUMENT_PROCESSED").equals(processName) && ("OK").equals(status)) {
						bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum + " PDF: "
								+ namePDFFact + " Estado : OK \r\n");
						statusOk++;
						generateProcessFail(file.getAbsolutePath(), log);
						bw.write(resultados);
					} else {
						bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum
								+ " Estado : NOK \r\n");
						statusNOk++;
						generateProcessFail(file.getAbsolutePath(), log);
						bw.write(resultados);
					}
				} 
				else if ((testCase.get(i).equals("1.3"))) {
					File fileSend = new File(documents.get(i));
					String nameFileSend = fileSend.getName();

					if (("DOCUMENT_PROCESSED").equals(processName) && ("OK").equals(status)
							&& nameFileGovernment.matches("face_[f,c,n,d][0-9]{10,11}[0-9A-Fa-f]{10}")
							&& namePDFFact.matches("face_[f,c,n,d][0-9]{10,11}[0-9A-Fa-f]{10}")) {
						bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum
								+ "\r\nNombre Comprobante en Zip: " + nameFileSend + " \r\nDocumento PDF : "
								+ namePDFFact + "\r\nArchivo de gobierno: " + nameFileGovernment + " Estado : OK \r\n");
						filesGenerates(file.getAbsolutePath(), log);
						generateProcessFail(file.getAbsolutePath(), log);
						bw.write(resultados);
						statusOk++;
					} else {
						bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum
								+ "\r\nNombre Comprobante en Zip: " + nameFileSend + "\r\n Estado : NOK \r\n");
						statusNOk++;
						filesGenerates(file.getAbsolutePath(), log);
						generateProcessFail(file.getAbsolutePath(), log);
						bw.write(resultados);
					}
				} 
				else if ((testCase.get(i).equals("1.8"))) {
					generateLogError(file.getAbsolutePath(), log);
					if (logError != null) {
						if (logError.contains("[ERROR-VA3]:")) {
							bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum + " PDF: "
									+ namePDFFact + " Estado : OK \r\n");
							bw.write(logError);
							statusOk++;
						} else {
							bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum + " PDF: "
									+ namePDFFact + " Estado : NOK \r\n"
									+ "No se genero el Log de errores correspondiente");
							bw.write(logError + "\r\n");
							statusNOk++;
						}
					} else {
						bw.write("No se genero Log de errores \r\n");
					}
				} else if ((testCase.get(i).equals("1.9"))) {

					String pathFile = CarvajalcompressFiles.unZip(documents.get(i));
					String facNum = CarvajalUtils.getFactNumber(pathFile);
					File fileInZip = new File(pathFile);
					String nameFileSend = fileInZip.getName();

					if (("DOCUMENT_PROCESSED").equals(processName) && ("OK").equals(status)
							&& nameFileGovernment.matches("face_[f,c,n,d][0-9]{10,11}[0-9A-Fa-f]{10}")
							&& namePDFFact.matches("face_[f,c,n,d][0-9]{10,11}[0-9A-Fa-f]{10}")) {
						bw.write("Documento enviado: " + file.getName() + " Factura No: " + facNum
								+ "\r\nNombre Comprobante en Zip: " + nameFileSend + " \r\nDocumento PDF : "
								+ namePDFFact + "\r\nArchivo de gobierno: " + nameFileGovernment + " Estado : OK \r\n");
						filesGenerates(file.getAbsolutePath(), log);
						generateProcessFail(file.getAbsolutePath(), log);
						bw.write(resultados);
						statusOk++;
					} else {
						bw.write("Documento enviado: " + file.getName() + " Factura No: " + facNum
								+ "\r\nNombre Comprobante en Zip: " + nameFileSend + "\r\n Estado : NOK \r\n");
						statusNOk++;
						filesGenerates(file.getAbsolutePath(), log);
						generateProcessFail(file.getAbsolutePath(), log);
						bw.write(resultados);
					}
				} else if ((testCase.get(i).equals("1.14")) || (testCase.get(i).equals("1.15")) || (testCase.get(i).equals("1.17"))){
					if(("DOCUMENT_PROCESSED").equals(processName) && ("OK").equals(status)){
						bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum
								+ "\n Estado : OK \r\n");
						generateProcessFail(file.getAbsolutePath(), log);
						bw.write(resultados);
						statusOk++;						
					} else bw.write("Documento enviado: " + file.getName() + " Factura No: " + factNum
							+ "\n Estado : NOK \r\n");
					statusNOk++;
					generateProcessFail(file.getAbsolutePath(), log);
					bw.write(resultados);
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
			factNum = CarvajalUtils.getFactNumber(fileSend.getAbsolutePath());
			docStatusPs.setString(1, fileSend.getName());

			try (ResultSet docStatusRs = docStatusPs.executeQuery()) {

				if (docStatusRs != null) {
					while (docStatusRs.next()) {
						status = docStatusRs.getString(4);
						processName = docStatusRs.getString(3);
						message = docStatusRs.getString(8);

						if (status.equalsIgnoreCase("FAIL")) {
							resultados = ("Los siguientes procesos fallaron: \r\n" + "Proceso: " + processName
									+ "\r\nEstado: " + status + " Mensaje: " + message + "\r\n");
							failProcess = true;
						}
					}
				} else {
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

			File fileSend = new File(filePathTest);
			factNum = CarvajalUtils.getFactNumber(fileSend.getAbsolutePath());
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
						nameFileGovernment = docXMLNameRs.getString(13);
						cufe = docXMLNameRs.getString(14);
					}
				}
			}
		}
	}

	private void generateLogError(String filePathTest, File log) throws SQLException, Exception {

		try (PreparedStatement logErrorPs = con.prepareStatement(logErrorQuery)) {

			File fileSend = new File(filePathTest);
			factNum = CarvajalUtils.getFactNumber(fileSend.getAbsolutePath());
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