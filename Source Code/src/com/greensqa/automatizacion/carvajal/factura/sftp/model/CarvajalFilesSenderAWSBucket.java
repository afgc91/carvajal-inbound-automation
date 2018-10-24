package com.greensqa.automatizacion.carvajal.factura.sftp.model;

import java.io.File;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class CarvajalFilesSenderAWSBucket {
	private String key;
	private String secretKey;
	private static String bucketName;
	private String region;
	private BasicAWSCredentials credentials;
	private static AmazonS3 s3Client;

	/**
	 * Constructor de la clase CarvajalFilesSenderAWSBucket, la cual permite enviar
	 * archivos hacia un bucket S3 de Amazon.
	 * 
	 * @param key Clave de acceso.
	 * @param secretKey Clave secreta.
	 * @param bucketName Nombre del bucket AWS.
	 * @param region Región geográfica AWS.
	 */
	public CarvajalFilesSenderAWSBucket(String key, String secretKey, String bucketName, String region) {
		this.key = key;
		this.secretKey = secretKey;
		this.bucketName = bucketName;
		this.region = region;
		this.credentials = new BasicAWSCredentials(key, secretKey);
		this.s3Client = AmazonS3Client.builder().withRegion(region)
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
	}

	/**
	 * Permite enviar un archivo hacia el bucket S3.
	 * @param keyName Nombre del archivo dentro del bucket S3 (los delimitadores "/" se usan como estructuras de
	 * directorios dentro del bucket.
	 * @param file Archivo local que se enviará al bucket S3.
	 */
	public void moveFileToS3Bucket(String keyName, File file) {
		try {
			s3Client.putObject(new PutObjectRequest(bucketName, keyName, file));
		} catch (SdkClientException ex) {
			ex.printStackTrace();
		}
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public BasicAWSCredentials getCredentials() {
		return credentials;
	}

	public void setCredentials(BasicAWSCredentials credentials) {
		this.credentials = credentials;
	}

	public AmazonS3 getS3Client() {
		return s3Client;
	}

	public void setS3Client(AmazonS3 s3Client) {
		this.s3Client = s3Client;
	}
}
