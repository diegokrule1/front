package com.mydomain.security.training.front.util;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtil {

	public static String encryptAES_ECB(String data) throws Exception{
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		String aesKey=System.getProperty("AES_KEY");
		byte[]key_decoded=Base64.getDecoder().decode(aesKey);
		SecretKey secret_key=new SecretKeySpec(key_decoded, "AES");
		cipher.init(Cipher.ENCRYPT_MODE, secret_key);
		byte[]cipher_text=cipher.doFinal(data.getBytes());
		return Base64.getEncoder().encodeToString(cipher_text);
	
	}
	
	public static String decryptAES_ECB(String data) throws Exception{
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		String aesKey=System.getProperty("AES_KEY");
		byte[]key_decoded=Base64.getDecoder().decode(aesKey);
		SecretKey secret_key=new SecretKeySpec(key_decoded, "AES");
		cipher.init(Cipher.DECRYPT_MODE, secret_key);
		byte[]plain_text_bytes=cipher.doFinal(Base64.getDecoder().decode(data.getBytes()));
		return new String(plain_text_bytes);
	}
	

}
