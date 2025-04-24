package org.tpc.form_builder.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.tpc.form_builder.audits.AuditAction;
import org.tpc.form_builder.enums.FieldType;

import java.util.HashSet;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormFieldData {
    private FieldType fieldType;
    private List<String> values;
    private String keyword;

    public AuditAction compareEquals(FormFieldData otherData) {
        if (otherData == null) {
            if (CollectionUtils.isEmpty(this.values)) return null;
            else return AuditAction.DELETE;
        }
        if (CollectionUtils.isEmpty(this.values) && CollectionUtils.isEmpty(otherData.getValues())) return null;
        return new HashSet<>(this.values).equals(new HashSet<>(otherData.getValues())) ? null : AuditAction.UPDATE;
    }
}
