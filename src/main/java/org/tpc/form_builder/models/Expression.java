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

    public boolean validateExpression() {
        if (expressionType == null || resultType == null) {
            return false;
        }
        if (ExpressionType.LEAF.equals(expressionType) && StringUtils.isNotEmpty(fieldId)) {
            return true;
        }
        return operands.stream().allMatch(Expression::validateExpression);
    }

    public List<String> evaluateExpression(Map<String, FormFieldData> fieldDataMap) throws IllegalArgumentException, ArithmeticException {
        if (!validateExpression()) {
            throw new IllegalArgumentException("Invalid expression structure");
        }

        return switch (expressionType) {
            case LEAF -> evaluateLeafExpression(fieldDataMap);
            case ARITHMETIC -> evaluateArithmeticExpression(fieldDataMap);
            case LOGICAL -> evaluateLogicalExpression(fieldDataMap);
            case COMPARISON -> evaluateComparisonExpression(fieldDataMap);
            default -> null;
        };
    }

    private List<String> evaluateLeafExpression(Map<String, FormFieldData> fieldDataMap) {
        if (OperationType.CONSTANT.equals(operatorType)) {
            return constantValues;
        }

        if (CollectionUtils.isEmpty(fieldDataMap) || !fieldDataMap.containsKey(fieldId) || CollectionUtils.isEmpty(fieldDataMap.get(fieldId).getValues())) {
            throw new IllegalArgumentException("Field data not found for field id: " + fieldId);
        }
        return fieldDataMap.get(fieldId).getValues();
    }

    private List<String> evaluateArithmeticExpression(Map<String, FormFieldData> fieldDataMap) throws ArithmeticException, IllegalArgumentException {
        if (operands.size() != 2) {
            throw new IllegalArgumentException("Invalid Number of operands"); // Not enough operands for arithmetic operation
        }

        Set<FieldType> operandTypes = operands.stream()
                .map(Expression::getResultType)
                .collect(Collectors.toSet());

        if (!areValidArithmeticOperandTypes(operandTypes)) {
            throw new IllegalArgumentException("Only Decimals and number allowed in arithmetic expressions"); // Only NUMBER or DECIMAL are supported for arithmetic operations
        }

        // Example: supporting only addition for now
        BigDecimal result = switch (operatorType) {
            case ADDITION -> handleAddition(operands.getFirst().evaluateExpression(fieldDataMap).getFirst(), operands.getLast().evaluateExpression(fieldDataMap).getFirst());
            case SUBTRACTION -> handleSubtraction(operands.getFirst().evaluateExpression(fieldDataMap).getFirst(), operands.getLast().evaluateExpression(fieldDataMap).getFirst());
            case MULTIPLICATION -> handleMultiplication(operands.getFirst().evaluateExpression(fieldDataMap).getFirst(), operands.getLast().evaluateExpression(fieldDataMap).getFirst());
            case DIVISION -> handleDivision(operands.getFirst().evaluateExpression(fieldDataMap).getFirst(), operands.getLast().evaluateExpression(fieldDataMap).getFirst());
            case EXPONENTIATION -> handlePow(operands.getFirst().evaluateExpression(fieldDataMap).getFirst(), operands.getLast().evaluateExpression(fieldDataMap).getFirst());
            case MODULUS -> handleModulus(operands.getFirst().evaluateExpression(fieldDataMap).getFirst(), operands.getLast().evaluateExpression(fieldDataMap).getFirst());
            default -> throw new UnsupportedOperationException("Unsupported operator: " + operatorType);
        };

        return List.of(String.valueOf(result));
    }
    
    private List<String> evaluateLogicalExpression(Map<String, FormFieldData> fieldDataMap) throws IllegalArgumentException {
        Set<FieldType> operandTypes = operands.stream()
                .map(Expression::getResultType)
                .collect(Collectors.toSet());

        if (!areValidLogicalOperandTypes(operandTypes)) {
            throw new IllegalArgumentException("Only Boolean fields allowed in logical expressions"); // Only CHECKBOX is supported for logical operations
        }

        return switch (operatorType) {
            case AND -> evaluateAndExpression(fieldDataMap);
            case OR -> evaluateOrExpression(fieldDataMap);
            case NOT -> evaluateNotExpression(fieldDataMap);
            default -> throw new UnsupportedOperationException("Unsupported operator for Logical Expression: " + operatorType);
        };
    }

    private List<String> evaluateComparisonExpression(Map<String, FormFieldData> fieldDataMap) {
        FieldType aggregatedType = validateAndGetComparisonType();

        return switch (operatorType) {
            case EQUALS -> List.of(String.valueOf(evaluateEqualsExpression(aggregatedType, fieldDataMap)));
            case NOT_EQUALS -> List.of(String.valueOf(evaluateNotEqualsExpression(aggregatedType, fieldDataMap)));
            case GREATER_THAN -> List.of(String.valueOf(evaluateGreaterThanExpression(aggregatedType, fieldDataMap, false)));
            case GREATER_THAN_OR_EQUAL_TO -> List.of(String.valueOf(evaluateGreaterThanExpression(aggregatedType, fieldDataMap, true)));
            case LESS_THAN -> List.of(String.valueOf(evaluateLesserThanExpression(aggregatedType, fieldDataMap, false)));
            case LESS_THAN_OR_EQUAL_TO -> List.of(String.valueOf(evaluateLesserThanExpression(aggregatedType, fieldDataMap, true)));
            case CONTAINS -> List.of(String.valueOf(evaluateContainsExpression(aggregatedType, fieldDataMap)));
            // Add CONTAINS, NOT_CONTAINS, CONTAINS_ALL, STARTS_WITH, ENDS_WITH
            default -> throw new UnsupportedOperationException("Unsupported operator for Comparison Expression: " + operatorType);
        };
    }

    private boolean evaluateEqualsExpression(FieldType aggregatedFieldType, Map<String, FormFieldData> fieldDataMap) {
        return switch (aggregatedFieldType) {
            case NUMBER, DECIMAL ->  evaluateNumericComparison(fieldDataMap) == 0;
            case TEXT, PARAGRAPH, DATE, DATETIME, BOOLEAN -> evaluateStringEqualsExpression(fieldDataMap);
            default -> throw new UnsupportedOperationException("Unsupported aggregated field type: " + aggregatedFieldType);
        };
    }

    private boolean evaluateNotEqualsExpression(FieldType aggregatedFieldType, Map<String, FormFieldData> fieldDataMap) {
        return switch (aggregatedFieldType) {
            case NUMBER, DECIMAL ->  evaluateNumericComparison(fieldDataMap) != 0;
            case TEXT, PARAGRAPH, DATE, DATETIME, BOOLEAN -> !evaluateStringEqualsExpression(fieldDataMap);
            default -> throw new UnsupportedOperationException("Unsupported aggregated field type: " + aggregatedFieldType);
        };
    }

    private boolean evaluateGreaterThanExpression(FieldType aggregatedFieldType, Map<String, FormFieldData> fieldDataMap, boolean allowEquals) {
        return switch (aggregatedFieldType) {
            case NUMBER, DECIMAL -> allowEquals ? evaluateNumericComparison(fieldDataMap) > 0 : evaluateNumericComparison(fieldDataMap) >= 0;
            // TODO - Add Date Types as well
            default -> throw new UnsupportedOperationException("Unsupported aggregated field type: " + aggregatedFieldType);
        };
    }

    private boolean evaluateLesserThanExpression(FieldType aggregatedFieldType, Map<String, FormFieldData> fieldDataMap, boolean allowEquals) {
        return switch (aggregatedFieldType) {
            case NUMBER, DECIMAL -> allowEquals ? evaluateNumericComparison(fieldDataMap) < 0 : evaluateNumericComparison(fieldDataMap) <= 0;
            // TODO - Add Date Types as well
            default -> throw new UnsupportedOperationException("Unsupported aggregated field type: " + aggregatedFieldType);
        };
    }

    private boolean evaluateStringEqualsExpression(Map<String, FormFieldData> fieldDataMap) {
        String value1 = operands.getFirst().evaluateAndExpression(fieldDataMap).getFirst();
        String value2 = operands.getLast().evaluateAndExpression(fieldDataMap).getFirst();

        return value1.equals(value2);
    }

    private boolean evaluateContainsExpression(FieldType aggregatedFieldType, Map<String, FormFieldData> fieldDataMap) {
        return switch (aggregatedFieldType) {
            case DROPDOWN -> evaluateDropdownContainsExpression(fieldDataMap);
            case TEXT, PARAGRAPH -> evaluateStringContainsExpression(fieldDataMap);
            default -> throw new IllegalStateException("Unexpected value: " + aggregatedFieldType);
        };
    }

    private boolean evaluateDropdownContainsExpression(Map<String, FormFieldData> fieldDataMap) {
        HashSet<String> baseValues = new HashSet<>(operands.getFirst().evaluateExpression(fieldDataMap));
        List<String> childValues = operands.getLast().evaluateExpression(fieldDataMap);

        return baseValues.containsAll(childValues);
    }

    private boolean evaluateStringContainsExpression(Map<String, FormFieldData> fieldDataMap) {
        String value1 = operands.getFirst().evaluateAndExpression(fieldDataMap).getFirst();
        String value2 = operands.getLast().evaluateAndExpression(fieldDataMap).getFirst();

        return value1.contains(value2);
    }

    private FieldType validateAndGetComparisonType() {
        if (operands.size() != 2) {
            throw new IllegalArgumentException("Invalid Number of operands for comparison operation"); // Not enough operands for comparison operation
        }

        if (!BOOLEAN.equals(resultType)) {
            throw new IllegalArgumentException("Result type not supported");
        }

        Set<FieldType> operandTypes = operands.stream()
                .map(Expression::getResultType)
                .collect(Collectors.toSet());

        // Get any object
        return operandTypes.iterator().next();
    }

    private int evaluateNumericComparison(Map<String, FormFieldData> fieldDataMap) {
        String value1 = operands.getFirst().evaluateAndExpression(fieldDataMap).getFirst();
        String value2 = operands.getLast().evaluateAndExpression(fieldDataMap).getFirst();

        return safeBigDecimal(value1).compareTo(safeBigDecimal(value2));
    }

    private List<String> evaluateAndExpression(Map<String, FormFieldData> fieldDataMap) {
        if (operands.size() < 2) {
            throw new IllegalArgumentException("Invalid Number of operands for logical operation"); // Not enough operands for logical operation
        }

        boolean result = operands.stream()
                .map(operand -> Boolean.parseBoolean(operand.evaluateExpression(fieldDataMap).getFirst()))
                .reduce(true, (a, b) -> a && b);

        return List.of(String.valueOf(result));
    }

    private List<String> evaluateOrExpression(Map<String, FormFieldData> fieldDataMap) {
        if (operands.size() < 2) {
            throw new IllegalArgumentException("Invalid Number of operands for logical operation"); // Not enough operands for logical operation
        }

        boolean result = operands.stream()
                .map(operand -> Boolean.parseBoolean(operand.evaluateExpression(fieldDataMap).getFirst()))
                .reduce(false, (a, b) -> a || b);

        return List.of(String.valueOf(result));
    }

    private List<String> evaluateNotExpression(Map<String, FormFieldData> fieldDataMap) {
        if (operands.size() != 1) {
            throw new IllegalArgumentException("Invalid Number of operands for logical operation"); // Not enough operands for logical operation
        }

        boolean result = !Boolean.parseBoolean(operands.getFirst().evaluateExpression(fieldDataMap).getFirst());
        return List.of(String.valueOf(result));
    }

    private boolean areValidLogicalOperandTypes(Set<FieldType> operandTypes) {
        return operandTypes.stream()
                .allMatch(type -> type == BOOLEAN);
    }

    private boolean areValidArithmeticOperandTypes(Set<FieldType> operandTypes) {
        return operandTypes.stream()
                .allMatch(type -> type == NUMBER || type == DECIMAL);
    }

    private List<String> evaluateStringExpression(Map<String, FormFieldData> fieldDataMap) {
        if (operands.size() < 2) {
            throw new IllegalArgumentException("Invalid Number of operands for string operation"); // Not enough operands for string operation
        }

        Set<FieldType> operandTypes = operands.stream()
                .map(Expression::getResultType)
                .collect(Collectors.toSet());

        boolean allString = operandTypes.stream()
                .allMatch(TEXT::equals);

        if (!allString) {
            throw new IllegalArgumentException("Only String fields allowed in string expressions"); // Only TEXT is supported for string operations
        }

        String result = switch (operatorType) {
            case CONCATENATION -> operands.getFirst().evaluateExpression(fieldDataMap).getFirst() + operands.getLast().evaluateExpression(fieldDataMap).getFirst();
            default -> throw new UnsupportedOperationException("Unsupported operator: " + operatorType);
        };

        return List.of(result);
    }

    // Helper methods for arithmetic operations
    private BigDecimal handleAddition(String value1, String value2) {
        BigDecimal num1 = safeBigDecimal(value1);
        BigDecimal num2 = safeBigDecimal(value2);
        return num1.add(num2);
    }

    private BigDecimal handleSubtraction(String value1, String value2) {
        BigDecimal num1 = safeBigDecimal(value1);
        BigDecimal num2 = safeBigDecimal(value2);
        return num1.subtract(num2);
    }

    private BigDecimal handleMultiplication(String value1, String value2) {
        BigDecimal num1 = safeBigDecimal(value1);
        BigDecimal num2 = safeBigDecimal(value2);
        return num1.multiply(num2);
    }

    private BigDecimal handleDivision(String value1, String value2) {
        BigDecimal num1 = safeBigDecimal(value1);
        BigDecimal num2 = safeBigDecimal(value2);
        if (num2.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Division by zero is not allowed");
        }
        // You should specify scale and rounding mode to avoid ArithmeticException
        return num1.divide(num2, 10, RoundingMode.HALF_UP);
    }

    private BigDecimal handlePow(String value1, String value2) {
        BigDecimal num1 = safeBigDecimal(value1);
        BigDecimal num2 = safeBigDecimal(value2);
        try {
            int exponent = num2.intValueExact(); // Will throw if not a whole number
            return num1.pow(exponent);
        } catch (ArithmeticException e) {
            // Fallback to double-based power for non-integer exponents (with precision loss)
            double result = Math.pow(num1.doubleValue(), num2.doubleValue());
            return BigDecimal.valueOf(result);
        }
    }

    private BigDecimal handleModulus(String value1, String value2) {
        BigDecimal num1 = safeBigDecimal(value1);
        BigDecimal num2 = safeBigDecimal(value2);
        return num1.remainder(num2);
    }


    /**
     * Safely parses a String into BigDecimal.
     * Throws IllegalArgumentException if input is invalid.
     */
    private BigDecimal safeBigDecimal(String input) {
        if (StringUtils.isEmpty(input)) {
            return BigDecimal.ZERO; // Default to zero if input is empty
        }
        try {
            return new BigDecimal(input.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number: " + input, e);
        }
    }
}
