package com.textura.framework.erutils;

import com.textura.framework.objects.main.Assertions;

public class ERManager {
	Assertions asrt;
	
	public ERManager(Assertions asrt){
		this.asrt=asrt;
	}
	
	/**<pre>{@code er.createER("ER5: Wildcarded strings were not equal")
				.expected("*A")
				.actual("AA")
				.wildCardEquals()
				.assertTrue();}</pre>
	 * @param message - The failureDetails message, handling printing actual/expected values automatically.
	 * @param <T> Use the type parameter for comparing values that aren't booleans, strings, or integers
	 * @return      ERObjectExecString
	 */
	public <T> ERObjectBuilder<T> createER(String message) {
		//The ERObjectBuilder is a way to handle the different types of values that will get passed in.
		//The current plan is to have 3 different paths- Strings, Booleans, and Generics  - like lists, integers
		//The goal of splitting it up like this is to limit the possibilities and errors when creating the ERs
		//so that one cannot call an advanced string comparison method like useWildCard on an integer,
		//or try to compare a string to an integer. these mistakes will not compile.
		return new ERObjectBuilder<T>(message, asrt);
	}
}
