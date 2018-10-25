package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FilesCompressor {

	private String dstPath;
	private String srcPath;

	public FilesCompressor(String srcPath, String dstPath) {

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
		// Integer total = getTotalFiles(folder);

		if (folder.isDirectory()) {
			addDirectoryToZip(path, srcFile, zip);

		} else {
			byte[] buf = new byte[1024];
			int len = 0;
			@SuppressWarnings("resource")
			FileInputStream in = new FileInputStream(srcFile);
			zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
			while ((len = in.read(buf)) > 0) {
				zip.write(buf, 0, len);
			}
		}
	}

	// Adicionar Archivos a la carpeta comprimida
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

	/**
	 * Contar la cantidad de archivos en la carpeta
	 * 
	 * @param directory
	 * @return
	 */
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

	// Descomprimir Documento enviado en .zip a Cen Financiero

	public static String unZip(String pathZip) {
		byte[] buffer = new byte[1024];
		String pathFileZip = "";
		try {
			File file = new File(pathZip);
			String pathDest = file.getParent();

			File folder = new File(pathDest);
			if (!folder.exists()) {
				folder.mkdir();
			}
			try (ZipInputStream zis = new ZipInputStream(new FileInputStream(pathZip))) {
				ZipEntry ze = zis.getNextEntry();
				String typeDocument = (ze.getName().split("\\.")[1]);
				System.out.println(ze.getName() + "archivo zip");
				
				while ((ze != null)){
					String fileName = ze.getName();
						File archivoNuevo = new File(pathDest + File.separator + fileName);
						pathFileZip = archivoNuevo.getAbsolutePath();
						new File(archivoNuevo.getParent()).mkdirs();
						try (FileOutputStream fos = new FileOutputStream(archivoNuevo);
								OutputStreamWriter sw = new OutputStreamWriter(fos)) {
							int len;
							while ((len = zis.read(buffer)) > 0) {
								fos.write(buffer, 0, len);
							}
							ze = zis.getNextEntry();
						}				
				zis.closeEntry();
			}}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println(pathFileZip);
		return pathFileZip;

	}
}
