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
	private String docNameAcceptQuery;
	private String docNameRtaQuery;
	private String docFactQuery;
	private String status;
	private String processName;
	private String message;
	private String factNum;
	private String nameFileGovernment = "";
	private String cufe = "";
	private String namePDFFact = "";
	private String nameFileAccept = "";
	private String codeRtaDian = "";
	private String infoRtaDian = "";
	private boolean failProcess = false;
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

		logErrorQuery = "select * from estados_procesamiento where id_transaccion = " + "(select * from (select id "
				+ "	from transacciones " + "	where nombre_archivo_original = ? "
				+ "	order by fecha_creacion desc) where rownum = 1)" + "order by fecha_creacion";

		if (con == null) {
			JOptionPane.showMessageDialog(null, "No se pudo realizar la conexión", "Conexión nula",
					JOptionPane.ERROR_MESSAGE);
		}

		try (PreparedStatement docStatusPs = con.prepareStatement(docStatusQuery);
				PreparedStatement docXMLNamePs = con.prepareStatement(docNameXMLQuery);
				PreparedStatement docPDFNamePs = con.prepareStatement(docNamePDFQuery);
				PreparedStatement docRtaDianPs = con.prepareStatement(docNameRtaQuery)) {

			// String file = new File(pathFile);
			ArrayList<String> path = ReadExcel.getValueFieldPosition(file.getAbsolutePath(), 2);

			for (int i = 0; i < path.size(); i++) {
				File fileSend = new File(path.get(i));
				factNum = CarvajalUtils.getFactNumber(path.get(i));
				docStatusPs.setString(1, fileSend.getName());
				docXMLNamePs.setString(1, factNum);
				docPDFNamePs.setString(1, factNum);
				docRtaDianPs.setString(1, factNum);

				try (ResultSet docStatusRs = docStatusPs.executeQuery();
						ResultSet docXMLNameRs = docXMLNamePs.executeQuery();
						ResultSet docNamePDFRs = docPDFNamePs.executeQuery();
						ResultSet docRtaDianRs = docRtaDianPs.executeQuery()) {

					if (docStatusRs != null) {
						while (docStatusRs.next()) {

							status = docStatusRs.getString(4);
							processName = docStatusRs.getString(3);
							message = docStatusRs.getString(8);
						}
					}
					if (docNamePDFRs.next()) {
						namePDFFact = docNamePDFRs.getString(5);
					}

					if (docRtaDianRs.next()) {
						codeRtaDian = docRtaDianRs.getString(4);
						infoRtaDian = docRtaDianRs.getString(5);
					}

					if (docXMLNameRs != null) {
						if (docXMLNameRs.next()) {
							nameFileGovernment = docXMLNameRs.getString(13);
							cufe = docXMLNameRs.getString(14);
						}
					}
				}
			}
		}
	}

	public void testCase(String pathFileTest) throws SQLException, Exception {

		String factNum = "";
		String status = "";
		int statusOk = 0;
		int statusNOk = 0;

		File fileFhater = new File(pathFileTest);
		String directory = fileFhater.getParent();
		File log = Log.getLogFile(directory);

		try (FileWriter fw = new FileWriter(log.getAbsoluteFile(), true); BufferedWriter bw = new BufferedWriter(fw)) {

			ArrayList<String> testCase = ReadExcel.getValueFieldPosition(pathFileTest, 0);
			ArrayList<String> documents = ReadExcel.getValueFieldPosition(pathFileTest, 2);

			for (int i = 0; i < testCase.size(); i++) {
				File file = new File(documents.get(i));
				if ((testCase.get(i)).equalsIgnoreCase("1.2") || (testCase.get(i)).equalsIgnoreCase("1.11")) {
					factNum = CarvajalUtils.getFactNumber(file.getAbsolutePath());
					String nameDocument = ((file.getName()).split("\\.")[0]);
					if (nameFileGovernment.contains(nameDocument) && namePDFFact.contains(nameDocument)) {
						bw.write("Factura No: " + factNum + "PDF: " + namePDFFact + " Estado : OK \r\n");
						statusOk++;
					} else {
						bw.write("Factura No: " + factNum + " Estado : NOK \r\n");
						statusNOk++;
					}
				}
				generateProcessFail(file.getAbsolutePath(), log);
				bw.write(resultados);
				bw.write("------------------------------------------------------------------------ \r\n");
				fw.flush();
			}
			
			for (int i = 0; i < testCase.size(); i++) {
				File file = new File(documents.get(i));
				if ((testCase.get(i).equalsIgnoreCase("1.8"))) {
					generateLogError(file.getAbsolutePath(), log);
					if (logError != null) {
						if (logError.contains("[ERROR-VA3]")) {
							bw.write("Factura No: " + factNum + namePDFFact + " Estado : OK \r\n");
							bw.write(logError);
						} else {
							bw.write("Factura No: " + factNum + namePDFFact + " Estado : NOK \r\n"
									+ "No se genero el Log de errores correspondiente");
							bw.write(logError);
						}
					} else {
						bw.write("No se genero Log de errores");
					}}
				}
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
					}
				}

				if (status.equalsIgnoreCase("FAIL")) {
					resultados = ("Los siguientes procesos fallaron: \r\n" + "Proceso: " + processName + "\r\nEstado: "
							+ status + " Mensaje: " + message + "\r\n");
					failProcess = true;
				} else
					resultados = ("El procesamiento del documento no generó errores. \r\n");
			}
		}
	}

	private void generateLogError(String filePathTest, File log) throws SQLException, Exception {

		try (PreparedStatement logErrorPs = con.prepareStatement(logErrorQuery);
				FileWriter fw = new FileWriter(log.getAbsoluteFile(), true);
				BufferedWriter bw = new BufferedWriter(fw)) {

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