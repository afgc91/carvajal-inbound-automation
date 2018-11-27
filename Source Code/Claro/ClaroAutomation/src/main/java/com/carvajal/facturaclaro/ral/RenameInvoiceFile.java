package com.carvajal.facturaclaro.ral;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.carvajal.facturaclaro.ral.dto.AuthorizationDTO;
import com.carvajal.facturaclaro.utils.PostgresConnector;

public class RenameInvoiceFile {
	
	private static String docNameXMLQuery;
	//private static String docNamePDFQuery; 
	public static String docNameXML; 
	public static String docNamePDF; 
	
	public static void docNameXML(AuthorizationDTO aut) throws SQLException {
		
		docNameXMLQuery= "select * from datos_documentos " + "where id_documento = (select id from documentos "
				+ "where numero_documento = ?)";
		
		try (PreparedStatement docNameXMLQueryPs = PostgresConnector.con.prepareStatement(docNameXMLQuery)) {
			String[] docNameArray= aut.getActivation().getPackagesName().split("\\.");
			String numero_documento = docNameArray[0];
			
			docNameXMLQueryPs.setString(1, numero_documento);
			try (ResultSet docNameXMLQueryRs = docNameXMLQueryPs.executeQuery()) {

				if (docNameXMLQueryRs.next()) {
					docNameXML = (docNameXMLQueryRs.getString(13));
				}
		}
	}}

	/**public static void docNamePDF(AuthorizationDTO aut) {
		docNamePDFQuery= 	
	}**/

} 