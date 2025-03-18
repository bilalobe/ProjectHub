package com.projecthub.base.student.infrastructure.event.adapter;

import com.projecthub.base.student.domain.event.StudentDomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncStudentEventAdapter implements StudentEventAdapter {
    private static final String EXCHANGE = "student.exchange";
    private final RabbitTemplate rabbitTemplate;

    @Async("studentEventExecutor")
    @Override
    public void publish(final StudentDomainEvent event) {
        log.debug("Publishing async event: {}", event);
        this.rabbitTemplate.convertAndSend(
            EXCHANGE,
            this.getRoutingKey(event),
            event
        );
    }

    @Override
    public String getRoutingKey(final StudentDomainEvent event) {
        return switch (event) {
            case StudentDomainEvent.Created e -> "student.created";
            case StudentDomainEvent.Updated e -> "student.updated";
            case StudentDomainEvent.Deleted e -> "student.deleted";
            case StudentDomainEvent.Activated e -> "student.activated";
            case StudentDomainEvent.Deactivated e -> "student.deactivated";
            case StudentDomainEvent.TeamAssigned e -> "student.team.assigned";
            case StudentDomainEvent.EmailChanged e -> "student.email.changed";
            case StudentDomainEvent.ContactUpdated e -> "student.contact.updated";
            case StudentDomainEvent.DetailsUpdated e -> "student.details.updated";
            case StudentDomainEvent.SubmissionTracked e -> "student.submission.tracked";
        };
    }

    @Override
    public String getExchange() {
        return EXCHANGE;
    }
}
