package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileLogger {

	public ArrayList<String> logList;
	private boolean isLogged;
	private String logFilePath;

	public FileLogger() {
		this.logList = new ArrayList<>();
	}
	
	public File getLogFile(String directory) throws IOException {
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

	private void generateLog(String directory) throws IOException {

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

	public boolean isLogged() {
		return isLogged;
	}

	public String getLogFilePath() {
		return logFilePath;
	}

	public void setLogFilePath(String logFilePath) {
		this.logFilePath = logFilePath;
	}

}
