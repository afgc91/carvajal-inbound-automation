package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Genera archivos de prueba de Carvajal y los distribuye en una lista de
 * directorios.
 * 
 * @author Andrés Fernando Gasca
 *
 */
public class FilesGenerator {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * Ruta del archivo con la lista de directorios de salida (donde deben quedar
	 * guardados los archivos).
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
	 * Índice para comenzar con la enumeración de los archivos.
	 */
	private int fileIndex;

	/**
	 * Archivo en formato JSON con configuraciones iniciales para la generación de
	 * archivos de prueba para el proceso funcional automatizado.
	 */
	private String configFilePath;

	/**
	 * Directorio donde se almacenarán los archivos para la automatización
	 * funcional.
	 */
	private String directoryOut;

	/**
	 * Cantidad de archivos a generar.
	 */
	private int filesNum;

	/**
	 * Datos de entrada del archivo de configuración.
	 */
	CarvajalStandardFactStructure standardFactStructure;

	/**
	 * Constructor de FilesGenerator para crear archivos distribuidos en varios
	 * directorios de salida especificados por el usuario.
	 * 
	 * @param baseFilePath           URL del archivo base.
	 * @param directoriesOutFilePath URL del archivo con los directorios de salida.
	 * @param filesPerDirectory      Cantidad de archivos que debe haber por cada
	 *                               directorio.
	 * @param fileIndex              Índice desde el cual se empieza la enumeración
	 *                               de los archivos de facturas.
	 * @throws IOException En caso de error en un flujo de entrada/salida.
	 */
	public FilesGenerator(String baseFilePath, String directoriesOutFilePath, int filesPerDirectory, int fileIndex)
			throws IOException {
		this.baseFilePath = baseFilePath;
		this.directoriesOutFilePath = directoriesOutFilePath;
		this.filesPerDirectory = filesPerDirectory;
		this.fileIndex = fileIndex;
		directoriesOut = new ArrayList<>();

		CarvajalUtils.loadDirectoriesFromFile(directoriesOutFilePath, directoriesOut);
		loadBaseFileContent();
	}

	/**
	 * Constructor de FilesGenerator para crear archivos que serán enviados en la
	 * automatización del proceso funcional.
	 * 
	 * @param baseFilePath   URL del archivo base.
	 * @param configFilePath URL del archivo JSON con la configuración inicial.
	 * @param directoryOut   URL del directorio donde se almacenarán temporalmente
	 *                       los archivos generados.
	 * @param filesNum       Número de archivos a generar para enviar vía SFTP.
	 * @throws ParseException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws                       java.text.ParseException
	 */
	public FilesGenerator(String baseFilePath, String configFilePath, String directoryOut, int filesNum)
			throws FileNotFoundException, IOException, ParseException, java.text.ParseException {
		this.baseFilePath = baseFilePath;
		this.configFilePath = configFilePath;
		this.directoryOut = directoryOut;
		this.filesNum = filesNum;
		this.standardFactStructure = CarvajalUtils.loadConfigFile(configFilePath, sdf);
	}

	/**
	 * Convierte el contenido del archivo base a texto, para poder crear las copias
	 * idénticas del archivo.
	 */
	private void loadBaseFileContent() {
		StringBuilder contentBuilder = new StringBuilder();
		try (Stream<String> stream = Files.lines(Paths.get(baseFilePath), StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		fileContent = contentBuilder.toString();
	}

	/**
	 * Inicia la creación y distribución de archivos de prueba en los directorios de
	 * salida.
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public void startFilesGeneration() throws UnsupportedEncodingException, FileNotFoundException, IOException {
		// int fileIndex = 0;
		for (int i = 0; i < directoriesOut.size(); i++) {
			String directory = directoriesOut.get(i);
			File dir = new File(directory);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			int numberOfFiles = dir.list().length;
			if (numberOfFiles < filesPerDirectory) {
				for (int j = 0; j < filesPerDirectory; j++, fileIndex++) {
					// Crear archivo
					// long millis = Calendar.getInstance().getTimeInMillis();
					String fileName = directory + "\\fact" + fileIndex + ".xml";
					String fileText = fileContent.replaceAll("\\HJY[0-9]+", "HYT" + fileIndex);
					try (Writer writer = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(fileName), "utf-8"))) {
						writer.write(fileText);
					}
				}
			}
		}
	}

	/**
	 * Genera los archivos de factura que el usuario desea generar en el directorio
	 * especificado por el usuario.
	 * 
	 * @return true si se crearon correctamente los archivos. false si no se crearon
	 *         los archivos, debido a que el archivo base no es válido.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws TransformerException 
	 */
	public boolean generateTestFiles()
			throws FileNotFoundException, IOException, ParserConfigurationException, SAXException, TransformerException {
		// Obtener datos del archivo base.
		long factStartNum = this.standardFactStructure.getFactStartNum();
		long index = factStartNum;
		String docTypeId = this.standardFactStructure.getDocTypeId();
		String nitSender = this.standardFactStructure.getNitSender();
		String nitReceiver = this.standardFactStructure.getNitReceiver();
		String type = CarvajalUtils.getFileExtension(this.baseFilePath);
		int docType = this.standardFactStructure.getDocType();
		long authNumber = this.standardFactStructure.getAuthNumber();
		Date startingRangeDate = this.standardFactStructure.getStartingRangeDate();
		Date endingRangeDate = this.standardFactStructure.getEndingRangeDate();
		String prefix = this.standardFactStructure.getFactPrefix();
		long startingRangeNum = this.standardFactStructure.getStartingRangeNum();
		long endingRangeNum = this.standardFactStructure.getEndingRangeNum();

		// Identificar el tipo de archivo a replicar.

		if (type.equalsIgnoreCase("txt")) {
			if (CarvajalUtils.isValidTxt(this.baseFilePath)) {
				return generateTxtFiles(factStartNum, index, docTypeId, nitSender, nitReceiver, type, docType,
						authNumber, startingRangeDate, endingRangeDate, prefix, startingRangeNum, endingRangeNum);
			}
		} else if (type.equalsIgnoreCase("xml")) {
			int xmlType = CarvajalUtils.getXmlType(this.baseFilePath);
			if (xmlType == 0) {
				return false;
			} else if (xmlType == 1) {
				return generateXmlStandardFiles(factStartNum, index, docTypeId, nitSender, nitReceiver, type, docType,
						authNumber, startingRangeDate, endingRangeDate, prefix, startingRangeNum, endingRangeNum);
			} else {
				return generateUBLStandardFiles();
			}
		}
		return false;
	}

	private boolean generateTxtFiles(long factStartNum, long index, String docTypeId, String nitSender,
			String nitReceiver, String type, int docType, long authNumber, Date startingRangeDate, Date endingRangeDate,
			String prefix, long startingRangeNum, long endingRangeNum) throws FileNotFoundException, IOException {
		File file = new File(this.baseFilePath);
		ArrayList<String> fileLines = new ArrayList<>();
		ArrayList<String> fileLinesCopy = null;
		try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
			String line = "";

			// Guardar líneas del archivo en el ArrayList.
			while (true) {
				line = br.readLine();
				if (line == null) {
					break;
				}
				fileLines.add(line);
			}

			String fact = "";
			String[] lineArray = null;
			String tag = "";
			String filePath = "";
			for (int i = 0; i < this.filesNum; i++) {
				fileLinesCopy = fileLines;
				fact = prefix + index;

				// Modificar las líneas que hay que cambiar al archivo base y crear el archivo
				String fileName = docType == 1 ? "FV"
						: docType == 2 ? "FE" : docType == 3 ? "FC" : docType == 9 ? "" : "UNKNOWN";
				if (fileName == "") {
					fileName = docTypeId;
				}
				fileName += "_" + fact;
				filePath = this.directoryOut + "/" + fileName + ".txt";
				File f = new File(filePath);
				f.createNewFile();

				try (FileWriter fw = new FileWriter(f);
						BufferedWriter bw = new BufferedWriter(fw);
						PrintWriter pw = new PrintWriter(bw)) {
					for (int j = 0; j < fileLinesCopy.size(); j++) {
						line = fileLinesCopy.get(j);
						lineArray = line.split(",");
						tag = lineArray[0];
						if (tag.equalsIgnoreCase("ENC")) {
							// Modificar campos ENC
							lineArray[1] = docTypeId;
							lineArray[2] = nitSender;
							lineArray[3] = nitReceiver;
							lineArray[6] = fact;
							lineArray[9] = docType + "";
							line = CarvajalUtils.concatTxtFileLineArray(lineArray);
							fileLinesCopy.set(j, line);
						} else if (tag.equalsIgnoreCase("EMI")) {
							// Modificar campos EMI
							lineArray[2] = nitSender;
							line = CarvajalUtils.concatTxtFileLineArray(lineArray);
							fileLinesCopy.set(j, line);
						} else if (tag.equalsIgnoreCase("ADQ")) {
							// Modificar campos ADQ
							lineArray[2] = nitReceiver;
							line = CarvajalUtils.concatTxtFileLineArray(lineArray);
							fileLinesCopy.set(j, line);
						} else if (tag.equalsIgnoreCase("DRF")) {
							// Modificar campos DRF
							lineArray[1] = authNumber + "";
							lineArray[2] = startingRangeDate + "";
							lineArray[3] = endingRangeDate + "";
							lineArray[4] = prefix;
							lineArray[5] = startingRangeNum + "";
							lineArray[6] = endingRangeNum + "";
							line = CarvajalUtils.concatTxtFileLineArray(lineArray);
							fileLinesCopy.set(j, line);
						} else if (tag.equalsIgnoreCase("QFA")) {
							lineArray[1] = nitSender;
							line = CarvajalUtils.concatTxtFileLineArray(lineArray);
							fileLinesCopy.set(j, line);
						} else if (tag.equalsIgnoreCase("AQF")) {
							lineArray[1] = nitReceiver;
							line = CarvajalUtils.concatTxtFileLineArray(lineArray);
							fileLinesCopy.set(j, line);
						}

						if (j != fileLinesCopy.size() - 1) {
							line += "\r\n";
						}
						pw.write(line);
						// System.out.println(line);
					}
				}

				index += 1;
			}
		}
		return true;
	}

	private boolean generateXmlStandardFiles(long factStartNum, long index, String docTypeId, String nitSender,
			String nitReceiver, String type, int docType, long authNumber, Date startingRangeDate, Date endingRangeDate,
			String prefix, long startingRangeNum, long endingRangeNum)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {

		File source = new File(this.baseFilePath);
		File dest = null;
		String fileName = "";
		String filePath = "";
		String fact = "";

		// Crear los archivos (inicialmente como copias idénticas al original y después
		// cambiar el contenido de los tags que se deben modificar.
		for (int i = 0; i < this.filesNum; i++) {
			fact = prefix + index;
			fileName = docType == 1 ? "FV" : docType == 2 ? "FE" : docType == 3 ? "FC" : docType == 9 ? "" : "UNKNOWN";
			if (fileName == "") {
				fileName = docTypeId;
			}
			fileName += "_" + fact;
			filePath = this.directoryOut + "/" + fileName + ".xml";
			dest = new File(filePath);
			dest.createNewFile();
			
			//Crear la copia del archivo
			CarvajalUtils.copyFileUsingStream(source, dest);
			
			// Modificar contenido del archivo
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
			Document doc = builder.parse(dest);
			doc.getDocumentElement().normalize();
			CarvajalUtils.setXmlNode(doc, "ENC_1", docTypeId);
			CarvajalUtils.setXmlNode(doc, "ENC_2", nitSender);
			CarvajalUtils.setXmlNode(doc, "ENC_3", nitReceiver);
			CarvajalUtils.setXmlNode(doc, "ENC_6", fact);
			CarvajalUtils.setXmlNode(doc, "ENC_9", docType + "");
			CarvajalUtils.setXmlNode(doc, "EMI_2", nitSender);
			CarvajalUtils.setXmlNode(doc, "ADQ_2", nitReceiver);
			CarvajalUtils.setXmlNode(doc, "DRF_1", authNumber + "");
			CarvajalUtils.setXmlNode(doc, "DRF_2", startingRangeDate + "");
			CarvajalUtils.setXmlNode(doc, "DRF_3", endingRangeDate + "");
			CarvajalUtils.setXmlNode(doc, "DRF_4", prefix);
			CarvajalUtils.setXmlNode(doc, "DRF_5", startingRangeNum + "");
			CarvajalUtils.setXmlNode(doc, "DRF_6", endingRangeNum + "");
			CarvajalUtils.setXmlNode(doc, "QFA_1", nitSender);
			CarvajalUtils.setXmlNode(doc, "AQF_2", nitReceiver);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(doc);
			System.out.println(filePath);
			try (Writer writer = new OutputStreamWriter(new FileOutputStream(new File(filePath)), "UTF-8")) {
				StreamResult streamResult = new StreamResult(writer);
				transformer.transform(domSource, streamResult);	
			}
			//System.out.println("Archivo " + i + " creado");
			index += 1;
		}
		
		return false;
	}

	private boolean generateUBLStandardFiles() {
		return false;
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

	public String getConfigFilePath() {
		return configFilePath;
	}

	public void setConfigFilePath(String configFilePath) {
		this.configFilePath = configFilePath;
	}

	public String getDirectoryOut() {
		return directoryOut;
	}

	public void setDirectoryOut(String directoryOut) {
		this.directoryOut = directoryOut;
	}

	public int getFilesNum() {
		return filesNum;
	}

	public void setFilesNum(int filesNum) {
		this.filesNum = filesNum;
	}
}
