package de.viathinksoft.utils.http;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

public class MyHttpResponse {
	
	private String content;
	private HttpResponse response;
	private int statusCode;

	public MyHttpResponse(HttpResponse response) throws ParseException, IOException {
		super();

		this.response = response;

		HttpEntity ent = response.getEntity();
		if (ent != null) {
			this.content = EntityUtils.toString(ent);
			ent.consumeContent();
		}
		
		this.statusCode = response.getStatusLine().getStatusCode();
	}

	public HttpResponse getResponse() {
		return response;
	}

	public String getContent() {
		return content;
	}

	public int getStatusCode() {
		return statusCode;
	}
	
	public String getHeader(String name) {
		Header[] h = response.getHeaders(name);
		if (h.length != 1) return null;
		return h[0].getValue();
	}
}
