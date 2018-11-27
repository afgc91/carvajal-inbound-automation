package com.carvajal.facturaclaro.ral;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.carvajal.facturaclaro.ral.dto.AuthorizationDTO;
import com.carvajal.facturaclaro.utils.PostgresConnector;

public class EventsPackageQuery {

	private static String eventsPackageQuery;
	public static ArrayList<String> messageEvent = new ArrayList<String>();

	public static void eventsPackage(AuthorizationDTO aut) throws SQLException {

		//eventsPackageQuery = "select * from envios_ws_externo where id_transaccion = (select id from transacciones where nombre_archivo_original= ? order by fecha_creacion limit 1) order by fecha_error desc limit 10";
		eventsPackageQuery = "select * from envios_ws_externo where id_emisor= ? order by fecha_error desc limit 10"; 
		
		try (PreparedStatement eventsPackagePs = PostgresConnector.con.prepareStatement(eventsPackageQuery)) {
			//String namePackage = aut.getActivation().getPackagesName();
			//eventsPackagePs.setString(1, namePackage);
			String id_emisor= aut.getActivation().getCompanyID();
			eventsPackagePs.setString(1, id_emisor);

			try (ResultSet eventsPackageRs = eventsPackagePs.executeQuery()) {

				if (eventsPackageRs.next()) {
					messageEvent.add(eventsPackageRs.getString(6));
				}
			}
		}
	}
}