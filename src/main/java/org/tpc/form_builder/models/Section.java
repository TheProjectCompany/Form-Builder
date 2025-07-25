package org.tpc.form_builder.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.tpc.form_builder.audits.AuditAction;
import org.tpc.form_builder.enums.SectionType;
import org.springframework.data.annotation.Id;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Section {
    private String id;
    private String name;
    private String description;
    @Builder.Default
    private SectionType type = SectionType.GENERAL;
    @Builder.Default
    private int sortOrder = 1;

    public AuditAction compareEquals(Section otherSection) {
        if (otherSection == null) {
            // Make Soft Delete Support here
            return AuditAction.DELETE;
        }
        if (!StringUtils.equals(this.getName(), otherSection.getName()) || !StringUtils.equals(this.getDescription(), otherSection.getDescription())) {
            return AuditAction.UPDATE;
        }
        return null;
    }
}
