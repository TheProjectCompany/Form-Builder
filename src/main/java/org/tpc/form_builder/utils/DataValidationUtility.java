package org.tpc.form_builder.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.tpc.form_builder.constants.CommonConstants;
import org.tpc.form_builder.enums.ComputationScope;
import org.tpc.form_builder.enums.FieldType;
import org.tpc.form_builder.models.*;
import org.tpc.form_builder.models.repository.FormFieldRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Component
@RequiredArgsConstructor
@Log4j2
public class DataValidationUtility {

    private final FormFieldRepository formFieldRepository;

    public Map<String, List<String>> validateProfileData(String profileId, Map<String, FormFieldData> profileDataFields) {
        log.info("Validating profile data");
        // Fetches all form fields that are active and present in the input
        List<FormField> formFields = formFieldRepository.findAllByClientIdAndProfileIdAndIsActive(
                CommonConstants.DEFAULT_CLIENT,
                profileId,
                Boolean.TRUE
        );
        return validateDataFields(formFields, profileDataFields);
    }

    public Map<String, List<String>> validateSpecificFields(Map<String, FormFieldData> dataFieldsMap) {
        log.info("Validating Specific fields");
        List<FormField> validationFields = formFieldRepository.findAllByClientIdAndIsActiveAndIdIn(
                CommonConstants.DEFAULT_CLIENT,
                Boolean.TRUE,
                dataFieldsMap.keySet().stream().toList()
        );
        return validateDataFields(validationFields, dataFieldsMap);
    }

    public Map<String, List<String>> validateDataFields(List<FormField> validationFieldsList, Map<String, FormFieldData> dataFieldsMap) {
        log.info("Validating data fields");

        // Initialize an error map to collect field-wise validation errors
        Map<String, List<String>> errorMap = new HashMap<>();

        if (dataFieldsMap == null || dataFieldsMap.isEmpty()) {
            log.warn("No data fields provided for validation.");
            return errorMap;
        }


        
        List<String> dropdownIdList = validationFieldsList.stream()
                .filter(formField -> FieldType.DROPDOWN.equals(formField.getFieldType()))
                .map(FormField::getReferenceId)
                .toList();
        

        for (FormField formField : validationFieldsList) {
            String fieldId = formField.getId();

            // Safety check: skip if the data field is missing for the current form field
            if (!dataFieldsMap.containsKey(fieldId)) {
                log.warn("Data field missing for field ID: {}", fieldId);
                continue;
            }

            // Perform validation and collect errors for each field
            List<String> fieldErrors = validateFieldData(formField, dataFieldsMap.get(fieldId));
            if (!CollectionUtils.isEmpty(fieldErrors)) {
                errorMap.put(fieldId, fieldErrors);
            }
        }

        return errorMap;
    }

    public List<String> validateFieldData(FormField formField, FormFieldData formFieldData) {
        List<String> errors = new ArrayList<>();

        if (Boolean.TRUE.equals(formField.getReadOnly())) {
            errors.add("Read-Only cannot be modified");
            return errors;
        }

        // 🚫 Skip validation for read-only fields or auto-computed fields or empty validation rules
        if (formField.getComputationRules() != null && ComputationScope.DISABLED.equals(formField.getComputationRules().getComputationScope())) {
            return errors;
        }

        // 🕳 If field data is missing or values are empty
        if (formFieldData == null || CollectionUtils.isEmpty(formFieldData.getValues())) {
            if (Boolean.TRUE.equals(formField.getRequired())) {
                // ❗ Required field is missing
                errors.add("This is a required field");
            }
            // Nothing else to validate if data is missing
            return errors;
        }

        errors.addAll(switch (formField.getFieldType()) {
            case TEXT, PARAGRAPH -> validateTextAndParagraphField(formField, formFieldData.getValues());
            case NUMBER, DECIMAL -> validateNumberAndDecimalField(formField, formFieldData.getValues());
            case BOOLEAN -> validateBooleanField(formField, formFieldData.getValues());
            case DATE -> validateDateField(formField, formFieldData.getValues());
            case DATETIME -> validateDateTimeField(formField, formFieldData.getValues());
            case DROPDOWN -> validateDropdownField(formField, formFieldData.getValues(), new HashMap<>());
            // 🧩 Add more field types here as you expand
            default -> List.of();  // No validation for unhandled types
        });

        return errors;
    }

    private List<String> validateDateField(FormField formField, List<String> values) {
        List<String> errors = new ArrayList<>();

        if (CollectionUtils.isEmpty(values)) {
            return List.of(); // No date value provided
        }

        String dateStr = values.getFirst();
        ValidationRules rules = formField.getValidationRules();
        boolean validationEnabled = rules != null && Boolean.TRUE.equals(rules.getEnabled());

        try {
            LocalDate dateValue = LocalDate.parse(dateStr);

            if (!validationEnabled) {
                return errors;
            }

            validateMinDate(rules, dateValue, errors);
            validateMaxDate(rules, dateValue, errors);

            if (rules.getDateValidationType() == null) {
                return errors;
            }

            switch (rules.getDateValidationType()) {
                case DOB -> validateDateOfBirth(dateValue, errors, formField.getFieldText());
                case DUE_DATE -> validateDueDate(dateValue, errors, formField.getFieldText());
                case AGE -> validateAgeConstraints(rules, dateValue, errors, formField.getFieldText());
                default -> log.info("Unhandled date validation type: {}", rules.getDateValidationType());
            }

        } catch (DateTimeParseException e) {
            errors.add(String.format("Invalid date format: '%s'. Expected format: yyyy-MM-dd", dateStr));
        }

        return errors;
    }

    private void validateMinDate(ValidationRules rules, LocalDate dateValue, List<String> errors) {
        if (rules.getMinDate() != null) {
            LocalDate minDate = LocalDate.parse(rules.getMinDate());
            if (dateValue.isBefore(minDate)) {
                errors.add(String.format("Date is before the allowed minimum of %s", minDate));
            }
        }
    }

    private void validateMaxDate(ValidationRules rules, LocalDate dateValue, List<String> errors) {
        if (rules.getMaxDate() != null) {
            LocalDate maxDate = LocalDate.parse(rules.getMaxDate());
            if (dateValue.isAfter(maxDate)) {
                errors.add(String.format("Date is after the allowed maximum of %s", maxDate));
            }
        }
    }

    private void validateDateOfBirth(LocalDate dateValue, List<String> errors, String fieldText) {
        if (dateValue.isAfter(LocalDate.now())) {
            errors.add(String.format("%s cannot be a future date", fieldText));
        }
    }

    private void validateDueDate(LocalDate dateValue, List<String> errors, String fieldText) {
        if (dateValue.isBefore(LocalDate.now())) {
            errors.add(String.format("%s cannot be a past date", fieldText));
        }
    }

    private void validateAgeConstraints(ValidationRules rules, LocalDate dateValue, List<String> errors, String fieldText) {
        int age = calculateAge(dateValue);

        if (rules.getMinAge() != null && age <= rules.getMinAge()) {
            errors.add(String.format("%s cannot be less than %d years old", fieldText, rules.getMinAge()));
        }

        if (rules.getMaxAge() != null && age > rules.getMaxAge()) {
            errors.add(String.format("%s cannot be greater than %d years old", fieldText, rules.getMaxAge()));
        }
    }

    private List<String> validateDateTimeField(FormField formField, List<String> values) {
        List<String> errors = new ArrayList<>();

        if (CollectionUtils.isEmpty(values)) {
            return List.of(); // No datetime value provided
        }

        String input = values.getFirst();
        ValidationRules rules = formField.getValidationRules();

        boolean validationEnabled = rules != null && Boolean.TRUE.equals(rules.getEnabled());

        try {
            // 🕒 Parse input datetime as UTC
            Instant instant = Instant.parse(input); // Must be ISO-8601 (e.g., 2025-04-18T14:30:00Z)
            ZonedDateTime inputDateTime = instant.atZone(ZoneOffset.UTC);
            ZonedDateTime nowUtc = ZonedDateTime.now(ZoneOffset.UTC);

            // ✅ Min datetime check
            if (validationEnabled && rules.getMinDateTime() != null) {
                ZonedDateTime minAllowed = ZonedDateTime.parse(rules.getMinDateTime()).withZoneSameInstant(ZoneOffset.UTC);
                if (inputDateTime.isBefore(minAllowed)) {
                    errors.add("Datetime is before the minimum allowed: " + minAllowed);
                }
            }

            // ✅ Max datetime check
            if (validationEnabled && rules.getMaxDateTime() != null) {
                ZonedDateTime maxAllowed = ZonedDateTime.parse(rules.getMaxDateTime()).withZoneSameInstant(ZoneOffset.UTC);
                if (inputDateTime.isAfter(maxAllowed)) {
                    errors.add("Datetime is after the maximum allowed: " + maxAllowed);
                }
            }

            // ✅ Future or Past only validation
            if (validationEnabled && rules.getDateValidationType() != null) {
                switch (rules.getDateValidationType()) {
                    case DOB -> validateDateOfBirth(inputDateTime, nowUtc, errors, formField.getFieldText());
                    case DUE_DATE -> validateDueDate(inputDateTime, nowUtc, errors, formField.getFieldText());
                    default -> log.info("Invalid date time validation type: {}", rules.getDateValidationType());
                }
            }

        } catch (DateTimeParseException e) {
            errors.add("Invalid datetime format. Expected ISO-8601 (e.g., 2025-01-01T12:00:00Z)");
        }

        return errors;
    }

    private void validateDueDate(ZonedDateTime inputDateTime, ZonedDateTime nowUtc, List<String> errors, String fieldText) {
        if (inputDateTime.isBefore(nowUtc)) {
            errors.add(String.format("%s must be in the future", fieldText));
        }
    }

    private void validateDateOfBirth(ZonedDateTime inputDateTime, ZonedDateTime nowUtc, List<String> errors, String fieldText) {
        if (inputDateTime.isBefore(nowUtc)) {
            errors.add(String.format("%s must be in the past", fieldText));
        }
    }

    private int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        Period period = Period.between(birthDate, today);
        return period.getYears();
    }

    /**
     * Validates BOOLEAN Fields
     */
    private List<String> validateBooleanField(FormField formField, List<String> values) {
        List<String> errors = new ArrayList<>();

        String value = values.getFirst().toLowerCase(Locale.ROOT);

        ValidationRules rules = formField.getValidationRules();
        boolean validationEnabled = rules != null && Boolean.TRUE.equals(rules.getEnabled());


        // ✅ Check for valid boolean input
        if (!value.equals("true") && !value.equals("false")) {
            errors.add("Invalid boolean value. Expected 'true' or 'false'");
        }

        // ✅ Optional: if only 'true' is allowed (e.g., user must accept terms)
        if (validationEnabled && Boolean.TRUE.equals(rules.getOnlyTrueAllowed()) && !value.equals("true")) {
            errors.add(String.format("'%s' must be set to TRUE", formField.getFieldText()));
        }

        return errors;
    }

    /**
     * Validates Text / Paragraph Fields
     */
    private List<String> validateTextAndParagraphField(FormField profileField, List<String> values) {
        List<String> errors = new ArrayList<>();

        String value = values.getFirst();

        ValidationRules rules = profileField.getValidationRules();
        boolean validationEnabled = rules != null && Boolean.TRUE.equals(rules.getEnabled());

        // ✅ Min length check
        if (validationEnabled && rules.getMinLength() != null && value.length() < rules.getMinLength()) {
            errors.add(String.format("Text is shorter than minimum length of %d", rules.getMinLength()));
        }

        // ✅ Max length check
        if (validationEnabled && rules.getMaxLength() != null && value.length() > rules.getMaxLength()) {
            errors.add(String.format("Text exceeds maximum length of %d", rules.getMaxLength()));
        }

        // ✅ Pattern (Regex) check
        if (validationEnabled && StringUtils.isNotBlank(rules.getPattern())) {
            try {
                Pattern pattern = Pattern.compile(rules.getPattern());
                if (!pattern.matcher(value).matches()) {
                    errors.add("Text does not match the required pattern");
                }
            } catch (PatternSyntaxException e) {
                errors.add("Invalid regex pattern in validation rules");
            }
        }

        return errors;
    }

    /**
     * Validates numeric fields
     */
    private List<String> validateNumberAndDecimalField(FormField formField, List<String> values) {
        List<String> fieldErrors = new ArrayList<>();

        ValidationRules rules = formField.getValidationRules();

        if (CollectionUtils.isEmpty(values)) {
            log.debug("No value provided for numeric field");
            return fieldErrors;
        }

        boolean validationEnabled = rules != null && Boolean.TRUE.equals(rules.getEnabled());

        try {
            BigDecimal numberValue = safeBigDecimal(values.getFirst());

            if (validationEnabled) {
                // Minimum value check
                fieldErrors.addAll(
                        validateMinimumValueCheck(numberValue, formField.getValidationRules().getMinValue())
                );

                // Maximum value check
                fieldErrors.addAll(
                        validateMaximumValueCheck(numberValue, formField.getValidationRules().getMaxValue())
                );

                // Optional: negative value validation (if applicable)
                fieldErrors.addAll(
                        validateAllowNegative(numberValue, formField.getValidationRules().getOnlyPositive())
                );
            }

            return fieldErrors;
        } catch (IllegalArgumentException e) {
            return List.of("Invalid Number / Integer");
        }
    }

    private List<String> validateAllowNegative(BigDecimal value, Boolean onlyPositive) {
        if (Boolean.TRUE.equals(onlyPositive) && value.compareTo(BigDecimal.ZERO) < 0) {
            return List.of("Negative values are not allowed");
        }
        return List.of();
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
    
    private List<String> validateDropdownField(FormField formField, List<String> values, Map<String, Dropdown> dropdownMap) {
        if (formField.getReferenceId() == null || dropdownMap == null || dropdownMap.get(formField.getReferenceId()) == null) {
            log.error("Dropdown reference ID or dropdown map is null. Cannot validate dropdown field.");
            return List.of("Invalid dropdown reference ID or dropdown map");
        }
        Dropdown selectedDropdown = dropdownMap.get(formField.getReferenceId());
        if (CollectionUtils.isEmpty(selectedDropdown.getDropdownElements())) {
            log.error("Dropdown elements are empty. Cannot validate dropdown field.");
            return List.of("Dropdown elements are empty");
        }
        List<String> selectedDropdownValues = selectedDropdown.getDropdownElements().stream()
                .filter(DropdownElement::isActive)
                .map(DropdownElement::getId)
                .toList();
        if (!new HashSet<>(selectedDropdownValues).containsAll(values))
            return List.of("Invalid dropdown values !!");
        return List.of();
    }
}
