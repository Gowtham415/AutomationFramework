package com.textura.framework.abstracttestsuite;

import java.lang.reflect.Field;

public class RunResults {

	public String runId = "";
	public String start = "";
	public String end = "";
	public String environment = "";
	public String codeBranch = "";
	public String codeRevision = "";
	public String browser = "";
	public String testClient = "";
	public String testNotes = "";

	public String toString() {
		StringBuilder result = new StringBuilder();
		Field[] fields = this.getClass().getFields();
		for (Field f : fields) {
			try {
				if (f.getType().equals(String.class)) {
					result.append(f.getName() + ": " + f.get(this) + "\n");
				} else {
					result.append(f.getType().getSimpleName() + " " + f.getName() + " " + f.get(this).toString() + "\n");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result.toString();
	}

}
