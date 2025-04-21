package org.tpc.form_builder.audits;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tpc.form_builder.constants.CommonConstants;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditDto {
    private String id;
    private String entityId;
    private String repository;
    @Builder.Default
    private String clientId = CommonConstants.DEFAULT_CLIENT;
    private Long associatedUserId;
    @Builder.Default
    private Map<String, ChangeDto> changes = new HashMap<>();
    @Builder.Default
    private AuditAction actionType = AuditAction.UPDATE;
    @Builder.Default
    private Instant createdOn = Instant.now();
}
