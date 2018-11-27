package com.carvajal.facturaclaro.ral;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.carvajal.facturaclaro.ral.dto.AuthorizationDTO;
import com.carvajal.facturaclaro.utils.PostgresConnector;

public class StatusPackageQueryRAL {

	private static String statusPackageQuery;
	private static String statusItemsPackageQuery;
	public static String status;
	public static String idPackage;
	public static ArrayList<String> itemStatus = new ArrayList<String>();
	public static ArrayList<String> nameDocument = new ArrayList<String>();

	public static void statusPackage(AuthorizationDTO aut) throws SQLException {

		statusPackageQuery = "select * from paquetes where id_transaccion = (select id  from transacciones where nombre_archivo_original = ?"
				+ "order by fecha_creacion desc limit 1)";
		statusItemsPackageQuery = "select * from paquetes_items where id_paquete = ?";

		try (PreparedStatement statusPackagePs = PostgresConnector.con.prepareStatement(statusPackageQuery)) {

			String namePackage = aut.getActivation().getPackagesName();
			statusPackagePs.setString(1, namePackage);

			try (ResultSet statusPackageRs = statusPackagePs.executeQuery();
					PreparedStatement statusItemsPackagePs = PostgresConnector.con
							.prepareStatement(statusItemsPackageQuery)) {
				if (statusPackageRs.next()) {
					status = statusPackageRs.getString(5);
					idPackage = statusPackageRs.getString(1);
				}
			}
		}
	}

	public static void statusPackageItems(AuthorizationDTO aut) throws SQLException {
		statusItemsPackageQuery = "select * from paquetes_items where id_paquete = ?";

		try (PreparedStatement statusItemsPackagePs = PostgresConnector.con.prepareStatement(statusItemsPackageQuery)) {
			statusItemsPackagePs.setString(1, idPackage);
			System.out.println("id del paquete " +idPackage);

			try (ResultSet statusItemsPackageRs = statusItemsPackagePs.executeQuery()) {
				if (statusItemsPackageRs.next()) {
					itemStatus.add(statusItemsPackageRs.getString(4));
					nameDocument.add(statusItemsPackageRs.getString(7));
				}

			}
		}
	}
}