package com.mydomain.security.training.front.controller;

import static com.mydomain.security.training.front.util.Util.sanitized;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

//import com.mydomain.security.library.crypto.service.Crypto;
import com.mydomain.security.training.front.request.JacksonRCERequest;
import com.mydomain.security.training.front.request.ProviderA;
import com.mydomain.security.training.front.response.JacksonRCEResponse;
import com.mydomain.security.training.front.service.FrontService;

@RestController
public class StatelessController {

	private static HttpClient client = HttpClientBuilder.create().build();
	
	@Autowired
	private FrontService frontService;

	/*
	@GetMapping("/check_symmetric")
	public ResponseEntity<String>statelessService(HttpServletRequest request){
		String sym_key=System.getProperty("sym_key");
		String product=request.getParameter("product");
		String price=request.getParameter("price");
		String url_template="http://localhost:8080/check_symmetric";
		String key_to_sign=String.format("product:%s&price:%s", product, price);
		try {
			sanitized(product);
			sanitized(price);
			String signed_data=Crypto.signSymmetric(sym_key, key_to_sign);
			URIBuilder builder=new URIBuilder(url_template);
			builder.addParameter("product", product);
			builder.addParameter("price", price);
			builder.addParameter("sign", signed_data);
			URI uri=builder.build();
			
			HttpGet get=new HttpGet(uri.toString());
			get.setHeader(HttpHeaders.ACCEPT, "application/json");
			get.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			HttpResponse resp=client.execute(get);
			HttpEntity entity = resp.getEntity();
			String responseAsString = EntityUtils.toString(entity, "UTF-8");
			System.out.println(responseAsString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
*/
	

	@GetMapping("/rce")
	public ResponseEntity<User>transformToJson(HttpServletRequest request){
		User u=null;
		try {
			u=frontService.transform(request.getParameter("data"), User.class);
			return ResponseEntity.ok(u);
		}catch(Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	
	//	//BeanDeserializerFactory
	@PostMapping("/jacksonRCE")
	public ResponseEntity<JacksonRCEResponse>rceJackson(@RequestBody JacksonRCERequest request){
		JacksonRCEResponse resp=new JacksonRCEResponse();
		resp.setName("name");
		return ResponseEntity.ok(resp);
	}
	
	
	@GetMapping("/jackson")
	public ResponseEntity<JacksonRCERequest>rceJackson(){
		JacksonRCERequest req=new JacksonRCERequest();
		ProviderA prov=new ProviderA();
		prov.setNum(1);
		req.setInvoice(prov);
		req.setName("name");
		return ResponseEntity.ok(req);
	}
		
		
	


}
