package com.textura.framework.utils;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FunctionIterator {
	
	public static boolean iterateSupplier(Supplier<Boolean> supplier, int iterations) {
		for (int i = 0; i < iterations; i++) {
			if ((boolean) supplier.get()) {
				return true;
			}
		}
		return false;
	}

	public static boolean iterateFunction(Function<Object, ?> function, int iterations, Object argument) {
		for (int i = 0; i < iterations; i++) {
			if ((Boolean)function.apply(argument)) {
				return true;
			}
		}
		return false;
	}

	public static void iterateConsumer(Consumer<Object> consumer, int iterations, Object argument) {
		for (int i = 0; i < iterations; i++) {
			consumer.accept(argument);
		}
	}
}
