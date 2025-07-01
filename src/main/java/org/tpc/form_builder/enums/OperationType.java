package org.tpc.form_builder.enums;

import lombok.Getter;

@SuppressWarnings("unused")
@Getter
public enum OperationType {
    CONSTANT("CONSTANT"),

    // Basic arithmetic operations
    ADDITION("+"), // Supported for Strings as well
    SUBTRACTION("-"),
    MULTIPLICATION("*"),
    DIVISION("/"),
    MODULUS("%"),
    EXPONENTIATION("^"),


    // Logical operations
    AND("&&"),
    OR("||"),
    NOT("!"),

    // Comparison operations
    // Supported for multiple types like numeric, date, string, dropdown
    EQUALS("=="),
    NOT_EQUALS("!="),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_THAN_OR_EQUAL_TO(">="),
    LESS_THAN_OR_EQUAL_TO("<="),
    CONTAINS("contains"),
    NOT_CONTAINS("!contains"),
    CONTAINS_ALL("containsAll"),
    STARTS_WITH("startsWith"),
    ENDS_WITH("endsWith"),

    // Aggregate Operations
    COUNT("count"),
    SUM("sum"),
    AVERAGE("average"),
    MIN("min"),
    MAX("max"),

    // Collection Operations
    LENGTH("length"),
    EMPTY("empty"),
    NOT_EMPTY("!empty"),


    // Other expressions
    FUNCTION_CALL("FUNCTION_CALL");

    private final String symbol;

    OperationType(String symbol) {
        this.symbol = symbol;
    }

}
