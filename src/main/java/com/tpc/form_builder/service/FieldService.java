package com.tpc.form_builder.service;

import com.tpc.form_builder.dto.FieldMetadataUpdateDto;
import com.tpc.form_builder.models.Field;

import java.util.List;
import java.util.UUID;

public interface FieldService {
    Field createField(UUID formId, Field field);
    Field getFieldById(UUID fieldId);
    List<Field> getFieldsByFormId(UUID formId);
    Field updateFieldMetadata(UUID fieldId, FieldMetadataUpdateDto fieldMetadataUpdateDto);
}
