package com.carvajal.facturaclaro.ral;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.carvajal.facturaclaro.ral.dto.AuthorizationDTO;
import com.carvajal.facturaclaro.utils.PostgresConnector;

public class RenameInvoiceFile {

	private static String docNameXMLQuery;
	public static String docNamePDFQuery;
	private static String transationIdItemsQuery;
	public static ArrayList<String> transationIdItemList = new ArrayList<String>();
	private static String processingIdItemsQuery;
	public static String proccesingIdItem;
	private static String xmlDocNameQuery;
	public static String xmlDocName;
	public static String docNameXML;
	public static String docNamePDF;

	public static void docNameXML(AuthorizationDTO aut) throws SQLException {

		docNameXMLQuery = "select * from datos_documentos " + "where id_documento = (select id from documentos "
				+ "where numero_documento = ?)";

		try (PreparedStatement docNameXMLQueryPs = PostgresConnector.con.prepareStatement(docNameXMLQuery)) {
			String[] docNameArray = aut.getActivation().getPackagesName().split("\\.");
			String numero_documento = docNameArray[0];

			docNameXMLQueryPs.setString(1, numero_documento);
			try (ResultSet docNameXMLQueryRs = docNameXMLQueryPs.executeQuery()) {

				if (docNameXMLQueryRs.next()) {
					docNameXML = (docNameXMLQueryRs.getString(13));
				}
			}
		}
	}

	public static void transationIdItems(AuthorizationDTO aut) throws SQLException {
		// Lista de items con el id transaccion de cada uno de los items del paquete enviado
		transationIdItemsQuery = "select * FROM paquetes_items where id_paquete = (select id from paquetes where nombre_archivo_sip= ? order by fecha_creacion limit 1)";
		try (PreparedStatement transationIdItemsQueryPs = PostgresConnector.con
				.prepareStatement(transationIdItemsQuery)) {

			String namePackage = aut.getActivation().getPackagesName();
			transationIdItemsQueryPs.setString(1, namePackage);

			try (ResultSet transationIdItemsQueryRs = transationIdItemsQueryPs.executeQuery()) {

				if (transationIdItemsQueryRs.next()) {
					transationIdItemList.add(transationIdItemsQueryRs.getString(3));
				}
			}
		}
	}
	
	//Obtener id estados procesamiento de cada item
	public static void processingIdItem(String id_transation) throws SQLException {
		processingIdItemsQuery = "select * from estados_procesamiento where id_transaccion = ? and nombre_proceso='SIGN'";
		try (PreparedStatement processingIdItemsQueryPs = PostgresConnector.con
				.prepareStatement(processingIdItemsQuery)) {

			processingIdItemsQueryPs.setString(1, id_transation);

			try (ResultSet processingIdItemsQueryRs = processingIdItemsQueryPs.executeQuery()) {

				if (processingIdItemsQueryRs.next()) {
					proccesingIdItem = processingIdItemsQueryRs.getString(1);
				}
			}
		}
	}
	
	//Consulta el nombre del documento firmado para cada uno de los items 
	
	public static void xmlDocName (String id_processing) throws SQLException {
		
		xmlDocNameQuery = "select * from archivos_procesamiento where id_estado_procesamiento = ?";
		try (PreparedStatement xmlDocNameQueryPs = PostgresConnector.con
				.prepareStatement(xmlDocNameQuery)) {

			xmlDocNameQueryPs.setString(1, id_processing);

			try (ResultSet xmlDocNameQueryRs = xmlDocNameQueryPs.executeQuery()) {

				if (xmlDocNameQueryRs.next()) {
					xmlDocName = xmlDocNameQueryRs.getString(5);
				}
			}
		}
	}
}