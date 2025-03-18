package com.projecthub.base.student.infrastructure.event.adapter;

import com.projecthub.base.student.domain.event.StudentDomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SyncStudentEventAdapter implements StudentEventAdapter {
    private static final String EXCHANGE = "student.sync.exchange";
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(final StudentDomainEvent event) {
        log.debug("Publishing sync event: {}", event);
        this.rabbitTemplate.convertAndSend(
            EXCHANGE,
            this.getRoutingKey(event),
            event
        );
    }

    @Override
    public String getRoutingKey(final StudentDomainEvent event) {
        return switch (event) {
            case StudentDomainEvent.Created e -> "student.sync.created";
            case StudentDomainEvent.Updated e -> "student.sync.updated";
            case StudentDomainEvent.Deleted e -> "student.sync.deleted";
            case StudentDomainEvent.Activated e -> "student.sync.activated";
            case StudentDomainEvent.Deactivated e -> "student.sync.deactivated";
            case StudentDomainEvent.TeamAssigned e -> "student.sync.team.assigned";
            case StudentDomainEvent.EmailChanged e -> "student.sync.email.changed";
            case StudentDomainEvent.ContactUpdated e -> "student.sync.contact.updated";
            case StudentDomainEvent.DetailsUpdated e -> "student.sync.details.updated";
            case StudentDomainEvent.SubmissionTracked e -> "student.sync.submission.tracked";
        };
    }

    @Override
    public String getExchange() {
        return EXCHANGE;
    }
}