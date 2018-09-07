package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class CarvajalUtils {
	
	/**
	 * Carga las rutas de los directorios del archivo de entrada.
	 * @throws IOException
	 */
	public static void loadDirectoriesFromFile(String directoriesInOutFilePath, ArrayList<String> directoriesOut) throws IOException {
		try (FileReader fr = new FileReader(directoriesInOutFilePath);
				BufferedReader br = new BufferedReader(fr)) {
			String str = "";
			
			while (true) {
				str = br.readLine();
				if (str == null || str.equals("")) {
					break;
				}
				directoriesOut.add(str);
			}
		}
	}
	
	/**
	 * Retorna las credenciales a partir del archivo cargado por el usuario.
	 * @param awsCredentialsFilePath Ruta del archivo cargado por el usuario.
	 * @return Array con las credenciales.
	 * Pos. 0: Access Key
	 * Pos. 1: Secret Access Key
	 * Pos. 2: Bucket Name
	 * Pos. 3: Region
	 * @throws FileNotFoundException si no encuentra el archivo.
	 * @throws IOException si hay un error de lectura/escritura.
	 */
	public static String[] getCredentialsFromFile(String awsCredentialsFilePath) throws FileNotFoundException, IOException {
		String[] credentials = new String[4];
		try (FileReader fr = new FileReader(awsCredentialsFilePath);
				BufferedReader br = new BufferedReader(fr)) {
			String str = "";
			
			for (int i = 0; i < credentials.length; i++) {
				str = br.readLine();
				credentials[i] = str;
			}
			
			return credentials;
		}
	}
	
	/**
	 * Concatena dos Arrays
	 * @param a Array 1
	 * @param b Array 2
	 * @return Array 1 + Array 2
	 */
	public static <T> T[] concatArrays(T[] a, T[] b) {
	    int aLen = a.length;
	    int bLen = b.length;

	    @SuppressWarnings("unchecked")
	    T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
	    System.arraycopy(a, 0, c, 0, aLen);
	    System.arraycopy(b, 0, c, aLen, bLen);

	    return c;
	}
}
