package com.carvajal.facturaclaro.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import com.carvajal.facturaclaro.bc.AuthorizationBC;
import com.carvajal.facturaclaro.ral.dto.AuthorizationDTO;
import com.carvajal.facturaclaro.test.CP_1_Emision_Factura_Retencion_Test;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class FileLogger {

	public ArrayList<String> logList;
	public static String logFilePath;

	public FileLogger() {
		this.logList = new ArrayList<>();
	}

	private File getLogFile() throws IOException {
		File directoryOut = new File(PATH.DATA_POOL);
		String directory = directoryOut.getParent();
		String dirPath = directory + "\\Logs";
		File dir = new File(dirPath);
		dir.mkdir();
		String logFilePath = "";
		logFilePath = dirPath + "\\log1.txt";
		File log = new File(logFilePath);
		log.canWrite();
		return log;
	}

	public static void log(AuthorizationDTO aut) throws IOException, SQLException, JSchException, SftpException {
		
		String messagePackage = AuthorizationBC.response.getMessage(); 
		String messagePackageItems = AuthorizationBC.response.getMessageItem();
		
			FileLogger fileLogger = new FileLogger();
			File log = fileLogger.getLogFile();
			try (FileWriter fw = new FileWriter(log.getAbsolutePath(), true);
					BufferedWriter bw = new BufferedWriter(fw)) {
				int file = CP_1_Emision_Factura_Retencion_Test.file;
				bw.write("Caso de Prueba Fallido: " + aut.getTestCase() +" Fila en el excel: "+ file + "\r\n"+ "\r\n" + "Error:" + messagePackage +"\n"+messagePackageItems
								+ "\r\nEmpresa de Prueba: " + aut.getNotificacion().getCompanyId() + "\r\nCuenta Empresa:"
								+ aut.getNotificacion().getAccount() + "\r\nRuta del Paquete: " + aut.getNotificacion().getPackagesPaths()+"\r\n");
				bw.write("-----------------------------------------------------------------"  + "\r\n");
		}
	}

	public ArrayList<String> getLogList() {
		return logList;
	}

	public void setLogList(ArrayList<String> logList) {
		this.logList = logList;
	}

	public static String getLogFilePath() {
		return logFilePath;
	}

	public static void setLogFilePath(String logFilePath) {
		FileLogger.logFilePath = logFilePath;
	}
}