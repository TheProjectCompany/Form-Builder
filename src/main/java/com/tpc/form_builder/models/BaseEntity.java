package com.tpc.form_builder.models;

import jakarta.persistence.MappedSuperclass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity extends AuditEntity{

    private UUID tenantId;

    @Builder.Default
    private boolean isActive = true;
}
