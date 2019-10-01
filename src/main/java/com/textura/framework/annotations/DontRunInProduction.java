package com.textura.framework.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates a method should not be ran in production
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.METHOD)
public @interface DontRunInProduction {

}
