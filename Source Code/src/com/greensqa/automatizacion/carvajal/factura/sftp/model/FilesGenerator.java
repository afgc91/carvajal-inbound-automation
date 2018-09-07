package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * Genera archivos de prueba de Carvajal y los distribuye en una lista de directorios.
 * @author Andrés Fernando Gasca
 *
 */
public class FilesGenerator {

	/**
	 * Ruta del archivo con la lista de directorios de salida (donde deben quedar guardados los archivos).
	 */
	private String directoriesOutFilePath;
	
	/**
	 * Lista de los directorios de salida.
	 */
	private ArrayList<String> directoriesOut;
	
	/**
	 * Ruta del archivo base.
	 */
	private String baseFilePath;
	
	/**
	 * Número de archivos que debe haber en cada directorio.
	 */
	private int filesPerDirectory;
	
	/**
	 * Contenido del archivo base en cadena de texto.
	 */
	private String fileContent;
	
	/**
	 * índice para comenzar con la enumeración de los archivos.
	 */
	private int fileIndex;
	
	public FilesGenerator(String baseFilePath, String directoriesOutFilePath, int filesPerDirectory, int fileIndex) throws IOException {
		this.baseFilePath = baseFilePath;
		this.directoriesOutFilePath = directoriesOutFilePath;
		this.filesPerDirectory = filesPerDirectory;
		this.fileIndex = fileIndex;
		directoriesOut = new ArrayList<>();
		
		CarvajalUtils.loadDirectoriesFromFile(directoriesOutFilePath, directoriesOut);
		loadBaseFileContent();
	}
	
	/**
	 * Convierte el contenido del archivo base a texto, para poder crear las copias idénticas del archivo.
	 */
	private void loadBaseFileContent() {
	    StringBuilder contentBuilder = new StringBuilder();
	    try (Stream<String> stream = Files.lines( Paths.get(baseFilePath), StandardCharsets.UTF_8)) {
	        stream.forEach(s -> contentBuilder.append(s).append("\n"));
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	    fileContent = contentBuilder.toString();
	}
	
	/**
	 * Inicia la creación y distribución de archivos de prueba en los directorios de salida.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	public void startFilesGeneration() throws UnsupportedEncodingException, FileNotFoundException, IOException {
		//int fileIndex = 0;
		for (int i = 0; i < directoriesOut.size(); i++) {
			String directory = directoriesOut.get(i);
			File dir = new File(directory);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			int numberOfFiles = dir.list().length;
			if (numberOfFiles < filesPerDirectory) {
				for (int j = 0; j < filesPerDirectory; j++, fileIndex++) {
					//Crear archivo
					//long millis = Calendar.getInstance().getTimeInMillis();
					String fileName = directory + "\\fact" + fileIndex + ".xml";
					String fileText = fileContent.replaceAll("\\HJY[0-9]+", "HYT" + fileIndex);
					try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "utf-8"))) {
						writer.write(fileText);
					}
				}
			}
		}
	}

	public String getDirectoriesOutFilePath() {
		return directoriesOutFilePath;
	}

	public void setDirectoriesOutFilePath(String directoriesOutFilePath) {
		this.directoriesOutFilePath = directoriesOutFilePath;
	}

	public ArrayList<String> getDirectoriesOut() {
		return directoriesOut;
	}

	public void setDirectoriesOut(ArrayList<String> directoriesOut) {
		this.directoriesOut = directoriesOut;
	}

	public String getBaseFilePath() {
		return baseFilePath;
	}

	public void setBaseFilePath(String baseFilePath) {
		this.baseFilePath = baseFilePath;
	}

	public int getFilesPerDirectory() {
		return filesPerDirectory;
	}

	public void setFilesPerDirectory(int filesPerDirectory) {
		this.filesPerDirectory = filesPerDirectory;
	}

	public String getFileContent() {
		return fileContent;
	}

	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}
	
	public int getFileIndex() {
		return this.fileIndex;
	}
	
	public void setFileIndex(int fileIndex) {
		this.fileIndex = fileIndex;
	}
}
