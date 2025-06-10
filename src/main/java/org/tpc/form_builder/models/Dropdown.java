package org.tpc.form_builder.models;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.tpc.form_builder.enums.DropdownType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
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
    @NotBlank(message = "Dropdown name must not be blank")
    private String name;
    private String description;
    @Builder.Default
    private List<DropdownElement> dropdownElements = new ArrayList<>();

    @Builder.Default
    private DropdownType type = DropdownType.CUSTOM;

}
