package org.tpc.form_builder.models;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.tpc.form_builder.enums.FieldType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.tpc.form_builder.exception.BadRequestException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "formField")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FormField extends BaseAttributes {

    @Id
    private String id;

    @NotNull(message = "Parent profile ID must not be null")
    private String profileId;

    @NotNull(message = "Section ID must not be null")
    private String sectionId;

    @NotNull(message = "Field text must not be null")
    private String fieldText;

    @NotNull(message = "Keyword must not be null")
    private String keyword;

    @NotNull(message = "Field type is required")
    private FieldType fieldType;

    private String helpText;
    private List<String> defaultValues;
    private Boolean required;
    private Boolean readOnly;
    private ValidationRules validationRules;
    private Visibility visibilityRules;
    private Computation computationRules;

    /**
     * Reference ID depends on fieldType:
     * - DROPDOWN: tagId
     * - PROFILEREF: referenceProfileId
     * - FIELDREF: referenceFieldId
     */
    private String referenceId;

    @NotNull(message = "Sort order must not be null")
    @Builder.Default
    private int sortOrder = 1;

    public List<String> validateFormField() {
        List<String> errors = new ArrayList<>();
        if (Boolean.TRUE.equals(required) && Boolean.TRUE.equals(readOnly)) {
            errors.add("Field cannot be both required and read-only.");
        }
        return errors;
    }
}

