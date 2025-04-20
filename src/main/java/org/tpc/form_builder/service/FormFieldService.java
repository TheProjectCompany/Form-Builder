package org.tpc.form_builder.service;

import org.tpc.form_builder.models.FormField;
import org.tpc.form_builder.service.dto.FormFieldDto;

import java.util.List;

public interface FormFieldService {
    FormField getFormFieldById(String id);
    List<FormField> getFormFieldsBySectionId(String sectionId);
    FormFieldDto createFormField(FormFieldDto formFieldDto);
}
