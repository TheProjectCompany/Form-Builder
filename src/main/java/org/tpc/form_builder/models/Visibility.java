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
public class Visibility {
    private Boolean enabled;
    private Expression expression;

    public boolean validateVisibilityRules() {
        if (Boolean.TRUE.equals(enabled) && expression != null) {
            return FieldType.BOOLEAN.equals(expression.getResultType());
        }
        return true;
    }
}
