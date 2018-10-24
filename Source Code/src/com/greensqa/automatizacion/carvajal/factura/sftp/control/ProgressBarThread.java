package com.greensqa.automatizacion.carvajal.factura.sftp.control;

import com.greensqa.automatizacion.carvajal.factura.sftp.model.FilesGenerator;
import com.greensqa.automatizacion.carvajal.factura.sftp.view.CarvajalPanel;

public class ProgressBarThread extends Thread {
	private CarvajalPanel panel;
	private FilesGenerator fg;
	
	public ProgressBarThread(CarvajalPanel panel, FilesGenerator fg) {
		this.panel = panel;
		this.fg = fg;
	}
	
	public void run() {
		while (fg.getGeneratedFiles() < fg.getFilesNum()) {
			//Actualizar la barrita de progreso.
			panel.getProgressBarFilesGeneration().setValue((int) (fg.getGeneratedFiles() * 100 / fg.getFilesNum()));
		}
		panel.getProgressBarFilesGeneration().setValue(100);
	}
}
