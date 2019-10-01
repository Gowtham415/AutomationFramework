package com.textura.framework.communication.protocols;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.CookieList;

public class HTTPResponse {

	public String responseCode;
	public String messageBody;
	public String username;
	public String password;
	public String requestType;
	public String reasonPhrase;
	public String cookie;
	public String location;
	public Header[] headers;
	public static String GET = "GET";
	public static String POST = "POST";
	public static String PUT = "PUT";
	public static String DELETE = "DELETE";
	public static String PATCH = "PATCH";
	public InputStream content;
	public String proxyHost;
	public int proxyPort;

	public HTTPResponse(String url, String requestType, String username, String password, String payload) {
		this(url, requestType, username, password, payload, null);
	}

	public HTTPResponse(String url, String requestType, String username, String password, String payload, List<String> headers) {
		this(url, requestType, username, password, payload, headers, null, 0);
	}

	public HTTPResponse(String url, String requestType, String username, String password, String payload, List<String> headers,
			String proxyHost, int proxyPort) {
		this.username = username;
		this.password = password;
		this.requestType = requestType;
		CloseableHttpClient client;

		if (proxyHost != null) {
			HttpHost proxy = new HttpHost(proxyHost, proxyPort);
			RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
			client = HttpClients.custom().setDefaultRequestConfig(config).build();
		} else {
			client = HttpClients.createDefault();
		}

		try {

			HttpUriRequest httpUriRequest = getHTTPRequest(url);
			if (username != null && password != null) {
				String basicCredentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes("UTF-8"));
				httpUriRequest.setHeader("Authorization", "Basic " + basicCredentials);
			}

			if ((requestType.equals(POST) || requestType.equals(PUT) || requestType.equals(PATCH)) && payload != null) {
				httpUriRequest.setHeader("Content-Type", "application/json");
				HttpEntity entity = new ByteArrayEntity(payload.getBytes());
				((HttpEntityEnclosingRequest) httpUriRequest).setEntity(entity);
			}

			if (headers != null) {
				String[] nameValue = null;
				for (String header : headers) {
					nameValue = header.split(":");
					httpUriRequest.setHeader(nameValue[0].trim(), nameValue[1].trim());
				}
			}

			CloseableHttpResponse response = client.execute(httpUriRequest);

			if (response.getEntity() != null) {
				HttpEntity entity = response.getEntity();
				byte[] contentBytes = EntityUtils.toByteArray(entity);
				this.content = new ByteArrayInputStream(contentBytes);
				this.messageBody = new String(contentBytes);
			}
			this.responseCode = String.valueOf(response.getStatusLine().getStatusCode());
			this.reasonPhrase = String.valueOf(response.getStatusLine().getReasonPhrase());
			this.headers = response.getAllHeaders();

			parseCookie(response);
			parseLocation(response);

			response.close();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public HTTPResponse(String url, String requestType, String cookie, String payload) {
		this(url, requestType, null, null, payload, Arrays.asList("Cookie:" + cookie));
	}

	public String getResponseCode() {
		return responseCode;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public String getReasonPhrase() {
		return reasonPhrase;
	}

	public String getStatusLine() {
		return responseCode + " " + reasonPhrase;
	}

	private HttpUriRequest getHTTPRequest(String uri) {
		if (requestType.equals(GET)) {
			return new HttpGet(uri);
		} else if (requestType.equals(POST)) {
			return new HttpPost(uri);
		} else if (requestType.equals(PUT)) {
			return new HttpPut(uri);
		} else if (requestType.equals(DELETE)) {
			return new HttpDelete(uri);
		} else if (requestType.equals(PATCH)) {
			return new HttpPatch(uri);
		}

		return null;
	}

	public String getSID() {
		String result = null;
		String sidKey = "_SID_=";
		if (cookie != null && cookie.contains(sidKey)) {
			try {
				result = CookieList.toJSONObject(cookie).getString("_SID_");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public String getCookie() {
		return cookie;
	}

	public String getLocation() {
		return location;
	}

	private void parseCookie(CloseableHttpResponse response) {
		String cookieKey = "Set-Cookie";
		if (response.containsHeader(cookieKey)) {
			this.cookie = response.getFirstHeader(cookieKey).getValue();
		}
	}

	private void parseLocation(CloseableHttpResponse response) {
		String locationKey = "Location";
		if (response.containsHeader(locationKey)) {
			this.location = response.getFirstHeader(locationKey).toString();
		}
	}

	public InputStream getContent() {
		return content;
	}

}
