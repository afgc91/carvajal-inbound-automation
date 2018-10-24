package com.greensqa.automatizacion.carvajal.factura.sftp.view;

import java.awt.Dimension;

import javax.swing.JFrame;


public class CarvajalFrame extends JFrame {


	private static final long serialVersionUID = 1L;
	
	private CarvajalPanel panel;

	public CarvajalFrame(String name, CarvajalPanel panel) {
		super(name);
		this.setPanel(panel);
		this.add(panel);
		this.setResizable(false);
		this.setSize(new Dimension(400, 200));
		this.setLocationRelativeTo(null);
		this.setVisible(true);

	}

	public CarvajalPanel getPanel() {
		return panel;
	}

	public void setPanel(CarvajalPanel panel) {
		this.panel = panel;
	}

}
