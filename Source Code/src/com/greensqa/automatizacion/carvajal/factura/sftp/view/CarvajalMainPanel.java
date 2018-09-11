package com.greensqa.automatizacion.carvajal.factura.sftp.view;


import javax.swing.ImageIcon;
import javax.swing.JButton;


import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class CarvajalMainPanel extends JPanel{

	private static final long serialVersionUID = 1L;

	/**
	 * Etiqueta para mostrar el archivo seleccionado Insumo en txt o xml.
	 */

	private JLabel fileLabel;

	/**
	 * Botón para seleccionar Archivo de Entrada - Insumo en txt o xml.
	 */

	private JButton selectFile;

	/**
	 * Etiqueta para mostrar el archivo de configuración seleccionado.
	 **/

	private JLabel configFileLabel;

	/**
	 * Botón para seleccionar el archivo de configuración de los archivos de salida.
	 */

	private JButton configFile;

	/**
	 * Etiqueta para mostrar el archivo con los datos de entrada seleccionado.
	 */
	private JButton selectDBFile;

	/**
	 * Etiqueta de la cantidad de archivos por folder.
	 */

	private JLabel fileBDLabel;

	/**
	 * Etiqueta para mostrar la ruta seleccionada.
	 **/

	private JLabel outFilePathLabel;

	/**
	 * Botón para seleccionar la ruta en la cual se guardaran los archivos
	 * generados.
	 **/

	private JButton outFilePath;

	/**
	 * Etiqueta de la cantidad de archivos por folder.
	 */
	private JLabel filesPerDirectoryLabel;

	/**
	 * Campo para ingresar número de archivos por folder;
	 */
	private JTextField filesPerDirectoryField;

	/**
	 * Imagen GreenSQA
	 */
	private JLabel image;

	/**
	 * Botón para inciar la ejecución
	 */
	
	private JButton accept;
	
	/**
	 * Seleccionador de archivo de entrada plano o xml. 
	 */	
	private JFileChooser fileFC; 
	
	/**
	 * Seleccionador de archivo de configuración de los nuevos archivos.
	 */
	private JFileChooser fileConfiFC; 
	/**
	 * Seleccionador de archivo para conexión a la BD.
	 */
	private JFileChooser fileConnectionFC; 
	/**
	 * Seleccionador de Carpeta de salida para los archivos generados. 
	 */
	private JFileChooser outDirectoryFC; 
	
	
	/**
	 * Texto mostrado cuando no se ha seleccionado ningún archivo
	 */

	private static final String DIR_SELECTED_DEFAULT = "<<Seleccione archivo...>>";

	public CarvajalMainPanel() {
		this.setLayout(null);
		initializeComponents();
	}

	/**
	 * Se crean y se ubican los elementos de la ventana
	 **/

	private void initializeComponents() {
		

		fileLabel = new JLabel(DIR_SELECTED_DEFAULT);
		selectFile = new JButton("Seleccionar Archivo de Entrada");
		selectFile.setToolTipText("Seleccionar Insumo de entrada plano o xml");
		configFileLabel = new JLabel(DIR_SELECTED_DEFAULT);
		configFile = new JButton("Seleccionar Datos Configuración");
		configFile.setToolTipText("Seleccionar el archivo con datos de configuración de los archivos de salida");
		selectDBFile = new JButton("Seleccionar Datos Conexión");
		fileBDLabel = new JLabel(DIR_SELECTED_DEFAULT);
		selectDBFile.setToolTipText("Seleccionar Archivo con Datos de conexión a la BD");
		outFilePathLabel = new JLabel("<<Seleccione carpeta...>>");
		outFilePath = new JButton("Seleccionar Carpeta de Salida");
		outFilePath.setToolTipText("Seleccionar la ruta en la cual se guardaran los archivos generados");
		filesPerDirectoryLabel = new JLabel("No. de Archivos a generar");
		filesPerDirectoryField = new JTextField("");
		fileFC = new JFileChooser(); 
		fileConfiFC = new JFileChooser();
		fileConnectionFC = new JFileChooser(); 
		outDirectoryFC = new JFileChooser(); 
		image = new JLabel(
				(new ImageIcon("src/com/greensqa/automatizacion/carvajal/factura/sftp/resources/greenSQA.png")));
		JButton accept = new JButton("Aceptar");

		int widthLabel = 1000, heightLabel = 100;

		fileLabel.setSize(widthLabel, heightLabel);
		configFileLabel.setSize(widthLabel, heightLabel);
		fileBDLabel.setSize(widthLabel, heightLabel);
		outFilePathLabel.setSize(widthLabel, heightLabel);
		filesPerDirectoryLabel.setSize(widthLabel, heightLabel);
		selectFile.setSize(selectFile.getPreferredSize());
		configFile.setSize(selectFile.getPreferredSize());
		selectDBFile.setSize(selectFile.getPreferredSize());
		outFilePath.setSize(selectFile.getPreferredSize());
		filesPerDirectoryField.setSize(50, 20);
		accept.setSize(accept.getPreferredSize());
		image.setSize(image.getPreferredSize());

		int x = 30, y = 30, d = 40;
		selectFile.setLocation(x, y);

		x += selectFile.getWidth() + d;
		y = -4;
		fileLabel.setLocation(x, y);

		x = 30;
		y = 70;
		configFile.setLocation(x, y);

		x += configFile.getWidth() + d;
		y = 30;
		configFileLabel.setLocation(x, y);

		x = 30;
		y = 110;
		selectDBFile.setLocation(x, y);

		x += selectDBFile.getWidth() + d;
		y = 70;
		fileBDLabel.setLocation(x, y);

		x = 30;
		y = 150;
		outFilePath.setLocation(x, y);

		x += outFilePath.getWidth() + d;
		y = 115;
		outFilePathLabel.setLocation(x, y);

		x = 30;
		y = 145;
		filesPerDirectoryLabel.setLocation(x, y);

		x += 160;
		y = 185;
		filesPerDirectoryField.setLocation(x, y);

		x = 230;
		y = 220;
		accept.setLocation(x, y);

		x = 365;
		y = 220;
		image.setLocation(x, y);

	
		this.add(selectFile);
		this.add(fileLabel);
		this.add(configFile);
		this.add(configFileLabel);
		this.add(selectDBFile);
		this.add(fileBDLabel);
		this.add(outFilePath);
		this.add(outFilePathLabel);
		this.add(filesPerDirectoryLabel);
		this.add(filesPerDirectoryField);
		this.add(accept);
		this.add(image);

	}
	
	/**
	 * Etiqueta para mostrar el archivo seleccionado Insumo en txt o xml.
	 */

	public JLabel getFileLabel() {
		return fileLabel;
	}
	
	/**
	 * Etiqueta para mostrar el archivo seleccionado Insumo en txt o xml.
	 */

	public void setFileLabel(JLabel fileLabel) {
		this.fileLabel = fileLabel;
	}

	/**
	 * Botón para seleccionar Archivo de Entrada - Insumo en txt o xml.
	 */
	
	public JButton getSelectFile() {
		return selectFile;
	}

	/**
	 * Botón para seleccionar Archivo de Entrada - Insumo en txt o xml.
	 */
	
	public void setSelectFile(JButton selectFile) {
		this.selectFile = selectFile;
	}


	/**
	 * Botón para inciar la ejecución
	 */
	public JButton getAccept() {
		return accept;
	}

	/**
	 * Botón para inciar la ejecución
	 */

	public void setAccept(JButton accept) {
		this.accept = accept;
	}

	public static String getDirSelectedDefault() {
		return DIR_SELECTED_DEFAULT;
	}

	/**
	 * Etiqueta de la cantidad de archivos por folder.
	 */

	public JLabel getFilesPerDirectoryLabel() {
		return filesPerDirectoryLabel;
	}

	/**
	 * Etiqueta de la cantidad de archivos por folder.
	 */
	public void setFilesPerDirectoryLabel(JLabel filesPerDirectoryLabel) {
		this.filesPerDirectoryLabel = filesPerDirectoryLabel;
	}

	/**
	 * Campo para ingresar la cantidad de archivos por folder.
	 */
	public JTextField getFilesPerDirectoryField() {
		return filesPerDirectoryField;
	}
	
	/**
	 * Campo para ingresar la cantidad de archivos por folder.
	 */

	public void setFilesPerDirectoryField(JTextField filesPerDirectoryField) {
		this.filesPerDirectoryField = filesPerDirectoryField;
	}

	/**
	 * Logo GreenSQA 
	**/
	public JLabel getImage() {
		return image;
	}
	
	/**
	 * Logo GreenSQA 
	**/

	public void setImage(JLabel image) {
		this.image = image;
	}
	
	/**
	 * Etiqueta para mostrar el archivo con los datos de entrada seleccionado.
	 */

	public JButton getSelectDBFile() {
		return selectDBFile;
	}
	
	/**
	 * Etiqueta para mostrar el archivo con los datos de entrada seleccionado.
	 */

	public void setSelectDBFile(JButton selectDBFile) {
		this.selectDBFile = selectDBFile;
	}
	
	/**
	 * Etiqueta de la cantidad de archivos por folder.
	 */


	public JLabel getFileBDLabel() {
		return fileBDLabel;
	}
	
	/**
	 * Etiqueta de la cantidad de archivos por folder.
	 */


	public void setFileBDLabel(JLabel fileBDLabel) {
		this.fileBDLabel = fileBDLabel;
	}

	/**
	 * Etiqueta para mostrar el archivo de configuración seleccionado.
	 **/

	public JLabel getConfigFileLabel() {
		return configFileLabel;
	}

	/**
	 * Etiqueta para mostrar el archivo de configuración seleccionado.
	 **/

	public void setConfigFileLabel(JLabel configFileLabel) {
		this.configFileLabel = configFileLabel;
	}

	/**
	 * Botón para seleccionar el archivo de configuración de los archivos de salida.
	 */
	public JButton getConfigFile() {
		return configFile;
	}
	/**
	 * Botón para seleccionar el archivo de configuración de los archivos de salida.
	 */

	public void setConfigFile(JButton configFile) {
		this.configFile = configFile;
	}
	
	/**
	 * Etiqueta para mostrar la ruta seleccionada.
	 **/

	public JLabel getOutFilePathLabel() {
		return outFilePathLabel;
	}
	
	/**
	 * Etiqueta para mostrar la ruta seleccionada.
	 **/

	public void setOutFilePathLabel(JLabel outFilePathLabel) {
		this.outFilePathLabel = outFilePathLabel;
	}
	
	/**
	 * Botón para seleccionar la ruta en la cual se guardaran los archivos
	 * generados.
	 **/

	public JButton getOutFilePath() {
		return outFilePath;
	}
	
	/**
	 * Botón para seleccionar la ruta en la cual se guardaran los archivos
	 * generados.
	 **/

	public void setOutFilePath(JButton outFilePath) {
		this.outFilePath = outFilePath;
	}
	
	
	/**
	 * Seleccionador de archivo de entrada plano o xml. 
	 */	
	public JFileChooser getFileFC() {
		return fileFC;
	}
	
	/**
	 * Seleccionador de archivo de entrada plano o xml. 
	 */	

	public void setFileFC(JFileChooser fileFC) {
		this.fileFC = fileFC;
	}
	
	/**
	 * Seleccionador de archivo de configuración de los nuevos archivos.
	 */

	public JFileChooser getFileConfiFC() {
		return fileConfiFC;
	}
	
	
	/**
	 * Seleccionador de archivo de configuración de los nuevos archivos.
	 */


	public void setFileConfiFC(JFileChooser fileConfiFC) {
		this.fileConfiFC = fileConfiFC;
	}
	
	/**
	 * Seleccionador de archivo para conexión a la BD.
	 */

	public JFileChooser getFileConnectionFC() {
		return fileConnectionFC;
	}
	
	/**
	 * Seleccionador de archivo para conexión a la BD.
	 */

	public void setFileConnectionFC(JFileChooser fileConnectionFC) {
		this.fileConnectionFC = fileConnectionFC;
	}
	
	/**
	 * Seleccionador de Carpeta de salida para los archivos generados. 
	 */

	public JFileChooser getOutDirectoryFC() {
		return outDirectoryFC;
	}

	/**
	 * Seleccionador de Carpeta de salida para los archivos generados. 
	 */
	
	public void setOutDirectoryFC(JFileChooser outDirectoryFC) {
		this.outDirectoryFC = outDirectoryFC;
	}
	

}
