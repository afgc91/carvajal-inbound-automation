package com.carvajal.facturaclaro.test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

import com.carvajal.facturaclaro.bc.AuthorizationBC;
import com.carvajal.facturaclaro.ral.AuthorizationRAL;
import com.carvajal.facturaclaro.ral.dto.AuthorizationDTO;
import com.carvajal.facturaclaro.utils.PATH;
import com.carvajal.facturaclaro.utils.PostgresConnector;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.carvajal.facturaclaro.utils.FileLogger;

/**
 * <pre>
 * Fecha      Autor     
 * 21-11-2018 Diana Marcela Valencia
 * </pre>
 * 
 * Se realiza proceso de emisión de factura claro cuando el cliente tiene
 * parametrizado la cuenta de envío con retención antes de firma. El paquete o
 * items enviados deberan quedar en estado stopped
 * 
 * @author Diana Marcela Valencia
 * @version 1.0
 * @category Test
 **/

public class CP_1_Emision_Factura_Retencion_Test {

	public static int file = 1;
	private static File log = null;
	private ArrayList<AuthorizationDTO> dataPool = new ArrayList<AuthorizationDTO>();

	// Ingresa los objetos con la configuración para realizar cada una de las
	// peticiones.
	public void setup() throws ClassNotFoundException, SQLException, IOException {
		PostgresConnector.getConnetion();
		this.dataPool = AuthorizationRAL.getAutorization(PATH.DATA_POOL);
	}

	/**
	 * <pre>
	 * Fecha      Autor     
	 * 21-11-2018 Diana Marcela Valencia
	 * </pre>
	 * 
	 * Ejecuta el caso de prueba de realizar una reserva.
	 * 
	 * @author Dilan Steven Mejia
	 * @throws IOException
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws SftpException
	 * @throws JSchException
	 * 
	 **/
	@Test
	public void emisionFacturaRetencion()
			throws IOException, ClassNotFoundException, SQLException, JSchException, SftpException {
		setup();
		
		boolean response = false;

		for (AuthorizationDTO authorization : dataPool) {
			/* Enviar cada objeto que contiene el escenario */
			

			switch (authorization.getTestCase()) {
			case "1.1":
				System.out.println("entro al case 1.1: " + authorization.getTestCase());
				response = AuthorizationBC.isOkRetention(authorization);
				this.wait(60);
				if (response == false) {
					FileLogger.log(authorization);
				}
				AuthorizationBC.validacionEventosRetencion(authorization);
				if (AuthorizationBC.validacionEventosRetencion(authorization).getCodError() != "200") {
					response = false; 
					FileLogger.log(authorization); 
				} 
				break;
			case "1.3":
				response = AuthorizationBC.isOkSendPackage(authorization);
				if (response == false) {
					FileLogger.log(authorization);
				}
				break;
			case "1.5":
				response = AuthorizationBC.isOkCancelledPackage(authorization);
				if(response == false) {
					FileLogger.log(authorization);
				}
			default:
				break;
			}
			file += file;
		}
		if (response == false) {
			Assert.assertEquals(response, true, "Caso de prueba Fallido, revisar el Log de Errores");
		}
	}

	// Se cierran las sesiones del driver.
	@AfterSuite
	public void stopAllDrivers() {

	}

	private void wait(int segundos) {
		try {
			Thread.sleep(segundos*1000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

}