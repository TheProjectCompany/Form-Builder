package org.tpc.form_builder.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tpc.form_builder.enums.ComputationScope;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Computation {
    private boolean isEnabled;
    // Dependent Field IDs
    private List<String> dependsOn;
    // Calculation Expression
    private String expression;
    private ComputationScope computationScope;
}
