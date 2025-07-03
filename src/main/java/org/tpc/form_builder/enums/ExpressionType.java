package org.tpc.form_builder.enums;

@SuppressWarnings("unused")
public enum  ExpressionType {
    CONDITIONAL,  //
    ARITHMETIC,   // Arithmetic operations like addition / substraction / multiplication / division / modulus / exponentiation / floor division
    LOGICAL,      // Logical operations like AND / OR / NOT
    COMPARISON,   // Comparison operations like equals / not equals / greater than / less than / greater than or equal to / less than or equal to / contains / not contains / contains all / starts with / ends with
    STRING,
    AGGREGATE,
    COLLECTION,
    LEAF
}
