package org.tpc.form_builder.audits;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tpc.form_builder.constants.CommonConstants;
import org.tpc.form_builder.models.User;

import java.time.Instant;
import java.util.Map;

@Data
@Entity
@Table(name = "audit_log")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(nullable = false)
    private String clientId = CommonConstants.DEFAULT_CLIENT;

    private String entityId;

    @Column(nullable = false)
    private String repository;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Convert(converter = ChangeMapConverter.class)
    @Column(columnDefinition = "json")
    private Map<String, ChangeDto> changes;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private AuditAction actionType = AuditAction.UPDATE;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private Instant createdOn = Instant.now();
}

