package com.tpc.form_builder.validation.validator.fieldtype;

import com.tpc.form_builder.models.Field;
import com.tpc.form_builder.models.FieldData;
import com.tpc.form_builder.models.ValidationRule;
import com.tpc.form_builder.models.enums.FieldType;
import com.tpc.form_builder.validation.dto.ValidationContext;
import com.tpc.form_builder.validation.dto.ValidationError;
import com.tpc.form_builder.validation.utils.CommonUtility;
import com.tpc.form_builder.validation.validator.FieldDataTypeValidator;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class NumberTypeValidator implements FieldDataTypeValidator {

    private static final List<FieldType> supportedTypes = List.of(FieldType.NUMBER, FieldType.DECIMAL);

    @Override
    public boolean supports(FieldType fieldType) {
        return supportedTypes.contains(fieldType);
    }

    @Override
    public List<ValidationError> validate(Field field, ValidationContext validationContext, FieldData fieldData) {
        List<ValidationRule> rules = CommonUtility.getEnabledRules(field);

        BigDecimal numberValue = fieldData.getNumberValue();

        List<ValidationError> errors = new ArrayList<>();

        for (ValidationRule rule : rules) {
            List<String> ruleErrors = validateNumberField(numberValue, rule);

            // Skip if no errors for this rule
            if (ruleErrors.isEmpty()) {
                continue;
            }

            ValidationError error = ValidationError.builder()
                    .message(rule.getErrorMessage())
                    .details(ruleErrors)
                    .build();

            errors.add(error);
        }

        return errors;
    }

    // ------- Validation Helpers -------

    private List<String> validateNumberField(BigDecimal numberValue, ValidationRule rule) {
        List<String> errors = new ArrayList<>();
        if (numberValue == null) numberValue = BigDecimal.ZERO;

        if (rule.getMinValue() != null) errors.addAll(validateMinimumValueCheck(numberValue, rule.getMinValue()));
        if (rule.getMaxValue() != null) errors.addAll(validateMaximumValueCheck(numberValue, rule.getMaxValue()));

        if (Boolean.TRUE.equals(rule.getOnlyPositive())) errors.addAll(validateAllowNegative(numberValue));
        return errors;
    }

    private List<String> validateMinimumValueCheck(BigDecimal value, BigDecimal minimumValue) {
        if (minimumValue != null && value.compareTo(minimumValue) < 0) {
            return List.of("Value is less than the allowed minimum");
        }
        return List.of();
    }

    private List<String> validateMaximumValueCheck(BigDecimal value, BigDecimal maximumValue) {
        if (maximumValue != null && value.compareTo(maximumValue) > 0) {
            return List.of("Value is greater than the allowed maximum");
        }
        return List.of();
    }

    private List<String> validateAllowNegative(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            return List.of("Negative values are not allowed");
        }
        return List.of();
    }
}
