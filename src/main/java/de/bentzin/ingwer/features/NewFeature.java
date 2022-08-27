package de.bentzin.ingwer.features;


import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface NewFeature {
    String author();
    String version();
}
