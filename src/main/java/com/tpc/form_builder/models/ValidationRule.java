package com.tpc.form_builder.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationRule {
    private Boolean enabled;
    private String errorMessage;

    // NUMBER / DECIMAL VALUES
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private Integer decimalPrecision;
    private Boolean onlyPositive;
}
