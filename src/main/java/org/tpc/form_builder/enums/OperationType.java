package org.tpc.form_builder.enums;

public enum OperationType {
    CONSTANT("CONSTANT"),

    // Basic arithmetic operations
    ADDITION("+"),
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

    // String operations
    CONCATENATION("+"),

    // Other expressions
    FUNCTION_CALL("FUNCTION_CALL");

    private final String symbol;

    OperationType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
