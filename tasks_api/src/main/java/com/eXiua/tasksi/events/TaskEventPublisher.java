package com.eXiua.tasksi.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TaskEventPublisher {
    private final Logger log = LoggerFactory.getLogger(TaskEventPublisher.class);

    public void publishTaskCreated(Long taskId) {
        // TODO: integrar con RabbitMQ. Ahora solo log para desarrollo.
        log.info("Event: TaskCreated -> id={}", taskId);
    }

    public void publishTaskUpdated(Long taskId) {
        log.info("Event: TaskUpdated -> id={}", taskId);
    }

    public void publishTaskDeleted(Long taskId) {
        log.info("Event: TaskDeleted -> id={}", taskId);
    }
}
