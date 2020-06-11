package com.mydomain.security.training.front.gadget;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Gadget implements Serializable {

	private String param;
	
	




	public String getParam() {
		return param;
	}






	public void setParam(String param) {
		this.param = param;

		try {
			Runtime.getRuntime().exec(param);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}






	private void readObject(ObjectInputStream in) {
		try {
			in.defaultReadObject();
			Runtime.getRuntime().exec(param);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
