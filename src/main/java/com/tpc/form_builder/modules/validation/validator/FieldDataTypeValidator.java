package com.tpc.form_builder.modules.validation.validator;

import com.tpc.form_builder.models.Field;
import com.tpc.form_builder.models.FieldData;
import com.tpc.form_builder.models.enums.FieldType;
import com.tpc.form_builder.modules.validation.dto.ValidationContext;
import com.tpc.form_builder.modules.validation.dto.ValidationError;

import java.util.List;

public interface FieldDataTypeValidator {

    boolean supports(FieldType fieldType);

    List<ValidationError> validate(Field field, ValidationContext validationContext, FieldData fieldData);

}
