package com.mydomain.security.training.front.conf;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

@Component
public class Session {
	
	private Map<String, String>session=new ConcurrentHashMap<>();
	
	public Boolean checkLogin(HttpServletRequest request) {
		Cookie[]cookies=request.getCookies();
		String loginCookie=null;
		String checkCookie=null;
		if(cookies==null)
			return false;
		for(Cookie c:cookies) {
			if(c.getName().equals("login")) {
				loginCookie=c.getValue();
				continue;
			}
			if(c.getName().equals("check")) {
				checkCookie=c.getValue();
			}
		}
		if(loginCookie==null|| checkCookie==null)
			return false;
		
		if(!session.get(loginCookie).equals(checkCookie))
			return false;
		
		return true;
	}
	
	public void put(String key, String value) {
		this.session.put(key, value);
	}

}
