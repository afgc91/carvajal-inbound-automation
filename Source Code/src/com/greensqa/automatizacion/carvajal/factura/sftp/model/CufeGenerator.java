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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CufeGenerator {

	private String cufeConfigFilePath;
	private String claroFilePath;
	private String order = "";

	public CufeGenerator(String cufeConfigFilePath, String claroFilePath) {
		this.cufeConfigFilePath = cufeConfigFilePath;
		this.claroFilePath = claroFilePath;
	}

	public String generateCufeClaroFile()
			throws FileNotFoundException, IOException, ParseException, java.text.ParseException {
		ArrayList<String> valuesFile = new ArrayList<String>();
		File file = new File(cufeConfigFilePath);
		if (!file.exists()) {
			return null;
		}
		try (FileReader fr = new FileReader(file)) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(fr);
			int index = 0;

			String type = "";
			String format = "";
			String label = ""; 
			int position = 0;
			Set<Object> jsonSet = json.keySet();
			ArrayList<String> cufeItems = new ArrayList<String>();	
			
			for (Object objKey : jsonSet) {
				String key = (String) objKey;
				JSONObject cufeItem = (JSONObject) json.get(key);
				type = (String) cufeItem.get("tipo");
				format = (String) cufeItem.get("formato");
				order = (String) cufeItem.get("orden");
				String[] labelArray = key.split("\\_");
				label = labelArray[0];
				position = Integer.parseInt(labelArray[1]);
				String[] values = CarvajalUtils.getCufeValuesByLocation(claroFilePath, label, position);
				cufeItems.addAll(transformationDate(values, type, format));
				
				index++;
			}
			
			
			System.out.println("Cufe: " + cufeItems);
			Collections.sort(cufeItems);
			String cufe = "";
			for (int i = 0; i < cufeItems.size(); i++) {
				cufe += cufeItems.get(i).split("-")[1]+"-";
			}

			System.out.println("Cufe: " + cufe);
			return cufe;
		}
	}

	private ArrayList<String> transformationDate(String[] value, String type, String format)
			throws java.text.ParseException {
		// rules for transformation

		SimpleDateFormat sdf = new SimpleDateFormat(format);
		ArrayList<String> data = new ArrayList<String>();

		for (int i = 0; i < value.length; i++) {
			if (type.equalsIgnoreCase("fecha")) {
				Date date = sdf.parse(value[i]);
				String dateFact = sdf.format(date);
				data.add(order + "-" + dateFact);
			} else if (type.equalsIgnoreCase("hora")) {
				LocalTime fechaHora = LocalTime.parse(value[i]);
				String hourFact = (DateTimeFormatter.ofPattern("hhmmss").format(fechaHora));
				data.add(order + "-" + hourFact);
			} else if (type.equalsIgnoreCase("double")) {
				Double totalValueFact = Double.parseDouble(value[i]);
				DecimalFormat df = new DecimalFormat(format);
				String totalValueFactFormat = df.format(totalValueFact).replaceAll("[,]", ".");
				data.add(order + "-" + totalValueFactFormat);
			} else if (type.equalsIgnoreCase("impuesto")) {
				if (!value[i].matches(format)) {
					String tax = String.format("%02d", Integer.parseInt(value[i]));
					data.add(order + "-" + tax);
				} else {
					data.add(order + "-" + value[i]);
				}
			} else {
				data.add(order + "-" + value[i]);
			}
		}
		return data;
	}
}
