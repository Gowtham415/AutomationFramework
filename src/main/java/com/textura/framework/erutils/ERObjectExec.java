package com.textura.framework.erutils;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ERObjectExec<T> {

	private ERObject<T> parent;
	
	ERObjectExec(ERObject<T> parent) {
		this.parent=parent;
	}
	
	public ERObjectExec<T> equals(){
		parent.compares= () -> {
			return parent.actual.equals(parent.expected);
		};
		parent.compareMethodString = "equals";
		return this;
	}
	
	private ERObjectExec<T> customComparator(BiFunction<T, T, Boolean> func, T arg1, T arg2) {
		//Use with Page :: method notation
		//This allows one to use any method with 2 parameters of type T that returns a boolean as a comparison method.
		parent.compares = () -> {
			return func.apply(arg1, arg2);
		};
		
		parent.compareMethodString = "using custom comparison method";
		return this;
	}
	
	/**
	 *This allows one to use any method with 2 parameters of type T (expected, actual) that returns a boolean as a comparison method .
	 *Use with Page :: method notation.
	 *Ex: customComparator(ViewReports :: compareSubSovLineItemByDrawReport)
	 * @return      ERObjectExec
	 */
	public ERObjectExec<T> customComparatorExpectedActual(BiFunction<T, T, Boolean> func) {
		return customComparator(func, parent.expected, parent.actual);
	}
	
	/**
	 *This allows one to use any method with 2 parameters of type T (actual, expected) that returns a boolean as a comparison method .
	 *Use with Page :: method notation.
	 *Ex: customComparator(ViewReports :: compareSubSovLineItemByDrawReport)
	 * @return      ERObjectExec
	 */
	public ERObjectExec<T> customComparatorActualExpected(BiFunction<T, T, Boolean> func) {
		//Use with Page :: method notation
		return customComparator(func, parent.actual, parent.expected);
	}

	public void assertExec(Consumer<ERObject<T>> consumer) {
		if (parent.actual == null) {
			//We do not want to allow one to try running the assert method without an actual value
			throw new RuntimeException("Actual value was not initialized.");
		}

		if (parent.compares == null) {
			//Compares method must also be set
			throw new RuntimeException("Compare method was not initialized.");
		}
		consumer.accept(parent);
	}
	
	public void assertTrue() {
		//here we can wait for er.actual and er.expected to be true if they are functional interfaces
		if (parent.getActual() instanceof Boolean) {
			parent.asrt.assertTrue(parent.compare(), parent.message + "\nExpected Value:\ntrue \nActual Value:\n" + parent.getActual() );
			return;
		}
		parent.asrt.assertTrue(parent.compare(), parent.message + "\nExpected Value:\n"+parent.getExpected()+ "\nActual Value:\n" + parent.getActual() );
	}
	
	public void assertFalse() {
		if (parent.getActual() instanceof Boolean) {
			parent.asrt.assertFalse(parent.compare(), parent.message + "\nExpected Value:\nfalse \nActual Value:\n" + parent.getActual() );
			return;
		}
		parent.asrt.assertFalse(parent.compare(), parent.message + "\nExpected Value:\n"+parent.getExpected()+ "\nActual Value:\n" + parent.getActual() );
	}
	
}
