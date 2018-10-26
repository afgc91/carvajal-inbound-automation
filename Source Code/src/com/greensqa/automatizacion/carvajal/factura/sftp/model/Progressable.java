package com.greensqa.automatizacion.carvajal.factura.sftp.model;

/**
 * Interfaz que clasifica a objetos a los cuales se les puede hacer una barra de
 * progreso desde la interfaz gráfica de usuario.
 * 
 * @author Andrés Fernando Gasca Cruz
 *
 */
public interface Progressable {

	public int getTotalItems();
	
	public int getProcessedItems();
}
