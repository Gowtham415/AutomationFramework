package com.textura.framework.communication.protocols;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.textura.framework.utils.JavaHelpers;
import sun.security.ssl.HandshakeMessage;

public class HTTPHandler {

	protected static final int HTTP_200 = 200;
	protected static final int HTTP_202 = 202;
	protected static final int HTTP_400 = 400;
	protected static final int HTTP_403 = 403;
	protected static final int HTTP_404 = 404;
	protected static final int HTTP_500 = 500;
	private static final int BUFFER_SIZE = 128;

	public static String getHTTPResponseHeader(String url) throws Exception {

		CloseableHttpClient client;
		client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response = client.execute(httpGet);
		return response.toString();
	}

	/**
	 * Performs an http GET and returns the map object with headers and Status Line.
	 * 
	 * @param url
	 *            The url to send the post
	 * @param username
	 *            The username
	 * @param password
	 *            The username's password
	 * @param fileContents
	 *            Data to send
	 * @return
	 * @throws Exception
	 */

	public static HashMap<String, String> getHTTPResponseHeader(String uri, String userName, String password) {

		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(uri);
		String basicCredentials;
		String result = null;
		CloseableHttpResponse response;
		HashMap<String, String> headersList;
		try {
			basicCredentials = Base64.getEncoder().encodeToString((userName + ":" + password).getBytes("UTF-8"));
			httpGet.setHeader("Authorization", "Basic " + basicCredentials);
			// HttpResponse httpResponse = client.execute(httpGet);
			response = client.execute(httpGet);
			result = EntityUtils.toString(response.getEntity());
			
			Header[] headers = response.getAllHeaders();
			headersList = new HashMap<String, String>();
			headersList.put("responseBodyMessage", result);
			for (Header header : headers) {
				headersList.put(header.getName(), header.getValue());
			}
			System.out.println(headersList.keySet());
			headersList.put("StatusLine", response.getStatusLine().toString());
			response.close();
			client.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

		return headersList;
	}

	/**
	 * Performs an http GET and returns the Status Line Ex:'HTTP/1.1 200 OK'.
	 * 
	 * @param url
	 *            The url to send the post
	 * @param username
	 *            The username
	 * @param password
	 *            The username's password
	 * @param fileContents
	 *            Data to send
	 * @return
	 * @throws Exception
	 */
	public static String getHTTPResponseStatusLine(String uri, String userName, String password) {

		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(uri);
		String basicCredentials;
		CloseableHttpResponse response;
		String statusLine;
		try {
			basicCredentials = Base64.getEncoder().encodeToString((userName + ":" + password).getBytes("UTF-8"));
			httpGet.setHeader("Authorization", "Basic " + basicCredentials);
			response = client.execute(httpGet);
			client.close();
			statusLine = response.getStatusLine().toString();

		} catch (

		Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

		return statusLine;
	}

	/**
	 * Performs an http POST and returns the map object with response along with headers and Status Line.
	 * 
	 * @param url
	 *            The url to send the post
	 * @param username
	 *            The username
	 * @param password
	 *            The username's password
	 * @param fileContents
	 *            Data to send
	 * @return
	 * @throws Exception
	 */
	public static HashMap<String, String> getPostResponseHeadersWithBasicAuth(String url, String username, String password, String fileContents) {

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		String basicCredentials;
		String result = null;
		HashMap<String, String> headersList;
		try {
			basicCredentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes("UTF-8"));
			httpPost.setHeader("Authorization", "Basic " + basicCredentials);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-Type", "application/json");
			HttpEntity entity = new ByteArrayEntity(fileContents.getBytes());
			httpPost.setEntity(entity);

			CloseableHttpResponse response = client.execute(httpPost);

			for (int x = 0; x <= 4; x++) {
				if (response.getStatusLine().getStatusCode() == 403) {
					response.close();
					response = client.execute(httpPost);
				} else {
					break;
				}
			}

			result = EntityUtils.toString(response.getEntity());
			
			headersList = new HashMap<String, String>();
			headersList.put("responseBodyMessage", result);
			Header[] headers = response.getAllHeaders();
			for (Header header : headers) {
				headersList.put(header.getName(), header.getValue());
			}
			headersList.put("StatusLine", response.getStatusLine().toString());
			response.close();
			client.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

		return headersList;
	}

	public static JsonNode getHTTPResponseBodyAsJson(String url) throws Exception {

		CloseableHttpClient client;
		client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response = client.execute(httpGet);
		String result = EntityUtils.toString(response.getEntity());
		response.close();
		client.close();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
		mapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));
		mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
		JsonNode jasonNode = mapper.readTree(result);
		return jasonNode;
	}

	public static JsonNode getHTTPResponseBodyAsJson(String url, String userName, String password) throws Exception {

		// Credential setup
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		AuthScope scope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT);
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(userName, password);
		credsProvider.setCredentials(scope, creds);

		CloseableHttpClient client;
		client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
		HttpGet httpGet = new HttpGet(url);

		CloseableHttpResponse response = client.execute(httpGet);
		String result = EntityUtils.toString(response.getEntity());
		response.close();
		client.close();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
		mapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));
		mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
		JsonNode jasonNode = mapper.readTree(result);
		return jasonNode;
	}

	public static String getHTTPResponseBody(String url) {

		CloseableHttpClient client;
		client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		String result = null;
		CloseableHttpResponse response;
		JsonNode jsonNode = null;
		try {
			response = client.execute(httpGet);
			result = EntityUtils.toString(response.getEntity());
			response.close();
			client.close();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
			mapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));
			mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
			jsonNode = mapper.readTree(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

		return jsonNode.toString();
	}

	/**
	 * Performs an http GET with pre-emptive basic authorization
	 * 
	 * @param url
	 *            The url to send the post
	 * @param username
	 *            The username
	 * @param password
	 *            The username's password
	 * @return
	 * @throws Exception
	 */
	public static String getHTTPRequestWithPreEmptiveBasicAuth(String url, String username, String password) throws Exception {

		// Credential setup
		HttpHost target = new HttpHost(new URI(url).getHost());

		// Credential setup
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		AuthScope scope = new AuthScope(target.getHostName(), AuthScope.ANY_PORT);
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
		credsProvider.setCredentials(scope, creds);

		CloseableHttpClient client;
		client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

		// Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();
		// Generate BASIC scheme object and add it to the local
		// auth cache
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(target, basicAuth);

		// Add AuthCache to the execution context
		HttpClientContext localContext = HttpClientContext.create();
		localContext.setAuthCache(authCache);

		HttpGet httpGet = new HttpGet(url);

		CloseableHttpResponse response = client.execute(target, httpGet, localContext);
		String result = EntityUtils.toString(response.getEntity());
		response.close();

		client.close();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
		mapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));
		mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
		JsonNode jasonNode = mapper.readTree(result);
		return jasonNode.toString();
	}

	public static String getHTTPResponseBody(String url, String userName, String password) throws Exception {

		// Credential setup
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		AuthScope scope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT);
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(userName, password);
		credsProvider.setCredentials(scope, creds);

		CloseableHttpClient client;
		client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response = client.execute(httpGet);
		String result = EntityUtils.toString(response.getEntity());
		response.close();
		client.close();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
		mapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));
		mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
		JsonNode jasonNode = mapper.readTree(result);
		return jasonNode.toString();
	}

	public static String postHTTPRequest(String url) throws Exception {

		CloseableHttpClient client;
		client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		CloseableHttpResponse response = client.execute(httpPost);
		String result = EntityUtils.toString(response.getEntity());
		response.close();
		client.close();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
		mapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));
		mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
		JsonNode jasonNode = mapper.readTree(result);
		return jasonNode.toString();
	}

	// more reliable and works without using a deprecated method
	public static String getHTTPRequestWithBasicAuth(String url, String username, String password) {

		// Credential setup
		CloseableHttpClient client = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url);
		String basicCredentials;
		JsonNode jsonNode = null;
		try {
			basicCredentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes("UTF-8"));
			httpGet.setHeader("Authorization", "Basic " + basicCredentials);
			CloseableHttpResponse response = client.execute(httpGet);
			String result = EntityUtils.toString(response.getEntity());
			response.close();
			client.close();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
			mapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));
			mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
			jsonNode = mapper.readTree(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

		return jsonNode.toString();
	}

	public static String putHTTPRequestAsJSON(String url, String fileContents) throws Exception {

		CloseableHttpClient client;
		client = HttpClients.createDefault();
		HttpPut httpPut = new HttpPut(url);
		httpPut.setEntity(new StringEntity(fileContents, ContentType.create("application/json")));
		HttpResponse httpResponse = client.execute(httpPut);
		String response = JavaHelpers.convertInputStreamToString(httpResponse.getEntity().getContent());
		client.close();
		return response;
	}

	public static String postHTTPRequestAsJSON(String url, String fileContents) throws Exception {

		CloseableHttpClient client;
		client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(new StringEntity(fileContents, ContentType.create("application/json")));
		HttpResponse httpResponse = client.execute(httpPost);
		String response = JavaHelpers.convertInputStreamToString(httpResponse.getEntity().getContent());
		client.close();
		return response;
	}

	/**
	 * Performs an http POST with pre-emptive basic authorization
	 * 
	 * @param url
	 *            The url to send the post
	 * @param username
	 *            The username
	 * @param password
	 *            The username's password
	 * @param fileContents
	 *            Data to send
	 * @return
	 * @throws Exception
	 */
	public static String getPostResponseWithPreEmptiveBasicAuth(String url, String username, String password, String fileContents)
			throws Exception {

		HttpHost target = new HttpHost(new URI(url).getHost());

		// Credential setup
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		AuthScope scope = new AuthScope(target.getHostName(), AuthScope.ANY_PORT);
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password);
		credsProvider.setCredentials(scope, creds);
		CloseableHttpClient client;

		// Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();
		// Generate BASIC scheme object and add it to the local
		// auth cache
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(target, basicAuth);

		// Add AuthCache to the execution context
		HttpClientContext localContext = HttpClientContext.create();
		localContext.setAuthCache(authCache);

		client = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/json");
		HttpEntity entity = new ByteArrayEntity(fileContents.getBytes("utf-8"));
		httpPost.setEntity(entity);

		HttpResponse httpResponse = client.execute(target, httpPost, localContext);
		String response = EntityUtils.toString(httpResponse.getEntity());

		client.close();
		return response;
	}

	/**
	 * Performs an http POST and returns the response.
	 * 
	 * @param url
	 * @param org
	 * @return
	 * @throws Exception
	 */
	public static String getPostResponseWithBasicAuth(String url, String username, String password) {

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		String basicCredentials;
		String result = null;
		try {
			basicCredentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes("UTF-8"));
			httpPost.setHeader("Authorization", "Basic " + basicCredentials);
			CloseableHttpResponse response = client.execute(httpPost);

			for (int x = 0; x <= 4; x++) {
				if (response.getStatusLine().getStatusCode() == 403) {
					response.close();
					response = client.execute(httpPost);
				} else {
					break;
				}
			}

			result = EntityUtils.toString(response.getEntity());
			response.close();
			client.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

		return result;
	}

	/**
	 * Performs an http POST through a proxy and returns the response.
	 * 
	 * @param url
	 * @param org
	 * @return
	 * @throws Exception
	 */
	public static String getPostResponseWithBasicAuthWithProxy(String url, String username, String password, String proxyHost,
			int proxyPort) {

		CloseableHttpClient client = HttpClients.createDefault();
		HttpHost proxy = new HttpHost(proxyHost, proxyPort);
		RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(config);
		String basicCredentials;
		String result = null;
		try {
			basicCredentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes("UTF-8"));
			httpPost.setHeader("Authorization", "Basic " + basicCredentials);
			CloseableHttpResponse response = client.execute(httpPost);

			for (int x = 0; x <= 4; x++) {
				if (response.getStatusLine().getStatusCode() == 403) {
					response.close();
					response = client.execute(httpPost);
				} else {
					break;
				}
			}

			result = EntityUtils.toString(response.getEntity());
			response.close();
			client.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

		return result;
	}

	/**
	 * Performs an http POST and returns the response.
	 * 
	 * @param url
	 *            The url to send the post
	 * @param username
	 *            The username
	 * @param password
	 *            The username's password
	 * @param fileContents
	 *            Data to send
	 * @return
	 * @throws Exception
	 */
	public static String getPostResponseWithBasicAuth(String url, String username, String password, String fileContents) {

		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		String basicCredentials;
		String result = null;
		try {
			basicCredentials = Base64.getEncoder().encodeToString((username + ":" + password).getBytes("UTF-8"));
			httpPost.setHeader("Authorization", "Basic " + basicCredentials);
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-Type", "application/json");
			HttpEntity entity = new ByteArrayEntity(fileContents.getBytes());
			httpPost.setEntity(entity);

			CloseableHttpResponse response = client.execute(httpPost);

			for (int x = 0; x <= 4; x++) {
				if (response.getStatusLine().getStatusCode() == 403) {
					response.close();
					response = client.execute(httpPost);
				} else {
					break;
				}
			}

			result = EntityUtils.toString(response.getEntity());
			response.close();
			client.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

		return result;
	}

	public static String getHTTPResponseBodyFromInputStream(String uri) throws Exception {

		CloseableHttpClient client;
		client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(uri);
		HttpResponse httpResponse = client.execute(httpGet);
		client.close();
		String auditReport = JavaHelpers.convertInputStreamToString(httpResponse.getEntity().getContent());
		return auditReport;
	}

	public static String getHTTPResponseBodyFromInputStream(String uri, String userName, String password) {

		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(uri);
		String basicCredentials;
		String auditReport = null;
		try {
			basicCredentials = Base64.getEncoder().encodeToString((userName + ":" + password).getBytes("UTF-8"));
			httpGet.setHeader("Authorization", "Basic " + basicCredentials);
			HttpResponse httpResponse = client.execute(httpGet);
			client.close();
			auditReport = JavaHelpers.convertInputStreamToString(httpResponse.getEntity().getContent());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}

		return auditReport;
	}

	public static String getHTTPResponseBodyIBIS(String url) throws Exception {
		boolean emptyVendorError = false;
		boolean zeroVendorError = false;
		boolean queryError = false;
		if (url.endsWith("/")) {
			emptyVendorError = true;
		} else if (url.endsWith("0")) {
			zeroVendorError = true;
		} else if (url.contains("?name=")) {
			queryError = true;
		}
		CloseableHttpClient client;
		client = HttpClients.createDefault();
		HttpGet httpGet;
		try {
			httpGet = new HttpGet(url);
		} catch (IllegalArgumentException e) {
			// IllegalArgument error
			if (queryError) {
				return "Error or no record found";
			} else {
				return "Not Found\nThe requested URL was not found on the server. "
						+ "If you entered the URL manually please check your spelling " + "and try again.";
			}
		}
		CloseableHttpResponse response = client.execute(httpGet);
		String result = EntityUtils.toString(response.getEntity());
		response.close();
		client.close();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
		mapper.setNodeFactory(JsonNodeFactory.withExactBigDecimals(true));
		mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
		JsonNode jasonNode = null;
		try {
			jasonNode = mapper.readTree(result);
		} catch (JsonParseException e) {
			if (!emptyVendorError && !zeroVendorError) {
				return "Error or no record found";
			} else {
				if (emptyVendorError) {
					return "Not Found\nThe requested URL was not found on the server. "
							+ "If you entered the URL manually please check your spelling " + "and try again.";
				}
				if (zeroVendorError) {
					return "Request is not properly formatted";
				}
			}
		}
		return jasonNode.toString();
	}

	public static String getResponseCode(String url, String username, String password) {

		HttpURLConnection conn = null;
		String response = "NO RESPONSE";
		try {
			URL url_ = new URL(url);

			conn = (HttpURLConnection) url_.openConnection();
			// request only the header of the http response
			conn.setRequestMethod("HEAD");
			String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes("UTF-8"));
			conn.setRequestProperty("Authorization", "Basic " + encoding);

			response = String.valueOf(conn.getResponseCode());

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return response;
	}

	public static String getResponseHeader(String url, String username, String password) {

		HttpURLConnection conn = null;
		String response = "NO RESPONSE";
		try {
			URL url_ = new URL(url);

			conn = (HttpURLConnection) url_.openConnection();
			// request only the header of the http response
			conn.setRequestMethod("HEAD");
			String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes("UTF-8"));
			conn.setRequestProperty("Authorization", "Basic " + encoding);

			response = String.valueOf(conn.getResponseCode());

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return response;
	}

	/**
	 * Uses basic authorization to connect and save the contents of the response at the given
	 * file location.
	 * 
	 * @param url
	 * @param org
	 * @param filePath
	 * @return The filepath
	 * @throws Exception
	 */
	public static boolean getHTTPRequestContent(String method, String url, String username, String pw, String payload, String filePath) {

		try {
			File f = new File(filePath);
			FileOutputStream outputStream = new FileOutputStream(f);

			CloseableHttpClient client = HttpClients.createDefault();
			HttpRequestBase httpRequest = null;
			if (method.equalsIgnoreCase("post")) {
				httpRequest = new HttpPost(url);
				if (payload != null) {
					HttpEntity entity = new ByteArrayEntity(payload.getBytes());
					((HttpPost) httpRequest).setEntity(entity);
				}
			} else {
				httpRequest = new HttpGet(url);
			}

			String basicCredentials = Base64.getEncoder().encodeToString((username + ":" + pw).getBytes("UTF-8"));
			httpRequest.setHeader("Authorization", "Basic " + basicCredentials);
			httpRequest.setHeader("Accept", "application/json");
			httpRequest.setHeader("Content-Type", "application/json");
			CloseableHttpResponse response = client.execute(httpRequest);
			InputStream in = response.getEntity().getContent();

			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((bytesRead = in.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.close();
			in.close();

			response.close();
			client.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static String getPutRequestResponseCode(String url, String payload) {
		return new HTTPResponse(url, HTTPResponse.PUT, null, null, payload).getResponseCode();
	}

	public static String getPostRequestResponseCodeWithAuth(String url, String orgName, String orgPassword, String payload) {
		return new HTTPResponse(url, HTTPResponse.POST, orgName, orgPassword, payload).getResponseCode();
	}

	public static String getPostRequestResponseCode(String url, String payload) {
		return new HTTPResponse(url, HTTPResponse.POST, null, null, payload).getResponseCode();
	}

	public static String getDeleteRequestResponseCode(String url, String payload) {
		return new HTTPResponse(url, HTTPResponse.DELETE, null, null, payload).getResponseCode();
	}

	public static HTTPResponse getRequestResponse(String apiUrl, String curlCommandTextFilePath) {
		String requestType, username, password, payload;
		requestType = username = password = payload = null;
		String[] usernameArr = null;
		List<String> headers = null;
		Iterator<String> iterator = null;

		try {

			String curlCommand = JavaHelpers.readFileAsString(curlCommandTextFilePath);
			curlCommand = curlCommand.replaceAll(": ", ":")
					.replaceAll(" :", ":")
					.replaceAll("--request", "-X")
					.replaceAll("--user", "-u")
					.replaceAll("--header", "-H")
					.replaceAll("--data", "-d");

			List<String> curlCommandList = new ArrayList<String>(Arrays.asList(curlCommand.split(" ")));

			if (curlCommand.contains("-X ")) {
				requestType = curlCommandList.get(curlCommandList.indexOf("-X") + 1);
			}

			if (curlCommand.contains("-u ")) {
				usernameArr = curlCommandList.get(curlCommandList.indexOf("-u") + 1).split(":");
				username = usernameArr[0];
				if (usernameArr.length == 2) {
					password = usernameArr[1];
				}
			}

			if (curlCommand.contains("-H ")) {
				headers = new ArrayList<String>();
				int firstHeader = curlCommandList.indexOf("-H");
				int lastHeader = curlCommandList.lastIndexOf("-H");

				if (firstHeader == lastHeader) {
					iterator = curlCommandList.subList(firstHeader, firstHeader + 2).iterator();
				} else {
					iterator = curlCommandList.subList(firstHeader, lastHeader).iterator();
				}

				while (iterator.hasNext()) {
					String current = iterator.next().toString();
					if (current.equals("-H")) {
						headers.add(iterator.next().toString().replaceAll("\"", ""));
					}
				}
			}

			if (curlCommand.contains("-d '")) {
				int bodyStart = curlCommand.indexOf("'") + 1;
				int bodyEnd = curlCommand.lastIndexOf("'");
				payload = curlCommand.substring(bodyStart, bodyEnd);
			}

		} catch (Exception e) {
			System.out.println(
					"Failure processing curl command. Make sure to follow Linux curl command conventions. Only these curl options are supported: -X, -u, -H, -d in the curl command.");
		}
		return new HTTPResponse(apiUrl, requestType, username, password, payload, headers);
	}

	public static String getRequestResponseCode(String apiUrl, String curlCommandTextFilePath) {
		return getRequestResponse(apiUrl, curlCommandTextFilePath).getResponseCode();
	}

}