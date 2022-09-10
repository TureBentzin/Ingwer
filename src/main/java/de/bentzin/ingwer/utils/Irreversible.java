package de.bentzin.ingwer.utils;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
/**
 * @implNote Identifies that this method mutates <code>this</code> irreversible!
 */
public @interface Irreversible {
}
