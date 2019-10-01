package com.textura.framework.testsuites;

import com.textura.framework.configadapter.ConfigAdapter;
import com.textura.framework.configadapter.ConfigAdapterFrm;
import com.textura.framework.configadapter.ConfigComponents;

public class TestBaseFramework {
	
	protected ConfigAdapter cfg;
	
	public TestBaseFramework() {
		
		cfg = new ConfigAdapterFrm(ConfigComponents.FRAMEWORK);
		cfg.readSettingsFromEnv();
		cfg.printSettings();
	}
}
