package com.mydomain.security.training.front.response;

import java.util.List;

import com.mydomain.security.training.front.entity.Customer;

public class CustomerListResponse {
	
	private List<Customer>customers;

	
	
	
	public CustomerListResponse(List<Customer> customers) {
		super();
		this.customers = customers;
	}

	public List<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(List<Customer> customers) {
		this.customers = customers;
	}
	
	

}
