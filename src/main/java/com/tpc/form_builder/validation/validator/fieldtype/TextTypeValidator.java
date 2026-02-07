package com.tpc.form_builder.validation.validator.fieldtype;

import com.tpc.form_builder.models.Field;
import com.tpc.form_builder.models.FieldData;
import com.tpc.form_builder.models.ValidationRule;
import com.tpc.form_builder.models.enums.FieldType;
import com.tpc.form_builder.validation.dto.ValidationContext;
import com.tpc.form_builder.validation.dto.ValidationError;
import com.tpc.form_builder.validation.utils.CommonUtility;
import com.tpc.form_builder.validation.validator.FieldDataTypeValidator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Component
public class TextTypeValidator implements FieldDataTypeValidator {

    private final List<FieldType> supportedTypes = List.of(FieldType.TEXT, FieldType.PARAGRAPH);

    @Override
    public boolean supports(FieldType fieldType) {
        return supportedTypes.contains(fieldType);
    }

    @Override
    public List<ValidationError> validate(Field field, ValidationContext validationContext, FieldData fieldData) {
        List<ValidationRule> rules = CommonUtility.getEnabledRules(field);

        String textFieldData = extractTextValue(fieldData, field);

        List<ValidationError> errors = new ArrayList<>();

        for (ValidationRule rule : rules) {
            List<String> ruleErrors = validateTextFieldRule(textFieldData, rule);

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

    private List<String> validateTextFieldRule(String textFieldData, ValidationRule rule) {
        List<String> errors = new ArrayList<>();
        if (textFieldData == null) textFieldData = "";

        // ✅ Min length check
        if (rule.getMinLength() != null && textFieldData.length() < rule.getMinLength()) {
            errors.add(String.format("Text is shorter than minimum length of %d", rule.getMinLength()));
        }

        // ✅ Max length check
        if (rule.getMaxLength() != null && textFieldData.length() > rule.getMaxLength()) {
            errors.add(String.format("Text exceeds maximum length of %d", rule.getMaxLength()));
        }

        // ✅ Pattern check
        if (StringUtils.isNotBlank(rule.getPattern())) {
            try {
                Pattern pattern = Pattern.compile(rule.getPattern());
                if (!pattern.matcher(textFieldData).matches()) {
                    errors.add("Text does not match the required pattern");
                }
            } catch (PatternSyntaxException _) {
                errors.add("Invalid regex pattern in validation rules");
            }
        }
        return errors;
    }

    private String extractTextValue(FieldData fieldData, Field field) {
        if (fieldData == null) {
            return "";
        }

        return FieldType.PARAGRAPH.equals(field.getFieldType()) ? fieldData.getLongTextValue() : fieldData.getTextValue();
    }
}
