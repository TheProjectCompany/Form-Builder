package org.bnbdevelopers.form_builder.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bnbdevelopers.form_builder.enums.VisibilityOperator;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Visibility {
    private String fieldId;
    private VisibilityOperator operator;
    private List<String> values;
}
