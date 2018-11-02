package com.greensqa.automatizacion.carvajal.factura.sftp.control;

import javax.swing.JOptionPane;

import com.greensqa.automatizacion.carvajal.factura.sftp.model.BusinessValidator;
import com.greensqa.automatizacion.carvajal.factura.sftp.model.FilesGenerator;
import com.greensqa.automatizacion.carvajal.factura.sftp.model.FilesSender;
import com.greensqa.automatizacion.carvajal.factura.sftp.model.Progressable;
import com.greensqa.automatizacion.carvajal.factura.sftp.view.CarvajalPanel;

public class ProgressBarThread extends Thread {
	private CarvajalPanel panel;
	private FilesGenerator fg;
	private FilesSender fs;
	private BusinessValidator bv;
	private int type;

	public ProgressBarThread(CarvajalPanel panel, FilesGenerator fg) {
		this.panel = panel;
		this.fg = fg;
		this.type = 1;
	}

	public ProgressBarThread(CarvajalPanel panel, FilesSender fs) {
		this.panel = panel;
		this.fs = fs;
		this.type = 2;
	}
	
	public ProgressBarThread(CarvajalPanel panel, BusinessValidator bv) {
		this.panel = panel;
		this.bv = bv;
		this.type = 3;
	}

	public void run() {
		switch (type) {
		case 1:
			panel.getProgressBar().setValue(0);
			updateProgressBar(fg);
			panel.getProgressBar().setValue(100);
			break;
		case 2:
			panel.getProgressBar().setValue(0);
			updateProgressBar(fs);
			panel.getProgressBar().setValue(100);
			break;
		case 3:
			panel.getProgressBar().setValue(0);
			updateProgressBar(bv);
			panel.getProgressBar().setValue(100);
			break;
		}
	}

	/**
	 * Permite actualizar la barrita de progreso a objetos que implementan la Interface Progressable.
	 * @param progressBarObj Objeto que implementa la Interface Progressable.
	 */
	private void updateProgressBar(Progressable progressBarObj) {
		while (progressBarObj.getProcessedItems() < progressBarObj.getTotalItems()) {
			// Actualizar barra de progreso
			panel.getProgressBar()
					.setValue((int) (progressBarObj.getProcessedItems() * 100 / progressBarObj.getTotalItems()));
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Se presentó un error " + e.getMessage(), "Error",
						JOptionPane.ERROR);
			}
		}
	}
	
	public void setType(int type) {
		this.type = type;
	}
}
