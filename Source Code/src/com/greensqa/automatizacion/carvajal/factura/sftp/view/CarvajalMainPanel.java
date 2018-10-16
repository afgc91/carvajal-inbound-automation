package com.greensqa.automatizacion.carvajal.factura.sftp.view;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

public class CarvajalMainPanel extends JPanel {

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
	private JComboBox selectOption;

	/**
	 * Botón para enviar la funcionalidad a ejecutar
	 **/
	private JButton acceptOption;

	/**
	 * Etiqueta para mostrar información acerca de la funcionalidad enviar archivos
	 **/
	private JLabel informationOption;

	// Generar archivos

	/**
	 * Etiqueta para mostrar información referente a la funcionalidad generar
	 * archivos
	 */
	private JLabel informationOptionGenerateFile;

	/**
	 * Etiqueta para enviar la ruta del archivo seleccionado Insumo en txt o xml.
	 */
	private JLabel fileLabel;

	/**
	 * Etiqueta para mostrar el nombre del archivo seleccionado Insumo en txt o xml.
	 */
	private JLabel fileViewLabel;

	/**
	 * Botón para seleccionar Archivo de Entrada - Insumo en txt o xml.
	 */
	private JButton selectFile;

	/**
	 * Etiqueta para enviar la ruta del archivo de configuración seleccionado.
	 **/
	private JLabel configFileLabel;

	/**
	 * Etiqueta para mostrar el nombre del archivo de configuración seleccionado.
	 **/
	private JLabel configViewFileLabel;

	/**
	 * Botón para seleccionar el archivo de configuración de los archivos de salida.
	 */
	private JButton configFile;

	/**
	 * Ruta de la carpeta en la cual se guardarán los archivos generados.
	 **/
	private JLabel outFilePathLabel;

	/**
	 * Nombre de la carpeta en la cual se guardarán los archivos generados.
	 **/
	private JLabel outViewFilePathLabel;

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
	 * Campo para seleccionar si desea comprimir la carpeta con los archivos
	 * generados
	 */
	private JCheckBox selectCompression;

	/**
	 * Etiqueta para preguntar por la cantidad de archivos a comprimir por carpeta
	 */
	private JLabel filesPerZipLabel;

	/**
	 * Campo para ingresar la cantidad de archivos por bloque que se van a comprimir
	 */
	private JTextField filesPerZipField;

	/**
	 * Imagen GreenSQA
	 */
	private JLabel image;

	/**
	 * Botón para inciar la Generación de Archivos
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
	 * Seleccionador de Carpeta de salida para los archivos generados.
	 */
	private JFileChooser outDirectoryFC;

	/**
	 * Texto mostrado cuando no se ha seleccionado ningún archivo
	 */
	private static final String DIR_SELECTED_DEFAULT = "<<Seleccione archivo...>>";

	/**
	 * Botón para volver a la ventana principal
	 */
	private JButton backMainPanel;

	// Envío de archivos vía SFTP

	/**
	 * Barra de progreso
	 **/
	private JProgressBar progressBar;

	/**
	 * Botón para seleccionar los datos de conexión (base de datos y sftp).
	 */
	private JButton selectDBFile;

	/**
	 * Etiqueta para enviar la ruta del nombre de archivo seleccionado con los datos
	 * de conexión.
	 */
	private JLabel fileBDLabel;

	/**
	 * Etiqueta que muestra el nombre de archivo seleccionado con los datos de
	 * conexión.
	 */
	private JLabel fileViewBDLabel;

	/**
	 * Seleccionador de archivo para conexión a la BD.
	 */
	private JFileChooser fileConnectionFC;

	/**
	 * Botón para el envío de los archivos a Cen Financiero
	 **/
	private JButton send;

	/**
	 * Seleccionar Archivos para enviar
	 **/
	private JButton selectSrcPath;

	/**
	 * Etiqueta para enviar la ruta del seleccionada con los archivos a enviar
	 **/
	private JLabel selectSrcPathLabel;

	/**
	 * Etiqueta para mostrar el nombre de la carpeta seleccionada que contiene los
	 * archivos a enviar.
	 **/
	private JLabel srcViewPathLabel;

	/**
	 * Seleccionador de carpeta donde estan los archivos a enviar
	 */
	private JFileChooser selectSrcPathFC;

	/**
	 * Botón parar Generar LOG de envío de documentos
	 */
	private JButton generateLog;

	public CarvajalMainPanel(int option) {
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
		selectOption = new JComboBox<>(opciones);
		selectOption.setSelectedIndex(0);
		selectOptionLabel = new JLabel("Seleccione una Funcionalidad:");
		acceptOption = new JButton("Aceptar");
		image = new JLabel(
				(new ImageIcon("src/com/greensqa/automatizacion/carvajal/factura/sftp/resources/greenSQA.png")));

		selectOptionLabel.setSize(selectOptionLabel.getPreferredSize());
		selectOption.setSize(selectOption.getPreferredSize());
		acceptOption.setSize(acceptOption.getPreferredSize());
		image.setSize(image.getPreferredSize());

		int x = 30, y = 30, d = 20;
		selectOptionLabel.setLocation(x, y);

		x += selectOptionLabel.getWidth() + d;
		y = 25;
		selectOption.setLocation(x, y);

		x = 180;
		y = 95;
		acceptOption.setLocation(x, y);

		x = 250;
		y = 105;
		image.setLocation(x, y);

		this.add(selectOptionLabel);
		this.add(selectOption);
		this.add(acceptOption);
		this.add(image);
	}

	private void initializeComponentsFilesGeneration(int option) {

		informationOptionGenerateFile = new JLabel("Generación de Archivos planos o xml según cantidad requerida:");
		fileLabel = new JLabel();
		fileViewLabel = new JLabel(DIR_SELECTED_DEFAULT);
		selectFile = new JButton(" Documento Base ");
		selectFile.setToolTipText("Seleccionar Insumo de entrada plano o xml");
		configFileLabel = new JLabel();
		configViewFileLabel = new JLabel(DIR_SELECTED_DEFAULT);
		configFile = new JButton("  Configuración  ");
		configFile.setToolTipText("Seleccionar el archivo con datos de configuración de los archivos de salida");
		outFilePathLabel = new JLabel();
		outViewFilePathLabel = new JLabel("<<Seleccione carpeta...>>");
		outFilePath = new JButton("Carpeta de Salida");
		outFilePath.setToolTipText("Seleccionar la ruta en la cual se guardaran los archivos generados");
		selectCompression = new JCheckBox("Comprimir Archivos");
		filesPerDirectoryLabel = new JLabel("No. de Archivos a generar");
		filesPerDirectoryField = new JTextField("");
		filesPerZipLabel = new JLabel("Cantidad por Zip");
		filesPerZipField = new JTextField("");
		fileFC = new JFileChooser();
		fileConfiFC = new JFileChooser();
		outDirectoryFC = new JFileChooser();
		image = new JLabel(
				(new ImageIcon("src/com/greensqa/automatizacion/carvajal/factura/sftp/resources/greenSQA.png")));
		accept = new JButton("Generar Archivos");
		backMainPanel = new JButton("Volver");

		int widthLabel = 300, heightLabel = 100;

		informationOptionGenerateFile.setSize(informationOptionGenerateFile.getPreferredSize());
		fileLabel.setSize(widthLabel, heightLabel);
		fileViewLabel.setSize(widthLabel, heightLabel);
		configFileLabel.setSize(widthLabel, heightLabel);
		configViewFileLabel.setSize(widthLabel, heightLabel);
		outFilePathLabel.setSize(widthLabel, heightLabel);
		outViewFilePathLabel.setSize(widthLabel, heightLabel);
		filesPerDirectoryLabel.setSize(widthLabel, heightLabel);
		selectFile.setSize(selectFile.getPreferredSize());
		configFile.setSize(selectFile.getPreferredSize());
		outFilePath.setSize(selectFile.getPreferredSize());
		filesPerDirectoryField.setSize(40, 20);
		selectCompression.setSize(140, 50);
		filesPerZipLabel.setSize(100, heightLabel);
		filesPerZipField.setSize(40, 20);
		accept.setSize(accept.getPreferredSize());
		backMainPanel.setSize(backMainPanel.getPreferredSize());
		image.setSize(image.getPreferredSize());

		int x = 10, y = 10, d = 20;
		informationOptionGenerateFile.setLocation(x, y);

		x = 30;
		y = 50;
		selectFile.setLocation(x, y);

		x += selectFile.getWidth() + d;
		y = 10;
		fileViewLabel.setLocation(x, y);

		x = 30;
		y = 80;
		configFile.setLocation(x, y);

		x += configFile.getWidth() + d;
		y = 43;
		configViewFileLabel.setLocation(x, y);

		x = 30;
		y = 110;
		outFilePath.setLocation(x, y);

		x += outFilePath.getWidth() + d;
		y = 75;
		outViewFilePathLabel.setLocation(x, y);

		x = 30;
		y = 115;
		filesPerDirectoryLabel.setLocation(x, y);

		x += 150;
		y = 155;
		filesPerDirectoryField.setLocation(x, y);

		x += filesPerDirectoryField.getWidth() + 10;
		y = 138;
		selectCompression.setLocation(x, y);

		x += selectCompression.getWidth() + 5;
		y = 112;
		filesPerZipLabel.setLocation(x, y);

		x += filesPerZipLabel.getWidth() + 1;
		y = 152;
		filesPerZipField.setLocation(x, y);

		x = 170;
		y = 200;
		backMainPanel.setLocation(x, y);

		x += backMainPanel.getWidth() + d;
		y = 200;
		accept.setLocation(x, y);

		x = 385;
		y = 210;
		image.setLocation(x, y);

		this.add(informationOptionGenerateFile);
		this.add(selectFile);
		this.add(fileLabel);
		this.add(fileViewLabel);
		this.add(configFile);
		this.add(configViewFileLabel);
		this.add(configFileLabel);
		this.add(outViewFilePathLabel);
		this.add(outFilePath);
		this.add(outFilePathLabel);
		this.add(filesPerDirectoryLabel);
		this.add(filesPerDirectoryField);
		this.add(selectCompression);
		this.add(filesPerZipLabel);
		this.add(filesPerZipField);
		this.add(accept);
		this.add(backMainPanel);
		this.add(image);

		fileLabel.setVisible(false);
		configFileLabel.setVisible(false);
		outFilePathLabel.setVisible(false);
		filesPerZipLabel.setVisible(false);
		filesPerZipField.setVisible(false);
	}

	public void initializeComponentsSftpSending(int option) {

		informationOption = new JLabel("Envío de documentos por medio de SFTP a CEN Financiero");
		selectDBFile = new JButton("Archivo de Conexión");
		fileBDLabel = new JLabel();
		fileViewBDLabel = new JLabel(DIR_SELECTED_DEFAULT);
		selectDBFile.setToolTipText("Seleccionar Archivo con Datos de conexión a la BD y al servidor SFTP");
		selectSrcPath = new JButton("   Insumos a Enviar   ");
		selectSrcPath.setToolTipText("Seleccionar la Carpeta con los documentos que serán enviados a CEN-F");
		selectSrcPathLabel = new JLabel();
		srcViewPathLabel = new JLabel("<<Seleccione Carpeta...>>");
		backMainPanel = new JButton("Volver");
		send = new JButton("  Enviar ");
		generateLog = new JButton("Generar LOG");
		fileConnectionFC = new JFileChooser();
		selectSrcPathFC = new JFileChooser();
		progressBar = new JProgressBar();
		image = new JLabel(
				(new ImageIcon("src/com/greensqa/automatizacion/carvajal/factura/sftp/resources/greenSQA.png")));

		int widthLabel = 250, heightLabel = 14;
		informationOption.setSize(informationOption.getPreferredSize());
		fileBDLabel.setSize(fileBDLabel.getPreferredSize());
		fileViewBDLabel.setSize(widthLabel, heightLabel);
		selectDBFile.setSize(selectDBFile.getPreferredSize());
		selectSrcPath.setSize(selectSrcPath.getPreferredSize());
		selectSrcPathLabel.setSize(selectSrcPathLabel.getPreferredSize());
		srcViewPathLabel.setSize(widthLabel, heightLabel);
		image.setSize(image.getPreferredSize());
		backMainPanel.setSize(backMainPanel.getPreferredSize());
		send.setSize(send.getPreferredSize());
		generateLog.setSize(generateLog.getPreferredSize());
		progressBar.setBounds(40, 40, 160, 30);

		int x = 10, y = 20, d = 20;
		informationOption.setLocation(x, y);

		x = 30;
		y = 70;
		selectSrcPath.setLocation(x, y);

		x += selectSrcPath.getWidth() + d;
		y = 72;
		srcViewPathLabel.setLocation(x, y);

		x = 30;
		y = 120;
		selectDBFile.setLocation(x, y);

		x = +selectDBFile.getWidth() + 50;
		y = 123;
		fileViewBDLabel.setLocation(x, y);

		x = 120;
		y = 180;
		backMainPanel.setLocation(x, y);

		x += backMainPanel.getWidth() + d;
		y = 180;
		send.setLocation(x, y);

		x += send.getWidth() + d;
		y = 180;
		generateLog.setLocation(x, y);

		x = 320;
		y = 180;
		image.setLocation(x, y);

		progressBar.setValue(0);
		this.add(progressBar);
		this.add(informationOption);
		this.add(selectSrcPath);
		this.add(selectSrcPathLabel);
		this.add(srcViewPathLabel);
		this.add(selectDBFile);
		this.add(fileBDLabel);
		this.add(fileViewBDLabel);
		this.add(image);
		this.add(backMainPanel);
		this.add(send);
		this.add(generateLog);

		selectSrcPathLabel.setVisible(false);
		fileBDLabel.setVisible(false);
		generateLog.setEnabled(false);
		progressBar.setVisible(false);
	}

	public void initializeComponentsTestSending(int option) {

		informationOption = new JLabel("Verificación casos de prueba: ");
		selectDBFile = new JButton("Archivo de Conexión");
		fileBDLabel = new JLabel();
		fileViewBDLabel = new JLabel(DIR_SELECTED_DEFAULT);
		selectDBFile.setToolTipText("Seleccionar Archivo con Datos de conexión a la BD y al servidor SFTP");
		selectSrcPath = new JButton("   Casos de Prueba   ");
		selectSrcPath.setToolTipText("Seleccionar el archivo con los casos de prueba a verificar");
		selectSrcPathLabel = new JLabel();
		srcViewPathLabel = new JLabel("<<Seleccione Archivo...>>");
		backMainPanel = new JButton("Volver");
		send = new JButton("  Enviar ");
		generateLog = new JButton("Generar LOG");
		fileConnectionFC = new JFileChooser();
		selectSrcPathFC = new JFileChooser();
		progressBar = new JProgressBar();
		image = new JLabel(
				(new ImageIcon("src/com/greensqa/automatizacion/carvajal/factura/sftp/resources/greenSQA.png")));

		int widthLabel = 250, heightLabel = 14;
		informationOption.setSize(informationOption.getPreferredSize());
		fileBDLabel.setSize(fileBDLabel.getPreferredSize());
		fileViewBDLabel.setSize(widthLabel, heightLabel);
		selectDBFile.setSize(selectDBFile.getPreferredSize());
		selectSrcPath.setSize(selectSrcPath.getPreferredSize());
		selectSrcPathLabel.setSize(selectSrcPathLabel.getPreferredSize());
		srcViewPathLabel.setSize(widthLabel, heightLabel);
		image.setSize(image.getPreferredSize());
		backMainPanel.setSize(backMainPanel.getPreferredSize());
		send.setSize(send.getPreferredSize());
		generateLog.setSize(generateLog.getPreferredSize());
		progressBar.setBounds(40, 40, 160, 30);

		int x = 10, y = 20, d = 20;
		informationOption.setLocation(x, y);

		x = 30;
		y = 70;
		selectSrcPath.setLocation(x, y);

		x += selectSrcPath.getWidth() + d;
		y = 72;
		srcViewPathLabel.setLocation(x, y);

		x = 30;
		y = 120;
		selectDBFile.setLocation(x, y);

		x = +selectDBFile.getWidth() + 50;
		y = 123;
		fileViewBDLabel.setLocation(x, y);

		x = 120;
		y = 180;
		backMainPanel.setLocation(x, y);

		x += backMainPanel.getWidth() + d;
		y = 180;
		send.setLocation(x, y);

		x += send.getWidth() + d;
		y = 180;
		generateLog.setLocation(x, y);

		x = 320;
		y = 180;
		image.setLocation(x, y);

		progressBar.setValue(0);
		this.add(progressBar);
		this.add(informationOption);
		this.add(selectSrcPath);
		this.add(selectSrcPathLabel);
		this.add(srcViewPathLabel);
		this.add(selectDBFile);
		this.add(fileBDLabel);
		this.add(fileViewBDLabel);
		this.add(image);
		this.add(backMainPanel);
		this.add(send);
		this.add(generateLog);

		selectSrcPathLabel.setVisible(false);
		fileBDLabel.setVisible(false);
		generateLog.setEnabled(false);
		progressBar.setVisible(false);
	}

	public boolean isValidInput() {
		boolean directoriesInFile = !fileViewLabel.getText().equals(DIR_SELECTED_DEFAULT);
		boolean directoriesConfi = !configViewFileLabel.getText().equals(DIR_SELECTED_DEFAULT);
		boolean directoryOutPath = !outViewFilePathLabel.getText().equals("<<Seleccione carpeta...>>");
		boolean filesPerDirectoy = filesPerDirectoryField.getText().matches("\\d+");
		return directoriesInFile && directoriesConfi && directoryOutPath && filesPerDirectoy;
	}

	public boolean isValidInputFileSend() {

		boolean directoriesBD = !fileViewBDLabel.getText().equals(DIR_SELECTED_DEFAULT);
		boolean directorySrcPath = !srcViewPathLabel.getText().equals("<<Seleccione Carpeta...>>");
		return directoriesBD && directorySrcPath;
	}

	public boolean isValidInputTestSend() {

		boolean directoriesBD = !fileViewBDLabel.getText().equals(DIR_SELECTED_DEFAULT);
		boolean directorySrcPath = !srcViewPathLabel.getText().equals("<<Seleccione Archivo...>>");
		return directoriesBD && directorySrcPath;
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
	 * Botón para inciar la Generación de Archivos
	 */
	public JButton getAccept() {
		return accept;
	}

	/**
	 * Botón para inciar la Generación de Archivos
	 */

	public void setAccept(JButton accept) {
		this.accept = accept;
	}

	/**
	 * Botón para enviar los archivos generados a Send Financiero
	 **/
	public JButton getSend() {
		return send;
	}

	/**
	 * Botón para enviar los archivos generados a Send Financiero
	 **/

	public void setSend(JButton send) {
		this.send = send;
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
	 * Botón para seleccionar el archivo con los datos de conexión a la BD.
	 */

	public JButton getSelectDBFile() {
		return selectDBFile;
	}

	/**
	 * Botón para seleccionar el archivo con los datos de conexión a la BD.
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

	public JLabel getSelectOptionLabel() {
		return selectOptionLabel;
	}

	public void setSelectOptionLabel(JLabel selectOptionLabel) {
		this.selectOptionLabel = selectOptionLabel;
	}

	public JComboBox getSelectOption() {
		return selectOption;
	}

	public void setSelectOption(JComboBox selectOption) {
		this.selectOption = selectOption;
	}

	public JButton getAcceptOption() {
		return acceptOption;
	}

	public void setAcceptOption(JButton acceptOption) {
		this.acceptOption = acceptOption;
	}

	public JLabel getInformationOption() {
		return informationOption;
	}

	public void setInformationOption(JLabel informationOption) {
		this.informationOption = informationOption;
	}

	public JLabel getInformationOptionGenerateFile() {
		return informationOptionGenerateFile;
	}

	public void setInformationOptionGenerateFile(JLabel informationOptionGenerateFile) {
		this.informationOptionGenerateFile = informationOptionGenerateFile;
	}

	public JButton getSelectSrcPath() {
		return selectSrcPath;
	}

	public void setSelectSrcPath(JButton selectSrcPath) {
		this.selectSrcPath = selectSrcPath;
	}

	public JLabel getSelectSrcPathLabel() {
		return selectSrcPathLabel;
	}

	public void setSelectSrcPathLabel(JLabel selectSrcPathLabel) {
		this.selectSrcPathLabel = selectSrcPathLabel;
	}

	public JFileChooser getSelectSrcPathFC() {
		return selectSrcPathFC;
	}

	public void setSelectSrcPathFC(JFileChooser selectSrcPathFC) {
		this.selectSrcPathFC = selectSrcPathFC;
	}

	public JCheckBox getSelectCompression() {
		return selectCompression;
	}

	public void setSelectCompression(JCheckBox selectCompression) {
		this.selectCompression = selectCompression;
	}

	public JLabel getFileViewLabel() {
		return fileViewLabel;
	}

	public void setFileViewLabel(JLabel fileViewLabel) {
		this.fileViewLabel = fileViewLabel;
	}

	public JLabel getConfigViewFileLabel() {
		return configViewFileLabel;
	}

	public void setConfigViewFileLabel(JLabel configViewFileLabel) {
		this.configViewFileLabel = configViewFileLabel;
	}

	public JLabel getOutViewFilePathLabel() {
		return outViewFilePathLabel;
	}

	public void setOutViewFilePathLabel(JLabel outViewFilePathLabel) {
		this.outViewFilePathLabel = outViewFilePathLabel;
	}

	public JLabel getFileViewBDLabel() {
		return fileViewBDLabel;
	}

	public void setFileViewBDLabel(JLabel fileViewBDLabel) {
		this.fileViewBDLabel = fileViewBDLabel;
	}

	public JLabel getSrcViewPathLabel() {
		return srcViewPathLabel;
	}

	public void setSrcViewPathLabel(JLabel srcViewPathLabel) {
		this.srcViewPathLabel = srcViewPathLabel;
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

	public JButton getGenerateLog() {
		return generateLog;
	}

	public void setGenerateLog(JButton generateLog) {
		this.generateLog = generateLog;
	}

	public JButton getBackMainPanel() {
		return backMainPanel;
	}

	public void setBackMainPanel(JButton backMainPanel) {
		this.backMainPanel = backMainPanel;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

	public void setProgressBar(JProgressBar progressBar) {
		this.progressBar = progressBar;
	}
}
