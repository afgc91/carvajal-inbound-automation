package com.carvajal.facturaclaro.ral.dto;

public class NotificationDTO {
	
	private String companyId; 
	private String account;
	private String batchId;
	private String packagesPaths;
	
	public String getPackagesPaths() {
		return packagesPaths;
	}
	public void setPackagesPaths(String packagesPaths) {
		this.packagesPaths = packagesPaths;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	} 
}
