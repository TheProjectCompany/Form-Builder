package org.tpc.form_builder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class AuditLogsConsumer {
    private final AuditLogsService auditLogsService;

    @Scheduled(fixedDelay = 10000)
    public void consumeAuditLogs() {
        auditLogsService.consumeAuditLogHelper();
    }
}
