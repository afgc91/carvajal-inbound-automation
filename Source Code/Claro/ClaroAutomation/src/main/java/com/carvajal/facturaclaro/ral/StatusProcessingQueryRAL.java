package com.carvajal.facturaclaro.ral;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.carvajal.facturaclaro.ral.dto.AuthorizationDTO;
import com.carvajal.facturaclaro.utils.PostgresConnector;

public class StatusProcessingQueryRAL {

	private static String statusProcessingQuery;
	public static String statusProcessingPackage;

	public static void statusProcessingQuery(AuthorizationDTO aut) throws SQLException {

		statusProcessingQuery = "select * from estados_procesamiento where id_transaccion = '839cb901a7e3493a8e6aa2661aa50ffc'"
				//+ "(select id  from transacciones where nombre_archivo_original = ? "
				+ "and tipo_mensaje = 'DOCUMENT_PROCESSED' and nombre_proceso='PACKAGES' and estado_proceso = ? order by fecha_creacion desc limit 1 ";
		
	/**	statusProcessingQuery = "select * from estados_procesamiento where id_transaccion = "
				+ "(select id  from transacciones where nombre_archivo_original = ? "
				+ "order by fecha_creacion desc limit 1) and tipo_mensaje = 'DOCUMENT_PROCESSED'";**/

		try (PreparedStatement statusProccesingQueryPs = PostgresConnector.con
				.prepareStatement(statusProcessingQuery)) {

			String namePackage = aut.getActivation().getPackagesName();
			statusProccesingQueryPs.setString(1, "OK");

			try (ResultSet statusProccesingQueryRs = statusProccesingQueryPs.executeQuery()) {
				System.out.println("verla " + ((statusProccesingQueryRs.next())));
				if (statusProccesingQueryRs.next() == true) {
						statusProcessingPackage = statusProccesingQueryRs.getString(4);
					} else {
						statusProcessingPackage = "";
				}
			}
		}
	}
}