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

	private static int file = 0;
	public static int fileExcel = 1; 
	public static String testCaseStatusMessage;
	public static boolean testCaseStatus;
	private ArrayList<AuthorizationDTO> dataPool = new ArrayList<AuthorizationDTO>();
	private static boolean response;
	private static boolean eventResponse;
	private static boolean cufeResponse;
	private static boolean fileRenameResponse;
     
	// Ingresa los objetos con la configuración para realizar cada una de las
	// peticiones.
	public void setup() {
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
	public void emisionFacturaRetencion() {
		setup();

		AuthorizationBC authoBC = new AuthorizationBC();

		for (AuthorizationDTO authorization : dataPool) {
			/* Enviar cada objeto que contiene el escenario */

			switch (authorization.getTestCase()) {
			case "1.1":
				response = authoBC.isOkRetention(authorization);
				FileLogger.log(authorization);
				eventResponse = authoBC.validacionEventosRetencion(authorization);
				FileLogger.log(authorization);
				if (response == true && eventResponse == true) {
					testCaseStatusMessage = "Caso de Prueba Exitoso: ";
				} else {
					testCaseStatusMessage = "Caso de Prueba Fallido: ";
				}
				break;
			case "1.2":
				response = authoBC.isOkRetention(authorization);
				FileLogger.log(authorization);
				eventResponse = authoBC.validacionEventosRetencion(authorization);
				FileLogger.log(authorization);
				if (response == true && eventResponse == true) {
					testCaseStatusMessage = "Caso de Prueba Exitoso: ";
				} else {
					testCaseStatusMessage = "Caso de Prueba Fallido: ";
				}
				break;
			case "1.3":
				response = authoBC.isOkSendPackage(authorization);
				FileLogger.log(authorization);
				eventResponse = authoBC.validacionEventosSinRetencion(authorization);
				FileLogger.log(authorization);
				this.waitQuery(10);
				cufeResponse = authoBC.alertaCufe(authorization);
				FileLogger.log(authorization);
				if (response == true && eventResponse == true) {
					testCaseStatusMessage = "Caso de Prueba Exitoso: ";
				} else {
					testCaseStatusMessage = "Caso de Prueba Fallido: ";
				}
				break;
			case "1.4":
				response = authoBC.isOkSendPackage(authorization);
				FileLogger.log(authorization);
				eventResponse = authoBC.validacionEventosSinRetencion(authorization);
				FileLogger.log(authorization);
				this.waitQuery(10);
				cufeResponse = authoBC.alertaCufe(authorization);
				FileLogger.log(authorization);
				this.waitQuery(10);
				fileRenameResponse = authoBC.renombramientoArchivos(authorization);
				FileLogger.log(authorization);
				if (response == true && eventResponse == true && cufeResponse && fileRenameResponse) {
					testCaseStatusMessage = "Caso de Prueba Exitoso: ";
				} else {
					testCaseStatusMessage = "Caso de Prueba Fallido: ";
				}
				break;
			case "1.5":
				response = authoBC.isOkCancelledPackage(authorization);
				FileLogger.log(authorization);
				eventResponse = authoBC.validacionEventosCancelacionPaquete(authorization);
				FileLogger.log(authorization);
				if (response == true && eventResponse) {
					testCaseStatusMessage = "Caso de Prueba Exitoso: ";
				} else {
					testCaseStatusMessage = "Caso de Prueba Fallido: ";
				}
			case "1.6":
				response = authoBC.isOkAutomaticCancelledPackage(authorization);
				FileLogger.log(authorization);
				eventResponse = authoBC.validacionEventosCancelacionPaquete(authorization);
				FileLogger.log(authorization);
				if (response == true && eventResponse) {
					testCaseStatusMessage = "Caso de Prueba Exitoso: ";
				} else {
					testCaseStatusMessage = "Caso de Prueba Fallido: ";
				}
				break;
			case "1.7":
				response = authoBC.isOkAuthorizationPackage(authorization);
				FileLogger.log(authorization);
				this.waitQuery(10);
				fileRenameResponse = authoBC.renombramientoArchivos(authorization);
				FileLogger.log(authorization);
				if (response == true && fileRenameResponse == true) {
					testCaseStatusMessage = "Caso de Prueba Exitoso: ";
				} else {
					testCaseStatusMessage = "Caso de Prueba Fallido: ";
				}
				break;
			case "1.8":
				response = authoBC.isOkAuthorizationPackage(authorization);
				FileLogger.log(authorization);
				this.waitQuery(10);
				fileRenameResponse = authoBC.renombramientoArchivos(authorization);
				FileLogger.log(authorization);
				this.waitQuery(10);
				cufeResponse = authoBC.alertaCufe(authorization);
				FileLogger.log(authorization);
				if (response == true && fileRenameResponse == true && cufeResponse == true) {
					testCaseStatusMessage = "Caso de Prueba Exitoso: ";
				} else {
					testCaseStatusMessage = "Caso de Prueba Fallido: ";
				}
				break;
			case "1.9":
				response = authoBC.isOkFailPackage(authorization);
				FileLogger.log(authorization);
				eventResponse = authoBC.validacionEventosSinRetencionFallido(authorization);
				FileLogger.log(authorization);
				this.waitQuery(10);
				cufeResponse = authoBC.alertaCufe(authorization);
				FileLogger.log(authorization);
				if (response == true && eventResponse == true && cufeResponse == true) {
					testCaseStatusMessage = "Caso de Prueba Exitoso: ";
				} else {
					testCaseStatusMessage = "Caso de Prueba Fallido: ";
				}
				break;
			default:
				break;
			}
			file++;
			fileExcel++; 
		}
		if (response == true && eventResponse == true && cufeResponse == true) {
			testCaseStatus = true;
		} else {
			testCaseStatus = false;
		}
		Assert.assertEquals(response, true, "Caso de prueba Fallido, revisar el Log de Errores");
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