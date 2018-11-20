package com.greensqa.automatizacion.carvajal.factura.sftp.control;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import com.greensqa.automatizacion.carvajal.factura.sftp.model.BusinessValidator;
import com.greensqa.automatizacion.carvajal.factura.sftp.model.PostgresConnector;
import com.greensqa.automatizacion.carvajal.factura.sftp.model.CarvajalUtils;
import com.greensqa.automatizacion.carvajal.factura.sftp.model.CufeGenerator;
import com.greensqa.automatizacion.carvajal.factura.sftp.model.FilesCompressor;
import com.greensqa.automatizacion.carvajal.factura.sftp.model.FilesGenerator;
import com.greensqa.automatizacion.carvajal.factura.sftp.model.SftpAndDbDataElement;
import com.greensqa.automatizacion.carvajal.factura.sftp.model.FilesSender;
import com.greensqa.automatizacion.carvajal.factura.sftp.model.TestCaseValidator;
import com.greensqa.automatizacion.carvajal.factura.sftp.view.CarvajalFrame;
import com.greensqa.automatizacion.carvajal.factura.sftp.view.CarvajalPanel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class CarvajalAutomationExe {

	private static CarvajalPanel mainPanel, filesGenerationPanel, sendFilesWithGenericLogPanel,
			sendFilesWithValidationsPanel;
	private static CarvajalFrame mainFrame, filesGenerationFrame, sendFilesWithGenericLogFrame,
			sendFilesWithValidationsFrame;
	private static CufeGenerator cufe; 

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException, java.text.ParseException {
		starApp();	
//		File file = new File("C:\\Users\\dvalencia\\Documents\\Test FECO\\Resultados\\FI_10480.fe");
//		cufe = new CufeGenerator("C:\\Users\\dvalencia\\Documents\\Test FECO\\generarCufe.xlsx",
//				file); 
//		cufe.generateCufeClaroFile();
	}

	/**
	 * Método que se encarga de los eventos de los botones para poner la información
	 * de entrada.
	 */
	public static void starApp() {
		mainPanel = new CarvajalPanel(3);
		mainFrame = new CarvajalFrame("Generador de Archivos FECO", mainPanel);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		listenSelectOption();
	}

	public static void listenSelectOption() {
		mainPanel.getAcceptButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int option = mainPanel.getOptionSelect().getSelectedIndex();

				if (option == 0) {
					mainFrame.setEnabled(false);
					filesGenerationPanel = new CarvajalPanel(0);
					filesGenerationFrame = new CarvajalFrame("Generador de Archivos FECO", filesGenerationPanel);
					filesGenerationFrame.setSize(new Dimension(535, 350));
					filesGenerationFrame.setLocationRelativeTo(null);
					listenEntryData();
					listenOkGenerateFiles();
					closeWindow(filesGenerationFrame, mainFrame);
				} else if (option == 1) {
					mainFrame.setEnabled(false);
					sendFilesWithGenericLogPanel = new CarvajalPanel(1);
					sendFilesWithGenericLogFrame = new CarvajalFrame("Generador de Archivos FECO",
							sendFilesWithGenericLogPanel);
					sendFilesWithGenericLogFrame.setSize(new Dimension(480, 330));
					sendFilesWithGenericLogFrame.setLocationRelativeTo(null);
					listenSendFilesWithGenericLog();
					closeWindow(sendFilesWithGenericLogFrame, mainFrame);
					sendFilesWithGenericLogPanel.getGenerateLogButton().setEnabled(true);
				} else if (option == 2) {
					mainFrame.setEnabled(false);
					sendFilesWithValidationsPanel = new CarvajalPanel(2);
					sendFilesWithValidationsFrame = new CarvajalFrame("Generador de Archivos FECO",
							sendFilesWithValidationsPanel);
					sendFilesWithValidationsFrame.setSize(new Dimension(480, 330));
					sendFilesWithValidationsFrame.setLocationRelativeTo(null);
					listenSendFilesWithTestCasesLog();
					closeWindow(sendFilesWithValidationsFrame, mainFrame);
				}
			}
		});
	}

	/**
	 * Método que escucha el botón aceptar de la funcionalidad de generar archivos.
	 */
	public static void listenOkGenerateFiles() {
		filesGenerationPanel.getGenerateFilesButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				if (!filesGenerationPanel.isValidInput()) {

					JOptionPane.showMessageDialog(null, "Las entradas no son válidas", "Entradas Inválidas",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				filesGenerationPanel.getGenerateFilesButton().setEnabled(false);
				filesGenerationPanel.getSelectFileButton().setEnabled(false);
				filesGenerationPanel.getConfigFileButton().setEnabled(false);
				filesGenerationPanel.getOutFileDirButton().setEnabled(false);
				filesGenerationPanel.getFilesPerDirectoryField().setEnabled(false);
				filesGenerationPanel.getBackButton().setEnabled(false);

				String directoriesInFilePath = filesGenerationPanel.getFilePathLabel().getText();
				String directoriesConfiFilePath = filesGenerationPanel.getConfigFilePathLabel().getText();
				String directoriesOutFilePath = filesGenerationPanel.getOutFileDirPathLabel().getText();

				int filesPerDirectory = Integer.parseInt(filesGenerationPanel.getFilesPerDirectoryField().getText());

				try {
					FilesGenerator fg = new FilesGenerator(directoriesInFilePath, directoriesConfiFilePath,
							directoriesOutFilePath, filesPerDirectory);

					// Tarea pesada.
					@SuppressWarnings("rawtypes")
					final SwingWorker worker = new SwingWorker() {

						@Override
						protected Object doInBackground() throws Exception {
							if (!fg.generateTestFiles()) {

								JOptionPane.showMessageDialog(filesGenerationPanel, "Se presento un error", "Error",
										JOptionPane.ERROR_MESSAGE);

							}
							JOptionPane.showMessageDialog(filesGenerationPanel, "Proceso Finalizado con éxito",
									"Proceso Finalizado", JOptionPane.INFORMATION_MESSAGE);

							filesGenerationPanel.getGenerateFilesButton().setEnabled(true);
							filesGenerationPanel.getSelectFileButton().setEnabled(true);
							filesGenerationPanel.getConfigFileButton().setEnabled(true);
							filesGenerationPanel.getOutFileDirButton().setEnabled(true);
							filesGenerationPanel.getFilesPerDirectoryField().setEnabled(true);
							filesGenerationPanel.getBackButton().setEnabled(true);

							boolean compressionCheckIsSelected = filesGenerationPanel.getCompressionCheck()
									.isSelected();

							if (compressionCheckIsSelected) {
								compressFiles();
							}
							return null;
						}
					};
					worker.execute();
					ProgressBarThread thread = new ProgressBarThread(filesGenerationPanel, fg);
					thread.start();
				} catch (IOException | ParseException | java.text.ParseException | HeadlessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Se presentó un error " + e1.getMessage(), "Error",
							JOptionPane.ERROR);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
	}

	public static void listenEntryData() {
		JFileChooser fileInFC = filesGenerationPanel.getFileChooser();
		JFileChooser fileConfi = filesGenerationPanel.getFileConfigChooser();
		JFileChooser directoryOut = filesGenerationPanel.getOutDirectoryChooser();

		filesGenerationPanel.getSelectFileButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para seleccionar el archivo base (archivo plano o xml)

				FileNameExtensionFilter Filter = new FileNameExtensionFilter("xml & txt", "xml", "txt");
				fileInFC.setFileFilter(Filter);

				fileInFC.setFileSelectionMode(JFileChooser.FILES_ONLY);

				int option = fileInFC.showOpenDialog(filesGenerationPanel);
				if (option == JFileChooser.APPROVE_OPTION) {

					File fileName = fileInFC.getSelectedFile();
					String directoriesFilePath = (fileName.getAbsolutePath());
					filesGenerationPanel.getFilePathLabel().setText(directoriesFilePath);
					String nameFileSelect = fileName.getName();
					filesGenerationPanel.getFileNameLabel().setText(nameFileSelect);
				}

			}
		});

		filesGenerationPanel.getConfigFileButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para subir el archivo de configuración para la generación de los nuevos
				// archivos.

				FileNameExtensionFilter Filter = new FileNameExtensionFilter("txt", "txt");
				fileConfi.setFileFilter(Filter);
				fileConfi.setCurrentDirectory(fileInFC.getCurrentDirectory());
				fileConfi.setFileSelectionMode(JFileChooser.FILES_ONLY);

				int option = fileConfi.showOpenDialog(filesGenerationPanel);
				if (option == JFileChooser.APPROVE_OPTION) {

					File fileName = fileConfi.getSelectedFile();
					String confiFilePath = (fileName.getAbsolutePath());
					filesGenerationPanel.getConfigFilePathLabel().setText(confiFilePath);
					String nameConfiFileSelect = fileName.getName();
					filesGenerationPanel.getConfigFileNameLabel().setText(nameConfiFileSelect);
				}
			}
		});

		filesGenerationPanel.getOutFileDirButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para seleccionar la carpeta en la cual se guardaran los archivos
				// generados.

				directoryOut.setCurrentDirectory(fileConfi.getCurrentDirectory());
				directoryOut.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int option = directoryOut.showOpenDialog(filesGenerationPanel);
				if (option == JFileChooser.APPROVE_OPTION) {

					File fileName = directoryOut.getSelectedFile();
					String outDirectoryPath = (fileName.getAbsolutePath());
					filesGenerationPanel.getOutFileDirPathLabel().setText(outDirectoryPath);
					String nameOutPathSelect = fileName.getName();
					filesGenerationPanel.getOutFileDirNameLabel().setText(nameOutPathSelect);
				}
			}
		});

		filesGenerationPanel.getCompressionCheck().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				boolean selectOptionZip = filesGenerationPanel.getCompressionCheck().isSelected();

				if (selectOptionZip != false) {
					filesGenerationPanel.getFilesPerZipLabel().setVisible(true);
					filesGenerationPanel.getFilesPerZipField().setVisible(true);
					return;
				}
			}
		});

		filesGenerationPanel.getBackButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Evento para regresar a la ventana principal
				mainFrame.setEnabled(true);
				filesGenerationFrame.setVisible(false);
			}
		});
	}

	public static void listenSendFilesWithGenericLog() {

		JFileChooser connectionFileChooser = sendFilesWithGenericLogPanel.getConnectionFileChooser();
		JFileChooser selectSrcDirChooser = sendFilesWithGenericLogPanel.getSelectSrcDirChooser();

		sendFilesWithGenericLogPanel.getSelectSrcPathButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para seleccionar la carpeta de la cual se seleccionaran los archivos a
				// enviar.

				selectSrcDirChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int option = selectSrcDirChooser.showOpenDialog(sendFilesWithGenericLogPanel);
				if (option == JFileChooser.APPROVE_OPTION) {

					File fileName = selectSrcDirChooser.getSelectedFile();
					String outDirectoryPath = (fileName.getAbsolutePath());
					sendFilesWithGenericLogPanel.getSelectSrcDirPathLabel().setText(outDirectoryPath);
					String nameSrcPath = fileName.getName();
					sendFilesWithGenericLogPanel.getSelectSrcDirNameLabel().setText(nameSrcPath);
				}
			}
		});

		sendFilesWithGenericLogPanel.getSelectDBFileButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para seleccionar el archivo de conexión a la BD

				FileNameExtensionFilter Filter = new FileNameExtensionFilter("txt", "txt");
				connectionFileChooser.setFileFilter(Filter);
				connectionFileChooser.setCurrentDirectory(selectSrcDirChooser.getCurrentDirectory());

				connectionFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int option = connectionFileChooser.showOpenDialog(sendFilesWithGenericLogPanel);

				if (option == JFileChooser.APPROVE_OPTION) {
					File fileName = connectionFileChooser.getSelectedFile();
					String connectionFilePath = (fileName.getAbsolutePath());
					sendFilesWithGenericLogPanel.getDbFilePathLabel().setText(connectionFilePath);
					String nameBDFile = fileName.getName();
					sendFilesWithGenericLogPanel.getDbFileNameLabel().setText(nameBDFile);
				}
			}
		});

		sendFilesWithGenericLogPanel.getBackButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Evento para regresar a la ventana principal

				mainFrame.setEnabled(true);
				sendFilesWithGenericLogFrame.setVisible(false);
			}
		});

		sendFilesWithGenericLogPanel.getSendButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (!sendFilesWithGenericLogPanel.isValidInputFileSend()) {

					JOptionPane.showMessageDialog(null, "Las entradas no son válidas", "Entradas Inválidas",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				sendFilesWithGenericLogPanel.getSendButton().setEnabled(false);
				sendFilesWithGenericLogPanel.getSelectSrcPathButton().setEnabled(false);
				sendFilesWithGenericLogPanel.getSelectDBFileButton().setEnabled(false);
				sendFilesWithGenericLogPanel.getBackButton().setEnabled(false);

				SftpAndDbDataElement sftpDbDataElement = null;
				File srcPath = selectSrcDirChooser.getSelectedFile();
				String inDirectoryPath = srcPath.getAbsolutePath();

				try {
					sftpDbDataElement = CarvajalUtils
							.loadConnectionsData(sendFilesWithGenericLogPanel.getDbFilePathLabel().getText());
				} catch (IOException | ParseException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				if (sftpDbDataElement != null) {
					FilesSender fs = new FilesSender(inDirectoryPath, sftpDbDataElement.getDestSftp(),
							sftpDbDataElement.getUserSftp(), sftpDbDataElement.getPasswordSftp(),
							sftpDbDataElement.getUrlSftp(), sftpDbDataElement.getPortSftp(), sftpDbDataElement.getKey(),
							sftpDbDataElement.getSecretKey(), sftpDbDataElement.getNameBucket(),
							sftpDbDataElement.getRegion());

					// Tarea pesada.
					@SuppressWarnings("rawtypes")
					final SwingWorker worker = new SwingWorker() {

						@Override
						protected Object doInBackground() throws Exception {
							try {
								fs.manageFilesSending(1);
								JOptionPane.showMessageDialog(null, "Archivos enviados con éxito", "Envío exitoso",
										JOptionPane.INFORMATION_MESSAGE);
								sendFilesWithGenericLogPanel.getGenerateLogButton().setEnabled(true);
							} catch (JSchException | SftpException | IOException | ParseException
									| ParserConfigurationException | SAXException e1) {
								e1.printStackTrace();
								JOptionPane.showMessageDialog(null,
										"Se presentó un error. Revise los datos de conexión y\nsu conexión a la VPN.",
										"Error", JOptionPane.ERROR_MESSAGE);
							} finally {
								sendFilesWithGenericLogPanel.getSendButton().setEnabled(true);
								sendFilesWithGenericLogPanel.getSelectSrcPathButton().setEnabled(true);
								sendFilesWithGenericLogPanel.getSelectDBFileButton().setEnabled(true);
								sendFilesWithGenericLogPanel.getBackButton().setEnabled(true);
							}
							return null;
						}
					};
					worker.execute();
					ProgressBarThread thread = new ProgressBarThread(sendFilesWithGenericLogPanel, fs);
					thread.start();
				} else {
					JOptionPane.showMessageDialog(null, "El archivo de conexiones es inválido", "Error",
							JOptionPane.ERROR_MESSAGE);
					sendFilesWithGenericLogPanel.getSendButton().setEnabled(true);
					sendFilesWithGenericLogPanel.getSelectSrcPathButton().setEnabled(true);
					sendFilesWithGenericLogPanel.getSelectDBFileButton().setEnabled(true);
					sendFilesWithGenericLogPanel.getBackButton().setEnabled(true);
				}
			}
		});

		sendFilesWithGenericLogPanel.getGenerateLogButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!sendFilesWithGenericLogPanel.isValidInputFileSend()) {
					JOptionPane.showMessageDialog(null, "Las entradas no son válidas", "Entradas Inválidas",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				File connectionFile = connectionFileChooser.getSelectedFile();
				String connectionFilePath = (connectionFile.getAbsolutePath());
				SftpAndDbDataElement sftpAndDbDataElement;

				try {
					sftpAndDbDataElement = CarvajalUtils.loadConnectionsData(connectionFilePath);
					PostgresConnector connector = new PostgresConnector(sftpAndDbDataElement.getUrlDb(),
							sftpAndDbDataElement.getUserDb(), sftpAndDbDataElement.getPasswordDb());
					File dir = selectSrcDirChooser.getSelectedFile();

					// Obtener padre de dir
					BusinessValidator bv = new BusinessValidator(
							connector.getConnetion(sftpAndDbDataElement.getTipoBD()),
							dir.getParentFile().getAbsolutePath());
					if (dir.exists()) {
						File[] files = dir.listFiles();
						bv.setTotalItems(files.length);
						bv.setProcessedItems(0);

						sendFilesWithGenericLogPanel.getSendButton().setEnabled(false);
						sendFilesWithGenericLogPanel.getSelectSrcPathButton().setEnabled(false);
						sendFilesWithGenericLogPanel.getSelectDBFileButton().setEnabled(false);
						sendFilesWithGenericLogPanel.getBackButton().setEnabled(false);
						sendFilesWithGenericLogPanel.getGenerateLogButton().setEnabled(false);

						// Tarea pesada.
						@SuppressWarnings("rawtypes")
						SwingWorker worker = new SwingWorker() {
							@Override
							protected Object doInBackground() throws Exception {
								for (int i = 0; i < files.length; i++) {
									bv.executeStatusQuery(files[i], connectionFilePath);
									bv.setProcessedItems(bv.getProcessedItems() + 1);
								}
								bv.getSummary();
								sendFilesWithGenericLogPanel.getSendButton().setEnabled(true);
								sendFilesWithGenericLogPanel.getSelectSrcPathButton().setEnabled(true);
								sendFilesWithGenericLogPanel.getSelectDBFileButton().setEnabled(true);
								sendFilesWithGenericLogPanel.getBackButton().setEnabled(true);
								sendFilesWithGenericLogPanel.getGenerateLogButton().setEnabled(true);
								JOptionPane.showMessageDialog(null, "Logs generados satisfactoriamente",
										"Operación realizada", JOptionPane.INFORMATION_MESSAGE);
								return null;
							}
						};
						worker.execute();
						ProgressBarThread thread = new ProgressBarThread(sendFilesWithGenericLogPanel, bv);
						thread.start();
					}

				} catch (IOException | ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}

	public static void listenSendFilesWithTestCasesLog() {

		JFileChooser selectSrcDirChooser = sendFilesWithValidationsPanel.getSelectSrcDirChooser();
		JFileChooser connectionFileChooser = sendFilesWithValidationsPanel.getConnectionFileChooser();

		sendFilesWithValidationsPanel.getSelectSrcPathButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para seleccionar la carpeta de la cual se seleccionaran los archivos a
				// enviar.
				selectSrcDirChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int option = selectSrcDirChooser.showOpenDialog(sendFilesWithValidationsPanel);
				if (option == JFileChooser.APPROVE_OPTION) {

					File fileName = selectSrcDirChooser.getSelectedFile();
					String outDirectoryPath = (fileName.getAbsolutePath());
					sendFilesWithValidationsPanel.getSelectSrcDirPathLabel().setText(outDirectoryPath);
					String nameSrcPath = fileName.getName();
					sendFilesWithValidationsPanel.getSelectSrcDirNameLabel().setText(nameSrcPath);
				}
			}
		});

		sendFilesWithValidationsPanel.getSelectDBFileButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para seleccionar el archivo de conexión a la BD

				FileNameExtensionFilter Filter = new FileNameExtensionFilter("txt", "txt");
				connectionFileChooser.setFileFilter(Filter);
				connectionFileChooser.setCurrentDirectory(selectSrcDirChooser.getCurrentDirectory());

				connectionFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int option = connectionFileChooser.showOpenDialog(sendFilesWithValidationsPanel);

				if (option == JFileChooser.APPROVE_OPTION) {
					File fileName = connectionFileChooser.getSelectedFile();
					String connectionFilePath = (fileName.getAbsolutePath());
					sendFilesWithValidationsPanel.getDbFilePathLabel().setText(connectionFilePath);
					String nameBDFile = fileName.getName();
					sendFilesWithValidationsPanel.getDbFileNameLabel().setText(nameBDFile);
				}
			}
		});

		sendFilesWithValidationsPanel.getBackButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Evento para regresar a la ventana principal

				sendFilesWithValidationsFrame.setVisible(false);
				starApp();
			}
		});

		sendFilesWithValidationsPanel.getSendButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (!sendFilesWithValidationsPanel.isValidInputFileSend()) {

					JOptionPane.showMessageDialog(null, "Las entradas no son válidas", "Entradas Inválidas",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				sendFilesWithValidationsPanel.getSendButton().setEnabled(false);
				sendFilesWithValidationsPanel.getSelectSrcPathButton().setEnabled(false);
				sendFilesWithValidationsPanel.getSelectDBFileButton().setEnabled(false);

				SftpAndDbDataElement sftpDbData = null;
				File srcPath = selectSrcDirChooser.getSelectedFile();
				String inDirectoryPath = srcPath.getAbsolutePath();
				FilesSender files = null;

				try {
					sftpDbData = CarvajalUtils
							.loadConnectionsData(sendFilesWithValidationsPanel.getDbFilePathLabel().getText());
				} catch (IOException | ParseException e2) {
					// TODO Auto-generated catch block
					JOptionPane.showMessageDialog(null, "El archivo de conexión no es válido", "Conexión fallida",
							JOptionPane.ERROR_MESSAGE);
				}

				if (sftpDbData != null) {
					files = new FilesSender(inDirectoryPath, sftpDbData.getDestSftp(), sftpDbData.getUserSftp(),
							sftpDbData.getPasswordSftp(), sftpDbData.getUrlSftp(), sftpDbData.getPortSftp(),
							sftpDbData.getKey(), sftpDbData.getSecretKey(), sftpDbData.getNameBucket(),
							sftpDbData.getRegion());
				}
				try {
					files.manageFilesSending(2);
					JOptionPane.showMessageDialog(null, "Archivos enviados con éxito", "Envío exitoso",
							JOptionPane.INFORMATION_MESSAGE);

				} catch (JSchException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "No se logro establecer la conexión", "Conexión no establecida",
							JOptionPane.ERROR_MESSAGE);
				} catch (SftpException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "No se pudieron enviar los archivos seleccionados",
							"Archivos No Enviados", JOptionPane.ERROR_MESSAGE);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SAXException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				sendFilesWithValidationsPanel.getSendButton().setEnabled(true);
				sendFilesWithValidationsPanel.getSelectSrcPathButton().setEnabled(true);
				sendFilesWithValidationsPanel.getSelectDBFileButton().setEnabled(true);
				sendFilesWithValidationsPanel.getGenerateLogButton().setEnabled(true);
			}
		});

		sendFilesWithValidationsPanel.getGenerateLogButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!sendFilesWithValidationsPanel.isValidInputFileSend()) {

					JOptionPane.showMessageDialog(null, "Las entradas no son válidas", "Entradas Inválidas",
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				File connectionFile = connectionFileChooser.getSelectedFile();
				String connectionFilePath = connectionFile.getAbsolutePath();

				try {
					SftpAndDbDataElement sftpAndDbDataElement = CarvajalUtils.loadConnectionsData(connectionFilePath);
					PostgresConnector conn = new PostgresConnector(sftpAndDbDataElement.getUrlDb(),
							sftpAndDbDataElement.getUserDb(), sftpAndDbDataElement.getPasswordDb());
					File fileName = selectSrcDirChooser.getSelectedFile();
					String srcExcel = (fileName.getAbsolutePath());
					File excel = new File(srcExcel);

					// Obtener padre de dir
					TestCaseValidator tc = new TestCaseValidator(conn.getConnetion(sftpAndDbDataElement.getTipoBD()));
					if (excel.exists()) {
						tc.executeQuery(excel);
						tc.testCase(srcExcel);
					}
				} catch (IOException | ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}

	public static void compressFiles() throws Exception {

		JFileChooser directory = filesGenerationPanel.getOutDirectoryChooser();
		File filesPath = directory.getSelectedFile();
		int filesPerDirectory = Integer.parseInt(filesGenerationPanel.getFilesPerDirectoryField().getText());
		int filesPerZipLabel = Integer.parseInt(filesGenerationPanel.getFilesPerZipField().getText());
		String directoryPath = filesPath.getAbsolutePath();
		String desFile = filesPath.getParentFile().getAbsolutePath();
		String nameFile = filesPath.getName();
		String desFileZip = desFile + "\\" + nameFile + ".zip";

		FilesCompressor compress = new FilesCompressor(directoryPath, desFileZip);
		compress.zipFiles(directoryPath,filesPerDirectory,filesPerZipLabel);

	}

	/**
	 * Devuelve el control a la ventana principal y cierra la secundaria. Detiene
	 * las tareas que se estén ejecutando.
	 * 
	 * @param frame Ventana que debe cerrarse.
	 * @param main  Ventana principal.
	 */
	public static void closeWindow(CarvajalFrame frame, CarvajalFrame main) {
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				
				main.setEnabled(true);
				frame.setVisible(false);
			}
		});
	}
}