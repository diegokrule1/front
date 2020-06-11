package key;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Base64;

import org.junit.Test;

public class KeyTest {
	
	
	@Test
	public void testHmacKey1() throws Exception{
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
	    generator.initialize(2048, new SecureRandom());
	    KeyPair pair = generator.generateKeyPair();
	    byte[]privateKey=pair.getPrivate().getEncoded();
	    byte[]publicKey=pair.getPublic().getEncoded();
	    String priv_enc=Base64.getEncoder().encodeToString(privateKey);
	    String public_key=Base64.getEncoder().encodeToString(publicKey);
	    System.out.println(priv_enc);
//	    System.out.println(public_key);
	}

}
