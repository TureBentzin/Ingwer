package de.bentzin.ingwer.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * That should not be overridden.
 * (If it should be overridden in some case you may annotate the "overriding" as {@link org.jetbrains.annotations.ApiStatus.Internal} or {@link org.jetbrains.annotations.ApiStatus.Experimental}
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface DoNotOverride {
}
