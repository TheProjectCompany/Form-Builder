package org.bnbdevelopers.form_builder.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bnbdevelopers.form_builder.enums.SectionType;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionView {
    private String id;
    private String name;
    private String description;
    private SectionType sectionType;
    private int sortOrder;
    private List<FormField> fieldsView;
}
