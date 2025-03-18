package com.projecthub.base.submission.infrastructure.event.adapter;

import com.projecthub.base.submission.domain.event.SubmissionDomainEvent;
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
        backoff = @Backoff(delay = 2000L, multiplier = 2.0)
    )
    @Override
    public void publish(SubmissionDomainEvent event) {
        log.debug("Publishing async event: {}", event);
        eventStore.save(event); // Store event before publishing
        rabbitTemplate.convertAndSend(
                EXCHANGE,
            getRoutingKey(event),
            event
        );
    }

    private static String getRoutingKey(SubmissionDomainEvent event) {
        return switch (event) {
            case SubmissionDomainEvent.Created e -> "submission.created";
            case SubmissionDomainEvent.Updated e -> "submission.updated";
            case SubmissionDomainEvent.Submitted e -> "submission.submitted";
            case SubmissionDomainEvent.Graded e -> "submission.graded";
            case SubmissionDomainEvent.Revoked e -> "submission.revoked";
            case SubmissionDomainEvent.CommentAdded e -> "submission.comment.added";
            default -> throw new IllegalArgumentException("Unknown event type: " + event.getClass().getSimpleName());
        };
    }

    @Override
    public String getExchange() {
        return EXCHANGE;
    }
}
