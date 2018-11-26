package com.carvajal.facturaclaro.ral.dto;

public class ActivationDTO {
	
	private String packagesName; 
	private String action;     
	private String companyID; 
	private String batchID;
	private String packageSend;
	
	public String getCompanyID() {
		return companyID;
	}
	public void setCompanyID(String companyID) {
		this.companyID = companyID;
	}
	public String getBatchID() {
		return batchID;
	}
	public void setBatchID(String batchID) {
		this.batchID = batchID;
	}
	public String getPackageSend() {
		return packageSend;
	}
	public void setPackageSend(String packageSend) {
		this.packageSend = packageSend;
	}
	public String getPackagesName() {
		return packagesName;
	}
	public void setPackagesName(String packagesName) {
		this.packagesName = packagesName;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
}
