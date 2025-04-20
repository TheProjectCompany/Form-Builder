package org.bnbdevelopers.form_builder.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DropdownElement {
    private String id;
    private String value;
    private boolean isActive;
    private Boolean isDefault;
}
