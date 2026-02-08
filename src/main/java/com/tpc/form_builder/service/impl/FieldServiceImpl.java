package com.tpc.form_builder.service.impl;

import com.tpc.form_builder.dto.FieldMetadataUpdateDto;
import com.tpc.form_builder.models.Field;
import com.tpc.form_builder.models.Form;
import com.tpc.form_builder.repository.FieldRepository;
import com.tpc.form_builder.service.FieldService;
import com.tpc.form_builder.service.FormService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class FieldServiceImpl implements FieldService {

    private final FieldRepository fieldRepository;
    private final FormService formService;

    @Override
    public Field createField(UUID formId, Field field) {
        log.info("Creating field for form with id: {}", formId);
        Form form = formService.getFormById(formId);
        field.setForm(form);
        return fieldRepository.save(field);
    }

    @Override
    public Field getFieldById(UUID fieldId) {
        return fieldRepository.findById(fieldId).orElse(null);
    }

    @Override
    public List<Field> getFieldsByFormId(UUID formId) {
        return fieldRepository.findAllByFormId(formId);
    }

    @Override
    public Field updateFieldMetadata(UUID fieldId, FieldMetadataUpdateDto fieldMetadataUpdateDto) {
        Optional<Field> optionalField = fieldRepository.findById(fieldId);
        if (optionalField.isPresent()) {
            Field field = optionalField.get();
            field.setFieldText(fieldMetadataUpdateDto.getFieldText());
            field.setDescription(fieldMetadataUpdateDto.getDescription());
            field.setSortOrder(fieldMetadataUpdateDto.getSortOrder());
            return fieldRepository.save(field);
        }
        return null;
    }
}
