package com.textura.framework.objects.main;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import com.textura.framework.utils.JavaHelpers;

public class HTTPRequestor {
	private static final Logger LOG = LogManager.getLogger(HTTPRequestor.class);
	private static final int BUFFER_SIZE = 128;

	public static String sendPost(String url, String cookie, String parameters){
		StringBuffer response = new StringBuffer();

		try {
			Proxy p = new Proxy(Type.HTTP, new InetSocketAddress("localhost", 8080));
			URL destination = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) destination.openConnection(p);
			conn.setRequestMethod("POST");
			conn.addRequestProperty("Cookie", cookie);
			conn.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(parameters);
			wr.flush();
			wr.close();

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String temp;
			while((temp = in.readLine()) != null){
				response.append(temp + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return response.toString().trim();
	}

	public static void downloadPostFile(String url, Map<String, String> headers, String parameters, String fileName){
		try {
			//String encoded = URLEncoder.encode(parameters);
			File f = new File(fileName);
			FileOutputStream outputStream = new FileOutputStream(f);

			//			Proxy p = new Proxy(Type.HTTP, new InetSocketAddress("localhost", 8080));
			URL destination = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) destination.openConnection();
			conn.setRequestMethod("POST");
			
			for(String key : headers.keySet()){
				conn.addRequestProperty(key, headers.get(key));
			}
			
			conn.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(parameters);
			wr.flush();
			wr.close();

			InputStream in = conn.getInputStream();

			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			while((bytesRead = in.read(buffer)) > 0){
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.close();
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}

	public static boolean downloadGetFile(String url, String cookie, String fileName) {
		FileOutputStream outputStream = null;
		InputStream in = null;
		for (int i = 0; i < 3; i++) {
			try {
				// String encoded = URLEncoder.encode(parameters);
				File f = new File(fileName);
				outputStream = new FileOutputStream(f);

				// Proxy p = new Proxy(Type.HTTP, new InetSocketAddress("localhost", 8080));
				URL destination = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) destination.openConnection();

				conn.setRequestMethod("GET");
				conn.addRequestProperty("Cookie", cookie);
				conn.setDoOutput(true);

				try {
					in = conn.getInputStream();
				} catch (IOException ex) {
					LOG.debug("Failed to read file on server, make sure file exists. Trying again... ");
					sleep(5); // wait for file to generate on server.
						continue;
				}
				int bytesRead = -1;
				byte[] buffer = new byte[BUFFER_SIZE];
				while ((bytesRead = in.read(buffer)) > 0) {
					outputStream.write(buffer, 0, bytesRead);
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (outputStream != null)
					try {
						outputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				if (in != null)
					try {
						in.close();
						if (new File(fileName).length() > 0) {
							return true;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
		return false;
	}
	
	public static InputStream streamGetFile(String url, String cookie){
		InputStream in = null;

			URL destination;
			try {
				destination = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) destination.openConnection();
				conn.setRequestMethod("GET");
				conn.addRequestProperty("Cookie", cookie);
				conn.setDoOutput(true);

				in = conn.getInputStream();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			

			return in;
		
	}
	
	public static HashMap<String, String> getLoginTokens(String url){
		//on foyr_url
		CloseableHttpClient client = HttpClients.createDefault();

		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;");
		httpGet.setHeader("Content-Type", "application/x-www-form-urlencoded");
		HashMap<String, String> result = new HashMap<String, String>();

		CloseableHttpResponse response = null;
		
		try {
			response = client.execute(httpGet);
			client.execute(httpGet);
	
			String responseText = JavaHelpers.convertInputStreamToString(response.getEntity().getContent());
			Header[] cookieHeaders = response.getHeaders("Set-Cookie");
			Pattern csrfToken = Pattern.compile("csrf_token: \"(.*)\"");
			Matcher csrfTokenFinder = csrfToken.matcher(responseText);
			csrfTokenFinder.find();
			result.put("xcsrf", csrfTokenFinder.group(1));
			
			for (Header headerCookie : cookieHeaders) {
				String cookieValue = headerCookie.getValue();
				LOG.debug("FSID COOKIE " +cookieValue);
				if (cookieValue.contains("_fsid_")) {
					int startIndex = cookieValue.indexOf("_fsid_");
					int endIndex = cookieValue.indexOf(";");
					result.put("_fsid_", cookieValue.substring(startIndex, endIndex+1));
				}
			}
		} catch (Exception e) {
		}
		finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		return result;
	}

	public static String getNewCPMSession(String url, String userName, String password, String fsid, String xcsrf){
		CloseableHttpClient client = HttpClients.createDefault();

		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Accept", "application/json, text/javascript, */*; q=0.01");
		httpPost.setHeader("Accept-Language", "en-US,en;q=0.9");
		httpPost.setHeader("Content-Type", "application/json; charset=UTF-8");
		httpPost.setHeader("Cookie", fsid);
		httpPost.setHeader("X-CSRF-Token", xcsrf);
		
		String usernamePassword = "{\"username\":\"" + userName + "\",\"password\":\"" + password + "\"}";
		HttpEntity payload = new ByteArrayEntity(usernamePassword.getBytes());
		httpPost.setEntity(payload);

		CloseableHttpResponse response = null;
		String cookie = "";
		try {
			response = client.execute(httpPost);
			client.execute(httpPost);
			String responseText = JavaHelpers.convertInputStreamToString(response.getEntity().getContent());
			
			
			Header[] cookieHeaders = response.getHeaders("Set-Cookie");
			for (Header headerCookie : cookieHeaders) {
				String cookieValue = headerCookie.getValue();
				if (cookieValue.contains(userName)) {
					int startIndex = cookieValue.indexOf("TEXTURA_AUTH");
					int endIndex = cookieValue.indexOf(";") + 1;

					cookie = cookie + " " + cookieValue.substring(startIndex, endIndex);
				}
				if (cookieValue.contains("_SID_")) {
					int startIndex = cookieValue.indexOf("_SID_");
					int endIndex = cookieValue.indexOf(";") + 1;

					cookie = cookie + " " + cookieValue.substring(startIndex, endIndex);
				}
			}
		} catch (Exception e) {
		}
		finally {
			try {
				response.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		return cookie;
	}

	public static String  getCSRFToken(String url, String cookie, String parameters){
		try {
			//String encoded = URLEncoder.encode(parameters);

			//			Proxy p = new Proxy(Type.HTTP, new InetSocketAddress("localhost", 8080));
			URL destination = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) destination.openConnection();
			conn.setRequestMethod("POST");
			conn.addRequestProperty("Cookie", cookie);
			conn.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(parameters);
			wr.flush();
			wr.close();

			InputStream in;
			if(conn.getResponseCode() == 200){
				in = conn.getInputStream();
			}
			else{
				in = conn.getErrorStream();
			}

			int bytesRead = -1;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[BUFFER_SIZE];
			while((bytesRead = in.read(buffer)) > 0){
				outputStream.write(buffer, 0, bytesRead);
			}
			String csrf = outputStream.toString();
			LOG.debug(csrf);
			String tell = "'csrftoken': '";
			String endTell = "',";
			int begin = csrf.indexOf(tell)+ tell.length();
			csrf = csrf.substring(begin);
			int end = csrf.indexOf(endTell);
			csrf = csrf.substring(0, end);
			return csrf;

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private static void sleep(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
