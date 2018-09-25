package com.greensqa.automatizacion.carvajal.factura.sftp.control;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import com.greensqa.automatizacion.carvajal.factura.sftp.model.CarvajalUtils;
import com.greensqa.automatizacion.carvajal.factura.sftp.model.FilesGenerator;
import com.greensqa.automatizacion.carvajal.factura.sftp.model.SftpAndDbData;
import com.greensqa.automatizacion.carvajal.factura.sftp.model.SftpFilesSender;
import com.greensqa.automatizacion.carvajal.factura.sftp.view.CarvajalFrame;
import com.greensqa.automatizacion.carvajal.factura.sftp.view.CarvajalMainPanel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.sun.javafx.scene.traversal.Algorithm;

public class CarvajalAutomationExe {

	private static CarvajalMainPanel panel, panel1, panel2;
	private static CarvajalFrame frame, frame1, frame2;

	public static void main(String[] args) {
		starApp();

	}

	/**
	 * Método que se encarga de los eventos de los botones para poner la información
	 * de entrada.
	 */

	public static void starApp() {
		panel = new CarvajalMainPanel(2);
		frame = new CarvajalFrame("Generador de Archivos FECO", panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// sendingFile();
		selectOption();

	}

	public static void selectOption() {
		panel.getAcceptOption().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				int option = panel.getSelectOption().getSelectedIndex();

				if (option == 0) {

					frame.setVisible(false);
					panel1 = new CarvajalMainPanel(0);
					frame1 = new CarvajalFrame("Generador de Archivos FECO", panel1);
					frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame1.setSize(new Dimension(535, 310));
					listenEntryData();
					listenOk();
				}

				if (option == 1) {

					frame.setVisible(false);
					panel.setVisible(false);
					panel2 = new CarvajalMainPanel(1);
					frame2 = new CarvajalFrame("Generador de Archivos FECO", panel2);
					frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame2.setSize(new Dimension(480, 280));
					sendingFile();
				}
			}
		});

	}

	public static void listenOk() {
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

				String directoriesInFilePath = panel1.getFileLabel().getText();
				String directoriesConfiFilePath = panel1.getConfigFileLabel().getText();
				String directoriesOutFilePath = panel1.getOutFilePathLabel().getText();

				int filesPerDirectory = Integer.parseInt(panel1.getFilesPerDirectoryField().getText());

				try {
					FilesGenerator file = new FilesGenerator(directoriesInFilePath, directoriesConfiFilePath,
							directoriesOutFilePath, filesPerDirectory);

					if (!file.generateTestFiles()) {

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

				} catch (IOException | ParseException | java.text.ParseException | ParserConfigurationException
						| SAXException | HeadlessException | TransformerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Se presentó un error " + e1.getMessage(), "Error",
							JOptionPane.ERROR);
				}

			}
		});

		panel1.getSelectCompression().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				boolean selectOptionZip = panel1.getSelectCompression().isSelected();

				if (selectOptionZip != false) {
					panel1.getFilesPerZipLabel().setVisible(true);
					panel1.getFilesPerZipField().setVisible(true);
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
	}

	public static void sendingFile() {

		JFileChooser directoryIn = panel2.getSelectSrcPathFC();
		JFileChooser fileConnection = panel2.getFileConnectionFC();

		panel2.getSelectSrcPath().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para seleccionar la carpeta de la cual se seleccionaran los archivos a
				// enviar.

				directoryIn.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int option = directoryIn.showOpenDialog(panel2);
				if (option == JFileChooser.APPROVE_OPTION) {

					File fileName = directoryIn.getSelectedFile();
					String outDirectoryPath = (fileName.getAbsolutePath());
					panel2.getSelectSrcPathLabel().setText(outDirectoryPath);
					String nameSrcPath = fileName.getName();
					panel2.getSrcViewPathLabel().setText(nameSrcPath);
				}
			}
		});

		panel2.getSelectDBFile().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para seleccionar el archivo de conexión a la BD

				FileNameExtensionFilter Filter = new FileNameExtensionFilter("txt", "txt");
				fileConnection.setFileFilter(Filter);
				fileConnection.setCurrentDirectory(directoryIn.getCurrentDirectory());

				fileConnection.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int option = fileConnection.showOpenDialog(panel2);

				if (option == JFileChooser.APPROVE_OPTION) {
					File fileName = fileConnection.getSelectedFile();
					String connectionFilePath = (fileName.getAbsolutePath());
					panel2.getFileBDLabel().setText(connectionFilePath);
					String nameBDFile = fileName.getName();
					panel2.getFileViewBDLabel().setText(nameBDFile);
				}
			}
		});

		panel2.getSend().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (!panel2.isValidInputFileSend()) {

					JOptionPane.showMessageDialog(null, "Las entradas no son válidas", "Entradas Inválidas",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				panel2.getSend().setEnabled(false);
				panel2.getSelectSrcPath().setEnabled(false);
				panel2.getSelectDBFile().setEnabled(false);

				SftpAndDbData sftpDbData = null;
				File srcPath = directoryIn.getSelectedFile();
				String inDirectoryPath = (srcPath.getAbsolutePath());
				SftpFilesSender files = null;

				try {
					sftpDbData = CarvajalUtils.loadConnectionsData(panel2.getFileBDLabel().getText());
				} catch (IOException | ParseException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				if (sftpDbData != null) {
					files = new SftpFilesSender(inDirectoryPath, sftpDbData.getDestSftp(), sftpDbData.getUserSftp(),
							sftpDbData.getPasswordSftp(), sftpDbData.getUrlSftp(), sftpDbData.getPortSftp());
				}				

				try {
					files.sendSftpFiles();
					JOptionPane.showMessageDialog(null, "Archivos enviados con éxito", "Envío exitoso",
							JOptionPane.INFORMATION_MESSAGE);
					panel2.getSend().setEnabled(true);
					panel2.getSelectSrcPath().setEnabled(true);
					panel2.getSelectDBFile().setEnabled(true);
				} catch (JSchException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Los Datos de conexión son Inválidos", "Datos Inválidos",
							JOptionPane.ERROR_MESSAGE);
				} catch (SftpException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "No se pudieron enviar los archivos seleccionados",
							"Archivos No Enviados", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

	}

}