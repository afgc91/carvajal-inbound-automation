package com.carvajal.facturaclaro.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.carvajal.facturaclaro.ral.dto.NotificationDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils<T> {

	public static String errorHTTP = "";

	@SuppressWarnings("unchecked")
	public T doRest(String URL, String method, T className, String body, String token, String md5, String date,
			String transationId, String length) {
		System.out.println("Nombre de la clase " + className);

		URL url;
		T object = null;
		try {
			url = new URL(URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod(method);
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/json");
			if (!token.equalsIgnoreCase("")) {
				conn.setRequestProperty("Authorization", "Bearer " + token);
			}
			if (!length.equalsIgnoreCase("")) {
				conn.setRequestProperty("Content-Length", length);
			}
			if (!md5.equalsIgnoreCase("")) {
				conn.setRequestProperty("Content-MD5", md5);
			}
			if (!date.equalsIgnoreCase("")) {
				conn.setRequestProperty("Date", date);
			}
			if (!transationId.equalsIgnoreCase("")) {
				conn.setRequestProperty("x-transaction-id", transationId);
			}

			OutputStream os = conn.getOutputStream();
			os.write(body.getBytes());
			os.flush();

			if (conn.getResponseCode() != 200) {

				errorHTTP = ("Failed : HTTP error code : " + conn.getResponseCode() + " Message:  "
						+ conn.getResponseMessage());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(

					(conn.getInputStream())));

			String output = "";
			String output1 = "";
			while ((output = br.readLine()) != null) {

				if (output.contains("[") || output.contains("]")) {
					String outputReplace = output.replace("[", "").replaceAll("]", "");
					output1 += outputReplace;
				} else {
					output1 += output;
				}
			}

			ObjectMapper objMapper = new ObjectMapper();
			objMapper.disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES);
			objMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			object = (T) objMapper.readValue(output1, className.getClass());
			conn.disconnect();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
}
