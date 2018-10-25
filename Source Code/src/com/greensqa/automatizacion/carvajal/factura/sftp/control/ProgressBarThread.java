package com.greensqa.automatizacion.carvajal.factura.sftp.control;

import javax.swing.JOptionPane;

import com.greensqa.automatizacion.carvajal.factura.sftp.model.FilesGenerator;
import com.greensqa.automatizacion.carvajal.factura.sftp.view.CarvajalPanel;

public class ProgressBarThread extends Thread {
	private CarvajalPanel panel;
	private FilesGenerator fg;
	private int type;

	public ProgressBarThread(CarvajalPanel panel, FilesGenerator fg) {
		this.panel = panel;
		this.fg = fg;
		this.type = 1;
	}

	public void run() {
		switch (type) {
		case 1: {
			while (fg.getGeneratedFiles() < fg.getFilesNum()) {
				// Actualizar la barrita de progreso para generación de archivos.
				panel.getProgressBar().setValue((int) (fg.getGeneratedFiles() * 100 / fg.getFilesNum()));
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Se presentó un error " + e.getMessage(), "Error",
							JOptionPane.ERROR);
				}
			}
			panel.getProgressBar().setValue(100);
			break;
		}
		case 2:
			
		}
	}
}
