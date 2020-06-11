package com.mydomain.security.training.front.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.util.HtmlUtils;

import com.mydomain.security.training.front.conf.Session;

@Controller
public class TransferController {
	
	@Autowired
	private Session session;
	
	@PostMapping("/transfer")
	public String transfer(Model model, HttpServletRequest request, HttpServletResponse response) {
		if(!session.checkLogin(request))
			return "login";
		else {
			model.addAttribute("dest",HtmlUtils.htmlEscape(request.getParameter("destination")));
			model.addAttribute("amount",HtmlUtils.htmlEscape(request.getParameter("amount")));
		}
		return "thanks";
	}
	
	

}
