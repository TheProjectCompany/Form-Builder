package org.tpc.form_builder.service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tpc.form_builder.enums.FieldType;
import org.tpc.form_builder.models.Visibility;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormFieldDto {

    private String id;

    @NotBlank(message = "Field text must not be null")
    private String fieldText;

    @NotBlank(message = "Keyword must not be null")
    private String keyword;

    private String sectionId;
    private String profileId;

    @NotNull(message = "Field type must not be null")
    private FieldType fieldType;

    private String helpText;
    private String description;
    private Boolean required;
    private String referenceId;
    private String defaultValue;

    private Visibility visibilityRules;

    @Builder.Default
    private int sortOrder = 1;
}
