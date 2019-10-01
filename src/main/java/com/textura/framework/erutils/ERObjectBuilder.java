package com.textura.framework.erutils;

import com.textura.framework.erutils.ERObjectBoolean.ERObjectExecBoolean;
import com.textura.framework.objects.main.Assertions;

public class ERObjectBuilder<T> {

	private String message;
	Assertions asrt;

	public ERObjectBuilder(String message, Assertions asrt) {
		// The ERObjectBuilder acts as a handler to create the correct class depending on what value is being passed in.
		// The way it is currently implemented, the caller MUST set the expected value first, unless a boolean is being used.
		this.message = message;
		this.asrt = asrt;
	}

	/**
	 * Creates ERObjectString object to handle string ERs
	 * 
	 * @param expected
	 *            - The Expected String value
	 * @return ERObjectString
	 */
	public ERObjectString expected(String expected) {
		// ERObjectString returns string specific comparison methods
		return new ERObjectString(message, asrt).expected(expected);
	}

	/**
	 * Creates Generic ER object to handle miscellaneous types like lists and custom objects
	 * 
	 * @param expected
	 *            - The expected Object value
	 * @return ERObjectString
	 */
	public ERObject<T> expected(T expected) {
		// ERObject<T> returns generic comparison methods for the types being compared
		return new ERObject<T>(message, asrt).expected(expected);
	}

	/**
	 * Creates ER object with type Integer to handle integer types
	 * 
	 * @param expected
	 *            - The expected Integer value
	 * @return ERObject<Integer>
	 */
	public ERObjectInteger expected(int expected) {
		// ERObject<Integer> returns generic comparison methods for integers
		return new ERObjectInteger(message, asrt).expected(expected);
	}

	/**
	 * Creates ER object with type Boolean to handle boolean types
	 * 
	 * @param expected
	 *            - The expected Boolean value
	 * @return ERObjectBoolean
	 */
	public ERObjectExecBoolean checkCondition(Boolean actual) {
		// ERObjectBoolean returns boolean specific comparison methods/behavior
		return new ERObjectBoolean(message, asrt).actual(actual);
	}
}
