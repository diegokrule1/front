package com.mydomain.security.training.front.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import com.mydomain.security.training.front.exception.InjectionException;
import com.mydomain.security.training.front.util.SerializeUtil;


@Service
public class FrontService {

	public <T> T transform(String data, Class<T>clazz)throws Exception{
		byte[] decoded=Base64Utils.decodeFromString(data);

		InputStream is=new ByteArrayInputStream(decoded);
	
		ObjectInputStream out = new ObjectInputStream(is);

		return clazz.cast(out.readObject());
	}
	
	
	public <T> T transform_secure(String data, Class<T>clazz)throws Exception{
		byte[] decoded=Base64Utils.decodeFromString(data);

		InputStream is=new ByteArrayInputStream(decoded);
	
		ObjectInputStream out = new ObjectInputStream(is);
		String decodedClassName=SerializeUtil.getClassName(decoded);
		String targetClassName=clazz.getName();
		if(!decodedClassName.equals(targetClassName))
			throw new InjectionException();

		return clazz.cast(out.readObject());
	}
}
