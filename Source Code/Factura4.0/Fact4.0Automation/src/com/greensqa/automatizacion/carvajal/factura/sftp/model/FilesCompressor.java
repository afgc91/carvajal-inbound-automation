package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FilesCompressor {

	private String dstPath;
	private String srcPath;
	private int filesPerDirectory;
	private int filesPerZipLabel;

	public FilesCompressor(String srcPath, String dstPath) {

		this.dstPath = dstPath;
		this.srcPath = srcPath;

	}

	public void zipFiles(String srcPath, int filesPerDirectory, int filesPerZipLabel) {
		try {

			getLogFile(srcPath);
			File file = new File(srcPath);
			File[] files = file.listFiles();
			
			int index = 0;
			int filesPerZip = filesPerDirectory / filesPerZipLabel;
			for (int i = 0; i < filesPerZip; i++) {

				FileOutputStream fos = new FileOutputStream((file.getParentFile()).getAbsolutePath()+"\\Paquetes\\Paquete" + i + ".zip");
				ZipOutputStream zos = new ZipOutputStream(fos);
				for (int j = 0; j < filesPerZipLabel; j++) {
					@SuppressWarnings("resource")
					FileInputStream in = new FileInputStream(files[index].getAbsoluteFile());
					zos.putNextEntry(new ZipEntry((files[index]).getName()));
	
					byte[] bytes = new byte[2048];
					int len;
					while ((len = in.read(bytes)) > 0) {
						zos.write(bytes, 0, len);
					}
					index++;
				}
				zos.closeEntry();
				zos.close();
			}

		} catch (FileNotFoundException ex) {
			System.err.println("Archivo no Existe: " + ex);
		} catch (IOException ex) {
			System.err.println("Entrada Inválida: " + ex);
		}
	}

	private void getLogFile(String srcPath) throws IOException {
		File file = new File(srcPath);
		String dirPath = file.getParent() + "\\Paquetes";
		File dir = new File(dirPath);
		dir.mkdir();
	}

	/**
	 * Contar la cantidad de archivos en la carpeta
	 * 
	 * @param directory
	 * @return
	 */
	@SuppressWarnings("unused")
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
		ArrayList<String> pathFilesInZip = new ArrayList<String>();
		try {
			File file = new File(pathZip);
			String pathDest = file.getParent();
			File folder = new File(pathDest);
			if (!folder.exists()) {
				folder.mkdir();
			}
			try (ZipInputStream zis = new ZipInputStream(new FileInputStream(pathZip))) {
				ZipEntry ze = zis.getNextEntry();
				String fileName = "";
				while (ze != null) {
					fileName = ze.getName();
					File archivoNuevo = new File(pathDest + File.separator + fileName);
					pathFileZip = archivoNuevo.getAbsolutePath();
					new File(archivoNuevo.getParent()).mkdirs();
					try (FileOutputStream fos = new FileOutputStream(archivoNuevo)) {
						int len;
						while ((len = zis.read(buffer)) > 0) {
							fos.write(buffer, 0, len);
						}
						ze = zis.getNextEntry();
					}
				}
				if (fileName.contains(".txt")) {
					pathFilesInZip.add(pathFileZip);
				}
				zis.closeEntry();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return pathFilesInZip.get(0);
	}
}
