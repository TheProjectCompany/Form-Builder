package com.tpc.form_builder.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(indexes = {
        @Index(columnList = "submissionId"),
        @Index(columnList = "fieldId"),
        @Index(columnList = "tenantId")
})
@Data
public class FieldData extends BaseEntity{
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    private UUID submissionId;
    private UUID fieldId;

    private Object value;
}
