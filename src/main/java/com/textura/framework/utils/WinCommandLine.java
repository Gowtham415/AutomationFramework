package com.textura.framework.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import com.textura.framework.environment.Project;

public class WinCommandLine {

	public static String runCommand(String command) {
		String sResult = "";

		try {
			String ls_str;
			Process ls_proc = Runtime.getRuntime().exec(command);
			BufferedReader ls_in = new BufferedReader(new InputStreamReader(ls_proc.getInputStream()));

			try {
				while ((ls_str = ls_in.readLine()) != null) {
					sResult = ls_str.toString();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			ls_in.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return sResult;
	}

	public static int executeCommand(String command) {
		try {
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(command);

			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = "";
			line = line + line;
			while ((line = input.readLine()) != null) {
				// System.out.println(line);
			}

			int exitVal = pr.waitFor();
			input.close();
			return exitVal;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1;
	}

	/**
	 * Do not use unless you enjoy cases hanging indefinately. Use executeCommand with timeout command
	 * @param cmd
	 */
	@Deprecated
	public static void executeCommand(String[] cmd) {
		ProcessBuilder pb = new ProcessBuilder();
		pb.redirectErrorStream(true);
		pb.command(cmd);
		try {
			Process p = pb.start();
			p.waitFor();			
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param cmd command to be executed
	 * @param timeout in seconds
	 */
	public static void executeCommand(String[] cmd, long timeout) {
		ProcessBuilder pb = new ProcessBuilder();
		pb.redirectErrorStream(true);
		pb.command(cmd);
		pb.directory(new File(Project.downloads("")));		//sets the working directory to downloads folder before executing the command
		
		try {
			Process p = pb.start();
		boolean timedOut = !p.waitFor(timeout, TimeUnit.SECONDS);		
		if(timedOut){
			String caseName = JavaHelpers.getTestCaseMethodName();
			String timestamp = DateHelpers.getCurrentDateAndTime();
			System.out.println(timestamp + " " + caseName + ": " + "Timed out waiting for command to execute: " + cmd);
		}

		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
	}
}
