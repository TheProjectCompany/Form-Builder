package com.tpc.form_builder.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldConfig {
    @Builder.Default
    private Boolean isRequired = false;

    private List<ValidationRule> validationRules;
}
