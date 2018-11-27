package com.carvajal.facturaclaro.ral;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.carvajal.facturaclaro.ral.dto.ActivationDTO;
import com.carvajal.facturaclaro.ral.dto.AuthorizationDTO;
import com.carvajal.facturaclaro.ral.dto.LoginDTO;
import com.carvajal.facturaclaro.ral.dto.NotificationDTO;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class AuthorizationRAL {
	

	public static ArrayList<AuthorizationDTO> getAutorization(String dataPool) {

		ArrayList<AuthorizationDTO> listAutorization = new ArrayList<AuthorizationDTO>();

		try {
			Workbook excelDataPool = Workbook.getWorkbook(new File(dataPool));
			Sheet hojaExcelDataPool = excelDataPool.getSheet(0);

			for (int fila = 1; fila < hojaExcelDataPool.getRows(); fila++) {
				AuthorizationDTO authorization = new AuthorizationDTO(); 
				LoginDTO login = new LoginDTO(); 
				NotificationDTO notification = new NotificationDTO(); 
				ActivationDTO activation = new ActivationDTO(); 
				
				login.setUser(validarDato(hojaExcelDataPool, "user", fila));
				login.setPassword(validarDato(hojaExcelDataPool, "password", fila));
				notification.setCompanyId(validarDato(hojaExcelDataPool, "companyId", fila));
				notification.setAccount(validarDato(hojaExcelDataPool, "account", fila));
				notification.setBatchId(validarDato(hojaExcelDataPool, "batchId", fila));
				notification.setPackagesPaths(validarDato(hojaExcelDataPool, "packagesPaths", fila));
				activation.setPackagesName(validarDato(hojaExcelDataPool, "package", fila));
				activation.setCompanyID(validarDato(hojaExcelDataPool, "companyId", fila));
				activation.setAction(validarDato(hojaExcelDataPool, "action", fila));
				activation.setBatchID(validarDato(hojaExcelDataPool, "batchId", fila));
				
				authorization.setLogin(login);
				authorization.setNotificacion(notification);
				authorization.setActivation(activation);
				authorization.setTestCase(validarDato(hojaExcelDataPool, "casoPrueba", fila));
				authorization.setPathSftp(validarDato(hojaExcelDataPool, "RutaSftp", fila));
				authorization.setPathFile(validarDato(hojaExcelDataPool, "RutaArchivo", fila));
				authorization.setConfiDB(validarDato(hojaExcelDataPool, "confiBD", fila));
				authorization.setConfiSftp(validarDato(hojaExcelDataPool, "confiSftp", fila));
				authorization.setPathWS(validarDato(hojaExcelDataPool, "RutaWS", fila));
	
				listAutorization.add(authorization);
			}

		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return listAutorization;
	}

	/**
	 * <pre>
	 * Fecha      Autor     
	 * 17-11-2018 Dilan Steven Mejia	
	 * 
	 * </pre>
	 * 
	 * Valida que si hay un elemento en la celda, sino retorna vacio.
	 * 
	 * @author Dilan Steven Mejia
	 * @return Elemento encontrado en la celda
	 * 
	 * **/

	public static String validarDato(Sheet hojaExcelDataPool,String campo,int fila){
		try {
			return hojaExcelDataPool.getCell(hojaExcelDataPool.findCell(campo).getColumn(),fila).getContents();

		} catch (Exception e) {

			return "";
		}	
}
}
