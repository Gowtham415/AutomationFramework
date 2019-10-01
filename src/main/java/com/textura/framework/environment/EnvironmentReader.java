package com.textura.framework.environment;

import java.lang.reflect.Field;
import org.dom4j.Document;
import org.dom4j.Node;
import com.textura.framework.utils.JavaHelpers;
import com.textura.framework.utils.XmlFileBuilder;

public class EnvironmentReader {

	/**
	 * This method will parse the xml String and populate values in the EnvironmentInfo object. Variable names of the EnvironmentInfo object are used to determine how to parse the
	 * xml file. An example xml is
	 * 
	 * <server hostname="hostname'>
	 * <environment name="qaa">
	 * <pqm>
	 * <url>https://pqm-qa.texturallc.net/</url>
	 * 
	 * The EnvironmentInfo object passed must have a String field declared as pqm_url.
	 * 
	 * The environmentName passed in is used to find which environment to read. The fields of the EnvironmentInfo object determine how to populate those fields.
	 * 
	 * @param xml
	 *            File describing path to xml file
	 * @param environmentName
	 *            name attribute of the environment in the xml
	 * @param o
	 *            An object to populated with values from the xml
	 */
	public static void readXMLIntoEnvironmentInfo(String xml, String environmentName, EnvironmentInfo o) {

		Document document = XmlFileBuilder.readXmlFile(xml);
		String hostname;
		String environment;
		Node server = null;
		Node env = null;

		// locate the environment by the attribute '@name'
		if (environmentName.lastIndexOf(".") < 0) {
			hostname = "";
			environment = environmentName;
		} else {
			hostname = environmentName.substring(0, environmentName.lastIndexOf("."));
			environment = environmentName.substring(hostname.length() + 1);
		}
		server = document.selectSingleNode("//server[@hostname='" + hostname + "']");
		if (server != null) {
			env = server.selectSingleNode("//server[@hostname='" + hostname + "']//environment[@name='" + environment + "']");
		}

		if (server == null || env == null) {
			throw new RuntimeException("Error: Could not find environment: '" + environment + "' in xml file:\n"
					+ JavaHelpers.readFileAsString(xml));
		}

		Field[] fields = o.getClass().getFields(); // reflection to inspect the object's fields
		for (Field f : fields) {
			try {
				if (f.getType().equals(String.class) && f.getName().contains("_")) { // if field name has a '_'
					String resourceType = f.getName().substring(0, f.getName().indexOf('_')); // the 'header'. aka <pqm> or <pdq>
					String resource = f.getName().substring(f.getName().indexOf('_') + 1); // the child tag under the header
					Node n = env.selectSingleNode(".//" + resourceType + "//" + resource);
					if (n == null) {
						System.out.println("Could not find specified field in xml: \n<" + resourceType + ">\n    <" + resource + ">\n"
								+ xml);
						f.set(o, "null"); // if didn't find node specified by field name, set field to 'null'
					} else {
						f.set(o, n.getText()); // found and store parsed text
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try { // also set common values to EnvironmentInfo
			o.environment = environment;
			o.url = env.selectSingleNode("./*/url").getText();
			o.server = env.selectSingleNode("./..").valueOf("@hostname");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
