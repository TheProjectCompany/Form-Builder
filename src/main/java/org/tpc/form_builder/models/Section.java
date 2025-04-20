package org.tpc.form_builder.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tpc.form_builder.enums.SectionType;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Section {
    @Id
    private String id;
    private String name;
    private String description;
    @Builder.Default
    private SectionType type = SectionType.GENERAL;
    @Builder.Default
    private int sortOrder = 1;
}
