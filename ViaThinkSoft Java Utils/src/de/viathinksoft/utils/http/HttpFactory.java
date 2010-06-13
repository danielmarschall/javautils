package de.viathinksoft.utils.http;

/**
 * This factory produces a HttpUtil instance.
 * The instance is only created once. 
 * @author Daniel Marschall
 */

public class HttpFactory {
	
	static HttpUtils instance = new HttpUtils();

	public static HttpUtils getInstance() {
		return instance;
	}

	private HttpFactory() {
	}

}
