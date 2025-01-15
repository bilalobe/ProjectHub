package com.projecthub.base.submission.infrastructure.event.adapter;

import com.projecthub.base.submission.domain.event.SubmissionEvent;
import com.projecthub.base.submission.domain.event.SubmissionEventPort;
import com.projecthub.base.submission.infrastructure.event.config.SubmissionRabbitMQConfig;
import com.projecthub.base.submission.infrastructure.event.store.EventStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component("asyncSubmissionEventAdapter")
@RequiredArgsConstructor
public class AsyncSubmissionEventAdapter implements SubmissionEventPort {
    private final RabbitTemplate rabbitTemplate;
    private final EventStore eventStore;
    private static final String EXCHANGE = SubmissionRabbitMQConfig.SUBMISSION_EXCHANGE;

    @Async("submissionEventExecutor")
    @Retryable(
        value = {Exception.class},
        maxAttempts = 5,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    @Override
    public void publish(SubmissionEvent event) {
        log.debug("Publishing async event: {}", event);
        eventStore.save(event); // Store event before publishing
        rabbitTemplate.convertAndSend(
            getExchange(),
            getRoutingKey(event),
            event
        );
    }

    private String getRoutingKey(SubmissionEvent event) {
        return switch (event) {
            case SubmissionEvent.SubmissionCreated e -> SubmissionRabbitMQConfig.SUBMISSION_CREATED_KEY;
            case SubmissionEvent.SubmissionUpdated e -> SubmissionRabbitMQConfig.SUBMISSION_UPDATED_KEY;
            case SubmissionEvent.SubmissionDeleted e -> SubmissionRabbitMQConfig.SUBMISSION_DELETED_KEY;
            default -> throw new IllegalArgumentException("Unknown event type: " + event.getClass().getSimpleName());
        };
    }

    @Override
    public String getExchange() {
        return EXCHANGE;
    }
}