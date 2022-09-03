package de.bentzin.ingwer.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @implNote Specifies that the element annotated with this is independent of Ingwers (or Papers) initialization.
 */
@Retention(RetentionPolicy.CLASS)
public @interface Independent {
}
