package de.viathinksoft.utils.http;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class NameValuePairArray extends ArrayList<NameValuePair> {
	
	private static final long serialVersionUID = 8746073419207983648L;

	public void add(String name, String value) {
		this.add(new BasicNameValuePair(name, value));
	}

}
