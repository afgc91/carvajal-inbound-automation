package com.greensqa.automatizacion.carvajal.factura.sftp.control;

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

import com.greensqa.automatizacion.carvajal.factura.sftp.model.FilesGenerator;
import com.greensqa.automatizacion.carvajal.factura.sftp.view.CarvajalFrame;
import com.greensqa.automatizacion.carvajal.factura.sftp.view.CarvajalMainPanel;

public class CarvajalAutomationExe {

	private static CarvajalMainPanel panel;
	private static CarvajalFrame frame;

	public static void main(String[] args) {
		starApp();

	}

	/**
	 * Método que se encarga de los eventos de los botones para poner la información
	 * de entrada.
	 */

	public static void starApp() {
		panel = new CarvajalMainPanel();
		frame = new CarvajalFrame("Generador de Archivos FECO", panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		listenEntryData();
		listenOk();

	}

	public static void listenOk() {
		panel.getAccept().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				if (!panel.isValidInput()) {

					JOptionPane.showMessageDialog(null, "Las entradas no son válidas", "Entradas Inválidas",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				panel.getAccept().setEnabled(false);
				panel.getSelectFile().setEnabled(false);
				panel.getConfigFile().setEnabled(false);
				panel.getSelectDBFile().setEnabled(false);
				panel.getOutFilePath().setEnabled(false);
				panel.getFilesPerDirectoryField().setEnabled(false);

				String directoriesInFilePath = panel.getFileLabel().getText();
				String directoriesConfiFilePath = panel.getConfigFileLabel().getText();
//				String directoriesBDFilePath = panel.getFileBDLabel().getText();
				String directoriesOutFilePath = panel.getOutFilePathLabel().getText();

				int filesPerDirectory = Integer.parseInt(panel.getFilesPerDirectoryField().getText());

				try {
					FilesGenerator file = new FilesGenerator(directoriesInFilePath, directoriesConfiFilePath,
							directoriesOutFilePath, filesPerDirectory);

					if (!file.generateTestFiles()) {

						JOptionPane.showMessageDialog(panel, "Se presento un error", "Error",
								JOptionPane.ERROR_MESSAGE);

					}
					JOptionPane.showMessageDialog(panel, "Proceso Finalizado con éxito", "Proceso Finalizado",
							JOptionPane.INFORMATION_MESSAGE);

					panel.getAccept().setEnabled(true);
					panel.getSelectFile().setEnabled(true);
					panel.getConfigFile().setEnabled(true);
					panel.getSelectDBFile().setEnabled(true);
					panel.getOutFilePath().setEnabled(true);
					panel.getFilesPerDirectoryField().setEnabled(true);

				} catch (IOException | ParseException | java.text.ParseException | ParserConfigurationException
						| SAXException | HeadlessException | TransformerException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, "Se presentó un error " + e1.getMessage(), "Error",
							JOptionPane.ERROR);
				}

			}
		});

	}

	public static void listenEntryData() {
		JFileChooser fileInFC = panel.getFileFC();
		JFileChooser fileConfi = panel.getFileConfiFC();
		JFileChooser fileConnection = panel.getFileConnectionFC();
		JFileChooser directoryOut = panel.getOutDirectoryFC();

		panel.getSelectFile().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para seleccionar el archivo base (archivo plano o xml)

				FileNameExtensionFilter Filter = new FileNameExtensionFilter("xml & txt", "xml", "txt");
				fileInFC.setFileFilter(Filter);

				fileInFC.setFileSelectionMode(JFileChooser.FILES_ONLY);

				int option = fileInFC.showOpenDialog(panel);
				if (option == JFileChooser.APPROVE_OPTION) {

					File fileName = fileInFC.getSelectedFile();
					String directoriesFilePath = (fileName.getAbsolutePath());
					panel.getFileLabel().setText(directoriesFilePath);

				}

			}
		});

		panel.getConfigFile().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para subir el archivo de configuración para la generación de los nuevos
				// archivos.

				FileNameExtensionFilter Filter = new FileNameExtensionFilter("txt", "txt");
				fileConfi.setFileFilter(Filter);
				fileConfi.setCurrentDirectory(fileInFC.getCurrentDirectory());
				fileConfi.setFileSelectionMode(JFileChooser.FILES_ONLY);

				int option = fileConfi.showOpenDialog(panel);
				if (option == JFileChooser.APPROVE_OPTION) {

					File fileName = fileConfi.getSelectedFile();
					String confiFilePath = (fileName.getAbsolutePath());
					panel.getConfigFileLabel().setText(confiFilePath);

				}
			}
		});

		panel.getSelectDBFile().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para seleccionar el archivo de conexión a la BD

				FileNameExtensionFilter Filter = new FileNameExtensionFilter("txt", "txt");
				fileConnection.setFileFilter(Filter);
				fileConnection.setCurrentDirectory(fileConfi.getCurrentDirectory());

				fileConnection.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int option = fileConnection.showOpenDialog(panel);
				if (option == JFileChooser.APPROVE_OPTION) {

					File fileName = fileConnection.getSelectedFile();
					String connectionFilePath = (fileName.getAbsolutePath());
					panel.getFileBDLabel().setText(connectionFilePath);

				}
			}
		});

		panel.getOutFilePath().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para seleccionar la carpeta en la cual se guardaran los archivos
				// generados.

				directoryOut.setCurrentDirectory(fileConnection.getCurrentDirectory());
				directoryOut.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int option = directoryOut.showOpenDialog(panel);
				if (option == JFileChooser.APPROVE_OPTION) {

					File fileName = directoryOut.getSelectedFile();
					String outDirectoryPath = (fileName.getAbsolutePath());
					panel.getOutFilePathLabel().setText(outDirectoryPath);

				}
			}
		});

	}

}
