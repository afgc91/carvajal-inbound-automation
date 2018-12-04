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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Genera archivos de prueba de Carvajal y los distribuye en una lista de
 * directorios.
 * 
 * @author Andrés Fernando Gasca
 *
 */
public class FilesGenerator implements Progressable {

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
	private int totalItems;

	/**
	 * Datos de entrada del archivo de configuración.
	 */
	protected StandardFactStructureElement standardFactStructure;

	/**
	 * Cantidad de archivos que se han generado hasta el momento. Se utiliza para
	 * llenar la barra de progreso de generación de archivos.
	 */
	private int processedItems;

	/**
	 * Bandera que indica si el trabajo en background debe seguir realizándose.
	 */
	private boolean keepWorking;

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
		this.totalItems = filesNum;
		this.standardFactStructure = CarvajalUtils.loadConfigFile(configFilePath, sdf);
		this.processedItems = 0;
		this.keepWorking = true;
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
	 * @throws                              java.text.ParseException
	 */
	public boolean generateTestFiles() throws FileNotFoundException, IOException, ParserConfigurationException,
			SAXException, TransformerException, java.text.ParseException {
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
		Date factDate = this.standardFactStructure.getFactDate();
		String cufePath = this.standardFactStructure.getCufePath();

		// Identificar el tipo de archivo a replicar.

		if (type.equalsIgnoreCase("txt")) {
			if (CarvajalUtils.isValidTxt(this.baseFilePath)) {
				return generateTxtFiles(factStartNum, index, docTypeId, nitSender, nitReceiver, type, docType,
						authNumber, startingRangeDate, endingRangeDate, prefix, startingRangeNum, endingRangeNum,
						factDate);
			}
		} else if (type.equalsIgnoreCase("fe")) {
			if (CarvajalUtils.isValidClaroFe(this.baseFilePath)) {
				return generateClaroTxtFiles(factStartNum, index, docTypeId, nitSender, nitReceiver, type, docType,
						prefix, factDate, cufePath);
			}
		} else if (type.equalsIgnoreCase("xml")) {
			int xmlType = CarvajalUtils.getXmlType(this.baseFilePath);
			if (xmlType == 0) {
				return false;
			} else if (xmlType == 1) {
				return generateXmlStandardFiles(factStartNum, index, docTypeId, nitSender, nitReceiver, type, docType,
						authNumber, startingRangeDate, endingRangeDate, prefix, startingRangeNum, endingRangeNum,
						factDate);
			} else {
				return generateUBLStandardFiles(factStartNum, index, docTypeId, nitSender, nitReceiver, type, docType,
						authNumber, startingRangeDate, endingRangeDate, prefix, startingRangeNum, endingRangeNum,
						factDate);
			}

		}
		return false;
	}

	private boolean generateTxtFiles(long factStartNum, long index, String docTypeId, String nitSender,
			String nitReceiver, String type, int docType, long authNumber, Date startingRangeDate, Date endingRangeDate,
			String prefix, long startingRangeNum, long endingRangeNum, Date factDate)
			throws FileNotFoundException, IOException {
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
				line = line.replaceAll("[^\\p{Graph}\n\r\t ]", "");
				fileLines.add(line);
			}

			String fact = "";
			String[] lineArray = null;
			String tag = "";
			String filePath = "";
			for (int i = 0; i < this.totalItems && keepWorking; i++) {
				fileLinesCopy = fileLines;
				fact = prefix + index;
				System.out.println("Prefijo " + prefix);

				// Modificar las líneas que hay que cambiar al archivo base y crear el archivo
				String fileName = docType == 1 ? "FV"
						: docType == 2 ? "FE" : docType == 3 ? "FC" : docType == 9 ? "" : "";
				if (fileName == "") {
					fileName = docTypeId;
				}
				fileName += fact;
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
							if (!docTypeId.equalsIgnoreCase("")) {
								lineArray[1] = docTypeId;
							}
							if (!nitSender.equalsIgnoreCase("")) {
								lineArray[2] = nitSender;
							}
							if (!nitReceiver.equalsIgnoreCase("")) {
								lineArray[3] = nitReceiver;
							}
							lineArray[6] = fact;
							if (docType != 0) {
								lineArray[9] = docType + "";
							}
							if (!(factDate + "").equalsIgnoreCase("1990-01-01")) {
								lineArray[7] = factDate + "";
							}
							line = CarvajalUtils.concatTxtFileLineArray(lineArray);
							fileLinesCopy.set(j, line);
						} else if (tag.equalsIgnoreCase("EMI")) {
							// Modificar campos EMI
							if (!nitSender.equalsIgnoreCase("")) {
								lineArray[2] = nitSender;
							}
							line = CarvajalUtils.concatTxtFileLineArray(lineArray);
							fileLinesCopy.set(j, line);
						} else if (tag.equalsIgnoreCase("ADQ")) {
							// Modificar campos ADQ
							if (!nitReceiver.equalsIgnoreCase("")) {
								lineArray[2] = nitReceiver;
							}
							line = CarvajalUtils.concatTxtFileLineArray(lineArray);
							fileLinesCopy.set(j, line);
						} else if (tag.equalsIgnoreCase("DRF")) {
							// Modificar campos DRF
							if (authNumber != 0) {
								lineArray[1] = authNumber + "";
							}
							if (!(startingRangeDate + "").equalsIgnoreCase("1990-01-01")) {
								lineArray[2] = startingRangeDate + "";
							}
							if (!(endingRangeDate + "").equalsIgnoreCase("1990-01-01")) {
								lineArray[3] = endingRangeDate + "";
							}
							if (!prefix.equalsIgnoreCase("")) {
								lineArray[4] = prefix;
							}
							if (!(startingRangeNum + "").equalsIgnoreCase("0")) {
								lineArray[5] = startingRangeNum + "";
							}
							if (!(endingRangeNum + "").equalsIgnoreCase("0")) {
								lineArray[6] = endingRangeNum + "";
							}
							line = CarvajalUtils.concatTxtFileLineArray(lineArray);
							fileLinesCopy.set(j, line);
						} else if (tag.equalsIgnoreCase("QFA")) {
							if (!nitSender.equalsIgnoreCase("")) {
								lineArray[1] = nitSender;
							}
							line = CarvajalUtils.concatTxtFileLineArray(lineArray);
							fileLinesCopy.set(j, line);
						} else if (tag.equalsIgnoreCase("AQF")) {
							if (!nitReceiver.equalsIgnoreCase("")) {
								lineArray[1] = nitReceiver;
							}
							line = CarvajalUtils.concatTxtFileLineArray(lineArray);
							fileLinesCopy.set(j, line);
						}

						if (j != fileLinesCopy.size() - 1) {
							line += "\r\n";
						}
						pw.write(line);
						System.out.println(line);
					}
				}

				index += 1;
				processedItems += 1;
			}
		}
		return true;
	}

	private boolean generateClaroTxtFiles(long factStartNum, long index, String docTypeId, String nitSender,
			String nitReceiver, String type, int docType, String prefix, Date factDate, String cufePath)
			throws FileNotFoundException, IOException, java.text.ParseException {
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
				line = line.replaceAll("[^\\p{Graph}\n\r\t ]", "");
				fileLines.add(line);
			}

			String fact = "";
			String[] lineArray = null;
			String tag = "";
			String filePath = "";
			String[] monthDate = (factDate + "").split("\\-");
			String monthDatePRC = monthDate[1];
			for (int i = 0; i < this.totalItems; i++) {
				fileLinesCopy = fileLines;
				fact = index + "";

				// Modificar las líneas que hay que cambiar al archivo base y crear el archivo
				String fileName = docType == 1 ? "FV"
						: docType == 2 ? "FE" : docType == 3 ? "FC" : docType == 4 ? "FI" : docType == 9 ? "" : "";
				if (fileName == "") {
					fileName = docTypeId;
				}
				fileName += fact;
				filePath = this.directoryOut + "/" + fileName + ".fe";
				File f = new File(filePath);
				f.createNewFile();

				try (FileWriter fw = new FileWriter(f);
						BufferedWriter bw = new BufferedWriter(fw);
						PrintWriter pw = new PrintWriter(bw)) {
					for (int j = 0; j < fileLinesCopy.size(); j++) {
						line = fileLinesCopy.get(j);
						lineArray = line.split("\\|");
						tag = lineArray[0];
						if (tag.equalsIgnoreCase("PRC")) {
							// Modificar campos PRC
							if (!nitSender.equalsIgnoreCase("")) {
								lineArray[3] = nitSender;
							}
							if (!(factDate + "").equalsIgnoreCase("1990-01-01")) {
								lineArray[6] = monthDatePRC;
							}
							line = CarvajalUtils.concatClaroFileLineArray(lineArray);
							fileLinesCopy.set(j, line);
						} else if (tag.equalsIgnoreCase("CAB")) {
							// Modificar campos CAB Encabezado
							if (!docTypeId.equalsIgnoreCase("")) {
								lineArray[1] = docTypeId;
							}
							if (docType != 0) {
								lineArray[2] = docType + "";
							}
							if (!prefix.equalsIgnoreCase("")) {
								lineArray[4] = prefix;
							}
							lineArray[5] = fact;
							if (!(factDate + "").equalsIgnoreCase("1990-01-01")) {
								lineArray[7] = factDate + "";
							}
							line = CarvajalUtils.concatClaroFileLineArray(lineArray);
							fileLinesCopy.set(j, line);
						} else if (tag.equalsIgnoreCase("ADQ")) {
							// Modificar campos ADQ
							if (!nitReceiver.equalsIgnoreCase("")) {
								lineArray[3] = nitReceiver;
							}
							line = CarvajalUtils.concatClaroFileLineArray(lineArray);
							fileLinesCopy.set(j, line);
						}
						if (j != fileLinesCopy.size() - 1) {
							line += "\r\n";
						}
						bw.write(line);
						System.out.println(line);
					}
				}

				if (!cufePath.equals("")) {
					ClaroCufeGenerator cufe = new ClaroCufeGenerator(cufePath, f);
					String cufeClaro = cufe.generateCufeClaroFile();

					try (FileWriter fw = new FileWriter(f);
							BufferedWriter bw = new BufferedWriter(fw);
							PrintWriter pw = new PrintWriter(bw)) {
						for (int j = 0; j < fileLinesCopy.size(); j++) {
							line = fileLinesCopy.get(j);
							lineArray = line.split("\\|");
							tag = lineArray[0];
							if (tag.equalsIgnoreCase("CAB")) {
								lineArray[6] = cufeClaro;
								line = CarvajalUtils.concatClaroFileLineArray(lineArray);
								fileLinesCopy.set(j, line);
							}
							if (j != fileLinesCopy.size() - 1) {
								line += "\r\n";
							}
							pw.write(line);
						}
					}
				}
				index += 1;
				processedItems += 1;
			}

		}
		return true;
	}

	private boolean generateXmlStandardFiles(long factStartNum, long index, String docTypeId, String nitSender,
			String nitReceiver, String type, int docType, long authNumber, Date startingRangeDate, Date endingRangeDate,
			String prefix, long startingRangeNum, long endingRangeNum, Date factDate)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {

		File source = new File(this.baseFilePath);
		File dest = null;
		String fileName = "";
		String filePath = "";
		String fact = "";

		// Crear los archivos (inicialmente como copias idénticas al original y después
		// cambiar el contenido de los tags que se deben modificar.
		for (int i = 0; i < this.totalItems; i++) {
			fact = prefix + index;
			fileName = docType == 1 ? "FV" : docType == 2 ? "FE" : docType == 3 ? "FC" : docType == 9 ? "" : "";
			if (fileName == "") {
				fileName = docTypeId;
			}
			fileName += fact;
			filePath = this.directoryOut + "/" + fileName + ".xml";
			dest = new File(filePath);
			dest.createNewFile();

			// Crear la copia del archivo
			CarvajalUtils.copyFileUsingStream(source, dest);

			// Modificar contenido del archivo
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
			Document doc = builder.parse(dest);
			doc.getDocumentElement().normalize();
			if (!docTypeId.equals("")) {
				CarvajalUtils.setXmlNode(doc, "ENC_1", docTypeId);
			}
			if (!nitSender.equals("")) {
				CarvajalUtils.setXmlNode(doc, "ENC_2", nitSender);
			}
			if (!nitReceiver.equals("")) {
				CarvajalUtils.setXmlNode(doc, "ENC_3", nitReceiver);
			}
			if (!fact.equals("")) {
				CarvajalUtils.setXmlNode(doc, "ENC_6", fact);
			}
			if (!(factDate + "").equals("")) {
				CarvajalUtils.setXmlNode(doc, "ENC_7", factDate + "");
			}
			if (!(docType + "").equals("0")) {
				CarvajalUtils.setXmlNode(doc, "ENC_9", docType + "");
			}
			if (!nitSender.equals("")) {
				CarvajalUtils.setXmlNode(doc, "EMI_2", nitSender);
			}
			if (!nitReceiver.equals("")) {
				CarvajalUtils.setXmlNode(doc, "ADQ_2", nitReceiver);
			}
			if (!(authNumber + "").equals("0")) {
				CarvajalUtils.setXmlNode(doc, "DRF_1", authNumber + "");
			}
			if (!(startingRangeDate + "").equals("1990-01-01")) {
				CarvajalUtils.setXmlNode(doc, "DRF_2", startingRangeDate + "");
			}
			if (!(endingRangeDate + "").equals("1990-01-01")) {
				CarvajalUtils.setXmlNode(doc, "DRF_3", endingRangeDate + "");
			}
			if (!prefix.equals("")) {
				CarvajalUtils.setXmlNode(doc, "DRF_4", prefix);
			}
			if (!(startingRangeNum + "").equals("0")) {
				CarvajalUtils.setXmlNode(doc, "DRF_5", startingRangeNum + "");
			}
			if (!(endingRangeNum + "").equals("0")) {
				CarvajalUtils.setXmlNode(doc, "DRF_6", endingRangeNum + "");
			}
			if (!nitSender.equals("")) {
				CarvajalUtils.setXmlNode(doc, "QFA_1", nitSender);
			}
			if (!nitReceiver.equals("")) {
				CarvajalUtils.setXmlNode(doc, "AQF_1", nitReceiver);
			}

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(doc);
			try (Writer writer = new OutputStreamWriter(new FileOutputStream(new File(filePath)), "UTF-8")) {
				StreamResult streamResult = new StreamResult(writer);
				transformer.transform(domSource, streamResult);
			}
			index += 1;
			processedItems += 1;
			;
		}

		return true;
	}

	private boolean generateUBLStandardFiles(long factStartNum, long index, String docTypeId, String nitSender,
			String nitReceiver, String type, int docType, long authNumber, Date startingRangeDate, Date endingRangeDate,
			String prefix, long startingRangeNum, long endingRangeNum, Date factDate)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {

		File source = new File(this.baseFilePath);
		File dest = null;
		String fileName = "";
		String filePath = "";
		String fact = "";
		long factIndex = factStartNum;

		// Crear los archivos (inicialmente como copias idénticas al original y después
		// cambiar el contenido de los tags que se deben modificar.
		for (int i = 0; i < this.totalItems; i++) {
			fact = prefix + index;
			fileName = docType == 1 ? "FV" : docType == 2 ? "FE" : docType == 3 ? "FC" : docType == 9 ? "" : "UNKNOWN";
			if (fileName == "") {
				fileName = docTypeId;
			}
			fileName += "_" + fact;
			filePath = this.directoryOut + "/" + fileName + ".xml";
			dest = new File(filePath);
			dest.createNewFile();

			// Crear la copia del archivo
			CarvajalUtils.copyFileUsingStream(source, dest);

			// Modificar contenido del archivo
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
			Document doc = builder.parse(dest);
			doc.getDocumentElement().normalize();
			NodeList cbcList = doc.getElementsByTagName("cbc:ID");
			Node authorizationNode = doc.getElementsByTagName("sts:InvoiceAuthorization").item(0);

			if (authorizationNode != null) {
				// Tag Número de autorización
				if (!(authNumber + "").equals("0")) {
					authorizationNode.setTextContent(authNumber + "");
				}
			}
			
			Node factDateNode = doc.getElementsByTagName("cbc:IssueDate").item(0); 
			if(factDateNode != null) {
				if(!(factDate + "").equals("1990-01-01")) {
					factDateNode.setTextContent(factDate + "");
				}
			}
			
			Node startDateNode = doc.getElementsByTagName("cbc:StartDate").item(0);
			if (startDateNode != null) {
				if(!(startDateNode + "").equals("1990-01-01")) {
				// Tag Fecha de Inicio del Rango del Prefijo
				startDateNode.setTextContent(startingRangeDate + "");
			}}
			Node endDateNode = doc.getElementsByTagName("cbc:EndDate").item(0);
			if (endDateNode != null) {
				if(!(endingRangeDate + "").equals("1990-01-01")) {
				// Tag Fecha final del rango del prefijo
				endDateNode.setTextContent(endingRangeDate + "");
			}}

			Node prefixNode = doc.getElementsByTagName("sts:Prefix").item(0);

			if (prefixNode != null) {
				if(!prefix.equals("")) {
				// Tag Prefijo
				prefixNode.setTextContent(prefix);
			}}

			Node startingRangeNode = doc.getElementsByTagName("sts:From").item(0);

			if (startingRangeNode != null) {
				if(!(startingRangeNum + "").equals("0")) {
				// Tag Número de inicio del rango para el prefijo
				startingRangeNode.setTextContent(startingRangeNum + "");
			}}

			Node endingRangeNode = doc.getElementsByTagName("sts:To").item(0);

			if (endingRangeNode != null) {
				if(!(endingRangeNum + "").equals("0")) {
				// Tag Número final del rango para el prefijo
				endingRangeNode.setTextContent(endingRangeNum + "");
			}}

			Node typeCodeDocNode = doc.getElementsByTagName("cbc:InvoiceTypeCode").item(0);

			if (typeCodeDocNode != null) {
				if(!(docType + "").equals("0")) {
				// Tag código para el tipo de documento
				typeCodeDocNode.setTextContent(docType + "");}
			}

			int nodeListIndex = 0;
			Node tmpNode = null;
			Node tmpParentNode = null;
			Node tmpGrandpaNode = null;
			Node tmpGreatGrndpaNode = null;

			while (true) {
				tmpNode = cbcList.item(nodeListIndex);
				if (tmpNode == null) {
					break;
				}

				tmpParentNode = tmpNode.getParentNode();
				if (tmpParentNode != null) {
					tmpGrandpaNode = tmpParentNode.getParentNode();
					if (tmpParentNode.getNodeName().equalsIgnoreCase("fe:Invoice")) {
						// Tag de factura
						tmpNode.setTextContent(prefix + factIndex);
					}
					if (tmpGrandpaNode != null) {
						tmpGreatGrndpaNode = tmpGrandpaNode.getParentNode();
						if (tmpGreatGrndpaNode != null) {
							if (tmpGreatGrndpaNode.getNodeName().equalsIgnoreCase("fe:AccountingSupplierParty")) {
								if(!nitSender.equals("")) {
								// Tag Nit de emisor
								tmpNode.setTextContent(nitSender);}
							} else if (tmpGreatGrndpaNode.getNodeName()
									.equalsIgnoreCase("fe:AccountingCustomerParty")) {
								// Tag Nit Receptor
								if(!nitReceiver.equals("")) {
								tmpNode.setTextContent(nitReceiver);}
							}
						}
					} else {
						tmpGreatGrndpaNode = null;
					}
				} else {
					tmpGrandpaNode = null;
				}
				nodeListIndex += 1;
			}
			factIndex += 1;

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(doc);
			try (Writer writer = new OutputStreamWriter(new FileOutputStream(new File(filePath)), "UTF-8")) {
				StreamResult streamResult = new StreamResult(writer);
				transformer.transform(domSource, streamResult);
			}
			index += 1;
			processedItems += 1;
			;
		}
		return true;
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

	public int getTotalItems() {
		return totalItems;
	}

	public int getProcessedItems() {
		return processedItems;
	}

	@Override
	public boolean isKeepWorking() {
		return this.keepWorking;
	}

	public void setKeepWorking(boolean keepWorking) {
		this.keepWorking = keepWorking;
	}
}