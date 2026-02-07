package com.tpc.form_builder.modules.validation.validator.fieldtype;

import com.tpc.form_builder.models.Field;
import com.tpc.form_builder.models.FieldData;
import com.tpc.form_builder.models.enums.FieldType;
import com.tpc.form_builder.modules.validation.dto.ValidationContext;
import com.tpc.form_builder.modules.validation.dto.ValidationError;
import com.tpc.form_builder.modules.validation.validator.FieldDataTypeValidator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BasicValidator implements FieldDataTypeValidator {
    @Override
    public boolean supports(FieldType fieldType) {
        // Basic validator supports all field types, it is used as a fallback when there is no specific validator for a field type
        return true;
    }

    @Override
    public List<ValidationError> validate(Field field, ValidationContext validationContext, FieldData fieldData) {
        return List.of();
    }
}
