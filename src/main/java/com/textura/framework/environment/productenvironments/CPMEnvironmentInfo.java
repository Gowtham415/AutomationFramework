package com.textura.framework.environment.productenvironments;

import com.textura.framework.environment.EnvironmentInfo;
import com.textura.framework.utils.DBParams;

public class CPMEnvironmentInfo extends EnvironmentInfo {

	public String sshUser;
	public String sshPassword;
	public String testServer;
	public String testEnvName;
	public String testURL;
	public String testDatabase;
	public String cpm_url;
	public String cpm_appserver;
	public String cpm_databaseserver;
	public String cpm_databasename;
	public String cpm_databaseuser;
	public String cpm_files;
	public String cpm_threadcount;
	public String cpm_timechangeserver;
	public String pdful_url;
	public String pdful_appserver;
	public String pdful_databaseserver;
	public String pdful_databasename;
	public String feebill_url;
	public String feebill2_url;
	public String feebill2_databaseserver;
	public String feebill2_databasename;
	public String feebill2_databaseuser;
	public String feebill_shafturl;
	public String feebill_appserver;
	public String feebill_databaseserver;
	public String feebill_databasename;
	public String feebill_databaseuser;
	public String feebill_celerylog;
	public String pdq_url;
	public String pdq_appserver;
	public String pdq_databaseserver;
	public String pdq_databasename;
	public String csvus2_url;
	public String csvus_url;
	public String csvus_apiurl;
	public String csvus_appserver;
	public String csvus_databaseserver;
	public String csvus_databasename;
	public String fleet_appserver;
	public String andre_apiurl;
	public String andre_appserver;
	public String ebis_url;
	public String ebis_apiurl;
	public String ebis_apiurlv2;
	public String ebis_appserver;
	public String ebis_databaseserver;
	public String ebis_databasename;
	public String ebis_databaseuser;
	public String ebis_javaclienturl;
	public String ibis_apiurl;
	public String tl_powertoolsurl;
	public String tl_tishmanurl;
	public DBParams params;
	public String foyr_url;
	public String ims_url;
	public String ims_apiurl;
	public String ims_internalapiurl;
	public String ims_databaseserver;
	public String ims_databasename;
	public String ims_databaseuser;
	public String ims_databasePassword;
	
	public String getPoPath() {
		return "/var/lib/textura/docs/" + environment.toLowerCase() + "/ebis/upload/";
	}

	public String getRaoPath() {
		return "/var/lib/textura/docs/" + environment.toLowerCase() + "/ebis/download/";
	}
}