package com.tpc.form_builder.modules.validation.factory;

import com.tpc.form_builder.modules.validation.exception.ValidationException;
import com.tpc.form_builder.models.enums.FieldType;
import com.tpc.form_builder.modules.validation.validator.FieldDataTypeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FieldDataTypeValidatorFactory {

    private final List<FieldDataTypeValidator> validators;


    public List<FieldDataTypeValidator> getAll(FieldType type) {

        List<FieldDataTypeValidator> result = validators.stream()
                .filter(v -> v.supports(type))
                .toList();

        if (result.isEmpty()) {
            throw new ValidationException("No validators for " + type);
        }

        return result;
    }
}