package org.tpc.form_builder.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "audit_snapshot")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AuditSnapShot extends BaseAttributes{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String className;
    private String instanceId;
    private String instanceVersion;

    @Column(columnDefinition = "json")
    private String auditObject;
}
