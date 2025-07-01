package org.tpc.form_builder.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.tpc.form_builder.enums.ExpressionType;
import org.tpc.form_builder.enums.FieldType;
import org.tpc.form_builder.enums.OperationType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import static org.tpc.form_builder.enums.FieldType.*;

@Data
@AllArgsConstructor
public class Expression {
    private FieldType resultType;
    private ExpressionType expressionType;
    private List<Expression> operands;
    private OperationType operatorType;
    private String fieldId;
    private List<String> constantValues;

    // --- Public API ---

    public boolean validateExpression() {
        if (expressionType == null || resultType == null) return false;
        if (ExpressionType.LEAF.equals(expressionType) && StringUtils.isNotEmpty(fieldId)) return true;
        return operands.stream().allMatch(Expression::validateExpression);
    }

    public List<String> evaluateExpression(Map<String, FormFieldData> fieldDataMap) {
        if (!validateExpression()) throw new IllegalArgumentException("Invalid expression structure");
        return switch (expressionType) {
            case LEAF -> evaluateLeaf(fieldDataMap);
            case ARITHMETIC -> evaluateArithmetic(fieldDataMap);
            case LOGICAL -> evaluateLogical(fieldDataMap);
            case COMPARISON -> evaluateComparison(fieldDataMap);
            case STRING -> evaluateString(fieldDataMap);
            case COLLECTION -> evaluateCollection(fieldDataMap);
            default -> null;
        };
    }

    // --- Expression Evaluation ---

    private List<String> evaluateLeaf(Map<String, FormFieldData> fieldDataMap) {
        if (OperationType.CONSTANT.equals(operatorType)) return constantValues;
        if (CollectionUtils.isEmpty(fieldDataMap) || !fieldDataMap.containsKey(fieldId) || CollectionUtils.isEmpty(fieldDataMap.get(fieldId).getValues())) {
            throw new IllegalArgumentException("Field data not found for field id: " + fieldId);
        }
        return fieldDataMap.get(fieldId).getValues();
    }

    private List<String> evaluateArithmetic(Map<String, FormFieldData> fieldDataMap) {
        validateOperandCount(2, "arithmetic");
        Set<FieldType> operandTypes = getOperandTypes();
        if (!areValidArithmeticOperandTypes(operandTypes)) throw new IllegalArgumentException("Only Decimals and number allowed in arithmetic expressions");
        BigDecimal result = switch (operatorType) {
            case ADDITION -> add(operandValue(0, fieldDataMap), operandValue(1, fieldDataMap));
            case SUBTRACTION -> subtract(operandValue(0, fieldDataMap), operandValue(1, fieldDataMap));
            case MULTIPLICATION -> multiply(operandValue(0, fieldDataMap), operandValue(1, fieldDataMap));
            case DIVISION -> divide(operandValue(0, fieldDataMap), operandValue(1, fieldDataMap));
            case EXPONENTIATION -> pow(operandValue(0, fieldDataMap), operandValue(1, fieldDataMap));
            case MODULUS -> modulus(operandValue(0, fieldDataMap), operandValue(1, fieldDataMap));
            default -> throw new UnsupportedOperationException("Unsupported operator: " + operatorType);
        };
        return List.of(String.valueOf(result));
    }

    private List<String> evaluateLogical(Map<String, FormFieldData> fieldDataMap) {
        Set<FieldType> operandTypes = getOperandTypes();
        if (!areValidLogicalOperandTypes(operandTypes)) throw new IllegalArgumentException("Only Boolean fields allowed in logical expressions");
        return switch (operatorType) {
            case AND -> logicalAnd(fieldDataMap);
            case OR -> logicalOr(fieldDataMap);
            case NOT -> logicalNot(fieldDataMap);
            default -> throw new UnsupportedOperationException("Unsupported operator for Logical Expression: " + operatorType);
        };
    }

    private List<String> evaluateComparison(Map<String, FormFieldData> fieldDataMap) {
        FieldType aggregatedType = validateAndGetComparisonType();
        return switch (operatorType) {
            case EQUALS -> List.of(String.valueOf(equalsComparison(aggregatedType, fieldDataMap)));
            case NOT_EQUALS -> List.of(String.valueOf(notEqualsComparison(aggregatedType, fieldDataMap)));
            case GREATER_THAN -> List.of(String.valueOf(greaterThanComparison(aggregatedType, fieldDataMap, false)));
            case GREATER_THAN_OR_EQUAL_TO -> List.of(String.valueOf(greaterThanComparison(aggregatedType, fieldDataMap, true)));
            case LESS_THAN -> List.of(String.valueOf(lessThanComparison(aggregatedType, fieldDataMap, false)));
            case LESS_THAN_OR_EQUAL_TO -> List.of(String.valueOf(lessThanComparison(aggregatedType, fieldDataMap, true)));
            case CONTAINS -> List.of(String.valueOf(containsComparison(aggregatedType, fieldDataMap)));
            default -> throw new UnsupportedOperationException("Unsupported operator for Comparison Expression: " + operatorType);
        };
    }

    private List<String> evaluateString(Map<String, FormFieldData> fieldDataMap) {
        validateOperandCount(2, "string");
        Set<FieldType> operandTypes = getOperandTypes();
        if (!areValidStringOperandTypes(operandTypes)) throw new IllegalArgumentException("Only Text and Paragraph fields allowed in string expressions");
        String v1 = operandValue(0, fieldDataMap);
        String v2 = operandValue(1, fieldDataMap);
        String result = switch (operatorType) {
            case ADDITION -> v1 + v2;
            case EQUALS -> String.valueOf(v1.equals(v2));
            case NOT_EQUALS -> String.valueOf(!v1.equals(v2));
            case STARTS_WITH -> String.valueOf(v1.startsWith(v2));
            case ENDS_WITH -> String.valueOf(v1.endsWith(v2));
            case CONTAINS -> String.valueOf(v1.contains(v2));
            case NOT_CONTAINS -> String.valueOf(!v1.contains(v2));
            default -> throw new UnsupportedOperationException("Unsupported operator: " + operatorType);
        };
        return List.of(result);
    }

    // --- Logical Operations ---

    private List<String> logicalAnd(Map<String, FormFieldData> fieldDataMap) {
        validateOperandCountAtLeast(2, "logical AND");
        boolean result = operands.stream().map(op -> Boolean.parseBoolean(op.evaluateExpression(fieldDataMap).getFirst())).reduce(true, (a, b) -> a && b);
        return List.of(String.valueOf(result));
    }

    private List<String> logicalOr(Map<String, FormFieldData> fieldDataMap) {
        validateOperandCountAtLeast(2, "logical OR");
        boolean result = operands.stream().map(op -> Boolean.parseBoolean(op.evaluateExpression(fieldDataMap).getFirst())).reduce(false, (a, b) -> a || b);
        return List.of(String.valueOf(result));
    }

    private List<String> logicalNot(Map<String, FormFieldData> fieldDataMap) {
        validateOperandCount(1, "logical NOT");
        boolean result = !Boolean.parseBoolean(operands.getFirst().evaluateExpression(fieldDataMap).getFirst());
        return List.of(String.valueOf(result));
    }

    // --- Comparison Operations ---

    private boolean equalsComparison(FieldType type, Map<String, FormFieldData> fieldDataMap) {
        return switch (type) {
            case NUMBER, DECIMAL -> numericComparison(fieldDataMap) == 0;
            case TEXT, PARAGRAPH, DATE, DATETIME, BOOLEAN -> stringEquals(fieldDataMap);
            default -> throw new UnsupportedOperationException("Unsupported aggregated field type: " + type);
        };
    }

    private boolean notEqualsComparison(FieldType type, Map<String, FormFieldData> fieldDataMap) {
        return switch (type) {
            case NUMBER, DECIMAL -> numericComparison(fieldDataMap) != 0;
            case TEXT, PARAGRAPH, DATE, DATETIME, BOOLEAN -> !stringEquals(fieldDataMap);
            default -> throw new UnsupportedOperationException("Unsupported aggregated field type: " + type);
        };
    }

    private boolean greaterThanComparison(FieldType type, Map<String, FormFieldData> fieldDataMap, boolean allowEquals) {
        return switch (type) {
            case NUMBER, DECIMAL -> allowEquals ? numericComparison(fieldDataMap) > 0 : numericComparison(fieldDataMap) >= 0;
            default -> throw new UnsupportedOperationException("Unsupported aggregated field type: " + type);
        };
    }

    private boolean lessThanComparison(FieldType type, Map<String, FormFieldData> fieldDataMap, boolean allowEquals) {
        return switch (type) {
            case NUMBER, DECIMAL -> allowEquals ? numericComparison(fieldDataMap) < 0 : numericComparison(fieldDataMap) <= 0;
            default -> throw new UnsupportedOperationException("Unsupported aggregated field type: " + type);
        };
    }

    private boolean containsComparison(FieldType type, Map<String, FormFieldData> fieldDataMap) {
        return switch (type) {
            case DROPDOWN -> dropdownContains(fieldDataMap);
            case TEXT, PARAGRAPH -> stringContains(fieldDataMap);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    // --- Comparison Helpers ---

    private FieldType validateAndGetComparisonType() {
        validateOperandCount(2, "comparison operation");
        if (!BOOLEAN.equals(resultType)) throw new IllegalArgumentException("Result type not supported");
        return getOperandTypes().iterator().next();
    }

    private int numericComparison(Map<String, FormFieldData> fieldDataMap) {
        return safeBigDecimal(operandValue(0, fieldDataMap)).compareTo(safeBigDecimal(operandValue(1, fieldDataMap)));
    }

    private boolean stringEquals(Map<String, FormFieldData> fieldDataMap) {
        return operandValue(0, fieldDataMap).equals(operandValue(1, fieldDataMap));
    }

    private boolean dropdownContains(Map<String, FormFieldData> fieldDataMap) {
        HashSet<String> baseValues = new HashSet<>(operands.getFirst().evaluateExpression(fieldDataMap));
        List<String> childValues = operands.getLast().evaluateExpression(fieldDataMap);
        return baseValues.containsAll(childValues);
    }

    private boolean stringContains(Map<String, FormFieldData> fieldDataMap) {
        return operandValue(0, fieldDataMap).contains(operandValue(1, fieldDataMap));
    }

    // --- Operand Utilities ---

    private Set<FieldType> getOperandTypes() {
        return operands.stream().map(Expression::getResultType).collect(Collectors.toSet());
    }

    private String operandValue(int index, Map<String, FormFieldData> fieldDataMap) {
        return operands.get(index).evaluateExpression(fieldDataMap).getFirst();
    }

    private void validateOperandCount(int expected, String context) {
        if (operands == null || operands.size() != expected) {
            throw new IllegalArgumentException("Invalid Number of operands for " + context);
        }
    }

    private void validateOperandCountAtLeast(int min, String context) {
        if (operands == null || operands.size() < min) {
            throw new IllegalArgumentException("Invalid Number of operands for " + context);
        }
    }

    // --- Type Validators ---

    private boolean areValidLogicalOperandTypes(Set<FieldType> operandTypes) {
        return operandTypes.stream().allMatch(type -> type == BOOLEAN);
    }

    private boolean areValidArithmeticOperandTypes(Set<FieldType> operandTypes) {
        return operandTypes.stream().allMatch(type -> type == NUMBER || type == DECIMAL);
    }

    private boolean areValidStringOperandTypes(Set<FieldType> operandTypes) {
        return operandTypes.stream().allMatch(type -> type == FieldType.TEXT || type == FieldType.PARAGRAPH);
    }

    // --- Arithmetic Helpers ---

    private BigDecimal add(String v1, String v2) { return safeBigDecimal(v1).add(safeBigDecimal(v2)); }
    private BigDecimal subtract(String v1, String v2) { return safeBigDecimal(v1).subtract(safeBigDecimal(v2)); }
    private BigDecimal multiply(String v1, String v2) { return safeBigDecimal(v1).multiply(safeBigDecimal(v2)); }
    private BigDecimal divide(String v1, String v2) {
        BigDecimal num2 = safeBigDecimal(v2);
        if (num2.compareTo(BigDecimal.ZERO) == 0) throw new ArithmeticException("Division by zero is not allowed");
        return safeBigDecimal(v1).divide(num2, 10, RoundingMode.HALF_UP);
    }
    private BigDecimal pow(String v1, String v2) {
        BigDecimal num1 = safeBigDecimal(v1);
        BigDecimal num2 = safeBigDecimal(v2);
        try {
            int exponent = num2.intValueExact();
            return num1.pow(exponent);
        } catch (ArithmeticException e) {
            double result = Math.pow(num1.doubleValue(), num2.doubleValue());
            return BigDecimal.valueOf(result);
        }
    }
    @SuppressWarnings("java:S3518")
    private BigDecimal modulus(String v1, String v2) {
        BigDecimal divisor = safeBigDecimal(v2);
        if (divisor == null || divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Modulus by zero is not allowed");
        }
        return safeBigDecimal(v1).remainder(divisor);
    }

    // --- Parsing Helpers ---

    private BigDecimal safeBigDecimal(String input) {
        if (StringUtils.isEmpty(input)) return BigDecimal.ZERO;
        try {
            return new BigDecimal(input.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number: " + input, e);
        }
    }

    // --- Collection Operations ---

    private List<String> evaluateCollection(Map<String, FormFieldData> fieldDataMap) {
        validateOperandCount(1, "collection");
        List<String> values = operands.getFirst().evaluateExpression(fieldDataMap);
        FieldType operandType = operands.getFirst().getResultType();
        String result;
        switch (operatorType) {
            case LENGTH, COUNT -> result = getLengthOrCount(values, operandType);
            case EMPTY -> result = String.valueOf(isEmptyCollection(values, operandType));
            case NOT_EMPTY -> result = String.valueOf(!isEmptyCollection(values, operandType));
            default -> throw new UnsupportedOperationException("Unsupported collection operator: " + operatorType);
        }
        return List.of(result);
    }

    private String getLengthOrCount(List<String> values, FieldType operandType) {
        switch (operandType) {
            case FieldType.TEXT, FieldType.PARAGRAPH -> {
                String first = (values.isEmpty() ? null : values.getFirst());
                return (first == null) ? "0" : String.valueOf(first.length());
            }
            case FieldType.DROPDOWN -> {
                return String.valueOf(values.size());
            }
            default -> throw new UnsupportedOperationException("LENGTH/COUNT not supported for field type: " + operandType);
        }
    }

    private boolean isEmptyCollection(List<String> values, FieldType operandType) {
        switch (operandType) {
            case FieldType.TEXT, FieldType.PARAGRAPH -> {
                String first = (CollectionUtils.isEmpty(values) ? null : values.getFirst());
                return StringUtils.isEmpty(first);
            }
            case FieldType.DROPDOWN -> {
                return CollectionUtils.isEmpty(values);
            }
            default -> throw new UnsupportedOperationException("EMPTY/NOT_EMPTY not supported for field type: " + operandType);
        }
    }
}
