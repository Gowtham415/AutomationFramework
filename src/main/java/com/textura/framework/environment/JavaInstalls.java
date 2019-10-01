package com.textura.framework.environment;

/**
 * This enum lists all Java versions which are part of repository. These Java versions are used
 * during automation runs for tools like Java client.
 * 
 * @param version
 *            Java version
 * @param folder
 *            Folder where Java files reside
 * @param description
 *            Full description of Java install
 */
public enum JavaInstalls {

	ver_1_4("1.4.2_19", "1.4.2_19/", "Java 1.4 Update 19"),
	ver_1_5("1.5.0_22", "1.5.0_22/", "Java 1.5 Update 22"),
	ver_1_6("jre6_45", "jre6_45/", "Java 1.6 Update 45"),
	ver_1_7("jre7_60", "jre7_60/", "Java 1.7 Update 60"),
	ver_1_8("jre8_05", "jre8_05/", "Java 1.8 Update 05");

	private String version;
	private String folder;
	private String description;

	private JavaInstalls(String version, String folder, String description) {
		this.version = version;
		this.folder = folder;
		this.description = description;
	}

	protected String getVersion() {
		return version;
	}

	protected String getFolder() {
		return folder;
	}

	protected String getDescription() {
		return description;
	}

	public String getPath() {
		return Project.pathRepository("resources/java/" + folder + "bin/java" + Project.executableExtension());
	}
}