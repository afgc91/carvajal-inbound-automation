package com.greensqa.automatizacion.carvajal.factura.sftp.view;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

public class CarvajalPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	// Seleccionar opción Generar Archivos o Enviar

	/**
	 * Etiqueta que indica que debe seleccionar una funcionalidad a ejecutar
	 * (Generar archivos o enviar archivos)
	 **/

	private JLabel selectOptionLabel;

	/**
	 * Seleccionar la opción a ejecutar (Generar Archivos o Enviar Archivos)
	 **/
	private JComboBox<String> optionSelect;

	/**
	 * Botón para enviar la funcionalidad a ejecutar
	 **/
	private JButton acceptButton;

	/**
	 * Etiqueta para mostrar información acerca de la funcionalidad enviar archivos
	 **/
	private JLabel optionLabel;

	// Generar archivos

	/**
	 * Etiqueta para mostrar información referente a la funcionalidad generar
	 * archivos
	 */
	private JLabel generateFilesLabel;

	/**
	 * Etiqueta para enviar la ruta del archivo seleccionado Insumo en txt o xml.
	 */
	private JLabel filePathLabel;

	/**
	 * Etiqueta para mostrar el nombre del archivo seleccionado Insumo en txt o xml.
	 */
	private JLabel fileNameLabel;

	/**
	 * Botón para seleccionar Archivo de Entrada - Insumo en txt o xml.
	 */
	private JButton selectFileButton;

	/**
	 * Etiqueta para enviar la ruta del archivo de configuración seleccionado.
	 **/
	private JLabel configFilePathLabel;

	/**
	 * Etiqueta para mostrar el nombre del archivo de configuración seleccionado.
	 **/
	private JLabel configFileNameLabel;

	/**
	 * Botón para seleccionar el archivo de configuración de los archivos de salida.
	 */
	private JButton configFileButton;

	/**
	 * Ruta de la carpeta en la cual se guardarán los archivos generados.
	 **/
	private JLabel outFileDirPathLabel;

	/**
	 * Nombre de la carpeta en la cual se guardarán los archivos generados.
	 **/
	private JLabel outFileDirNameLabel;

	/**
	 * Botón para seleccionar la ruta en la cual se guardaran los archivos
	 * generados.
	 **/
	private JButton outFileDirButton;

	/**
	 * Etiqueta de la cantidad de archivos por folder.
	 */
	private JLabel filesPerDirectoryLabel;

	/**
	 * Campo para ingresar número de archivos por folder;
	 */
	private JTextField filesPerDirectoryField;

	/**
	 * Campo para seleccionar si desea comprimir la carpeta con los archivos
	 * generados
	 */
	private JCheckBox compressionCheck;

	/**
	 * Etiqueta para preguntar por la cantidad de archivos a comprimir por carpeta
	 */
	private JLabel filesPerZipLabel;

	/**
	 * Campo para ingresar la cantidad de archivos por bloque que se van a comprimir
	 */
	private JTextField filesPerZipField;

	/**
	 * Etiqueta de la imagen GreenSQA
	 */
	private JLabel imageLabel;

	/**
	 * Botón para inciar la Generación de Archivos
	 */
	private JButton generateFilesButton;

	/**
	 * Seleccionador de archivo de entrada plano o xml.
	 */
	private JFileChooser fileChooser;

	/**
	 * Seleccionador de archivo de configuración de los nuevos archivos.
	 */
	private JFileChooser fileConfigChooser;

	/**
	 * Seleccionador de Carpeta de salida para los archivos generados.
	 */
	private JFileChooser outDirectoryChooser;

	/**
	 * Texto mostrado cuando no se ha seleccionado ningún archivo
	 */
	private static final String DIR_SELECTED_DEFAULT = "<<Seleccione archivo...>>";

	/**
	 * Botón para volver a la ventana principal
	 */
	private JButton backButton;

	// Envío de archivos vía SFTP

	/**
	 * Botón para seleccionar los datos de conexión (base de datos y sftp).
	 */
	private JButton selectDBFileButton;

	/**
	 * Etiqueta para enviar la ruta del archivo seleccionado con los datos
	 * de conexión.
	 */
	private JLabel dbFilePathLabel;

	/**
	 * Etiqueta que muestra el nombre de archivo seleccionado con los datos de
	 * conexión.
	 */
	private JLabel dbFileNameLabel;

	/**
	 * Seleccionador de archivo para conexión a la BD.
	 */
	private JFileChooser connectionFileChooser;

	/**
	 * Botón para el envío de los archivos a Cen Financiero
	 **/
	private JButton sendButton;

	/**
	 * Seleccionar Archivos para enviar
	 **/
	private JButton selectSrcPathButton;

	/**
	 * Etiqueta para enviar la ruta seleccionada con los archivos a enviar
	 **/
	private JLabel selectSrcDirPathLabel;

	/**
	 * Etiqueta para mostrar el nombre de la carpeta seleccionada que contiene los
	 * archivos a enviar.
	 **/
	private JLabel selectSrcDirNameLabel;

	/**
	 * Seleccionador de carpeta donde estan los archivos a enviar.
	 */
	private JFileChooser selectSrcDirChooser;

	/**
	 * Botón parar Generar LOG de envío de documentos.
	 */
	private JButton generateLogButton;
	
	/**
	 * Barra de progreso.
	 */
	private JProgressBar progressBar;

	public CarvajalPanel(int option) {
		this.setLayout(null);
		if (option == 3) {
			initializeComponentsMain(3);
		}
		if (option == 0) {
			initializeComponentsFilesGeneration(0);
		}
		if (option == 1) {
			initializeComponentsSftpSending(1);
		}
		if (option == 2) {
			initializeComponentsTestSending(2);
		}
	}

	/**
	 * Se crean y se ubican los elementos de la ventana
	 **/
	public void initializeComponentsMain(int option) {

		String[] opciones = { "Generar Archivos", "Enviar Archivos", "Test" };
		optionSelect = new JComboBox<>(opciones);
		optionSelect.setSelectedIndex(0);
		selectOptionLabel = new JLabel("Seleccione una Funcionalidad:");
		acceptButton = new JButton("Aceptar");
		imageLabel = new JLabel(
				(new ImageIcon("src/com/greensqa/automatizacion/carvajal/factura/sftp/resources/greenSQA.png")));

		selectOptionLabel.setSize(selectOptionLabel.getPreferredSize());
		optionSelect.setSize(optionSelect.getPreferredSize());
		acceptButton.setSize(acceptButton.getPreferredSize());
		imageLabel.setSize(imageLabel.getPreferredSize());

		int x = 30, y = 30, d = 20;
		selectOptionLabel.setLocation(x, y);

		x += selectOptionLabel.getWidth() + d;
		y = 25;
		optionSelect.setLocation(x, y);

		x = 160;
		y = 95;
		acceptButton.setLocation(x, y);

		x = 250;
		y = 105;
		imageLabel.setLocation(x, y);

		this.add(selectOptionLabel);
		this.add(optionSelect);
		this.add(acceptButton);
		this.add(imageLabel);
	}

	private void initializeComponentsFilesGeneration(int option) {

		generateFilesLabel = new JLabel("Generación de Archivos planos o xml según cantidad requerida:");
		filePathLabel = new JLabel();
		fileNameLabel = new JLabel(DIR_SELECTED_DEFAULT);
		selectFileButton = new JButton(" Documento Base ");
		selectFileButton.setToolTipText("Seleccionar Insumo de entrada plano o xml");
		configFilePathLabel = new JLabel();
		configFileNameLabel = new JLabel(DIR_SELECTED_DEFAULT);
		configFileButton = new JButton("  Configuración  ");
		configFileButton.setToolTipText("Seleccionar el archivo con datos de configuración de los archivos de salida");
		outFileDirPathLabel = new JLabel();
		outFileDirNameLabel = new JLabel("<<Seleccione carpeta...>>");
		outFileDirButton = new JButton("Carpeta de Salida");
		outFileDirButton.setToolTipText("Seleccionar la ruta en la cual se guardaran los archivos generados");
		compressionCheck = new JCheckBox("Comprimir Archivos");
		filesPerDirectoryLabel = new JLabel("No. de Archivos a generar");
		filesPerDirectoryField = new JTextField("");
		filesPerZipLabel = new JLabel("Cantidad por Zip");
		filesPerZipField = new JTextField("");
		fileChooser = new JFileChooser();
		fileConfigChooser = new JFileChooser();
		outDirectoryChooser = new JFileChooser();
		imageLabel = new JLabel(
				(new ImageIcon("src/com/greensqa/automatizacion/carvajal/factura/sftp/resources/greenSQA.png")));
		generateFilesButton = new JButton("Generar Archivos");
		backButton = new JButton("Volver");
		progressBar = new JProgressBar();
		progressBar.setMaximum(100);
		progressBar.setMinimum(0);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		int widthLabel = 300, heightLabel = 100;

		generateFilesLabel.setSize(generateFilesLabel.getPreferredSize());
		filePathLabel.setSize(widthLabel, heightLabel);
		fileNameLabel.setSize(widthLabel, heightLabel);
		configFilePathLabel.setSize(widthLabel, heightLabel);
		configFileNameLabel.setSize(widthLabel, heightLabel);
		outFileDirPathLabel.setSize(widthLabel, heightLabel);
		outFileDirNameLabel.setSize(widthLabel, heightLabel);
		filesPerDirectoryLabel.setSize(widthLabel, heightLabel);
		selectFileButton.setSize(selectFileButton.getPreferredSize());
		configFileButton.setSize(selectFileButton.getPreferredSize());
		outFileDirButton.setSize(selectFileButton.getPreferredSize());
		filesPerDirectoryField.setSize(40, 20);
		compressionCheck.setSize(140, 50);
		filesPerZipLabel.setSize(100, heightLabel);
		filesPerZipField.setSize(40, 20);
		generateFilesButton.setSize(generateFilesButton.getPreferredSize());
		backButton.setSize(backButton.getPreferredSize());
		imageLabel.setSize(imageLabel.getPreferredSize());
		progressBar.setSize(400, 20);

		int x = 30, y = 10, d = 20;
		generateFilesLabel.setLocation(x, y);

		y += 40;
		selectFileButton.setLocation(x, y);

		x += selectFileButton.getWidth() + d;
		y = 10;
		fileNameLabel.setLocation(x, y);

		x = 30;
		y += 70;
		configFileButton.setLocation(x, y);

		x += configFileButton.getWidth() + d;
		y = 43;
		configFileNameLabel.setLocation(x, y);

		x = 30;
		y = 110;
		outFileDirButton.setLocation(x, y);

		x += outFileDirButton.getWidth() + d;
		y = 75;
		outFileDirNameLabel.setLocation(x, y);

		x = 30;
		y = 115;
		filesPerDirectoryLabel.setLocation(x, y);

		x += 150;
		y = 155;
		filesPerDirectoryField.setLocation(x, y);

		x += filesPerDirectoryField.getWidth() + 10;
		y = 138;
		compressionCheck.setLocation(x, y);

		x += compressionCheck.getWidth() + 5;
		y = 112;
		filesPerZipLabel.setLocation(x, y);

		x += filesPerZipLabel.getWidth() + 1;
		y = 152;
		filesPerZipField.setLocation(x, y);
		
		x = 60;
		y += 40;
		progressBar.setLocation(x, y);

		x = 170;
		y = 240;
		backButton.setLocation(x, y);

		x += backButton.getWidth() + d;
		generateFilesButton.setLocation(x, y);

		x = 385;
		y = 250;
		imageLabel.setLocation(x, y);

		this.add(generateFilesLabel);
		this.add(selectFileButton);
		this.add(filePathLabel);
		this.add(fileNameLabel);
		this.add(configFileButton);
		this.add(configFileNameLabel);
		this.add(configFilePathLabel);
		this.add(outFileDirNameLabel);
		this.add(outFileDirButton);
		this.add(outFileDirPathLabel);
		this.add(filesPerDirectoryLabel);
		this.add(filesPerDirectoryField);
		this.add(compressionCheck);
		this.add(filesPerZipLabel);
		this.add(filesPerZipField);
		this.add(progressBar);
		this.add(generateFilesButton);
		this.add(backButton);
		this.add(imageLabel);

		filePathLabel.setVisible(false);
		configFilePathLabel.setVisible(false);
		outFileDirPathLabel.setVisible(false);
		filesPerZipLabel.setVisible(false);
		filesPerZipField.setVisible(false);
	}

	public void initializeComponentsSftpSending(int option) {

		optionLabel = new JLabel("Envío de documentos por medio de SFTP a CEN Financiero");
		selectDBFileButton = new JButton("Archivo de Conexión");
		dbFilePathLabel = new JLabel();
		dbFileNameLabel = new JLabel(DIR_SELECTED_DEFAULT);
		selectDBFileButton.setToolTipText("Seleccionar Archivo con Datos de conexión a la BD y al servidor SFTP");
		selectSrcPathButton = new JButton("   Insumos a Enviar   ");
		selectSrcPathButton.setToolTipText("Seleccionar la Carpeta con los documentos que serán enviados a CEN-F");
		selectSrcDirPathLabel = new JLabel();
		selectSrcDirNameLabel = new JLabel("<<Seleccione Carpeta...>>");
		backButton = new JButton("Volver");
		sendButton = new JButton("  Enviar ");
		generateLogButton = new JButton("Generar LOG");
		connectionFileChooser = new JFileChooser();
		selectSrcDirChooser = new JFileChooser();
		imageLabel = new JLabel(
				(new ImageIcon("src/com/greensqa/automatizacion/carvajal/factura/sftp/resources/greenSQA.png")));
		progressBar = new JProgressBar();
		progressBar.setMaximum(100);
		progressBar.setMinimum(0);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		int widthLabel = 250, heightLabel = 14;
		optionLabel.setSize(optionLabel.getPreferredSize());
		dbFilePathLabel.setSize(dbFilePathLabel.getPreferredSize());
		dbFileNameLabel.setSize(widthLabel, heightLabel);
		selectDBFileButton.setSize(selectDBFileButton.getPreferredSize());
		selectSrcPathButton.setSize(selectSrcPathButton.getPreferredSize());
		selectSrcDirPathLabel.setSize(selectSrcDirPathLabel.getPreferredSize());
		selectSrcDirNameLabel.setSize(widthLabel, heightLabel);
		imageLabel.setSize(imageLabel.getPreferredSize());
		backButton.setSize(backButton.getPreferredSize());
		sendButton.setSize(sendButton.getPreferredSize());
		generateLogButton.setSize(generateLogButton.getPreferredSize());
		progressBar.setSize(400, 20);

		int x = 10, y = 20, d = 20;
		optionLabel.setLocation(x, y);

		x = 30; y = 70;
		selectSrcPathButton.setLocation(x, y);

		x += selectSrcPathButton.getWidth() + d; y = 72;
		selectSrcDirNameLabel.setLocation(x, y);

		x = 30; y = 120;
		selectDBFileButton.setLocation(x, y);

		x = +selectDBFileButton.getWidth() + 50; y = 123;
		dbFileNameLabel.setLocation(x, y);

		x = 35; y += 40;
		progressBar.setLocation(x, y);
		
		x = 100; y = 210;
		backButton.setLocation(x, y);

		x += backButton.getWidth() + d;
		sendButton.setLocation(x, y);

		x += sendButton.getWidth() + d;
		generateLogButton.setLocation(x, y);

		x = 320;
		imageLabel.setLocation(x, y);

		this.add(optionLabel);
		this.add(selectSrcPathButton);
		this.add(selectSrcDirPathLabel);
		this.add(selectSrcDirNameLabel);
		this.add(selectDBFileButton);
		this.add(dbFilePathLabel);
		this.add(dbFileNameLabel);
		this.add(imageLabel);
		this.add(backButton);
		this.add(sendButton);
		this.add(generateLogButton);
		this.add(progressBar);

		selectSrcDirPathLabel.setVisible(false);
		dbFilePathLabel.setVisible(false);
		generateLogButton.setEnabled(false);
	}

	public void initializeComponentsTestSending(int option) {

		optionLabel = new JLabel("Verificación casos de prueba: ");
		selectDBFileButton = new JButton("Archivo de Conexión");
		dbFilePathLabel = new JLabel();
		dbFileNameLabel = new JLabel(DIR_SELECTED_DEFAULT);
		selectDBFileButton.setToolTipText("Seleccionar Archivo con Datos de conexión a la BD y al servidor SFTP");
		selectSrcPathButton = new JButton("   Casos de Prueba   ");
		selectSrcPathButton.setToolTipText("Seleccionar el archivo con los casos de prueba a verificar");
		selectSrcDirPathLabel = new JLabel();
		selectSrcDirNameLabel = new JLabel("<<Seleccione Archivo...>>");
		backButton = new JButton("Volver");
		sendButton = new JButton("  Enviar ");
		generateLogButton = new JButton("Generar LOG");
		connectionFileChooser = new JFileChooser();
		selectSrcDirChooser = new JFileChooser();
		imageLabel = new JLabel(
				(new ImageIcon("src/com/greensqa/automatizacion/carvajal/factura/sftp/resources/greenSQA.png")));

		int widthLabel = 250, heightLabel = 14;
		optionLabel.setSize(optionLabel.getPreferredSize());
		dbFilePathLabel.setSize(dbFilePathLabel.getPreferredSize());
		dbFileNameLabel.setSize(widthLabel, heightLabel);
		selectDBFileButton.setSize(selectDBFileButton.getPreferredSize());
		selectSrcPathButton.setSize(selectSrcPathButton.getPreferredSize());
		selectSrcDirPathLabel.setSize(selectSrcDirPathLabel.getPreferredSize());
		selectSrcDirNameLabel.setSize(widthLabel, heightLabel);
		imageLabel.setSize(imageLabel.getPreferredSize());
		backButton.setSize(backButton.getPreferredSize());
		sendButton.setSize(sendButton.getPreferredSize());
		generateLogButton.setSize(generateLogButton.getPreferredSize());

		int x = 10, y = 20, d = 20;
		optionLabel.setLocation(x, y);

		x = 30;
		y = 70;
		selectSrcPathButton.setLocation(x, y);

		x += selectSrcPathButton.getWidth() + d;
		y = 72;
		selectSrcDirNameLabel.setLocation(x, y);

		x = 30;
		y = 120;
		selectDBFileButton.setLocation(x, y);

		x = +selectDBFileButton.getWidth() + 50;
		y = 123;
		dbFileNameLabel.setLocation(x, y);

		x = 120;
		y = 180;
		backButton.setLocation(x, y);

		x += backButton.getWidth() + d;
		y = 180;
		sendButton.setLocation(x, y);

		x += sendButton.getWidth() + d;
		y = 180;
		generateLogButton.setLocation(x, y);

		x = 320;
		y = 180;
		imageLabel.setLocation(x, y);

		this.add(optionLabel);
		this.add(selectSrcPathButton);
		this.add(selectSrcDirPathLabel);
		this.add(selectSrcDirNameLabel);
		this.add(selectDBFileButton);
		this.add(dbFilePathLabel);
		this.add(dbFileNameLabel);
		this.add(imageLabel);
		this.add(backButton);
		this.add(sendButton);
		this.add(generateLogButton);

		selectSrcDirPathLabel.setVisible(false);
		dbFilePathLabel.setVisible(false);
		generateLogButton.setEnabled(false);
	}

	public boolean isValidInput() {
		boolean directoriesInFile = !fileNameLabel.getText().equals(DIR_SELECTED_DEFAULT);
		boolean directoriesConfi = !configFileNameLabel.getText().equals(DIR_SELECTED_DEFAULT);
		boolean directoryOutPath = !outFileDirNameLabel.getText().equals("<<Seleccione carpeta...>>");
		boolean filesPerDirectoy = filesPerDirectoryField.getText().matches("\\d+");
		return directoriesInFile && directoriesConfi && directoryOutPath && filesPerDirectoy;
	}

	public boolean isValidInputFileSend() {

		boolean directoriesBD = !dbFileNameLabel.getText().equals(DIR_SELECTED_DEFAULT);
		boolean directorySrcPath = !selectSrcDirNameLabel.getText().equals("<<Seleccione Carpeta...>>");
		return directoriesBD && directorySrcPath;
	}

	public boolean isValidInputTestSend() {

		boolean directoriesBD = !dbFileNameLabel.getText().equals(DIR_SELECTED_DEFAULT);
		boolean directorySrcPath = !selectSrcDirNameLabel.getText().equals("<<Seleccione Archivo...>>");
		return directoriesBD && directorySrcPath;
	}

	/**
	 * Etiqueta para mostrar el archivo seleccionado Insumo en txt o xml.
	 */

	public JLabel getFilePathLabel() {
		return filePathLabel;
	}

	/**
	 * Etiqueta para mostrar el archivo seleccionado Insumo en txt o xml.
	 */

	public void setFilePathLabel(JLabel filePathLabel) {
		this.filePathLabel = filePathLabel;
	}

	/**
	 * Botón para seleccionar Archivo de Entrada - Insumo en txt o xml.
	 */

	public JButton getSelectFileButton() {
		return selectFileButton;
	}

	/**
	 * Botón para seleccionar Archivo de Entrada - Insumo en txt o xml.
	 */

	public void setSelectFileButton(JButton selectFileButton) {
		this.selectFileButton = selectFileButton;
	}

	/**
	 * Botón para inciar la Generación de Archivos.
	 */
	public JButton getGenerateFilesButton() {
		return generateFilesButton;
	}

	/**
	 * Botón para inciar la Generación de Archivos.
	 */

	public void setGenerateFilesButton(JButton generateFilesButton) {
		this.generateFilesButton = generateFilesButton;
	}

	/**
	 * Botón para enviar los archivos generados a CEN Financiero.
	 **/
	public JButton getSendButton() {
		return sendButton;
	}

	/**
	 * Botón para enviar los archivos generados a CEN Financiero.
	 **/

	public void setSendButton(JButton sendButton) {
		this.sendButton = sendButton;
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
	public JLabel getImageLabel() {
		return imageLabel;
	}

	/**
	 * Logo GreenSQA
	 **/

	public void setImageLabel(JLabel imageLabel) {
		this.imageLabel = imageLabel;
	}

	/**
	 * Botón para seleccionar el archivo con los datos de conexión a la BD.
	 */

	public JButton getSelectDBFileButton() {
		return selectDBFileButton;
	}

	/**
	 * Botón para seleccionar el archivo con los datos de conexión a la BD.
	 */

	public void setSelectDBFileButton(JButton selectDBFileButton) {
		this.selectDBFileButton = selectDBFileButton;
	}

	/**
	 * Etiqueta de la cantidad de archivos por folder.
	 */

	public JLabel getDbFilePathLabel() {
		return dbFilePathLabel;
	}

	/**
	 * Etiqueta de la cantidad de archivos por folder.
	 */

	public void setDbFilePathLabel(JLabel dbFilePathLabel) {
		this.dbFilePathLabel = dbFilePathLabel;
	}

	/**
	 * Etiqueta para mostrar el archivo de configuración seleccionado.
	 **/

	public JLabel getConfigFilePathLabel() {
		return configFilePathLabel;
	}

	/**
	 * Etiqueta para mostrar el archivo de configuración seleccionado.
	 **/

	public void setConfigFilePathLabel(JLabel configFileLabel) {
		this.configFilePathLabel = configFileLabel;
	}

	/**
	 * Botón para seleccionar el archivo de configuración de los archivos de salida.
	 */
	public JButton getConfigFileButton() {
		return configFileButton;
	}

	/**
	 * Botón para seleccionar el archivo de configuración de los archivos de salida.
	 */

	public void setConfigFileButton(JButton configFileButton) {
		this.configFileButton = configFileButton;
	}

	/**
	 * Etiqueta para mostrar la ruta seleccionada.
	 **/

	public JLabel getOutFileDirPathLabel() {
		return outFileDirPathLabel;
	}

	/**
	 * Etiqueta para mostrar la ruta seleccionada.
	 **/

	public void setOutFileDirPathLabel(JLabel outFileDirPathLabel) {
		this.outFileDirPathLabel = outFileDirPathLabel;
	}

	/**
	 * Botón para seleccionar la ruta en la cual se guardaran los archivos
	 * generados.
	 **/

	public JButton getOutFileDirButton() {
		return outFileDirButton;
	}

	/**
	 * Botón para seleccionar la ruta en la cual se guardaran los archivos
	 * generados.
	 **/

	public void setOutFileDirButton(JButton outFileDirButton) {
		this.outFileDirButton = outFileDirButton;
	}

	/**
	 * Seleccionador de archivo de entrada plano o xml.
	 */
	public JFileChooser getFileChooser() {
		return fileChooser;
	}

	/**
	 * Seleccionador de archivo de entrada plano o xml.
	 */
	public void setFileChooser(JFileChooser fileChooser) {
		this.fileChooser = fileChooser;
	}

	/**
	 * Seleccionador de archivo de configuración de los nuevos archivos.
	 */
	public JFileChooser getFileConfigChooser() {
		return fileConfigChooser;
	}

	/**
	 * Seleccionador de archivo de configuración de los nuevos archivos.
	 */
	public void setFileConfigChooser(JFileChooser fileConfigChooser) {
		this.fileConfigChooser = fileConfigChooser;
	}

	/**
	 * Seleccionador de archivo para conexión a la BD.
	 */
	public JFileChooser getConnectionFileChooser() {
		return connectionFileChooser;
	}
	
	/**
	 * Seleccionador de archivo para conexión a la BD.
	 */
	public void setConnectionFileChooser(JFileChooser connectionFileChooser) {
		this.connectionFileChooser = connectionFileChooser;
	}

	/**
	 * Seleccionador de Carpeta de salida para los archivos generados.
	 */
	public JFileChooser getOutDirectoryChooser() {
		return outDirectoryChooser;
	}

	/**
	 * Seleccionador de Carpeta de salida para los archivos generados.
	 */
	public void setOutDirectoryChooser(JFileChooser outDirectoryChooser) {
		this.outDirectoryChooser = outDirectoryChooser;
	}

	public JLabel getSelectOptionLabel() {
		return selectOptionLabel;
	}

	public void setSelectOptionLabel(JLabel selectOptionLabel) {
		this.selectOptionLabel = selectOptionLabel;
	}

	public JComboBox<String> getOptionSelect() {
		return this.optionSelect;
	}

	public void setOptionSelect(JComboBox<String> optionSelect) {
		this.optionSelect = optionSelect;
	}

	public JButton getAcceptButton() {
		return acceptButton;
	}

	public void setAcceptButton(JButton acceptButton) {
		this.acceptButton = acceptButton;
	}

	public JLabel getOptionLabel() {
		return optionLabel;
	}

	public void setOptionLabel(JLabel optionLabel) {
		this.optionLabel = optionLabel;
	}

	public JLabel getGenerateFilesLabel() {
		return generateFilesLabel;
	}

	public void setGenerateFilesLabel(JLabel informationOptionGenerateFile) {
		this.generateFilesLabel = informationOptionGenerateFile;
	}

	public JButton getSelectSrcPathButton() {
		return selectSrcPathButton;
	}

	public void setSelectSrcPathButton(JButton selectSrcPathButton) {
		this.selectSrcPathButton = selectSrcPathButton;
	}

	public JLabel getSelectSrcDirPathLabel() {
		return selectSrcDirPathLabel;
	}

	public void setSelectSrcDirPathLabel(JLabel selectSrcDirPathLabel) {
		this.selectSrcDirPathLabel = selectSrcDirPathLabel;
	}

	public JFileChooser getSelectSrcDirChooser() {
		return selectSrcDirChooser;
	}

	public void setSelectSrcDirChooser(JFileChooser selectSrcDirChooser) {
		this.selectSrcDirChooser = selectSrcDirChooser;
	}

	public JCheckBox getCompressionCheck() {
		return compressionCheck;
	}

	public void setCompressionCheck(JCheckBox compressionCheck) {
		this.compressionCheck = compressionCheck;
	}

	public JLabel getFileNameLabel() {
		return fileNameLabel;
	}

	public void setFileNameLabel(JLabel fileNameLabel) {
		this.fileNameLabel = fileNameLabel;
	}

	public JLabel getConfigFileNameLabel() {
		return configFileNameLabel;
	}

	public void setConfigFileNameLabel(JLabel configFileNameLabel) {
		this.configFileNameLabel = configFileNameLabel;
	}

	public JLabel getOutFileDirNameLabel() {
		return outFileDirNameLabel;
	}

	public void setOutFileDirNameLabel(JLabel outFileDirNameLabel) {
		this.outFileDirNameLabel = outFileDirNameLabel;
	}

	public JLabel getDbFileNameLabel() {
		return dbFileNameLabel;
	}

	public void setDbFileNameLabel(JLabel dbFileNameLabel) {
		this.dbFileNameLabel = dbFileNameLabel;
	}

	public JLabel getSelectSrcDirNameLabel() {
		return selectSrcDirNameLabel;
	}

	public void setSelectSrcDirNameLabel(JLabel srcViewPathLabel) {
		this.selectSrcDirNameLabel = srcViewPathLabel;
	}

	public JLabel getFilesPerZipLabel() {
		return filesPerZipLabel;
	}

	public void setFilesPerZipLabel(JLabel filesPerZipLabel) {
		this.filesPerZipLabel = filesPerZipLabel;
	}

	public JTextField getFilesPerZipField() {
		return filesPerZipField;
	}

	public void setFilesPerZipField(JTextField filesPerZipField) {
		this.filesPerZipField = filesPerZipField;
	}

	public JButton getGenerateLogButton() {
		return generateLogButton;
	}

	public void setGenerateLogButton(JButton generateLogButton) {
		this.generateLogButton = generateLogButton;
	}

	public JButton getBackButton() {
		return backButton;
	}

	public void setBackButton(JButton backButton) {
		this.backButton = backButton;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(JProgressBar progressBarFilesGeneration) {
		this.progressBar = progressBarFilesGeneration;
	}
}
