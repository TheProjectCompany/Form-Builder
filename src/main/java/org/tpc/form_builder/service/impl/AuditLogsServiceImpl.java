package org.tpc.form_builder.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tpc.form_builder.audits.AuditDto;
import org.tpc.form_builder.audits.AuditLog;
import org.tpc.form_builder.queues.AuditLogQueue;
import org.tpc.form_builder.audits.AuditLogRepository;
import org.tpc.form_builder.service.AuditLogsService;
import org.tpc.form_builder.service.mapper.AuditLogMapper;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuditLogsServiceImpl implements AuditLogsService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;
    private final AuditLogQueue auditLogQueue;

    @Override
    @Async("asyncTaskExecutor")
    public void consumeAuditLogHelper() {
        ZonedDateTime now = ZonedDateTime.now();
        log.info("Audit Log Consumer ({}): {} items in queue", now, auditLogQueue.size());

        List<AuditDto> batch = auditLogQueue.dequeueBatch(100);

        if (!batch.isEmpty()) {
            try {
                auditLogRepository.saveAll(auditLogMapper.toEntity(batch));
                log.info("Audit Log Consumer ({}): {} items saved", now, batch.size());
            }
            catch (Exception e) {
                // Requeue the items (in order) so they’re not lost
                for (AuditDto auditDto : batch) {
                    try {
                        auditLogQueue.enqueue(auditDto);
                    } catch (Exception queueEx) {
                        // This should rarely fail unless the queue is full or corrupt
                        log.error("Failed to requeue audit log ({}): {}", now , auditDto, queueEx);
                    }
                }
            }
        }
    }

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
