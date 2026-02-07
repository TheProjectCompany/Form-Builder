package com.tpc.form_builder.formula.function;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FunctionLibrary {
    /**
     * Optional exposed name; if empty the method name is used.
     */
    String name() default "";
    /**
     * Short help text.
     */
    String help() default "";
}