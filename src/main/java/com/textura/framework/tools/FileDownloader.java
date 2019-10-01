package com.textura.framework.tools;

import java.io.File;
import java.net.URL;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.openqa.selenium.WebDriver;

public class FileDownloader {

	private WebDriver driver;
	private BasicCookieStore mimicWebDriverCookieStore = new BasicCookieStore();

	public FileDownloader(WebDriver driverObject) {
		this.driver = driverObject;
	}

	public static FileDownloader setDriver(WebDriver driver) {
		return new FileDownloader(driver);
	}

	private BasicCookieStore mimicCookieState(Set<org.openqa.selenium.Cookie> seleniumCookieSet) {
		for (org.openqa.selenium.Cookie seleniumCookie : seleniumCookieSet) {
			BasicClientCookie cookie = new BasicClientCookie(seleniumCookie.getName(), seleniumCookie.getValue());
			cookie.setDomain(seleniumCookie.getDomain());
			cookie.setSecure(seleniumCookie.isSecure());
			cookie.setExpiryDate(seleniumCookie.getExpiry());
			cookie.setPath(seleniumCookie.getPath());
			mimicWebDriverCookieStore.addCookie(cookie);
		}
		return mimicWebDriverCookieStore;
	}

	/**
	 * Downloads a file using an HTTP request with the cookies in the webdriver.
	 * 
	 * @param url
	 * @param fullFilePath
	 *            full path to save file
	 * @return path to the downloaded file
	 */
	public String downloadFile(String url, String fullFilePath) {
		File downloadedFile = new File(fullFilePath);
		int status = -1;
		try {
			URL fileToDownload = new URL(url);
			HttpClient client = HttpClientBuilder.create().setDefaultCookieStore(mimicCookieState(this.driver.manage().getCookies())).build();
			HttpGet request = new HttpGet(fileToDownload.toURI());
			HttpResponse response = client.execute(request);
			status = response.getStatusLine().getStatusCode();
			if (status != 200) {
				throw new RuntimeException("Received HTTP " + status + " response from url: " + url);
			}
			FileUtils.copyInputStreamToFile(response.getEntity().getContent(), downloadedFile);
			response.getEntity().getContent().close();

		} catch (Exception e) {
			e.printStackTrace();
			RuntimeException f = new RuntimeException("Failure Downloading file from url: " + url + "\nStatus code: " + status + "\n"
					+ e.getClass().toString() + ": " + e.getMessage());
			f.setStackTrace(e.getStackTrace());
			throw f;
		}

		return downloadedFile.getAbsolutePath();
	}

}