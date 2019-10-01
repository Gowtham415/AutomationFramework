package com.textura.framework.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates a method can only be run on the local machine.
 * Presence of the Annotation is checked before each test is executed
 * in AbstractTestSuite
 * 
 * The local machine node must be configured with the capability "applicationName": "hub"
 * Starting the node script SeleniumNodeHub-start.bat will set this
 * 
 */
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(java.lang.annotation.ElementType.METHOD)
public @interface LocalOnly {

}
