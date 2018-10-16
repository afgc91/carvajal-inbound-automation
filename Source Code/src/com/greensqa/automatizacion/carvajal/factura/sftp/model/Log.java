package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Log {

	public static ArrayList<String> logList = new ArrayList<String>();
	private static boolean isLogged;
	private static String logFilePath;

	public static File getLogFile(String directory) throws IOException {
		String dirPath = directory + "\\Logs";
		File dir = new File(dirPath);
		dir.mkdir();
		File[] files = dir.listFiles();
		String logFilePath = "";
		if (files != null) {
			logFilePath = dirPath + "\\log" + (files.length + 1) + ".txt";
		} else {
			logFilePath = dirPath + "\\log1.txt";
		}
		File log = new File(logFilePath);
		log.canWrite();
		return log;
	}

	private static void generateLog(String directory) throws IOException {

		File log = null;

		if (!isLogged) {
			isLogged = true;
			log = getLogFile(directory);
			log.createNewFile();
			logFilePath = log.getAbsolutePath();
		} else {
			log = new File(logFilePath);
		}
	}
//
//	public static void createLog(String nameFile) {
//		try {
//			File archivo = new File(nameFile);
//			BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));
//			String enter = System.getProperty("line.separator");
//			for (String log : logList) {
//				bw.write(log+enter);
//			}
//
//			bw.close();
//
//		} catch (IOException e) {
//			JOptionPane.showMessageDialog(null, e.getMessage());
//		}
//		
//		resetLog();
//
//	}
//
//	public static void resetLog() {
//		logList = null;
//	}

	public boolean isLogged() {
		return isLogged;
	}

	public void setLogged(boolean isLogged) {
		this.isLogged = isLogged;
	}

	public String getLogFilePath() {
		return logFilePath;
	}

	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}

}
