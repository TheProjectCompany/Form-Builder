package org.tpc.form_builder.audits;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.tpc.form_builder.service.mapper.AuditLogMapper;

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
        List<AuditDto> batch = auditLogQueue.dequeueBatch(100);

        if (!batch.isEmpty()) {
            try {
                auditLogRepository.saveAll(auditLogMapper.toEntity(batch));
            }
            catch (Exception e) {
                // Requeue the items (in order) so they’re not lost
                for (AuditDto auditDto : batch) {
                    try {
                        auditLogQueue.enqueue(auditDto);
                    } catch (Exception queueEx) {
                        // This should rarely fail unless the queue is full or corrupt
                        log.error("Failed to requeue audit log: {}", auditDto, queueEx);
                    }
                }
            }
        }
    }

}
