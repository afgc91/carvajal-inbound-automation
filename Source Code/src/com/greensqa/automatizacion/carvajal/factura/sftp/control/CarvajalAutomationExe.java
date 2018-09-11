package com.greensqa.automatizacion.carvajal.factura.sftp.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

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
	
	public static void starApp(){
		panel = new CarvajalMainPanel();
		frame = new CarvajalFrame("Generador de Archivos FECO", panel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		listenEntryData();	
		
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
					String getFileName = fileName.getName();
					panel.getFileLabel().setText(getFileName);
					System.out.println(directoriesFilePath);

				}
				
			}
		});
		
		panel.getConfigFile().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para subir el archivo de configuración para la generación de los nuevos archivos. 
				
				FileNameExtensionFilter Filter = new FileNameExtensionFilter("txt", "txt");
				fileConfi.setFileFilter(Filter);
				
				fileConfi.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int option = fileConfi.showOpenDialog(panel); 
				if (option == JFileChooser.APPROVE_OPTION) {
					
					
					File fileName = fileConfi.getSelectedFile(); 
					String confiFilePath = (fileName.getAbsolutePath());
					String getFileConfiName = fileName.getName();
					panel.getConfigFileLabel().setText(getFileConfiName);
					System.out.println(confiFilePath);
					
				}	
			}
		});
		
		panel.getSelectDBFile().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para seleccionar el archivo de conexión a la BD
				
				FileNameExtensionFilter Filter = new FileNameExtensionFilter("txt", "txt");
				fileConnection.setFileFilter(Filter);
				
				fileConnection.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int option = fileConnection.showOpenDialog(panel); 
				if(option== JFileChooser.APPROVE_OPTION) {
					
					File fileName = fileConnection.getSelectedFile(); 		
					String connectionFilePath = (fileName.getAbsolutePath());
					String getFileInName = fileName.getName();
					panel.getFileBDLabel().setText(getFileInName);
					System.out.println(connectionFilePath);
				}
			}
		});
		
		panel.getOutFilePath().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// Botón para seleccionar la carpeta en la cual se guardaran los archivos generados. 
				
				directoryOut.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int option = directoryOut.showOpenDialog(panel); 
				if(option==JFileChooser.APPROVE_OPTION) {
					
					File fileName = directoryOut.getSelectedFile(); 
					String outDirectoryPath = (fileName.getAbsolutePath());
					String getDirectoryName = fileName.getName();
					panel.getOutFilePathLabel().setText(getDirectoryName);
					System.out.println(outDirectoryPath);	
					
				}
			}
		});
		

	}

}
