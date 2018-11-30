package com.carvajal.facturaclaro.ral;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

public class MD5GeneratorRAL {

//	public static void main (String [] args) {
//		ContentMD5Base64("{\"companyId\":\"830053800\",\"account\":\"830053800_01\",\"batchId\":\"6003142\",\"packagesPaths\":[\"feco/Transform2/6003142.zip\"]}","MD5"); 
//	String o = "{\"companyId\":\"100123456\",\"account\":\"Cuenta_01\",\"batchId\":\"767\",\"packagesPaths\":[\"feco/Transform2/pruebaDMV.zip\"]}";
//	String b = o.replace("[", ""); 
//	System.out.println("mirar b" + o);
//	}

	public String hash(String Hash, String Modo) {
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance(Modo);
		} catch (NoSuchAlgorithmException ex) {

		}
		m.reset();
		m.update(Hash.getBytes());
		byte[] digest = m.digest();
		BigInteger bigInt = new BigInteger(1, digest);
		String hashtext = bigInt.toString(16);
		while (hashtext.length() < 32) {
			hashtext = "0" + hashtext;
		}
		return ContentMD5Base64(Hash, Modo);
	}

	public static String ContentMD5Base64(String Hash, String Modo) {
		System.out.println(Hash);
		MessageDigest m = null;
		try {
			m = MessageDigest.getInstance(Modo);
		} catch (NoSuchAlgorithmException ex) {

		}
		m.reset();
		m.update(Hash.getBytes(StandardCharsets.UTF_8));
		byte[] digest = m.digest();

		String encodedString = Base64.encodeBase64String(digest);

		System.out.println(encodedString);
		return encodedString;
	}
}