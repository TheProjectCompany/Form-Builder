package com.tpc.form_builder.modules.formula.function.libraries;

import com.tpc.form_builder.modules.formula.function.FormulaFunction;
import com.tpc.form_builder.modules.formula.function.FunctionLibrary;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@FunctionLibrary(name = "DateFunctions", help = "Date and time related functions")
public class DateFunctionLibrary {

    @FormulaFunction(help = "TODAY() — local date")
    public static LocalDate TODAY() { return LocalDate.now(); }

    @FormulaFunction(help = "NOW() — local datetime")
    public static LocalDateTime NOW() { return LocalDateTime.now(); }

    @FormulaFunction(help = "DATE('yyyy-MM-dd')")
    public static LocalDate DATE(String iso) { return LocalDate.parse(iso, DateTimeFormatter.ISO_LOCAL_DATE); }

    @FormulaFunction(help = "DATETIME('yyyy-MM-ddTHH:mm:ss')")
    public static LocalDateTime DATETIME(String iso) { return LocalDateTime.parse(iso, DateTimeFormatter.ISO_LOCAL_DATE_TIME); }

    @FormulaFunction(help = "DATE_ADD(date, days)")
    public static LocalDate DATE_ADD(LocalDate date, long days) { return date.plusDays(days); }

    @FormulaFunction(help = "DATETIME_ADD(datetime, minutes)")
    public static LocalDateTime DATETIME_ADD(LocalDateTime dt, long minutes) { return dt.plusMinutes(minutes); }

    @FormulaFunction(help = "DAYS_BETWEEN(start, end)")
    public static Long DAYS_BETWEEN(LocalDate start, LocalDate end) { return ChronoUnit.DAYS.between(start, end); }

    @FormulaFunction(help = "MINUTES_BETWEEN(start, end)")
    public static Long MINUTES_BETWEEN(LocalDateTime s, LocalDateTime e) { return ChronoUnit.MINUTES.between(s, e); }

    @FormulaFunction(help = "DATE_FORMAT(date/datetime, pattern)")
    public static String DATE_FORMAT(Object dateObj, String pattern) {
        if (dateObj == null || pattern == null) return null;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern);
        if (dateObj instanceof LocalDate ld) return ld.format(fmt);
        if (dateObj instanceof LocalDateTime ldt) return ldt.format(fmt);
        return dateObj.toString();
    }

    @FormulaFunction(help = "PARSE_DATE(str, pattern) -> LocalDate")
    public static LocalDate PARSE_DATE(String s, String pattern) {
        return LocalDate.parse(s, DateTimeFormatter.ofPattern(pattern));
    }

    @FormulaFunction(help = "ADD_MONTHS(date, months)")
    public static LocalDate ADD_MONTHS(LocalDate date, long months) { return date.plusMonths(months); }

    @FormulaFunction(help = "DATE_EQUALS - tolerant equality")
    public static Boolean DATE_EQUALS(Object d1, Object d2) {
        if (d1 == null || d2 == null) return false;
        if (d1 instanceof LocalDate ld1 && d2 instanceof LocalDate ld2) return ld1.isEqual(ld2);
        if (d1 instanceof LocalDateTime ldt1 && d2 instanceof LocalDateTime ldt2) return ldt1.isEqual(ldt2);
        return Objects.equals(d1, d2);
    }
}

