package com.textura.framework.erutils;

import com.textura.framework.objects.main.Assertions;

public class ERObjectInteger extends ERObject<Integer> {

	public ERObjectInteger(String message, Assertions asrt) {
		super(message, asrt);
		// TODO Auto-generated constructor stub
	}

	public ERObjectInteger expected(int expected) {
		this.expected = expected;
		return this;
	}

	public ERObjectExec<Integer> actual(int actual) {
		this.actual = actual;
		return new ERObjectExec<Integer>(this);
	}

}
