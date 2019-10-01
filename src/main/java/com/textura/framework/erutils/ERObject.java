package com.textura.framework.erutils;
import com.textura.framework.objects.main.Assertions;


public class ERObject<T>{
	
	public final String message;
	protected T expected;
	protected T actual;
	protected FunctionExecutor compares;
	protected String compareMethodString;
	protected Assertions asrt;
	//compares is a functional interface with behavior defined at runtime
	
	//FORMATTER RULES INSTRUCTIONS:
	//https://stackoverflow.com/questions/2105024/eclipse-formatter-settings-for-the-builder-pattern
	//https://stackoverflow.com/questions/18324017/how-to-set-eclipse-code-formatter-to-support-fluent-interfaces
	//I suggest leaving the indentation as 'default indentation'.
	
	@FunctionalInterface
	public interface FunctionExecutor {

		public boolean execute();
	}
	
	
	public ERObject(String message, Assertions asrt) {
		//The idea of the ERObject is to create a class which holds values and evaluates them using a specified comparison method.
		//The behavior for certain classes is slightly different so they are subclassed.
		//The intended usage is to create an ERObject using the ERObjectBuilder which delegates the creation to the correct subclass as needed based on
		//the specified actual and expected values.
		//The goals are to minimize the variables named and created in test suites, to improve the ability to copy paste ERs, and to make modification easier.
		this.message=message;
		this.asrt=asrt;
	}
	
	public ERObjectExec<T> actual(T actual){
		this.actual= actual;
		return new ERObjectExec<T>(this);
	}
	
	public ERObject<T> expected(T expected){
		this.expected=expected;
		return this;
	}
	
	public String getCompareMethodString(){
		return compareMethodString;
	}
	
	public boolean compare(){
		return compares.execute();
	}
	
	public T getActual(){
		return actual;
	}
	
	public T getExpected(){
		return expected;
	}
	

	
}
