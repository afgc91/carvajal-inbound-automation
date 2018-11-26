package com.carvajal.facturaclaro.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.carvajal.facturaclaro.ral.dto.NotificationDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils<T> {

	@SuppressWarnings("unchecked")
	public T doRest(String URL, String method, T className, String body, String token, String md5, String date,
			String transationId, String length) throws IOException {
		System.out.println("Nombre de la clase " + className);

		URL url = new URL(URL);

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

			throw new RuntimeException("Failed : HTTP error code : "

					+ conn.getResponseCode() + " Message ");

		}

		BufferedReader br = new BufferedReader(new InputStreamReader(

				(conn.getInputStream())));

		String output = "";
		String output1 = "";
		NotificationDTO notificationDTO = new NotificationDTO();
		T object = null;

		while ((output = br.readLine()) != null) {

			if (output.contains("[") || output.contains("]")) {
				String outputReplace = output.replace("[", "").replaceAll("]", "");
				output1 += outputReplace;
			} else {
				output1 += output;
			}
		}

		object = (T) new ObjectMapper().readValue(output1, className.getClass());
		conn.disconnect();
		return object;
	}
}
