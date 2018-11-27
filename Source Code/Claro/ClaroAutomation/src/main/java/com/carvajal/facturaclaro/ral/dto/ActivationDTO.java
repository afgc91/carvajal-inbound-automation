package com.carvajal.facturaclaro.ral.dto;

public class ActivationDTO {

	private String packagesName;
	private String action;
	private String companyId;
	private String batchId;
	private String packageSend;

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
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
