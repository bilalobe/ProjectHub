package com.projecthub.base.submission.infrastructure.event.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.support.AmqpRejectAndDontRequeueException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SubmissionEventListener {

    @RabbitListener(
        queues = "${submission.queue.name}",
        containerFactory = "submissionListenerContainerFactory"
    )
    public void handleSubmissionEvent(SubmissionEvent event) {
        try {
            log.debug("Processing submission event: {}", event);
            // Process event
        } catch (Exception e) {
            log.error("Error processing submission event: {}", event, e);
            throw new AmqpRejectAndDontRequeueException("Failed to process event", e);
        }
    }
}
