package com.mydomain.security.training.front.util;

import org.apache.commons.codec.binary.Hex;

public class SerializeUtil {

	public static String getClassName(String[] hexArray) {
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

	public static String[] getHexaArray(String hexa) {
		String[] hex_arr = new String[hexa.length() / 2];
		for (int i = 0; i < hex_arr.length; i++) {
			hex_arr[i] = hexa.substring(2 * i, 2 * (i + 1));
		}
		return hex_arr;
	}

	public static String getClassName(byte[] serializedObject) {
		String hexa = Hex.encodeHexString(serializedObject);
		String[] hexArray = getHexaArray(hexa);
		String className = getClassName(hexArray);
		return className;

	}

}
