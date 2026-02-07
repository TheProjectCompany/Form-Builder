package com.tpc.form_builder.formula.function.libraries;

import com.tpc.form_builder.formula.function.FormulaFunction;
import com.tpc.form_builder.formula.function.FunctionLibrary;

@FunctionLibrary
public class LogicalFunctionsLibrary {

    @FormulaFunction(help = "AND(a,b,...)")
    public static Boolean AND(Boolean... vals) {
        for (Boolean b : vals) {
            if (b == null || !b) return false;
        }
        return true;
    }

    @FormulaFunction(help = "OR(a,b,...)")
    public static Boolean OR(Boolean... vals) {
        for (Boolean b : vals) {
            if (b != null && b) return true;
        }
        return false;
    }

    @FormulaFunction(help = "NOT(a)")
    public static Boolean NOT(Boolean a) { return a == null ? null : !a; }

    @FormulaFunction(help = "EQ(a,b)")
    public static Boolean EQ(Object a, Object b) { return a == null ? b == null : a.equals(b); }

    @FormulaFunction(help = "NEQ(a,b)")
    public static Boolean NEQ(Object a, Object b) { return !EQ(a,b); }

    @FormulaFunction(help = "GT(a,b)")
    public static Boolean GT(Number a, Number b) { return a != null && b != null && a.doubleValue() > b.doubleValue(); }

    @FormulaFunction(help = "LT(a,b)")
    public static Boolean LT(Number a, Number b) { return a != null && b != null && a.doubleValue() < b.doubleValue(); }
}

