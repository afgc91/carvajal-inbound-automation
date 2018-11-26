package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.sql.Date;

public class StandardFactStructureElement {

	private String factPrefix;
	private long factStartNum;
	private String nitSender;
	private String nitReceiver;
	private long authNumber;
	private Date startingRangeDate;
	private Date endingRangeDate;
	private long startingRangeNum;
	private long endingRangeNum;
	private String docTypeId;
	private int docType;
	private Date factDate;
	private String cufePath;

	/**
	 * Constructor de CarvajalStandardFactStructure.
	 * 
	 * @param factPrefix        Prefijo de las facturas.
	 * @param factStartNum      N�mero inicial de las facturas.
	 * @param nitSender         NIT del emisor.
	 * @param nitReceiver       NIT del receptor.
	 * @param authNumber        N�mero de autorizaci�n.
	 * @param startingRangeDate Fecha de inicio del rango.
	 * @param endingRangeDate   Fecha de fin del rango.
	 * @param startingRangeNum  N�mero inicial del rango.
	 * @param endingRangeNum    N�mero final del rango.
	 * @param docTypeId         Id del tipo de documento.
	 * @param docType           Tipo del documento.
	 * @param cufePath
	 */
	public StandardFactStructureElement(String factPrefix, long factStartNum, String nitSender, String nitReceiver,
			long authNumber, Date startingRangeDate, Date endingRangeDate, long startingRangeNum, long endingRangeNum,
			String docTypeId, int docType, Date factDate, String cufePath) {
		this.factPrefix = factPrefix;
		this.factStartNum = factStartNum;
		this.nitSender = nitSender;
		this.nitReceiver = nitReceiver;
		this.authNumber = authNumber;
		this.startingRangeDate = startingRangeDate;
		this.endingRangeDate = endingRangeDate;
		this.startingRangeNum = startingRangeNum;
		this.endingRangeNum = endingRangeNum;
		this.docTypeId = docTypeId;
		this.docType = docType;
		this.factDate = factDate;
		this.cufePath = cufePath;
	}

	/**
	 * Obtiene el prefijo de las facturas.
	 * 
	 * @return Prefijo de las facturas.
	 */
	public String getFactPrefix() {
		return factPrefix;
	}

	/**
	 * Obtiene el n�mero inicial de las facturas.
	 * 
	 * @return Npumero inicial de las facturas.
	 */
	public long getFactStartNum() {
		return factStartNum;
	}

	/**
	 * Obtiene el NIT del emisor.
	 * 
	 * @return NIT del emisor.
	 */
	public String getNitSender() {
		return nitSender;
	}

	/**
	 * Obtiene el NIT del receptor.
	 * 
	 * @return NIT del receptor.
	 */
	public String getNitReceiver() {
		return nitReceiver;
	}

	/**
	 * Obtiene el n�mero de autorizaci�n.
	 * 
	 * @return N�mero de autorizaci�n.
	 */
	public long getAuthNumber() {
		return authNumber;
	}

	/**
	 * Obtiene la fecha inicial del rango.
	 * 
	 * @return Fecha inicial del rango.
	 */
	public Date getStartingRangeDate() {
		return startingRangeDate;
	}

	/**
	 * Obtiene la fecha fin del rango.
	 * 
	 * @return Fecha fin del rango.
	 */
	public Date getEndingRangeDate() {
		return endingRangeDate;
	}

	/**
	 * Obtiene el n�mero inicial del rango.
	 * 
	 * @return N�mero inicial del rango.
	 */
	public long getStartingRangeNum() {
		return startingRangeNum;
	}

	/**
	 * Obtiene el n�mero final del rango.
	 * 
	 * @return N�mero final del rango.
	 */
	public long getEndingRangeNum() {
		return endingRangeNum;
	}

	/**
	 * Obtiene el id de tipo de documento.
	 * 
	 * @return Id de tipo de documento.
	 */
	public String getDocTypeId() {
		return docTypeId;
	}

	/**
	 * Obtiene el tipo de documento.
	 * 
	 * @return Tipo de documento.
	 */
	public int getDocType() {
		return docType;
	}

	/**
	 * Obtiene la fecha de la factura
	 * 
	 * @return fecha de la factura
	 */
	public Date getFactDate() {
		return factDate;
	}

	public String getCufePath() {
		return cufePath;
	}
}