package org.tpc.form_builder.models;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.tpc.form_builder.enums.DropdownType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Document(collection = "dropdowns")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Dropdown extends BaseAttributes{
    @Id
    private String id;
    private String name;
    private String description;
    private List<DropdownElement> dropdownElements;

    @Builder.Default
    private DropdownType type = DropdownType.CUSTOM;

    @Builder.Default
    private boolean isActive = true;
}
