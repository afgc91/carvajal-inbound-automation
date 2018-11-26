package com.carvajal.facturaclaro.ral;

import java.awt.List;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.carvajal.facturaclaro.utils.PostgresConnector;


public class EventsPackageQuery {
	
	private String eventsPackageQuery; 
	public static ArrayList<String> messageEvent = new ArrayList<String>();
	
	public void eventsPackage() throws SQLException {
		
		eventsPackageQuery = "select * from envios_ws_externo order by fecha_error desc limit 10";
		
		try (PreparedStatement eventsPackagePs = PostgresConnector.con.prepareStatement(eventsPackageQuery);
			 ResultSet eventsPackageRs = eventsPackagePs.executeQuery()) {
			while (eventsPackageRs.next()) {
				messageEvent.add(eventsPackageRs.getString(6)); 
			}
		}
	}
}
