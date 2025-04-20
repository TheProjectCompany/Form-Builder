package org.tpc.form_builder.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tpc.form_builder.enums.FieldType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormFieldView {
    private String id;
    private String profileId;
    private String sectionId;
    private String fieldText;
    private String keyword;
    private String helpText;
    private String defaultValue;
    private Boolean allowLinking;
    private Boolean allowAddition;
    private Boolean allowMultiple;
    private FieldType fieldType;

}
