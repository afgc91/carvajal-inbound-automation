package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {

	private static Workbook workbook;

	// Obtiene el valor de una celda con la posicion de la fila y la columna
	public static ArrayList<String> getValueFieldPosition(String rutaArchivoExcel, int colIndex) throws IOException {

		ArrayList<String> list = new ArrayList<String>();
		int filaActual = 1;

		FileInputStream inputStream = new FileInputStream(new File(rutaArchivoExcel));
		workbook = new XSSFWorkbook(inputStream);
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