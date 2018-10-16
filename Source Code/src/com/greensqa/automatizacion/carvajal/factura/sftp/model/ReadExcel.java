package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadExcel {

//	public static void main(String[] a) throws Exception {
//		Workbook wb2;
//		File file = new File("C:\\Users\\dvalencia\\Documents\\ProyectoFECO\\DatosPruebas\\data1.xlsx");
//
//		if (file.exists()) {
//			ReadExcel.getValueFieldPosition("C:\\Users\\dvalencia\\Documents\\ProyectoFECO\\DatosPruebas\\data1.xlsx",
//					1);
//			// TODO Auto-generated constructor stub
//		}
//	}

	// Obtiene el valor de una celda con la posicion de la fila y la columna
	public static ArrayList<String> getValueFieldPosition(String rutaArchivoExcel, int colIndex) throws IOException {

		ArrayList<String> list = new ArrayList<String>();
		int filaActual = 1;

		FileInputStream inputStream = new FileInputStream(new File(rutaArchivoExcel));
		Workbook workbook = new XSSFWorkbook(inputStream);
		Sheet firstSheet = workbook.getSheetAt(0);
		int numFilas = firstSheet.getLastRowNum();

		while (filaActual <= numFilas) {
			Row rowActual = firstSheet.getRow(filaActual);
			Cell cell = rowActual.getCell(colIndex);
			String value = cell.toString();			
			list.add(value);
			filaActual += 1;
		}
		System.out.println(list);
		return list;
	}
}