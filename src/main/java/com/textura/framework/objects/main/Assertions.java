package com.textura.framework.objects.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.Assertion;
import org.testng.asserts.IAssert;
import net.javacrumbs.jsonunit.JsonAssert;
import net.javacrumbs.jsonunit.core.Configuration;
import net.javacrumbs.jsonunit.core.Option;

public class Assertions extends Assertion {

	private List<String> assertMessages = Collections.synchronizedList(new ArrayList<String>());

	@SuppressWarnings("rawtypes")
	@Override
	public void executeAssert(IAssert assertion) {
		try {
			assertion.doAssert();
		} catch (AssertionError ex) {
			String message = "";
			if (assertion.getMessage() == null) {
				message = ex.getMessage();
			} else {
				message = assertion.getMessage();
			}
			throw new AssertionError(message);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onBeforeAssert(IAssert assertion) {
		assertMessages.add("onBeforeAssert " + assertion.getMessage());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onAfterAssert(IAssert assertion) {
		assertMessages.add("onAfterAssert " + assertion.getMessage());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onAssertFailure(IAssert assertion) {
		// throw new AssertionError(assertion.getMessage());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void onAssertSuccess(IAssert as) {
		assertMessages.add("onAssertSuccess " + as.getMessage());
	}

	public List<String> getAssertMessages(WebDriver driver) {
		return assertMessages;
	}

	public void assertTrue(String message, boolean condition) {
		super.assertTrue(condition, message);
	}

	public void assertTrue(boolean condition) {
		super.assertTrue(condition);
	}

	public void assertFalse(String message, boolean condition) {
		assertFalse(condition, message);
	}

	public void assertFalse(boolean condition) {
		super.assertFalse(condition);
	}

	public void assertJSONEquals(String failureDetails, String expectedStr, String actualStr) {
		try {
			Configuration config = Configuration.empty().withIgnorePlaceholder("*").withOptions(Option.IGNORING_ARRAY_ORDER).withTolerance(
					0);
			JsonAssert.assertJsonEquals(expectedStr, actualStr, config);
		} catch (AssertionError e2) {
			e2.printStackTrace();
			throw new AssertionError(failureDetails);
		}

	}

}
