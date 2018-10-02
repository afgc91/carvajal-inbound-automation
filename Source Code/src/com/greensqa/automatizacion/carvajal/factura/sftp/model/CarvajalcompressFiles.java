package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CarvajalcompressFiles {

	private String dstPath;
	private String srcPath;

	public CarvajalcompressFiles(String srcPath, String dstPath) {

		this.dstPath = dstPath;
		this.srcPath = srcPath;

	}

	public void compressFiles(File file) throws Exception {

		ZipOutputStream zip = null;
		FileOutputStream fileWriter = null;
		fileWriter = new FileOutputStream(dstPath);
		zip = new ZipOutputStream(fileWriter);
		addDirectoryToZip("", srcPath, zip);

		zip.flush();
		zip.close();

	}

	private static void addFilesToZip(String path, String srcFile, ZipOutputStream zip) throws Exception {

		File folder = new File(srcFile);
		//Integer total = getTotalFiles(folder);

		if (folder.isDirectory()) {
			addDirectoryToZip(path, srcFile, zip);

		} else {
			byte[] buf = new byte[1024];
			int len=0;
			@SuppressWarnings("resource")
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
		}
	}
	
	//Adicionar Archivos a la carpeta comprimida
	private static void addDirectoryToZip(String path, String srcDirectory, ZipOutputStream zip) throws Exception {
		File folder = new File(srcDirectory);

		for (String fileName : folder.list()) {
			if (path.equals("")) {
				addFilesToZip(folder.getName(), srcDirectory + "/" + fileName, zip);
			} else {
				addFilesToZip(path + "/" + folder.getName(), srcDirectory + "/" + fileName, zip);
			}
		}
	}

	//Contar la cantidad de archivos en la carpeta
	private static int getTotalFiles(File directory) {
		int total = 0;
		String[] arrArchivos = directory.list();
		total += arrArchivos.length;
		File tmpFile;
		for (int i = 0; i < arrArchivos.length; ++i) {
			tmpFile = new File(directory.getPath() + "/" + arrArchivos[i]);
			if (tmpFile.isDirectory()) {
				total += getTotalFiles(tmpFile);
			}
		}
		return total;
	}

}