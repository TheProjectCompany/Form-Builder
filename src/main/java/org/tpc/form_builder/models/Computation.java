package org.tpc.form_builder.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tpc.form_builder.enums.ComputationScope;
import org.tpc.form_builder.enums.FieldType;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Computation {
    // Dependent Field IDs
    private Set<String> dependsOn;
    private Expression expression;
    @Builder.Default
    private ComputationScope computationScope = ComputationScope.DISABLED;
}
