package com.projecthub.base.submission.infrastructure.event.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SubmissionDeadLetterHandler {

    @RabbitListener(queues = SubmissionRabbitMQConfig.SUBMISSION_DLQ)
    public void handleDeadLetter(
            Message failedMessage,
            @Header(AmqpHeaders.DEATH_REASON) String reason,
            @Header("x-first-death-exchange") String exchange,
            @Header("x-first-death-queue") String queue,
            @Header("x-first-death-reason") String firstReason) {
        
        log.error("Handling dead letter message from queue: {}", queue);
        log.error("Original exchange: {}", exchange);
        log.error("Death reason: {}", reason);
        log.error("First death reason: {}", firstReason);
        log.error("Message: {}", failedMessage);
    }
}
