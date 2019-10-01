package com.textura.framework.configadapter;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.dom4j.Document;
import com.textura.framework.environment.EnvironmentInfo;
import com.textura.framework.testng.TestSuitesBuilder;
import com.textura.framework.testng.TestngConfig;
import com.textura.framework.tools.EncryptionHelper;

public abstract class ConfigAdapter {
	
	TestSuitesBuilder testSuitesCreator;
	TestngConfig testngConfig;
	
	protected Map<String,String> env;
	protected String appPath;
	protected Document document;
	protected ConfigComponents component;
	protected TestngConfig tng;
	protected String configFileName;
	protected String sshUser;
	protected String sshPassword;
	
	public abstract void readSettingsFromEnv();
	public abstract void readSettingsFromEnvPQM();
	public abstract void readSettingsFromFile();	
	public abstract void validateSettings();
	public abstract void createSettingsFile();
	public abstract void cleanEnv();
	public abstract void printSettings();
	public abstract TestngConfig getTngConfig();
	public abstract EnvironmentInfo getEnvironmentInfo();

	public ConfigAdapter() {
		
		env = System.getenv();
		configFileName = "testconfig.xml";
		sshUser = "tpm_automation";
		sshPassword = "password";
	}

	public void createTestSuites(TestngConfig tngConfig){
		
		testSuitesCreator.createTestSuites(tngConfig);
	}
	
	public void quitConfigAdapter(String msg) {
		
		System.err.println();
	}

	public void setMockEnvironmentVariables() {
		Map<String, String> env = new HashMap<String, String>(System.getenv());
		env.put("Testbed", "dfappqa1.rk1"); 
		set(env);
	}

	public static void set(Map<String, String> newenv) {
		Class<?>[] classes = Collections.class.getDeclaredClasses();
		Map<String, String> env = System.getenv();
		for (Class<?> cl : classes) {
			try {
				if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
					Field field = cl.getDeclaredField("m");
					field.setAccessible(true);
					Object obj = field.get(env);
					@SuppressWarnings("unchecked")
					Map<String, String> map = (Map<String, String>) obj;
					map.clear();
					map.putAll(newenv);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void encryptActiveDirectoryFields(){
		EncryptionHelper encyptor = new EncryptionHelper();
		EnvironmentInfo env = getEnvironmentInfo();
		env.activeDirectoryUsername = encyptor.encrypt(env.activeDirectoryUsername);
		env.activeDirectoryPassword = encyptor.encrypt(env.activeDirectoryPassword);
	}
}
