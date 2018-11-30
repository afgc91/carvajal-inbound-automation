package com.carvajal.facturaclaro.ral.dto;

public class ResponseDTO {

	private String codError;
	private String message;
	private String codErrorItem; 
	private String messageItem; 
	private String codErrorEvent; 
	private String messageItemEvent; 
	private String eventMessage; 
	private String packageRetentionMessage; 
	private String packageNotRetentionMessage; 
	private String eventFailMessage; 
	private String eventCancelMessage; 
	private String cufeAlertMessage; 
	private String fileRenameMessage; 
	private String failSendMessage; 
	private String cancelSendMessage; 
	
	public String getEventMessage() {
		return eventMessage;
	}
	public void setEventMessage(String eventMessage) {
		this.eventMessage = eventMessage;
	}
	public String getPackageRetentionMessage() {
		return packageRetentionMessage;
	}
	public void setPackageRetentionMessage(String packageRetentionMessage) {
		this.packageRetentionMessage = packageRetentionMessage;
	}
	public String getPackageNotRetentionMessage() {
		return packageNotRetentionMessage;
	}
	public void setPackageNotRetentionMessage(String packageNotRetentionMessage) {
		this.packageNotRetentionMessage = packageNotRetentionMessage;
	}
	public String getEventFailMessage() {
		return eventFailMessage;
	}
	public void setEventFailMessage(String eventFailMessage) {
		this.eventFailMessage = eventFailMessage;
	}
	public String getEventCancelMessage() {
		return eventCancelMessage;
	}
	public void setEventCancelMessage(String eventCancelMessage) {
		this.eventCancelMessage = eventCancelMessage;
	}
	public String getCufeAlertMessage() {
		return cufeAlertMessage;
	}
	public void setCufeAlertMessage(String cufeAlertMessage) {
		this.cufeAlertMessage = cufeAlertMessage;
	}
	public String getFileRenameMessage() {
		return fileRenameMessage;
	}
	public void setFileRenameMessage(String fileRenameMessage) {
		this.fileRenameMessage = fileRenameMessage;
	}
	public String getFailSendMessage() {
		return failSendMessage;
	}
	public void setFailSendMessage(String failSendMessage) {
		this.failSendMessage = failSendMessage;
	}
	public String getCancelSendMessage() {
		return cancelSendMessage;
	}
	public void setCancelSendMessage(String cancelSendMessage) {
		this.cancelSendMessage = cancelSendMessage;
	}
	public String getCodError() {
		return codError;
	}
	public void setCodError(String codError) {
		this.codError = codError;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCodErrorItem() {
		return codErrorItem;
	}
	public void setCodErrorItem(String codErrorItem) {
		this.codErrorItem = codErrorItem;
	}
	public String getMessageItem() {
		return messageItem;
	}
	public void setMessageItem(String messageItem) {
		this.messageItem = messageItem;
	}
	public String getCodErrorEvent() {
		return codErrorEvent;
	}
	public void setCodErrorEvent(String codErrorEvent) {
		this.codErrorEvent = codErrorEvent;
	}
	public String getMessageItemEvent() {
		return messageItemEvent;
	}
	public void setMessageItemEvent(String messageItemEvent) {
		this.messageItemEvent = messageItemEvent;
	}
}
