package com.mydomain.security.training.front.controller;

import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.mydomain.security.training.front.conf.Session;

@Controller
public class AuthController {

	@Autowired
	private Session session;

	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	
	
	@PostMapping("/login")
	public String login_submit(HttpServletRequest request, HttpServletResponse response) {
		String userName=request.getParameter("userName");
		if(userName.equals("pepe")) {
			UUID u=UUID.randomUUID();
			UUID check=UUID.randomUUID();
			Cookie c=new Cookie("login", u.toString());
			Cookie c1=new Cookie("check", check.toString());
			response.addCookie(c);
			response.addCookie(c1);
			session.put(u.toString(), check.toString());
		}else
			return "login";
		return "transfer";
	}
	
	





}
