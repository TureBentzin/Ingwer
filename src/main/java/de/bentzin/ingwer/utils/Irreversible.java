package de.bentzin.ingwer.utils;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @implNote Identifies that this method mutates <code>this</code> irreversible!
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface Irreversible {
}
