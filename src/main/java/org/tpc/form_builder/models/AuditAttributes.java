package org.tpc.form_builder.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AuditAttributes {
    @CreatedDate
    private Instant createdOn;
    @LastModifiedDate
    private Instant modifiedOn;
    private String createdBy;
    private String modifiedBy;
}
