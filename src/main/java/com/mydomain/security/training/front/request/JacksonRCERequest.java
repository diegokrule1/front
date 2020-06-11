package com.mydomain.security.training.front.request;

import java.io.Serializable;

public class JacksonRCERequest {
	
	private Serializable invoice;
	private String name;
	
	

	
	public Serializable getInvoice() {
		return invoice;
	}

	public void setInvoice(Serializable invoice) {
		this.invoice = invoice;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	

}
