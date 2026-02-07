package com.tpc.form_builder.validation.utils;

import com.tpc.form_builder.models.Field;
import com.tpc.form_builder.models.ValidationRule;

import java.util.List;

public class CommonUtility {
    private CommonUtility() {
        throw new AssertionError("No instances allowed");
    }

    public static List<ValidationRule> getEnabledRules(Field field) {
        return field != null && field.getFieldConfig() != null ? field.getFieldConfig().getValidationRules().stream()
                .filter(rule -> Boolean.TRUE.equals(rule.getEnabled()))
                .toList() : List.of();
    }
}
