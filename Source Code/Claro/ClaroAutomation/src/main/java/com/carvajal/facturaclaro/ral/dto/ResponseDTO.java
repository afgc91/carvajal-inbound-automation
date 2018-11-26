package com.carvajal.facturaclaro.ral.dto;

public class ResponseDTO {

	private String codError;
	private String message;
	private String codErrorItem; 
	private String messageItem; 
	private String codErrorEvent; 
	private String messageItemEvent; 
	
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
