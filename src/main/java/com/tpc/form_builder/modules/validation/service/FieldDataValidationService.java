package com.tpc.form_builder.modules.validation.service;

import com.tpc.form_builder.models.Field;
import com.tpc.form_builder.models.FieldData;
import com.tpc.form_builder.modules.validation.dto.ValidationContext;
import com.tpc.form_builder.modules.validation.dto.ValidationError;

import java.util.List;

public interface FieldDataValidationService {
    List<ValidationError> validateFieldData(Field field, ValidationContext validationContext, FieldData fieldData);
}
