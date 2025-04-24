package org.tpc.form_builder.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tpc.form_builder.audits.AuditDto;

public interface AuditLogsViewService {
    Page<AuditDto> getClientAuditLogs(String clientId, Pageable pageable);
    Page<AuditDto> getCompanyAuditLogs(String clientId, String companyId, Pageable pageable);
    Page<AuditDto> getUsersAuditLogs(String clientId, String userReferenceId, Pageable pageable);
    Page<AuditDto> getInstanceAuditLogs(String clientId, String instanceId, Pageable pageable);
}
