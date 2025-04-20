package org.bnbdevelopers.form_builder.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bnbdevelopers.form_builder.enums.FieldType;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormFieldData {
    private FieldType fieldType;
    private List<String> values;
    private String keyword;
    private String formFieldId;
}
