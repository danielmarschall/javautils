package de.viathinksoft.utils.http;

// Needs Apache Client 4.x
// http://hc.apache.org/downloads.cgi

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

public class HttpUtils {

	private DefaultHttpClient httpClient;

	public HttpUtils() {
		httpClient = new DefaultHttpClient();
	}
	
	public HttpUtils(String userAgent) {
		this();

		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpProtocolParams.setUserAgent(params, userAgent);
		HttpProtocolParams.setUseExpectContinue(params, true);

		httpClient.setParams(params);
	}

	public DefaultHttpClient getHttpClient() {
		return httpClient;
	}

	public List<Cookie> getAllCookies() {
		return httpClient.getCookieStore().getCookies();
	}

	public void clearAllCookies() {
		httpClient.getCookieStore().clear();
	}

	public MyHttpResponse doGet(String url) throws ClientProtocolException,
			IOException {
		HttpGet httpget = new HttpGet(url);
		HttpResponse response = httpClient.execute(httpget);
		return new MyHttpResponse(response);
	}

	public MyHttpResponse doGet(String url, NameValuePairArray parameters,
			NameValuePairArray requestHeaders) throws ClientProtocolException,
			IOException {
		String myurl = url.concat("?");

		for (NameValuePair p : parameters) {
			myurl = myurl.concat(p.getName()).concat("=").concat(
					URLEncoder.encode(p.getValue(), "UTF-8")).concat("&");
		}

		HttpGet httpget = new HttpGet(myurl);

		for (NameValuePair rh : requestHeaders) {
			httpget.addHeader(rh.getName(), rh.getValue());
		}

		HttpResponse response = httpClient.execute(httpget);
		return new MyHttpResponse(response);
	}

	public MyHttpResponse doGet(String url, NameValuePairArray parameters)
			throws ClientProtocolException, IOException {
		return doGet(url, parameters, new NameValuePairArray());
	}

	public MyHttpResponse doSimplePost(String url, NameValuePairArray postData,
			NameValuePairArray requestHeaders) throws ClientProtocolException,
			IOException {
		HttpPost httppost = new HttpPost(url);

		httppost.setEntity(new UrlEncodedFormEntity(postData, HTTP.UTF_8));

		for (NameValuePair rh : requestHeaders) {
			httppost.addHeader(rh.getName(), rh.getValue());
		}

		HttpResponse response = httpClient.execute(httppost);
		return new MyHttpResponse(response);
	}

	public MyHttpResponse doSimplePost(String url, NameValuePairArray postData)
			throws ClientProtocolException, IOException {
		return doSimplePost(url, postData, new NameValuePairArray());
	}

	public MyHttpResponse doMultiPartPost(String url,
			MultipartPostData postData, NameValuePairArray requestHeaders)
			throws ClientProtocolException, IOException {
		DefaultHttpClient httpclient = new DefaultHttpClient();

		HttpPost httppost = new HttpPost(url);
		httppost.setEntity(postData);

		for (NameValuePair rh : requestHeaders) {
			httppost.addHeader(rh.getName(), rh.getValue());
		}

		HttpResponse response = httpclient.execute(httppost);
		return new MyHttpResponse(response);
	}

	public MyHttpResponse doMultiPartPost(String url, MultipartPostData postData)
			throws ClientProtocolException, IOException {
		return doMultiPartPost(url, postData, new NameValuePairArray());
	}

	protected void writeInputStreamToFile(InputStream inputStream, File outFile)
			throws IOException {
		OutputStream out = new FileOutputStream(outFile);
		byte buf[] = new byte[1024];
		int len;
		while ((len = inputStream.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.close();
		inputStream.close();
	}

	protected void writeResponseToFile(HttpResponse response, File outputFile)
			throws IllegalStateException, IOException {
		File tmp = new File("~~download.tmp");
		writeInputStreamToFile(response.getEntity().getContent(), tmp);
		if (outputFile.exists()) {
			if (!outputFile.delete()) {
				tmp.delete();
				throw new IOException("Destination file already exists and could not be deleted.");
			}
		}

		if (!tmp.renameTo(outputFile)) {
			tmp.delete();
			throw new IOException("File could not moved to destination! Does the destination directory with accurate permissions exist?");
		}
	}

	public void downloadFile(String url, File outputFile,
			NameValuePairArray requestHeaders) throws ClientProtocolException,
			IOException {
		HttpGet httpget = new HttpGet(url);

		for (NameValuePair rh : requestHeaders) {
			httpget.addHeader(rh.getName(), rh.getValue());
		}

		HttpResponse response = httpClient.execute(httpget);
		writeResponseToFile(response, outputFile);
	}

	public void downloadFile(String url, File outputFile)
			throws ClientProtocolException, IOException {
		downloadFile(url, outputFile, new NameValuePairArray());
	}
}
