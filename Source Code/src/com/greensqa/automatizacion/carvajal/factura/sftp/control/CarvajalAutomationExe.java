package com.greensqa.automatizacion.carvajal.factura.sftp.control;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

	private static CarvajalPanel panel, panel1, sendFilesWithGenericLogPanel, panel3;
	private static CarvajalFrame frame, frame1, sendFilesWithGenericLogFrame, frame3;

	public static void main(String[] args) {
		starApp();
	}

	/**
	 * Método que se encarga de los eventos de los botones para poner la información
	 * de entrada.
	 */

	public static void starApp() {
		panel = new CarvajalPanel(3);
		frame = new CarvajalFrame("Generador de Archivos FECO", panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		selectOption();
	}

	public static void selectOption() {
		panel.getAcceptOption().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				int option = panel.getSelectOption().getSelectedIndex();

				if (option == 0) {
					frame.setEnabled(false);
					panel1 = new CarvajalPanel(0);
					frame1 = new CarvajalFrame("Generador de Archivos FECO", panel1);
					frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame1.setSize(new Dimension(535, 350));
					frame1.setLocationRelativeTo(null);
					listenEntryData();
					listenOkGenerateFiles();
				} else if (option == 1) {
					frame.setEnabled(false);
					sendFilesWithGenericLogPanel = new CarvajalPanel(1);
					sendFilesWithGenericLogFrame = new CarvajalFrame("Generador de Archivos FECO", sendFilesWithGenericLogPanel);
					sendFilesWithGenericLogFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					sendFilesWithGenericLogFrame.setSize(new Dimension(480, 330));
					sendFilesWithGenericLogFrame.setLocationRelativeTo(null);
					listenSendFilesWithGenericLog();
				} else if (option == 2) {
					frame.setEnabled(false);
					panel3 = new CarvajalPanel(2);
					frame3 = new CarvajalFrame("Generador de Archivos FECO", panel3);
					frame3.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame3.setSize(new Dimension(480, 330));
					frame3.setLocationRelativeTo(null);
					listenSendFilesWithTestCasesLog();
				}
			}
		});
	}

	/**
	 * Método que escucha el botón aceptar de la funcionalidad de generar archivos.
	 */
	public static void listenOkGenerateFiles() {
		panel1.getAccept().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				if (!panel1.isValidInput()) {

					JOptionPane.showMessageDialog(null, "Las entradas no son válidas", "Entradas Inválidas",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				panel1.getAccept().setEnabled(false);
				panel1.getSelectFile().setEnabled(false);
				panel1.getConfigFile().setEnabled(false);
				panel1.getOutFilePath().setEnabled(false);
				panel1.getFilesPerDirectoryField().setEnabled(false);
				panel1.getBackMainPanel().setEnabled(false);

				String directoriesInFilePath = panel1.getFileLabel().getText();
				String directoriesConfiFilePath = panel1.getConfigFileLabel().getText();
				String directoriesOutFilePath = panel1.getOutFilePathLabel().getText();

				int filesPerDirectory = Integer.parseInt(panel1.getFilesPerDirectoryField().getText());

				try {
					FilesGenerator fg = new FilesGenerator(directoriesInFilePath, directoriesConfiFilePath,
							directoriesOutFilePath, filesPerDirectory);

					// Tarea pesada.
					@SuppressWarnings("rawtypes")
					final SwingWorker worker = new SwingWorker() {

						@Override
						protected Object doInBackground() throws Exception {
							if (!fg.generateTestFiles()) {

								JOptionPane.showMessageDialog(panel1, "Se presento un error", "Error",
										JOptionPane.ERROR_MESSAGE);

							}
							JOptionPane.showMessageDialog(panel1, "Proceso Finalizado con éxito", "Proceso Finalizado",
									JOptionPane.INFORMATION_MESSAGE);

							panel1.getAccept().setEnabled(true);
							panel1.getSelectFile().setEnabled(true);
							panel1.getConfigFile().setEnabled(true);
							panel1.getOutFilePath().setEnabled(true);
							panel1.getFilesPerDirectoryField().setEnabled(true);
							panel1.getBackMainPanel().setEnabled(true);

							boolean selectCompressOption = panel1.getSelectCompression().isSelected();

							if (selectCompressOption == true) {
								compressFiles();
							}
							return null;
						}
					};
					worker.execute();
					ProgressBarThread thread = new ProgressBarThread(panel1, fg);
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
		JFileChooser fileInFC = panel1.getFileFC();
		JFileChooser fileConfi = panel1.getFileConfiFC();
		JFileChooser directoryOut = panel1.getOutDirectoryFC();

		panel1.getSelectFile().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para seleccionar el archivo base (archivo plano o xml)

				FileNameExtensionFilter Filter = new FileNameExtensionFilter("xml & txt", "xml", "txt");
				fileInFC.setFileFilter(Filter);

				fileInFC.setFileSelectionMode(JFileChooser.FILES_ONLY);

				int option = fileInFC.showOpenDialog(panel1);
				if (option == JFileChooser.APPROVE_OPTION) {

					File fileName = fileInFC.getSelectedFile();
					String directoriesFilePath = (fileName.getAbsolutePath());
					panel1.getFileLabel().setText(directoriesFilePath);
					String nameFileSelect = fileName.getName();
					panel1.getFileViewLabel().setText(nameFileSelect);
				}

			}
		});

		panel1.getConfigFile().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para subir el archivo de configuración para la generación de los nuevos
				// archivos.

				FileNameExtensionFilter Filter = new FileNameExtensionFilter("txt", "txt");
				fileConfi.setFileFilter(Filter);
				fileConfi.setCurrentDirectory(fileInFC.getCurrentDirectory());
				fileConfi.setFileSelectionMode(JFileChooser.FILES_ONLY);

				int option = fileConfi.showOpenDialog(panel1);
				if (option == JFileChooser.APPROVE_OPTION) {

					File fileName = fileConfi.getSelectedFile();
					String confiFilePath = (fileName.getAbsolutePath());
					panel1.getConfigFileLabel().setText(confiFilePath);
					String nameConfiFileSelect = fileName.getName();
					panel1.getConfigViewFileLabel().setText(nameConfiFileSelect);
				}
			}
		});

		panel1.getOutFilePath().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para seleccionar la carpeta en la cual se guardaran los archivos
				// generados.

				directoryOut.setCurrentDirectory(fileConfi.getCurrentDirectory());
				directoryOut.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int option = directoryOut.showOpenDialog(panel1);
				if (option == JFileChooser.APPROVE_OPTION) {

					File fileName = directoryOut.getSelectedFile();
					String outDirectoryPath = (fileName.getAbsolutePath());
					panel1.getOutFilePathLabel().setText(outDirectoryPath);
					String nameOutPathSelect = fileName.getName();
					panel1.getOutViewFilePathLabel().setText(nameOutPathSelect);
				}
			}
		});

		panel1.getSelectCompression().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				boolean selectOptionZip = panel1.getSelectCompression().isSelected();

				if (selectOptionZip != false) {
					// panel1.getFilesPerZipLabel().setVisible(true);
					// panel1.getFilesPerZipField().setVisible(true);
					return;
				}
			}
		});

		panel1.getBackMainPanel().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Evento para regresar a la ventana principal
				frame.setEnabled(true);
				frame1.setVisible(false);
			}
		});
	}

	public static void listenSendFilesWithGenericLog() {

		JFileChooser directoryIn = sendFilesWithGenericLogPanel.getSelectSrcPathFC();
		JFileChooser connectionFile = sendFilesWithGenericLogPanel.getConnectionFileFC();

		sendFilesWithGenericLogPanel.getSelectSrcPath().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para seleccionar la carpeta de la cual se seleccionaran los archivos a
				// enviar.

				directoryIn.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				int option = directoryIn.showOpenDialog(sendFilesWithGenericLogPanel);
				if (option == JFileChooser.APPROVE_OPTION) {

					File fileName = directoryIn.getSelectedFile();
					String outDirectoryPath = (fileName.getAbsolutePath());
					sendFilesWithGenericLogPanel.getSelectSrcPathLabel().setText(outDirectoryPath);
					String nameSrcPath = fileName.getName();
					sendFilesWithGenericLogPanel.getSrcViewPathLabel().setText(nameSrcPath);
				}
			}
		});

		sendFilesWithGenericLogPanel.getSelectDBFile().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para seleccionar el archivo de conexión a la BD

				FileNameExtensionFilter Filter = new FileNameExtensionFilter("txt", "txt");
				connectionFile.setFileFilter(Filter);
				connectionFile.setCurrentDirectory(directoryIn.getCurrentDirectory());

				connectionFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int option = connectionFile.showOpenDialog(sendFilesWithGenericLogPanel);

				if (option == JFileChooser.APPROVE_OPTION) {
					File fileName = connectionFile.getSelectedFile();
					String connectionFilePath = (fileName.getAbsolutePath());
					sendFilesWithGenericLogPanel.getFileBDLabel().setText(connectionFilePath);
					String nameBDFile = fileName.getName();
					sendFilesWithGenericLogPanel.getFileViewBDLabel().setText(nameBDFile);
				}
			}
		});

		sendFilesWithGenericLogPanel.getBackMainPanel().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Evento para regresar a la ventana principal

				frame.setEnabled(true);
				sendFilesWithGenericLogFrame.setVisible(false);
			}
		});

		sendFilesWithGenericLogPanel.getSend().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (!sendFilesWithGenericLogPanel.isValidInputFileSend()) {

					JOptionPane.showMessageDialog(null, "Las entradas no son válidas", "Entradas Inválidas",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				sendFilesWithGenericLogPanel.getSend().setEnabled(false);
				sendFilesWithGenericLogPanel.getSelectSrcPath().setEnabled(false);
				sendFilesWithGenericLogPanel.getSelectDBFile().setEnabled(false);
				sendFilesWithGenericLogPanel.getBackMainPanel().setEnabled(false);

				SftpAndDbDataElement sftpDbDataElement = null;
				File srcPath = directoryIn.getSelectedFile();
				String inDirectoryPath = srcPath.getAbsolutePath();

				try {
					sftpDbDataElement = CarvajalUtils.loadConnectionsData(sendFilesWithGenericLogPanel.getFileBDLabel().getText());
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
								sendFilesWithGenericLogPanel.getGenerateLog().setEnabled(true);
								listenGenerateLog();
							} catch (JSchException | SftpException | IOException | ParseException
									| ParserConfigurationException | SAXException e1) {
								e1.printStackTrace();
								JOptionPane.showMessageDialog(null,
										"Se presentó un error. Revise los datos de conexión y\nsu conexión a la VPN.",
										"Error", JOptionPane.ERROR_MESSAGE);
							} finally {
								sendFilesWithGenericLogPanel.getSend().setEnabled(true);
								sendFilesWithGenericLogPanel.getSelectSrcPath().setEnabled(true);
								sendFilesWithGenericLogPanel.getSelectDBFile().setEnabled(true);
								sendFilesWithGenericLogPanel.getBackMainPanel().setEnabled(true);
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
					sendFilesWithGenericLogPanel.getSend().setEnabled(true);
					sendFilesWithGenericLogPanel.getSelectSrcPath().setEnabled(true);
					sendFilesWithGenericLogPanel.getSelectDBFile().setEnabled(true);
					sendFilesWithGenericLogPanel.getBackMainPanel().setEnabled(true);
				}
			}
		});

	}

	public static void listenSendFilesWithTestCasesLog() {

		JFileChooser directoryIn = panel3.getSelectSrcPathFC();
		JFileChooser fileConnection = panel3.getConnectionFileFC();

		panel3.getSelectSrcPath().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para seleccionar la carpeta de la cual se seleccionaran los archivos a
				// enviar.

				directoryIn.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int option = directoryIn.showOpenDialog(panel3);
				if (option == JFileChooser.APPROVE_OPTION) {

					File fileName = directoryIn.getSelectedFile();
					String outDirectoryPath = (fileName.getAbsolutePath());
					panel3.getSelectSrcPathLabel().setText(outDirectoryPath);
					String nameSrcPath = fileName.getName();
					panel3.getSrcViewPathLabel().setText(nameSrcPath);
				}
			}
		});

		panel3.getSelectDBFile().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para seleccionar el archivo de conexión a la BD

				FileNameExtensionFilter Filter = new FileNameExtensionFilter("txt", "txt");
				fileConnection.setFileFilter(Filter);
				fileConnection.setCurrentDirectory(directoryIn.getCurrentDirectory());

				fileConnection.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int option = fileConnection.showOpenDialog(panel3);

				if (option == JFileChooser.APPROVE_OPTION) {
					File fileName = fileConnection.getSelectedFile();
					String connectionFilePath = (fileName.getAbsolutePath());
					panel3.getFileBDLabel().setText(connectionFilePath);
					String nameBDFile = fileName.getName();
					panel3.getFileViewBDLabel().setText(nameBDFile);
				}
			}
		});

		panel3.getBackMainPanel().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Evento para regresar a la ventana principal

				frame3.setVisible(false);
				starApp();
			}
		});

		panel3.getSend().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (!panel3.isValidInputFileSend()) {

					JOptionPane.showMessageDialog(null, "Las entradas no son válidas", "Entradas Inválidas",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				panel3.getSend().setEnabled(false);
				panel3.getSelectSrcPath().setEnabled(false);
				panel3.getSelectDBFile().setEnabled(false);

				SftpAndDbDataElement sftpDbData = null;
				File srcPath = directoryIn.getSelectedFile();
				String inDirectoryPath = srcPath.getAbsolutePath();
				FilesSender files = null;

				try {
					sftpDbData = CarvajalUtils.loadConnectionsData(panel3.getFileBDLabel().getText());
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

				panel3.getSend().setEnabled(true);
				panel3.getSelectSrcPath().setEnabled(true);
				panel3.getSelectDBFile().setEnabled(true);
				panel3.getGenerateLog().setEnabled(true);
				validateTestCase();
			}
		});
	}

	public static void listenGenerateLog() {

		JFileChooser filesConnetion = sendFilesWithGenericLogPanel.getConnectionFileFC();
		JFileChooser sentFiles = sendFilesWithGenericLogPanel.getSelectSrcPathFC();

		sendFilesWithGenericLogPanel.getGenerateLog().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				File bdPathFile = filesConnetion.getSelectedFile();
				String directoryBDPath = (bdPathFile.getAbsolutePath());
				SftpAndDbDataElement sftpAndDbDataElement;

				try {
					sftpAndDbDataElement = CarvajalUtils.loadConnectionsData(directoryBDPath);
					PostgresConnector conn = new PostgresConnector(sftpAndDbDataElement.getUrlDb(), sftpAndDbDataElement.getUserDb(),
							sftpAndDbDataElement.getPasswordDb());
					File dir = sentFiles.getSelectedFile();

					// Obtener padre de dir
					BusinessValidator bv = new BusinessValidator(conn.getConnetion(sftpAndDbDataElement.getTipoBD()),
							dir.getParentFile().getAbsolutePath());
					if (dir.exists()) {
						File[] files = dir.listFiles();
						bv.setTotalItems(files.length);
						bv.setProcessedItems(0);
						
						sendFilesWithGenericLogPanel.getSend().setEnabled(false);
						sendFilesWithGenericLogPanel.getSelectSrcPath().setEnabled(false);
						sendFilesWithGenericLogPanel.getSelectDBFile().setEnabled(false);
						sendFilesWithGenericLogPanel.getBackMainPanel().setEnabled(false);
						
						//Tarea pesada.
						@SuppressWarnings("rawtypes")
						SwingWorker worker = new SwingWorker() {
							@Override
							protected Object doInBackground() throws Exception {
								for (int i = 0; i < files.length; i++) {
									bv.executeStatusQuery(files[i], directoryBDPath);
									bv.setProcessedItems(bv.getProcessedItems() + 1);
								}
								bv.getSummary();
								sendFilesWithGenericLogPanel.getSend().setEnabled(true);
								sendFilesWithGenericLogPanel.getSelectSrcPath().setEnabled(true);
								sendFilesWithGenericLogPanel.getSelectDBFile().setEnabled(true);
								sendFilesWithGenericLogPanel.getBackMainPanel().setEnabled(true);
								return null;
							}	
						};
						worker.execute();
						
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

	public static void validateTestCase() {

		JFileChooser filesConnetion = panel3.getConnectionFileFC();
		JFileChooser filesSending = panel3.getSelectSrcPathFC();

		panel3.getGenerateLog().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				File fileBDPath = filesConnetion.getSelectedFile();
				String directoryBDPath = (fileBDPath.getAbsolutePath());
				SftpAndDbDataElement sftpDbData;

				try {
					sftpDbData = CarvajalUtils.loadConnectionsData(directoryBDPath);
					PostgresConnector conn = new PostgresConnector(sftpDbData.getUrlDb(), sftpDbData.getUserDb(),
							sftpDbData.getPasswordDb());
					File fileName = filesSending.getSelectedFile();
					String srcExcel = (fileName.getAbsolutePath());
					File excel = new File(srcExcel);

					// Obtener padre de dir
					TestCaseValidator tc = new TestCaseValidator(conn.getConnetion(sftpDbData.getTipoBD()));
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

		JFileChooser directory = panel1.getOutDirectoryFC();
		File filesPath = directory.getSelectedFile();
		String directoryPath = filesPath.getAbsolutePath();
		String desFile = filesPath.getParentFile().getAbsolutePath();
		String nameFile = filesPath.getName();
		String desFileZip = desFile + "\\" + nameFile + ".zip";

		FilesCompressor compress = new FilesCompressor(directoryPath, desFileZip);
		compress.compressFiles(filesPath);

	}
}