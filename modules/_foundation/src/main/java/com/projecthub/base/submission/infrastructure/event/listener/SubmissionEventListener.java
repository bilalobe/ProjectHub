package com.projecthub.base.submission.infrastructure.event.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.support.AmqpRejectAndDontRequeueException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SubmissionEventListener {

    public SubmissionEventListener() {
    }

    @RabbitListener(
        queues = "${submission.queue.name}",
        containerFactory = "submissionListenerContainerFactory"
    )
    public static void handleSubmissionEvent(SubmissionEvent event) {
        try {
            log.debug("Processing submission event: {}", event);
            // Process event
        } catch (RuntimeException e) {
            log.error("Error processing submission event: {}", event, e);
            throw new AmqpRejectAndDontRequeueException("Failed to process event", e);
        }
    }
}
