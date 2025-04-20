package org.bnbdevelopers.form_builder.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDto {

    private String id;

    @NotBlank(message = "Profile name cannot be blank")
    private String name;

    private List<SectionDto> sections;

    @Builder.Default
    private int sortOrder = 1;
}