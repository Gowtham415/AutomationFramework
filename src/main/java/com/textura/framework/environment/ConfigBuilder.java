package com.textura.framework.environment;

import com.textura.framework.configadapter.ConfigAdapter;
import com.textura.framework.configadapter.ConfigAdapterInit;

/**
 * This needs to be packaged as ConfigBuilder.jar and placed in the project's root
 */
public class ConfigBuilder {
	
	public static void main(String[] args) {
		
		if(args.length < 1) {
			throw new RuntimeException("Component must be passed as command line argument");
		}
		ConfigAdapter config = ConfigAdapterInit.getConfig(args[0].toString());

//		if(args[0].equals("PQM")) {
//			config.readSettingsFromEnvPQM();
//			System.out.println("I am going through product select set up");
//		}
		
		config.readSettingsFromEnv();
		
		config.encryptActiveDirectoryFields();

		config.validateSettings();

		config.createSettingsFile();

		config.printSettings();

		config.createTestSuites(config.getTngConfig());
		
	}
}
