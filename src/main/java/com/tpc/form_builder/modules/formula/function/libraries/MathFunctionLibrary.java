package com.tpc.form_builder.modules.formula.function.libraries;

import com.tpc.form_builder.modules.formula.function.FormulaFunction;
import com.tpc.form_builder.modules.formula.function.FunctionLibrary;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@FunctionLibrary(
        name = "Mathematical Functions",
        help = "Mathematical functions like SUM, AVG, MAX, MIN, ROUND, FLOOR, CEIL, ABS, POWER, SQRT..."
)
public class MathFunctionLibrary {

    @FormulaFunction(help = "SUM over numbers or collections: SUM(a,b,..., collection)")
    public static Double SUM(Object... values) {
        return Arrays.stream(values)
                .flatMap(v -> v instanceof Collection ? ((Collection<?>) v).stream() : Arrays.stream(new Object[]{v}))
                .filter(Number.class::isInstance)
                .mapToDouble(v -> ((Number) v).doubleValue())
                .sum();
    }

    @FormulaFunction(help = "AVG of values")
    public static Double AVG(Object... values) {
        List<Double> nums = Arrays.stream(values)
                .flatMap(v -> v instanceof Collection ? ((Collection<?>) v).stream() : Arrays.stream(new Object[]{v}))
                .filter(Number.class::isInstance)
                .map(v -> ((Number) v).doubleValue())
                .toList();
        return nums.isEmpty() ? 0.0 : nums.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    @FormulaFunction(help = "MAX of values")
    public static Double MAX(Object... values) {
        return Arrays.stream(values)
                .flatMap(v -> v instanceof Collection ? ((Collection<?>) v).stream() : Arrays.stream(new Object[]{v}))
                .filter(Number.class::isInstance)
                .mapToDouble(v -> ((Number) v).doubleValue())
                .max()
                .orElse(0.0);
    }

    @FormulaFunction(help = "MIN of values")
    public static Double MIN(Object... values) {
        return Arrays.stream(values)
                .flatMap(v -> v instanceof Collection ? ((Collection<?>) v).stream() : Arrays.stream(new Object[]{v}))
                .filter(Number.class::isInstance)
                .mapToDouble(v -> ((Number) v).doubleValue())
                .min()
                .orElse(0.0);
    }

    @FormulaFunction(help = "ROUND(value, scale)")
    public static Double ROUND(Number value, Integer scale) {
        if (value == null || scale == null) return null;
        double factor = Math.pow(10, scale);
        return Math.round(value.doubleValue() * factor) / factor;
    }

    @FormulaFunction(help = "FLOOR")
    public static Double FLOOR(Number v) { return Math.floor(v.doubleValue()); }

    @FormulaFunction(help = "CEIL")
    public static Double CEIL(Number v) { return Math.ceil(v.doubleValue()); }

    @FormulaFunction(help = "ABS")
    public static Double ABS(Number v) { return Math.abs(v.doubleValue()); }

    @FormulaFunction(help = "POWER")
    public static Double POWER(Number a, Number b) { return Math.pow(a.doubleValue(), b.doubleValue()); }

    @FormulaFunction(help = "SQRT")
    public static Double SQRT(Number a) { return Math.sqrt(a.doubleValue()); }
}

