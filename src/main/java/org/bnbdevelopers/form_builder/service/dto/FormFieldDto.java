package org.bnbdevelopers.form_builder.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bnbdevelopers.form_builder.enums.FieldType;

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

    @NotBlank(message = "Section ID must not be null")
    private String sectionId;

    @NotBlank(message = "Profile ID must not be null")
    private String profileId;

    @NotBlank(message = "Field type must not be null")
    private FieldType fieldType;

    private String helpText;
    private String description;
    private Boolean required;
    private String referenceId;
    private String defaultValue;

    @Builder.Default
    private int sortOrder = 1;
}
