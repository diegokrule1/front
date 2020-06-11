package com.mydomain.security.training.front.controller;

import static com.mydomain.security.training.front.util.Util.sanitized;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.owasp.encoder.Encode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.JavaScriptUtils;

//import com.mydomain.security.library.crypto.exception.CryptoException;
//import com.mydomain.security.library.crypto.service.Crypto;
import com.mydomain.security.training.front.exception.InjectionException;
import com.mydomain.security.training.front.util.CryptoUtil;

@Controller
public class FrontController {
	
	private static HttpClient client = HttpClientBuilder.create().build();
	
	@GetMapping("/welcome_html_injectable")
	public String welcome_html_injectable(Model model, HttpServletRequest request) {
		String name=request.getParameter("name");
		model.addAttribute("name",name);
		return "welcome_html";
	}
	
	@GetMapping("/welcome_html_non_injectable")
	public String welcome_non_html_injectable(Model model, HttpServletRequest request) {
		String name=request.getParameter("name");
		String encoded=HtmlUtils.htmlEscape(name);
		model.addAttribute("name",encoded);
		return "welcome_html";
	}
	
	@GetMapping("/welcome_js_injectable")
	public String welcome_js_injectable(Model model, HttpServletRequest request) {
		String name=request.getParameter("name");
		model.addAttribute("name",name);
		return "welcome_js";
	}
	
	@GetMapping("/welcome_js_injectable_bad_escaping")
	public String welcome_js_injectable_bad_escaping(Model model, HttpServletRequest request) {
		String name=request.getParameter("name");
		String encoded=HtmlUtils.htmlEscape(name);
		model.addAttribute("name",encoded);
		return "welcome_js";
	}
	
	
//	//http://localhost:8082/welcome_js_injectable_good_escaping_not_working?name=alert(1);var a='juan';alert(a) 
//	//http://localhost:8082/welcome_js_injectable_good_escaping_not_working?name=alert(1) does not work
	@GetMapping("/welcome_js_injectable_good_escaping_not_working")
	public String welcome_js_injectable_good_escaping(Model model, HttpServletRequest request) {
		String name=request.getParameter("name");
		String encoded=JavaScriptUtils.javaScriptEscape(name);
		model.addAttribute("name",encoded);
		return "welcome_js";
	}
//	
//
//	
//	
//	//http://localhost:8082/welcome_js_injectable_owasp_escaping?name=alert(1);var a='juan';alert(a) works
//	//http://localhost:8082/welcome_js_injectable_owasp_escaping?name=alert(1) does not work
	@GetMapping("/welcome_js_injectable_owasp_escaping")
	public String welcome_js_injectable_owasp_escaping(Model model, HttpServletRequest request) {
		String name=request.getParameter("name");
		String encoded=Encode.forJavaScript(name);
		model.addAttribute("name",encoded);
		return "welcome_js";
	}
	
	
	@GetMapping("/welcome_js_pattern_validation")
	public String welcome_js_pattern_validation(Model model, HttpServletRequest request) {
		String name=request.getParameter("name");
		try {
			sanitized(name);
		} catch (InjectionException e) {
			e.printStackTrace();
			return "error_stateless_with_sign";
		}
		model.addAttribute("name",name);
		return "welcome_js";
	}
	
	@GetMapping("/stateful_step1")
	public String stateful_step1(Model model, HttpServletRequest request) {
		return "stateful_step1";
	}
	
	private Map<String, Map<String,String>>map=new ConcurrentHashMap<>();
	
	private Set<String>credit_cards=new ConcurrentHashMap<>().newKeySet();
	
	public FrontController() {
		Map<String,String>m=new HashMap<>();
		m.put("nombre", "Juana");
		m.put("apellido", "Fernandez");
		map.put("1", m);
	}
	
	
	@GetMapping("/stateful_step1_complete")
	public String stateful_step1_complete (Model model, HttpServletRequest request,RedirectAttributes redirectAttributes, HttpServletResponse response) {
		String id=UUID.randomUUID().toString();
		String credit_card=UUID.randomUUID().toString();
		String name=request.getParameter("nombre");
		String lastName=request.getParameter("apellido");
		try {
			sanitized(name);
			sanitized(lastName);
		} catch (InjectionException e) {
			e.printStackTrace();
			return "error_stateless_with_sign";
		}
		Map<String,String>m=new HashMap<>();
		m.put("nombre", name);
		m.put("apellido", lastName);
		map.put(id, m);
		credit_cards.add(credit_card);
		response.addCookie(new Cookie("secure_token", credit_card));
		model.addAttribute("nombre", name);
		return "redirect:stateful_step2?id="+id;
	}
	

	
	@GetMapping("/stateful_step2")
	public String stateful_step2_ctrl (Model model, HttpServletRequest request) {
		String id=request.getParameter("id");
		Map<String,String>vals=map.get(id);
		for(Entry<String, String> e:vals.entrySet()) {
			model.addAttribute(e.getKey(),e.getValue());
		}
		return "stateful_step2";
	}
		
	
	
	@PostMapping("/purchase")
	public String stateful_step3 (Model model, HttpServletRequest request) {
		String secure_token=getCreditCardCookie(request.getCookies());
		if(secure_token==null || !credit_cards.contains(secure_token))
			return "error_token";
		model.addAttribute("nombre", request.getParameter("nombre"));
		return "success_purchase";
	}
	
	private String getCreditCardCookie(Cookie[] cookies) {
		for(Cookie c:cookies) {
			if(c.getName().equals("secure_token"))
				return c.getValue();
		}
		return null;
	}

	@GetMapping("/stateless_0")
	public String stateless_0 (Model model, HttpServletRequest request) {
		return "stateless_0";
	}
	
	@GetMapping("/stateless_0_crypto_ecb")
	public String stateless_0_crypto_ecb (Model model, HttpServletRequest request) {
		return "stateless_0_crypto_ecb";
	}
	
	
	@PostMapping("/stateless_step1_complete")
	public String stateless_step1_complete (Model model, HttpServletRequest request) {
		String quantity=request.getParameter("cantidad");
		String price=request.getParameter("precio");
		try {
			sanitized(quantity);
			sanitized(price);
		} catch (InjectionException e) {
			e.printStackTrace();
			return "error_stateless_with_sign";
		}
		return String.format("redirect:stateless_step2?quantity=%s&price=%s",quantity,price);
	}
	
	@PostMapping("/stateless_step1_complete_crypto_ecb")
	public String stateless_step1_complete_crypto_ecb (Model model, HttpServletRequest request) {
		String quantity=request.getParameter("cantidad");
		String price=request.getParameter("precio");
		try {
			sanitized(quantity);
			sanitized(price);
			String pt=createPt_ecb(quantity,price);
			String cipher=CryptoUtil.encryptAES_ECB(pt);
			return String.format("redirect:stateless_step2_crypto_ecb?quantity=%s&price=%s&crypto=%s",quantity,price,URLEncoder.encode(cipher,"UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
			return "error_stateless_with_sign";
		}
	}
	
	private String createPt_ecb(String quantity, String price) {
		int quantityLen=quantity.length();
		StringBuilder sb=new StringBuilder();
		int priceLen=price.length();
		sb.append("pr=");
		for(int i=0;i<12-priceLen;i++) {
			sb.append(0);
		}
		sb.append(price);
		sb.append("ct=");
		for(int i=0;i<12-quantityLen;i++) {
			sb.append(0);
		}
		sb.append(quantity);
		return sb.toString();
	}

	@GetMapping("/stateless_step2_crypto_ecb")
	public String stateless_step2_crypto_ecb (Model model, HttpServletRequest request) {
		String quantity=request.getParameter("quantity");
		String price=request.getParameter("price");
		String crypto=request.getParameter("crypto");
		try {
			String plainText=CryptoUtil.decryptAES_ECB(crypto);
			checkIntegrity(plainText,price,quantity);
			sanitized(quantity);
			sanitized(price);
		} catch (Exception e) {
			e.printStackTrace();
			return "error_stateless_with_sign";
		}
		model.addAttribute("quantity",quantity);
		model.addAttribute("price", price);
		return "stateless_step2";
	}
	
	
	private void checkIntegrity(String plainText, String price, String quantity) {
		String priceCheck=plainText.substring(0, 15);
		String quantityCheck=plainText.substring(15,plainText.length());
		String priceInCheck=priceCheck.split("=")[1];
		String quantityInCheck=quantityCheck.split("=")[1];
		if(!(Integer.parseInt(priceInCheck)==Integer.parseInt(price) && Integer.parseInt(quantityInCheck)==Integer.parseInt(quantity)))
				throw new RuntimeException();
		
	}

	@GetMapping("/stateless_step2")
	public String stateless_step2 (Model model, HttpServletRequest request) {
		String quantity=request.getParameter("quantity");
		String price=request.getParameter("price");
		try {
			sanitized(quantity);
			sanitized(price);
		} catch (InjectionException e) {
			e.printStackTrace();
			return "error_stateless_with_sign";
		}
		model.addAttribute("quantity",quantity);
		model.addAttribute("price", price);
		return "stateless_step2";
	}
	
	
	@GetMapping("/stateless_step1_with_sign")
	public String stateless_step1(Model model, HttpServletRequest request) {
		return "stateless_step1_with_sign";
	}
	
	
	@GetMapping("/stateless_step1_with_sign_asymmetric")
	public String stateless_step1_with_sign_asymmetric(Model model, HttpServletRequest request) {
		return "stateless_step1_with_sign_asymmetric";
	}
	
/*
	@GetMapping("/stateless_step1_complete_with_sign")
	public String stateless_step1_complete_with_sign (Model model, HttpServletRequest request)throws Exception {
		String sym_key=System.getProperty("sym_key");
		String name=request.getParameter("nombre");
		String lastName=request.getParameter("apellido");
		try {
			sanitized(name);
			sanitized(lastName);
		} catch (InjectionException e) {
			e.printStackTrace();
			return "error_stateless_with_sign";
		}
		String sign=String.format("nombre:%s&apellido:%s", name, lastName);
		String signed=null;
		try {
			signed=Crypto.signSymmetric(sym_key, sign);

		} catch (CryptoException e) {
			e.printStackTrace();
		}
		String uri=String.format("redirect:stateless_step2_with_sign?name=%s&lastName=%s&sign=%s", name,lastName,URLEncoder.encode(signed,"UTF-8"));
		return uri;
	}
	
	
	@GetMapping("/stateless_step1_complete_with_sign_asymmetric")
	public String stateless_step1_complete_with_sign_asymmetric (Model model, HttpServletRequest request)throws Exception {
		String asym_private_key=System.getProperty("asym_private_key");
		String name=request.getParameter("nombre");
		String lastName=request.getParameter("apellido");
		try {
			sanitized(name);
			sanitized(lastName);
		} catch (InjectionException e) {
			e.printStackTrace();
			return "error_stateless_with_sign";
		}
		String sign=String.format("nombre:%s&apellido:%s", name, lastName);
		String signed=null;
		try {
			signed=Crypto.signAsymmetric(asym_private_key, sign);

		} catch (CryptoException e) {
			e.printStackTrace();
		}
		String uri=String.format("redirect:stateless_step2_with_sign_asymmetric?name=%s&lastName=%s&sign=%s", name,lastName,URLEncoder.encode(signed,"UTF-8"));
		return uri;
	}
	
*/
	@GetMapping("/third")
	public String third (Model model, HttpServletRequest request) {
		return "third";
	}
/*

	@GetMapping("/stateless_step2_with_sign")
	public String stateless_step2_with_sign (Model model, HttpServletRequest request) {
		String sym_key=System.getProperty("sym_key");
		String name=request.getParameter("name");
		String lastName=request.getParameter("lastName");
		String signature=request.getParameter("sign");
		try {
			sanitized(name);
			sanitized(lastName);
		} catch (InjectionException e) {
			e.printStackTrace();
			return "error_stateless_with_sign";
		}
		String sign_template=String.format("nombre:%s&apellido:%s", name, lastName);
		try {
			Boolean check=Crypto.checkSymmetricSignature(sym_key, sign_template, signature);
			if(check) {
				model.addAttribute("name",name);
				model.addAttribute("lastName",lastName);
				return "stateless_step2_with_sign";
			}
		} catch (CryptoException e) {
			
			e.printStackTrace();
		}
		return "error_stateless_with_sign";
	}
	
	
	@GetMapping("/stateless_step2_with_sign_asymmetric")
	public String stateless_step2_with_sign_asymmetric (Model model, HttpServletRequest request) {
		String asym_public_key=System.getProperty("asym_public_key");
		String name=request.getParameter("name");
		String lastName=request.getParameter("lastName");
		String signature=request.getParameter("sign");
		try {
			sanitized(name);
			sanitized(lastName);
		} catch (InjectionException e) {
			e.printStackTrace();
			return "error_stateless_with_sign";
		}
		String sign_template=String.format("nombre:%s&apellido:%s", name, lastName);
		try {
			Boolean check=Crypto.checkAsymetricSignature(asym_public_key, signature, sign_template);
			if(check) {
				model.addAttribute("name",name);
				model.addAttribute("lastName",lastName);
				return "stateless_step2_with_sign";
			}
		} catch (CryptoException e) {
			
			e.printStackTrace();
		}
		return "error_stateless_with_sign";
	}
	
	
*/

	@GetMapping("/injected_from_service")
	public String injected_from_service (Model model, HttpServletRequest request,RedirectAttributes redirectAttributes, HttpServletResponse response) {
		try {
			//ver proyecto availability
			HttpGet get=new HttpGet("http://localhost:8080/injected");
			get.setHeader(HttpHeaders.ACCEPT, "application/json");
			get.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			HttpResponse resp=client.execute(get);
			HttpEntity entity = resp.getEntity();
			String responseAsString = EntityUtils.toString(entity, "UTF-8");
			model.addAttribute("message", responseAsString);
			return "injected_from_service";
		}catch(Exception e) {
			e.printStackTrace();
		}

		return "error_stateless_with_sign";
	}
	
	@GetMapping("/sanitized_injected_from_service")
	public String sanitized_injected_from_service (Model model, HttpServletRequest request,RedirectAttributes redirectAttributes, HttpServletResponse response) {
		try {
			HttpGet get=new HttpGet("http://localhost:8080/injected");
			get.setHeader(HttpHeaders.ACCEPT, "application/json");
			get.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			HttpResponse resp=client.execute(get);
			HttpEntity entity = resp.getEntity();
			String responseAsString = EntityUtils.toString(entity, "UTF-8");
			model.addAttribute("message", HtmlUtils.htmlEscape(responseAsString));
			return "injected_from_service";
		}catch(Exception e) {
			e.printStackTrace();
		}

		return "error_stateless_with_sign";
	}


}
