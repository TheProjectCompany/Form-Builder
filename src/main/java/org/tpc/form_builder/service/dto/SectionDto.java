package org.tpc.form_builder.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tpc.form_builder.enums.SectionType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionDto {

    private String id;

    @NotBlank(message = "Section name must not be blank")
    private String name;

    private String description;

    @Builder.Default
    private SectionType type = SectionType.GENERAL;

    @Builder.Default
    private int sortOrder = 1;
}