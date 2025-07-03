package org.tpc.form_builder.service.mapper;

import org.tpc.form_builder.models.FormField;
import org.tpc.form_builder.service.dto.FormFieldDto;
import org.mapstruct.Mapper;

import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FormFieldMapper extends EntityMapper<FormFieldDto, FormField> {
    @Mapping(source = "id", target = "id")
    FormFieldDto toDto(FormFieldDto formFieldDto);
    @Mapping(target = "clientId", expression = "java(org.tpc.form_builder.constants.CommonConstants.DEFAULT_CLIENT)")
    FormField toEntity(FormField formField);
}