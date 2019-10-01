package com.textura.framework.loganalyzer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LogAnalyzerReader {

	protected static List<String> readJenkinsLogFromFile(String filePath) {

		BufferedReader readFile = null;
		String line = null;
		List<String> fileContents = new ArrayList<String>();

		try {
			readFile = new BufferedReader((new FileReader(filePath)));
			while ((line = readFile.readLine()) != null)
				fileContents.add(line);
			readFile.close();
			return fileContents;
		} catch (FileNotFoundException e) {
			System.out.println("Unable to find file: " + filePath);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Unable to read file: " + filePath);
			e.printStackTrace();
		}
		return fileContents;
	}

	protected static void readJenkinsLogFromUrl() {

		try {
			URL url = new URL("http://dfjenkinspro1.texturallc.net:8080/view/CPM/job/CPM_QACPMAUTO7_Master/265/consoleText");
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

			// System.out.println("scaner " + in.toString());
			// Scanner s = new Scanner(url.getContent()));
			// System.out.println("scaner " + s.findInLine("Environment"));

			String inputLine;

			while ((inputLine = in.readLine()) != null)
				System.out.println(inputLine);
			in.close();

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
