package com.textura.framework.annotations;

import static java.lang.annotation.ElementType.METHOD;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows automation team members to add their Bitbucket user name in front of
 * their newly created test cases, for example:
 *
 * @Test
 * @Author(name = "firstname.lastname")
 *              ...
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
public @interface Author {

	String name();
}