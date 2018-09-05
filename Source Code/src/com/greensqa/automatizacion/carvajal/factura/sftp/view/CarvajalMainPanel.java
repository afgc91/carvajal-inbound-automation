package com.greensqa.automatizacion.carvajal.factura.sftp.view;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CarvajalMainPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Etiqueta para mostrar el archivo seleccionado.
	 */

	private JLabel fileLabel;

	/**
	 * Botón para seleccionar Archivo de Entrada
	 */

	private JButton selectFile;

	/**
	 * Botón para inciar la ejecución
	 */

	private JButton accept;

	/**
	 * Texto mostrado cuando no se ha seleccionado ningún archivo
	 */

	private static final String DIR_SELECTED_DEFAULT = "<<Seleccione archivo...>>";

	public CarvajalMainPanel() {
		this.setLayout(null);
		initializeComponents();
	}

	private void initializeComponents() {

		fileLabel = new JLabel(DIR_SELECTED_DEFAULT);
		JButton selectFile = new JButton("Seleccionar Archivo de Entrada");
		JButton accept = new JButton("Aceptar");

		int widthLabel = 1000, heightLabel = 100;

		fileLabel.setSize(widthLabel, heightLabel);
		selectFile.setSize(selectFile.getPreferredSize());
		accept.setSize(accept.getPreferredSize());

		int x = 30, y = 30, d = 40;

		selectFile.setLocation(x, y);
		x += selectFile.getWidth() + d;
		y = -4;
		fileLabel.setLocation(x, y);
		y += d + 50;
		x -= 50;
		accept.setLocation(x, y);

		this.add(selectFile);
		this.add(fileLabel);
		this.add(accept);

	}

	public boolean isValidInput() {
		boolean directoriesInFlag = !fileLabel.getText().equals(DIR_SELECTED_DEFAULT);
		return directoriesInFlag;
	}

	public JLabel getFileLabel() {
		return fileLabel;
	}

	public void setFileLabel(JLabel fileLabel) {
		this.fileLabel = fileLabel;
	}

	public JButton getSelectFile() {
		return selectFile;
	}

	public void setSelectFile(JButton selectFile) {
		this.selectFile = selectFile;
	}

	public JButton getAccept() {
		return accept;
	}

	public void setAccept(JButton accept) {
		this.accept = accept;
	}

	public static String getDirSelectedDefault() {
		return DIR_SELECTED_DEFAULT;
	}

}
