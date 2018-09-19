package com.greensqa.automatizacion.carvajal.factura.sftp.view;

import java.awt.Dimension;

import javax.swing.JFrame;

import javafx.scene.paint.Color;


public class CarvajalFrame extends JFrame {


	private static final long serialVersionUID = 1L;
	
	private CarvajalMainPanel panel;

	public CarvajalFrame(String name, CarvajalMainPanel panel) {
		super(name);
		this.setPanel(panel);
		this.add(panel);
		this.setResizable(false);
		this.setSize(new Dimension(545, 320));
		this.setLocationRelativeTo(null);
		this.setVisible(true);

	}

	public CarvajalMainPanel getPanel() {
		return panel;
	}

	public void setPanel(CarvajalMainPanel panel) {
		this.panel = panel;
	}

}
