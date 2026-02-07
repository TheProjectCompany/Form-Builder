package com.tpc.form_builder.validation.service.impl;

import com.tpc.form_builder.models.Field;
import com.tpc.form_builder.models.FieldData;
import com.tpc.form_builder.validation.dto.ValidationContext;
import com.tpc.form_builder.validation.dto.ValidationError;
import com.tpc.form_builder.validation.factory.FieldDataTypeValidatorFactory;
import com.tpc.form_builder.validation.service.FieldDataValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class FieldDataValidationServiceServiceImpl implements FieldDataValidationService {

    private final FieldDataTypeValidatorFactory fieldDataTypeValidatorFactory;

    @Override
    public List<ValidationError> validateFieldData(Field field, ValidationContext validationContext, FieldData fieldData) {
        List<ValidationError> errors = new ArrayList<>();

        fieldDataTypeValidatorFactory.getAll(field.getFieldType()).forEach(v -> {
            List<ValidationError> fieldErrors = v.validate(field, validationContext, fieldData);
            if (!fieldErrors.isEmpty()) {
                errors.addAll(fieldErrors);
            }
        });

        return errors;
    }
}
