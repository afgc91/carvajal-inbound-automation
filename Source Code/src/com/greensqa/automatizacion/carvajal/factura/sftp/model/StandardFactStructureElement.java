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
	 * @param factStartNum      Número inicial de las facturas.
	 * @param nitSender         NIT del emisor.
	 * @param nitReceiver       NIT del receptor.
	 * @param authNumber        Número de autorización.
	 * @param startingRangeDate Fecha de inicio del rango.
	 * @param endingRangeDate   Fecha de fin del rango.
	 * @param startingRangeNum  Número inicial del rango.
	 * @param endingRangeNum    Número final del rango.
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
	 * Obtiene el número inicial de las facturas.
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
	 * Obtiene el número de autorización.
	 * 
	 * @return Número de autorización.
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
	 * Obtiene el número inicial del rango.
	 * 
	 * @return Número inicial del rango.
	 */
	public long getStartingRangeNum() {
		return startingRangeNum;
	}

	/**
	 * Obtiene el número final del rango.
	 * 
	 * @return Número final del rango.
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