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
import com.carvajal.facturaclaro.utils.WSPropertiesReader;
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
		this.dataPool = AuthorizationRAL.getAutorization(PATH.DATA_POOL);
		WSPropertiesReader.getWSPath(dataPool.get(file).getPathWS());
		PostgresConnector.getConnetion(dataPool.get(file).getConfiDB());
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
	 * @throws InterruptedException
	 * 
	 **/
	@Test
	public void emisionFacturaRetencion() throws IOException, ClassNotFoundException, SQLException, JSchException,
			SftpException, InterruptedException {
		setup();

		boolean response = false;
		AuthorizationBC authoBD = new AuthorizationBC();

		for (AuthorizationDTO authorization : dataPool) {
			/* Enviar cada objeto que contiene el escenario */

			switch (authorization.getTestCase()) {
			case "1.1":
				System.out.println("entro al case 1.1: " + authorization.getTestCase());
				response = authoBD.isOkRetention(authorization);
				if (response == false) {
					FileLogger.log(authorization);
				}
				response = authoBD.validacionEventosRetencion(authorization);
				if (response == false) {
					FileLogger.log(authorization);
				}
				break;
			case "1.2":
				System.out.println("entro al case 1.1: " + authorization.getTestCase());
				response = authoBD.isOkRetention(authorization);
				if (response == false) {
					FileLogger.log(authorization);
				}
				response = authoBD.validacionEventosRetencion(authorization);
				if (response == false) {
					FileLogger.log(authorization);
				}
				break;
			case "1.3":
				response = authoBD.isOkSendPackage(authorization);
				if (response == false) {
					FileLogger.log(authorization);
				}
				response = authoBD.validacionEventosSinRetencion(authorization);
				if (response == false) {
					FileLogger.log(authorization);
				}
				response = authoBD.alertaCufe(authorization);
				if (response == false) {
					FileLogger.log(authorization);
				}
				break;
			case "1.4":
				response = authoBD.isOkSendPackage(authorization);
				if (response == false) {
					FileLogger.log(authorization);
				}
				response = authoBD.validacionEventosSinRetencion(authorization);
				if (response == false) {
					FileLogger.log(authorization);
				}
				this.waitQuery(10);
				response = authoBD.alertaCufe(authorization);
				if (response == false) {
					FileLogger.log(authorization);
				}
				this.waitQuery(10);
				response = authoBD.renombramientoArchivos(authorization);
				if (response == false) {
					FileLogger.log(authorization);
				}
				break;
			case "1.5":
				response = authoBD.isOkCancelledPackage(authorization);
				if (response == false) {
					FileLogger.log(authorization);
				}
				response = authoBD.validacionEventosCancelacionPaquete(authorization);
				if (response == false) {
					FileLogger.log(authorization);
				}
			case "1.6":
				response = authoBD.isOkCancelledPackage(authorization);
				if (response == false) {
					FileLogger.log(authorization);
				}
				response = authoBD.validacionEventosCancelacionPaquete(authorization);
				if (response == false) {
					FileLogger.log(authorization);
				}
				break;
			case "1.7": 
				response = authoBD.isOkAuthorizationPackage(authorization); 
				if(response == false) {
					FileLogger.log(authorization);
				}
				this.waitQuery(10);
				response = authoBD.renombramientoArchivos(authorization); 
				if(response == false) {
					FileLogger.log(authorization);
				}
			case "1.8": 
				response = authoBD.isOkAuthorizationPackage(authorization); 
				if(response == false) {
					FileLogger.log(authorization);
				}
				this.waitQuery(10);
				response = authoBD.renombramientoArchivos(authorization); 
				if(response == false) {
					FileLogger.log(authorization);
				}
				this.waitQuery(10);
				response = authoBD.alertaCufe(authorization); 
				if(response == true) {
					FileLogger.log(authorization);
				}
			case "1.9": 
				response = authoBD.isOkFailPackage(authorization); 
				if(response == false) {
					FileLogger.log(authorization);
				}
				response = authoBD.validacionEventosSinRetencionFallido(authorization);
				if(response == false) {
					FileLogger.log(authorization);
				}
				this.waitQuery(10);
				response = authoBD.alertaCufe(authorization); 
				if(response == false) {
					FileLogger.log(authorization);
				}
			break;
			default:
				break;
			}
			file += file;
		}
		if (response == false) {
			Assert.assertEquals(response, true, "Caso de prueba Fallido, revisar el Log de Errores");
		}
	}
	
	private void waitQuery(int segundos) {
		try {
			Thread.sleep(segundos * 1000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	// Se cierran las sesiones del driver.
	@AfterSuite
	public void stopAllDrivers() {

	}
}