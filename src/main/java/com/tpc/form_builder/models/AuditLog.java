package com.tpc.form_builder.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
public class AuditLog extends BaseEntity{
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    private String entityType;
    private String entityId;

    private String operation;

    @Column(columnDefinition = "jsonb")
    private String oldValue;

    @Column(columnDefinition = "jsonb")
    private String newValue;

}
