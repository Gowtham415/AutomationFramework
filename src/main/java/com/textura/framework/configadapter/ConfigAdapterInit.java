package com.textura.framework.configadapter;

import com.textura.framework.environment.EnvironmentInfo;
import com.textura.framework.environment.productenvironments.BOEnvironmentInfo;
import com.textura.framework.environment.productenvironments.BSEnvironmentInfo;
import com.textura.framework.environment.productenvironments.CPMEnvironmentInfo;
import com.textura.framework.environment.productenvironments.GDBEnvironmentInfo;
import com.textura.framework.environment.productenvironments.LATEnvironmentInfo;
import com.textura.framework.environment.productenvironments.PEEnvironmentInfo;
import com.textura.framework.environment.productenvironments.PQMEnvironmentInfo;
import com.textura.framework.environment.productenvironments.SEEnvironmentInfo;
import com.textura.framework.environment.productenvironments.TCSEnvironmentInfo;
import com.textura.framework.environment.productenvironments.TPMEnvironmentInfo;

public class ConfigAdapterInit {

	public static ConfigAdapter getConfig(String component) {
	
		System.out.println("Create configuration for: " + component);
		
		if (component.equals(ConfigComponents.CPM.getComponent())) {
			EnvironmentInfo env = new CPMEnvironmentInfo();
			return new ConfigAdapterApp(ConfigComponents.CPM, env);
		}
		else if (component.equals(ConfigComponents.PQM.getComponent())) {
			EnvironmentInfo env = new PQMEnvironmentInfo();
			return new ConfigAdapterApp(ConfigComponents.PQM, env);
		}
		else if (component.equals(ConfigComponents.SE.getComponent())) {
			EnvironmentInfo env = new SEEnvironmentInfo();
			return new ConfigAdapterApp(ConfigComponents.SE, env);
		}
		else if (component.equals(ConfigComponents.GDB.getComponent())) {
			EnvironmentInfo env = new GDBEnvironmentInfo();
			return new ConfigAdapterApp(ConfigComponents.GDB, env);
		}
		else if (component.equals(ConfigComponents.TPM.getComponent())) {
			EnvironmentInfo env = new TPMEnvironmentInfo();
			return new ConfigAdapterApp(ConfigComponents.TPM, env);
		}
		else if (component.equals(ConfigComponents.LAT.getComponent())) {
			EnvironmentInfo env = new LATEnvironmentInfo();
			return new ConfigAdapterApp(ConfigComponents.LAT, env);
		}
		else if (component.equals(ConfigComponents.PE.getComponent())) {
			EnvironmentInfo env = new PEEnvironmentInfo();
			return new ConfigAdapterApp(ConfigComponents.PE, env);
		}
		else if (component.equals(ConfigComponents.TCS.getComponent())) {
			EnvironmentInfo env = new TCSEnvironmentInfo();
			return new ConfigAdapterApp(ConfigComponents.TCS, env);
		}
		else if (component.equals(ConfigComponents.BO.getComponent())) {
			EnvironmentInfo env = new BOEnvironmentInfo();
			return new ConfigAdapterApp(ConfigComponents.BO, env);
		}
		else if (component.equals(ConfigComponents.BS.getComponent())) {
			EnvironmentInfo env = new BSEnvironmentInfo();
			return new ConfigAdapterApp(ConfigComponents.BS, env);
		}
		else if (component.equals(ConfigComponents.FRAMEWORK.getComponent())) {
			return new ConfigAdapterFrm(ConfigComponents.FRAMEWORK);
		}
		else {
			throw new RuntimeException("Unable to create configuration for: " + component);
		}
	}
}
