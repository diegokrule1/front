package com.mydomain.security.training.front.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mydomain.security.training.front.entity.Customer;
import com.mydomain.security.training.front.repository.CustomerRepository;
import com.mydomain.security.training.front.response.CustomerListResponse;

@RestController
public class ORMController {

	@Autowired
	private CustomerRepository repo;
	
	@GetMapping("/customerList")
	public ResponseEntity<CustomerListResponse>getCustomerByName(HttpServletRequest request){
		//checkPermissions
		String name=request.getParameter("name");
		//validate
		List<Customer>cus=repo.getCustomerByName(name);
		return ResponseEntity.ok(new CustomerListResponse(cus));
	}
}
