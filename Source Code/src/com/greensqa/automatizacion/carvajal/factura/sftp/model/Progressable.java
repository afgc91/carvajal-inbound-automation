package com.greensqa.automatizacion.carvajal.factura.sftp.model;

/**
 * Interfaz que clasifica a objetos a los cuales se les puede hacer una barra de
 * progreso desde la interfaz gr�fica de usuario.
 * 
 * @author Andr�s Fernando Gasca Cruz
 *
 */
public interface Progressable {

	public int getTotalItems();
	
	public int getProcessedItems();
}
