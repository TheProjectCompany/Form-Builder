package com.tpc.form_builder.formula.function.libraries;

import com.tpc.form_builder.formula.function.FormulaFunction;

public class NullFunctionLibrary {

    @FormulaFunction(help = "IS_NULL(val)")
    public static Boolean IS_NULL(Object v) { return v == null; }

    @FormulaFunction(help = "IF_NULL(val, default)")
    public static Object IF_NULL(Object v, Object def) { return v == null ? def : v; }

    @FormulaFunction(help = "COALESCE(a,b,...) — first non-null")
    public static Object COALESCE(Object... vals) {
        for (Object o : vals) if (o != null) return o;
        return null;
    }

    @FormulaFunction(help = "NULL_IF(a,b) — returns null if a == b")
    public static Object NULL_IF(Object a, Object b) { return (a == null ? b == null : a.equals(b)) ? null : a; }
}
