package com.tpc.form_builder.repository;

import com.tpc.form_builder.models.enums.FieldType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class SubmissionFilter {
    private UUID fieldId;
    private Operator operator;
    private Object value;
    private FieldType fieldType;
}

