package com.textura.framework.testrail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Map;
import org.json.JSONObject;

public class HttpRequest {

	public static String httpGet(String url, String username, String password) {
		return httpRequest(url, "GET", username, password, null);
	}

	public static String httpPost(String url, String username, String password, Map<String, Object> data) {
		return httpRequest(url, "POST", username, password, data);
	}

	public static String httpRequest(String url, String method, String username, String password, Map<String, Object> data) {
		try {
			URL url_ = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) url_.openConnection();
			connection.setRequestMethod(method);
			connection.addRequestProperty("Content-Type", "application/json");
			String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes("UTF-8"));
			connection.setRequestProperty("Authorization", "Basic " + encoding);

			if (data != null) {
				byte[] block = new JSONObject(data).toString().getBytes("UTF-8");
				connection.setDoOutput(true);
				OutputStream ostream = connection.getOutputStream();
				ostream.write(block);
				ostream.flush();
			}

			int responseCode = connection.getResponseCode();
			InputStream content = null;
			if (responseCode != 200) {
				content = connection.getErrorStream();
				if (content == null) {
					throw new RuntimeException("HTTP get failed " + responseCode + " (No additional error message received)");
				}
			} else {
				content = connection.getInputStream();
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(content));
			StringBuilder result = new StringBuilder();
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
			// if (responseCode != 200) {
			// String d = "";
			// if(data != null) {
			// d = data.toString();
			// }
			// throw new RuntimeException("HTTP get failed for url " + url + " " + username + ":" + password + "\n" + responseCode + "\n"
			// + d + "\n"
			// + result);
			//
			// }
			return result.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
