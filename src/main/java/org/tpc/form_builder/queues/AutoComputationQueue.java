package org.tpc.form_builder.queues;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.tpc.form_builder.service.dto.ComputationDto;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
@Log4j2
public class AutoComputationQueue {
    private final BlockingQueue<ComputationDto> autoComputationQueue = new LinkedBlockingQueue<>();

    public void enqueue(ComputationDto computationDto) {
        autoComputationQueue.add(computationDto);
    }

    public ComputationDto dequeue() {
        return autoComputationQueue.poll();
    }
}
