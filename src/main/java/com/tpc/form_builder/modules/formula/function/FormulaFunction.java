package com.tpc.form_builder.modules.formula.function;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@SuppressWarnings("unused")
public @interface FormulaFunction {
    /**
     * Optional exposed name; if empty the method name is used.
     */
    String name() default "";
    /**
     * Short help text.
     */
    String help() default "";
}
