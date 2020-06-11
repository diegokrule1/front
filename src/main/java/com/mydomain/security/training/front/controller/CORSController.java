package com.mydomain.security.training.front.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mydomain.security.training.front.conf.Session;

@RestController
public class CORSController {

	@Autowired
	private Session session;
	
	@CrossOrigin(allowCredentials="true", origins="https://www.mydomain.com:8085")
	@GetMapping("/cors_dangerous")
	public ResponseEntity<String>cors_dangerous(HttpServletRequest request){
		if(session.checkLogin(request))
			return ResponseEntity.ok("Success");
		else
			return ResponseEntity.status(401).build();
	}
	
	@CrossOrigin(allowCredentials="true", origins="https://api.mydomain.com:8085")
	@PutMapping("/same_domain")
	public ResponseEntity<String>same_domain_put(HttpServletRequest request){
		if(session.checkLogin(request))
			return ResponseEntity.ok("Success");
		else
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	}
	
	@RequestMapping(value="/same_domain", method=RequestMethod.OPTIONS)
	public void same_domain(HttpServletRequest request, HttpServletResponse response){
		response.setHeader("Access-Control-Request-Method", "PUT");
	}
	
	@CrossOrigin(allowCredentials="true",origins="*")
	@GetMapping("/cors_ok")
	public ResponseEntity<String>cors_ok(HttpServletRequest request){
		if(session.checkLogin(request))
			return ResponseEntity.ok("Success");
		else
			return ResponseEntity.status(401).build();
	}
}
