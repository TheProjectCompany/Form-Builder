package org.tpc.form_builder.audits;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tpc.form_builder.enums.FieldType;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeDto {
    private AuditAction action;
    private AuditEntity auditEntity;
    private String fieldName;
    private FieldType fieldType;
    private List<Object> previousValues;
    private List<Object> newValues;
}
