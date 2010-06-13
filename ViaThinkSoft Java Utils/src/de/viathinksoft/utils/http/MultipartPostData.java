package de.viathinksoft.utils.http;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

public class MultipartPostData extends MultipartEntity {

	void addString(String name, String value)
			throws UnsupportedEncodingException {
		this.addPart(name, new StringBody(value));
	}

	void addString(String name, File file) {
		this.addPart(name, new FileBody(file));
	}

}
