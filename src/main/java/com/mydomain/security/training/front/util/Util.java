package com.mydomain.security.training.front.util;

import com.mydomain.security.training.front.exception.InjectionException;

public class Util {

	
	public static void sanitized(String param) throws InjectionException {
		String pattern = "[A-Za-z0-9]*";
		if (!param.matches(pattern))
			throw new InjectionException();

	}
}
