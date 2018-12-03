package com.carvajal.facturaclaro.test;

import javax.swing.JOptionPane;

import com.carvajal.facturaclaro.utils.PATH;

public class TestExe {

	public static void main(String[] args) {
		startApp();
	}
	
	private static void startApp() {
		String inputFilePath = JOptionPane.showInputDialog("Ruta del archivo de configuración");
		if (inputFilePath.equals("")) {
			inputFilePath = "C:\\Users\\dvalencia\\Documents\\Test FECO\\Configuraciones\\dataWS.xls";
		}
		PATH.DATA_POOL= inputFilePath;
		CP_1_Emision_Factura_Retencion_Test test = new CP_1_Emision_Factura_Retencion_Test();
		try {
			test.emisionFacturaRetencion();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
