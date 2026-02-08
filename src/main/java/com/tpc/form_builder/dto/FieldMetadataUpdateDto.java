package com.tpc.form_builder.dto;

import lombok.Data;

@Data
public class FieldMetadataUpdateDto {
    private String fieldText;
    private String description;
    private int sortOrder;
}
