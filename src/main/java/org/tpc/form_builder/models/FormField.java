package org.tpc.form_builder.models;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.tpc.form_builder.enums.FieldType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

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
}

