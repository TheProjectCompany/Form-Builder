package org.tpc.form_builder.queues;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.tpc.form_builder.audits.AuditDto;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
@Log4j2
public class AuditLogQueue {
    private final BlockingQueue<AuditDto> auditQueue = new LinkedBlockingQueue<>();

    public void enqueue(AuditDto auditDto) {
        auditQueue.add(auditDto);
    }

    public List<AuditDto> dequeueBatch(int maxBatchSize) {
        List<AuditDto> batch = new ArrayList<>();
        auditQueue.drainTo(batch, maxBatchSize);
        return batch;
    }

     public int size() {
        return auditQueue.size();
     }
}
