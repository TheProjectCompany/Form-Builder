package com.tpc.form_builder.modules.formula.function.libraries;

import com.tpc.form_builder.modules.formula.function.FormulaFunction;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class StringFunctionLibrary {

    @FormulaFunction(help = "UPPER(text)")
    public static String UPPER(String s) { return s == null ? null : s.toUpperCase(); }

    @FormulaFunction(help = "LOWER(text)")
    public static String LOWER(String s) { return s == null ? null : s.toLowerCase(); }

    @FormulaFunction(help = "TRIM(text)")
    public static String TRIM(String s) { return s == null ? null : s.trim(); }

    @FormulaFunction(help = "CONCAT(a,b,...)")
    public static String CONCAT(Object... parts) {
        return Arrays.stream(parts).map(Objects::toString).collect(Collectors.joining());
    }

    @FormulaFunction(help = "SUBSTRING(text, start, length) — start is 0-based")
    public static String SUBSTRING(String s, Integer start, Integer len) {
        if (s == null) return null;
        if (start == null) start = 0;
        if (len == null) return s.substring(start);
        int to = Math.min(s.length(), start + len);
        return s.substring(Math.max(0, start), Math.max(start, to));
    }

    @FormulaFunction(help = "REPLACE(text, old, new)")
    public static String REPLACE(String s, String oldStr, String newStr) {
        if (s == null) return null;
        return s.replace(oldStr, newStr);
    }

    @FormulaFunction(help = "STARTS_WITH(text, prefix)")
    public static Boolean STARTS_WITH(String s, String prefix) { return s != null && prefix != null && s.startsWith(prefix); }

    @FormulaFunction(help = "ENDS_WITH(text, suffix)")
    public static Boolean ENDS_WITH(String s, String suffix) { return s != null && suffix != null && s.endsWith(suffix); }

    @FormulaFunction(help = "LENGTH(text)")
    public static Integer LENGTH(String s) { return s == null ? 0 : s.length(); }
}


