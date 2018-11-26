package com.carvajal.facturaclaro.bc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import com.carvajal.facturaclaro.ral.EventsPackageQuery;
import com.carvajal.facturaclaro.ral.MD5GeneratorRAL;
import com.carvajal.facturaclaro.ral.StatusPackageQueryRAL;
import com.carvajal.facturaclaro.ral.StatusProcessingQueryRAL;
import com.carvajal.facturaclaro.ral.dto.ActivationDTO;
import com.carvajal.facturaclaro.ral.dto.AuthorizationDTO;
import com.carvajal.facturaclaro.ral.dto.LoginDTO;
import com.carvajal.facturaclaro.ral.dto.NotificationDTO;
import com.carvajal.facturaclaro.ral.dto.ResponseDTO;
import com.carvajal.facturaclaro.sl.ActivationSL;
import com.carvajal.facturaclaro.sl.LoginSL;
import com.carvajal.facturaclaro.sl.NotificationSL;
import com.carvajal.facturaclaro.utils.FilesSender;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

/**
 * <pre>
 * Fecha      Autor     
 * 21-11-2018 Diana Marcela Valencia
 * </pre>
 * 
 * Logica de negocio del envío de facturas para Claro - Carvajal
 * 
 * @author Diana Marcela Valencia
 * @version 1.0
 * @category Business Component
 **/

public class AuthorizationBC {

	public static ResponseDTO response = new ResponseDTO();

	/**
	 * <pre>
	 * Fecha      Autor     
	 * 21-11-2018 Diana Marcela Valencia
	 * </pre>
	 * 
	 * Se realiza la autorización de envío a procesamiento de una factura, NC o ND
	 * enviada al CENFINANCIERO
	 * 
	 * @author Diana Marcela Valencia
	 * @param aut Objeto AuthorizationDTO
	 * @return respuesta Objeto Login
	 * @throws IOException
	 * @throws SQLException
	 * @throws SftpException
	 * @throws JSchException
	 * 
	 **/

	public static boolean isOkRetention(AuthorizationDTO aut)
			throws IOException, SQLException, JSchException, SftpException {
		notificacionEnvioPaqueteRetencion(aut);
		if (response.getCodError().equalsIgnoreCase("200") && response.getCodErrorItem().equalsIgnoreCase("200")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isOkSendPackage(AuthorizationDTO aut)
			throws IOException, JSchException, SftpException, SQLException {
		notificacionEnvioSinRetencion(aut);
		if (response.getCodError().equalsIgnoreCase("200") && response.getCodErrorItem().equalsIgnoreCase("200")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isOkCancelledPackage(AuthorizationDTO aut)
			throws JSchException, SftpException, IOException, SQLException {
		cancelarEnvioPaquete(aut);
		if (response.getCodError().equalsIgnoreCase("200") && response.getCodErrorItem().equalsIgnoreCase("200")) {
			return true;
		} else {
			return false;
		}
	}

	public static ResponseDTO notificacionEnvioPaqueteRetencion(AuthorizationDTO aut)
			throws IOException, SQLException, JSchException, SftpException {
		FilesSender fileSender = new FilesSender();
		fileSender.sendFiles(aut);
		realizarNotificacion(aut);
		ArrayList<String> itemsStatus = new ArrayList<String>();

		StatusProcessingQueryRAL.statusProcessingQuery(aut);
		String statusProcessingPackage = StatusProcessingQueryRAL.statusProcessingPackage;

		String itemStatus = "";

		if (statusProcessingPackage.equalsIgnoreCase("OK")) {
			StatusPackageQueryRAL.statusPackage(aut);
			String status = StatusPackageQueryRAL.status;
			if (status.equalsIgnoreCase("STOPPED")) {
				response.setCodError("200");
				response.setMessage("El estado del paquete es: " + status);
				StatusPackageQueryRAL.statusPackageItems(aut);
				itemsStatus = StatusPackageQueryRAL.itemStatus;

				for (int i = 0; i < itemsStatus.size(); i++) {
					if (itemsStatus.get(i).equalsIgnoreCase("STOPPED")) {
						response.setCodErrorItem("200");
						response.setMessageItem(itemStatus);
					} else {
						response.setCodErrorItem("404");
						response.setMessageItem("El estado del paquete es: " + itemStatus);
					}
				}
			} else {
				response.setCodError("404");
				response.setMessage("El estado del paquete es: " + status);
			}
		} else {
			response.setCodError("404");
			response.setMessage("El paquete enviado sin retención no ha sido procesado");
		}
		return response;
	}

	public static ResponseDTO notificacionEnvioSinRetencion(AuthorizationDTO aut)
			throws IOException, JSchException, SftpException, SQLException {
		FilesSender fileSender = new FilesSender();
		fileSender.sendFiles(aut);
		realizarNotificacion(aut);
		ArrayList<String> itemsStatus = new ArrayList<String>();

		StatusProcessingQueryRAL.statusProcessingQuery(aut);
		String statusProcessingPackage = StatusProcessingQueryRAL.statusProcessingPackage;

		String itemStatus = "";

		if (statusProcessingPackage.equalsIgnoreCase("OK")) {
			StatusPackageQueryRAL.statusPackage(aut);
			String status = StatusPackageQueryRAL.status;
			if (status.equalsIgnoreCase("COMPLETED")) {
				response.setCodError("200");
				response.setMessage("El estado del paquete es: " + status);
				StatusPackageQueryRAL.statusPackageItems(aut);
				itemsStatus = StatusPackageQueryRAL.itemStatus;

				for (int i = 0; i < itemsStatus.size(); i++) {
					if (itemsStatus.get(i).equalsIgnoreCase("COMPLETED")) {
						response.setCodErrorItem("200");
						response.setMessageItem(itemStatus);
					} else {
						response.setCodErrorItem("404");
						response.setMessageItem("El estado del paquete es: " + itemStatus);
					}
				}
			} else {
				response.setCodError("404");
				response.setMessage("El estado del paquete es: " + status);
			}
		} else {
			response.setCodError("404");
			response.setMessage("El paquete enviado no ha sido procesado");
		}
		return response;
	}

	private static ResponseDTO cancelarEnvioPaquete(AuthorizationDTO aut)
			throws JSchException, SftpException, IOException, SQLException {
		FilesSender fileSender = new FilesSender();
		fileSender.sendFiles(aut);
		realizarNotificacion(aut);
		activacionDeEnvio(aut);

		ArrayList<String> itemsStatus = new ArrayList<String>();
		StatusProcessingQueryRAL.statusProcessingQuery(aut);
		String statusProcessingPackage = StatusProcessingQueryRAL.statusProcessingPackage;

		String itemStatus = "";

		if (statusProcessingPackage.equalsIgnoreCase("OK")) {
			StatusPackageQueryRAL.statusPackage(aut);
			String status = StatusPackageQueryRAL.status;
			if (status.equalsIgnoreCase("CANCELLED")) {
				response.setCodError("200");
				response.setMessage("El estado del paquete es: " + status);
				StatusPackageQueryRAL.statusPackageItems(aut);
				itemsStatus = StatusPackageQueryRAL.itemStatus;

				for (int i = 0; i < itemsStatus.size(); i++) {
					if (itemsStatus.get(i).equalsIgnoreCase("CANCELLED")) {
						response.setCodErrorItem("200");
						response.setMessageItem(itemStatus);
					} else {
						response.setCodErrorItem("404");
						response.setMessageItem("El estado del paquete es: " + itemStatus);
					}
				}
			} else {
				response.setCodError("404");
				response.setMessage("El estado del paquete es: " + status);
			}
		} else {
			response.setCodError("404");
			response.setMessage("El paquete enviado para el flujo de retención cancelación no ha sido procesado");
		}
		return response;
	}

	private static LoginDTO obtenerToken(AuthorizationDTO aut) throws IOException {

		Random aleatorio = new Random(System.currentTimeMillis());
		int intAletorio = aleatorio.nextInt(3000);
		aleatorio.setSeed(System.currentTimeMillis());

		String bodyWS = "{\"user\":\"" + aut.getLogin().getUser() + "\",\"password\":\"" + aut.getLogin().getPassword()
				+ "\"}";
		String length = String.valueOf(bodyWS.length());
		String md5 = MD5GeneratorRAL.ContentMD5Base64(bodyWS, "MD5").trim();
		String date = returnDate();
		String transationId = "UUID-" + intAletorio;

		System.out.println("La fecha " + date + " " + length + " " + transationId + " " + md5);

		return LoginSL.getLogin(bodyWS, md5, date, transationId, length);

	}

	public static NotificationDTO realizarNotificacion(AuthorizationDTO aut) throws IOException {

		Random aleatorio = new Random(System.currentTimeMillis());
		int intAletorio = aleatorio.nextInt(3000);
		aleatorio.setSeed(System.currentTimeMillis());

		String token = obtenerToken(aut).getToken();
		System.out.println("token: " + token);
		String bodyWS = "{\"companyId\":\"" + aut.getNotificacion().getCompanyId() + "\",\"account\":\""
				+ aut.getNotificacion().getAccount() + "\",\"batchId\":\"" + aut.getNotificacion().getBatchId()
				+ "\",\"packagesPaths\":[\"" + aut.getNotificacion().getPackagesPaths() + "\"]}";
		String length = String.valueOf(bodyWS);
		String md5 = MD5GeneratorRAL.ContentMD5Base64(bodyWS, "MD5").trim();
		String transationId = "UUID-" + intAletorio;
		String date = returnDate();

		return NotificationSL.getNotification(bodyWS, token, md5, date, transationId, length);
	}

	private static ActivationDTO activacionDeEnvio(AuthorizationDTO aut) throws IOException {

		Random aleatorio = new Random(System.currentTimeMillis());
		int intAletorio = aleatorio.nextInt(3000);
		aleatorio.setSeed(System.currentTimeMillis());

		String token = obtenerToken(aut).getToken();
		String bodyWS = "{\"package\":\"" + aut.getActivation().getPackagesName() + "\",\"companyId\":\""
				+ aut.getActivation().getCompanyID() + "\",\"action\":\"" + aut.getActivation().getAction()
				+ "\",\"batchId\":\"" + aut.getActivation().getBatchID() + "\"}";
		String length = String.valueOf(bodyWS);
		String md5 = MD5GeneratorRAL.ContentMD5Base64(bodyWS, "MD5");
		String date = returnDate();
		String transationId = "UUID-" + intAletorio;

		return ActivationSL.getActivation(bodyWS, token, md5, date, transationId, length);
	}

	private static String returnDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
		calendar.add(Calendar.HOUR, 5);
		Date systemDate = calendar.getTime();
		String date = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH).format(systemDate);
		return date;

	}

}
