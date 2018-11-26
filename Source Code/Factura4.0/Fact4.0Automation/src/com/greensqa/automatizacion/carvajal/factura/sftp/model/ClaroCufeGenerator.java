package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.jcraft.jsch.jce.SHA1;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class ClaroCufeGenerator {

	private String cufeConfigFilePath;
	private File claroFile = null;
	private static int valuePrevious;
	private ArrayList<String> labelList = null;
	private ArrayList<String> indexList = null;
	private ArrayList<String> typeList = null;
	private ArrayList<String> formatList = null;
	private ArrayList<String> conditionList = null;
	private ArrayList<String> valuesFileList = new ArrayList<String>();
	private ArrayList<String> valueConditionList = null;

	public ClaroCufeGenerator(String cufeConfigFilePath, File claroFile) {
		this.cufeConfigFilePath = cufeConfigFilePath;
		this.claroFile = claroFile;
	}

	public String generateCufeClaroFile() throws IOException, java.text.ParseException {

		ArrayList<String> cufeItems = new ArrayList<String>();
		String claroFilePath =  claroFile.getAbsolutePath(); 
		String label = "";
		String value = "";
		String sha_1 = "";
		int position = 0;
		int index = 0;

		labelList = ExcelReader.getValueFieldPosition(cufeConfigFilePath, 0);
		indexList = ExcelReader.getValueFieldPosition(cufeConfigFilePath, 1);
		typeList = ExcelReader.getValueFieldPosition(cufeConfigFilePath, 2);
		formatList = ExcelReader.getValueFieldPosition(cufeConfigFilePath, 3);
		conditionList = ExcelReader.getValueFieldPosition(cufeConfigFilePath, 4);
		valueConditionList = ExcelReader.getValueFieldPosition(cufeConfigFilePath, 5);
		for (int i = 0; i < labelList.size(); i++) {			
			if (!typeList.get(i).isEmpty()) {
				String[] labelArray = labelList.get(i).split("\\_");
				label = labelArray[0];
				position = (labelArray[1])  != null ? Integer.parseInt(labelArray[1]) : 0;
				String[] indexArray = indexList.get(i).split("\\.");
				index = Integer.parseInt(indexArray[0]);
				value = CarvajalUtils.getCufeValuesByLocation(claroFilePath, label, position, index);
				valuesFileList.add(value);
				cufeItems.add(transformationDate(value, typeList.get(i), formatList.get(i), conditionList.get(i)));
			} else {
				cufeItems.add(labelList.get(i)+ "");
			}
			valuePrevious = i;
		}
		String cufe = "";
		for (int i = 0; i < cufeItems.size(); i++) {
			cufe += cufeItems.get(i);
		}
		sha_1 = DigestUtils.sha1Hex(cufe);
		return sha_1;
		
	}

	private String transformationDate(String value, String type, String format, String condition)
			throws java.text.ParseException, FileNotFoundException, IOException {

		// Rules for transformation
		SimpleDateFormat originalDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyyMMdd");
		String temp = "";

		if (value == null) {
			temp = "";
		} else if (type.equalsIgnoreCase("fecha")) {
			Date date = originalDateFormat.parse(value);
			String dateFact = newDateFormat.format(date);
			temp = dateFact;
		} else if (type.equalsIgnoreCase("hora")) {
			LocalTime fechaHora = LocalTime.parse(value);
			String hourFact = (DateTimeFormatter.ofPattern("hhmmss").format(fechaHora));
			temp = hourFact;
		} else if (type.equalsIgnoreCase("double")) {
			Double totalValueFact = Double.parseDouble(value);
			DecimalFormat df = new DecimalFormat(format);
			String totalValueFactFormat = df.format(totalValueFact).replaceAll("[,]", ".");
			temp = totalValueFactFormat;
		} else if (type.equalsIgnoreCase("impuesto")) {
			if (!value.matches(format)) {
				String tax = String.format("%02d", Integer.parseInt(value));
				temp = tax;
			} else {
				temp = value;
			}
		} else {
			temp = value.toString();
		}

		if (!condition.isEmpty()) {
			if (!temp.equalsIgnoreCase(condition)) {
				temp = condition;
			}
		}

		int i = valuePrevious--;
		if (conditionList.size() > 0 && !conditionList.get(i).isEmpty()) {
			value = valuesFileList.get(i);
			if (typeList.get(i).contains("impuesto")) {
				if (value != null && !value.matches(formatList.get(i))) {
					value = String.format("%02d", Integer.parseInt(value));
				}
				if ((value == null) || !value.equalsIgnoreCase(conditionList.get(i))) {
					temp = valueConditionList.get(valuePrevious);
				}
			}
		}
		return temp;
	}

}