package com.tpc.form_builder.models;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AuditEntity {
    @Builder.Default
    private Instant createdAt = Instant.now();
    @Builder.Default
    private Instant updatedAt = Instant.now();

    private UUID createdBy;
    private UUID updatedBy;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
