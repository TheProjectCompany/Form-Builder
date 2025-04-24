package org.tpc.form_builder.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.tpc.form_builder.audits.AuditDto;
import org.tpc.form_builder.audits.AuditLog;
import org.tpc.form_builder.audits.AuditLogRepository;
import org.tpc.form_builder.service.AuditLogsViewService;
import org.tpc.form_builder.service.mapper.AuditLogMapper;

@Service
@RequiredArgsConstructor
public class AuditLogsViewServiceImpl implements AuditLogsViewService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    public Page<AuditDto> getClientAuditLogs(String clientId, Pageable pageable) {
        Page<AuditLog> clientAuditLogs = auditLogRepository.findAllByClientId(clientId, pageable);
        return new PageImpl<>(auditLogMapper.toDto(clientAuditLogs.getContent()), pageable, clientAuditLogs.getTotalElements());
    }

    @Override
    public Page<AuditDto> getCompanyAuditLogs(String clientId, String companyId, Pageable pageable) {
        Page<AuditLog> companyAuditLogs = auditLogRepository.findAllByClientIdAndCompanyId(clientId, companyId, pageable);
        return new PageImpl<>(auditLogMapper.toDto(companyAuditLogs.getContent()), pageable, companyAuditLogs.getTotalElements());
    }

    @Override
    public Page<AuditDto> getUsersAuditLogs(String clientId, String userPublicId, Pageable pageable) {
        Page<AuditLog> userAuditLogs = auditLogRepository.findAllByClientIdAndUser_PublicIdAndUser_IsActive(clientId, userPublicId, Boolean.TRUE, pageable);
        return new PageImpl<>(auditLogMapper.toDto(userAuditLogs.getContent()), pageable, userAuditLogs.getTotalElements());
    }

    @Override
    public Page<AuditDto> getInstanceAuditLogs(String clientId, String instanceId, Pageable pageable) {
        Page<AuditLog> instanceAuditLogs = auditLogRepository.findAllByClientIdAndInstanceId(clientId, instanceId, pageable);
        return new PageImpl<>(auditLogMapper.toDto(instanceAuditLogs.getContent()), pageable, instanceAuditLogs.getTotalElements());
    }

}
