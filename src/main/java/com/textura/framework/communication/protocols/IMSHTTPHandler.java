package com.textura.framework.communication.protocols;

import java.util.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class IMSHTTPHandler {

	protected static final int HTTP_200 = 200;
	protected static final int HTTP_202 = 202;
	protected static final int HTTP_400 = 400;
	protected static final int HTTP_403 = 403;
	protected static final int HTTP_404 = 404;
	protected static final int HTTP_500 = 500;
	private static final int BUFFER_SIZE = 128;

	public static String getHTTPResponse(String url) throws Exception {
		CloseableHttpClient client;
		client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response = client.execute(httpGet);
		String result = "";
		try {
			result = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			StatusLine statusLine = response.getStatusLine();
			result = statusLine.getStatusCode() + " " + statusLine.getReasonPhrase();
		}
		response.close();
		client.close();
		return result;
	}

	public static String getHTTPResponseWithBasicAuth(String url, String username, String password) throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url);
		String basicCredentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes("UTF-8"));
		httpGet.setHeader("Authorization", "Basic " + basicCredentials);
		CloseableHttpResponse response = client.execute(httpGet);
		String result = "";
		try {
			result = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			StatusLine statusLine = response.getStatusLine();
			result = statusLine.getStatusCode() + " " + statusLine.getReasonPhrase();
		}
		response.close();
		client.close();
		return result;
	}

	public static String getHTTPResponseWithCookie(String url, String cookie) throws Exception {

		CloseableHttpClient client = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Content-Type", "application/json");
		httpGet.addHeader("Cookie", cookie);
		CloseableHttpResponse response = client.execute(httpGet);
		String result = "";
		try {
			result = EntityUtils.toString(response.getEntity());
			if (result.equals("")) {
				throw new Exception();
			}
		} catch (Exception e) {
			StatusLine statusLine = response.getStatusLine();
			result = statusLine.getStatusCode() + " " + statusLine.getReasonPhrase();
		}
		response.close();
		client.close();
		return result;
	}

	public static String getPostHTTPResponse(String url, String username, String password, String payload) throws Exception {
		CloseableHttpClient client = HttpClients.createDefault();

		HttpPost httpPost = new HttpPost(url);
		if (username != null && password != null) {
			String basicCredentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes("UTF-8"));
			httpPost.setHeader("Authorization", "Basic " + basicCredentials);
		}
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/json");
		HttpEntity entity = new ByteArrayEntity(payload.getBytes());
		httpPost.setEntity(entity);
		CloseableHttpResponse response = client.execute(httpPost);
		String result = "";
		try {
			result = EntityUtils.toString(response.getEntity());
			if (result.equals("")) {
				throw new Exception();
			}
		} catch (Exception e) {
			StatusLine statusLine = response.getStatusLine();
			result = statusLine.getStatusCode() + " " + statusLine.getReasonPhrase();
		}
		response.close();
		client.close();
		return result;
	}

}
