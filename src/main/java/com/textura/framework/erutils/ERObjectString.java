package com.textura.framework.erutils;

import com.textura.framework.frfparser.FRFParser;
import com.textura.framework.objects.main.Assertions;
import com.textura.framework.utils.JavaHelpers;
import net.javacrumbs.jsonunit.JsonAssert;
import net.javacrumbs.jsonunit.core.Configuration;
import net.javacrumbs.jsonunit.core.Option;

public class ERObjectString extends ERObject<String> {

	public ERObjectString(String message, Assertions asrt) {
		super(message, asrt);
		// TODO Auto-generated constructor stub
	}

	public ERObjectString expected(String expected) {
		this.expected = expected;
		return this;
	}

	public ERObjectExecString actual(String actual) {
		this.actual = actual;
		return new ERObjectExecString(this);
	}

	public class ERObjectExecString extends ERObjectExec<String> {

		ERObjectExecString(ERObject<String> parent) {
			super(parent);
			// TODO Auto-generated constructor stub
		}

		/**
		 * This comparator checks if the actual string matches a pattern defined by the expected string.
		 * 
		 * @return ERObjectExecString
		 */
		public ERObjectExecString wildCardEquals() {
			// This method switches the comparison method to wildcard for strings
			compares = () -> {
				return JavaHelpers.wildCardMatch((String) getActual(), (String) getExpected());
			};
			compareMethodString = "string wildcard";
			return this;
		}

		/**
		 * This comparator checks if the actual string contains the expected string
		 * 
		 * @return ERObjectExecString
		 */
		public ERObjectExecString contains() {
			compares = () -> {
				// This method switches the comparison method to contains for strings
				return ((String) actual).contains((String) expected);
			};
			compareMethodString = "contains";
			return this;
		}

		/**
		 * This call transforms the expected value using the actual value using FRF parser.
		 * Add this call as needed before using a comparison method like equals or wildcardEquals.
		 * 
		 * @param expected
		 *            string
		 * @param actual
		 *            string
		 */
		public ERObjectExecString frfParserTransformation() {
			expected = FRFParser.getExpectedValue((String) expected, (String) actual);
			return this;

		}

		public ERObjectExecString jsonEquals() {
			// This method switches the comparison method to json for strings
			compares = () -> {
				try {
					Configuration config = Configuration.empty()
							.withIgnorePlaceholder("*")
							.withOptions(Option.IGNORING_ARRAY_ORDER)
							.withTolerance(
									0);
					JsonAssert.assertJsonEquals((String) expected, (String) actual, config);
				} catch (AssertionError e2) {
					e2.printStackTrace();
					return false;
				}
				return true;
			};
			compareMethodString = "string json comparison";
			return this;
		}
	}
}
