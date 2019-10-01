package com.textura.framework.erutils;

import com.textura.framework.objects.main.Assertions;

public class ERObjectBoolean extends ERObject<Boolean> {

	public ERObjectBoolean(String message, Assertions asrt) {
		super(message, asrt);
		// TODO Auto-generated constructor stub
	}
	
	public ERObjectExecBoolean actual(Boolean actual){
		this.actual= actual;
		//Boolean comparison should be implicitly handled when using assertTrue and assertFalse, thus no need for an expected value
		//and setting comparison method at the declaration of the actual.
		this.compares= () -> {
			return (boolean) actual;
		};
		this.compareMethodString="boolean evaluation";
		
		return new ERObjectExecBoolean(this);
	}

	public class ERObjectExecBoolean extends ERObjectExec<Boolean> {
		ERObjectExecBoolean(ERObject<Boolean> parent) {
			super(parent);
			// TODO Auto-generated constructor stub
		}

		public ERObjectExec<Boolean> checkCondition(){
			return this;
		}
	}
}
