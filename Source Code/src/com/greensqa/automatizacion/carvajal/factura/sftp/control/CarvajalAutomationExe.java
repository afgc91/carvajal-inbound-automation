package com.greensqa.automatizacion.carvajal.factura.sftp.control;

import javax.swing.JFrame;

import com.greensqa.automatizacion.carvajal.factura.sftp.view.CarvajalFrame;
import com.greensqa.automatizacion.carvajal.factura.sftp.view.CarvajalMainPanel;

public class CarvajalAutomationExe {

	public static void main(String[] args) {
		CarvajalMainPanel panel = new CarvajalMainPanel(); 
		CarvajalFrame ventana = new CarvajalFrame("FECO", panel);
		ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
