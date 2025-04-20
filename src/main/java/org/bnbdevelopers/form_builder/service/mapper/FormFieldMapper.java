package org.bnbdevelopers.form_builder.service.mapper;

import org.bnbdevelopers.form_builder.models.FormField;
import org.bnbdevelopers.form_builder.service.dto.FormFieldDto;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FormFieldMapper extends EntityMapper<FormFieldDto, FormField> {
    @Mapping(source = "id", target = "id")
    FormFieldDto toDto(FormFieldDto formFieldDto);
    FormField toEntity(FormField formField);
}