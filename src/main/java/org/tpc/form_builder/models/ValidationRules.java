package org.tpc.form_builder.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tpc.form_builder.enums.DateValidationType;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationRules {
    private Boolean enabled;
    private String errorMessage;

    // NUMBER / DECIMAL VALUES
    private BigDecimal minValue;
    private BigDecimal maxValue;
    private Integer decimalPrecision;
    private Boolean onlyPositive;

    // TEXT / PARAGRAPH
    private Integer minLength;
    private Integer maxLength;
    private String pattern;

    // DAT
    private String minDate;
    private String maxDate;
    private Integer minAge;
    private Integer maxAge;

    // DATE TIME
    private String minDateTime;
    private String maxDateTime;

    // DATE / DATETIME
    private DateValidationType dateValidationType;
    private String dateTimeFormat;

    // DROPDOWN
    private Boolean allowMultiple;
    private Integer maximumSelections;

    // PROFILEREF
    private Boolean allowLinking;
    private Boolean allowAddition;

    // BOOLEAN OR CHECKBOX
    private Boolean onlyTrueAllowed;
}
