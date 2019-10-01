package com.textura.framework.abstracttestsuite;

import java.lang.reflect.Field;

public class TestResults {

	public String testId = "";
	public String runId = "";
	public String testCase = "";
	public String testDescription = "";
	public String hits = "";
	public String lastValue = "";
	public String min = "";
	public String max = "";
	public String avg = "";
	public String std_dev = "";
	public String total = "";
	public String start = "";
	public String end = "";

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