package org.tpc.form_builder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tpc.form_builder.audits.AuditDto;
import org.tpc.form_builder.audits.AuditLogQueue;
import org.tpc.form_builder.audits.AuditLogRepository;
import org.tpc.form_builder.service.mapper.AuditLogMapper;

import java.time.ZonedDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class AuditLogsConsumer {
    private final AuditLogQueue auditLogQueue;
    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Scheduled(fixedDelay = 10000)
    public void consumeAuditLogs() {
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
}
