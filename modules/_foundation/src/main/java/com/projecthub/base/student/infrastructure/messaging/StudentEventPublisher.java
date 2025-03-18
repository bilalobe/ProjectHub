package com.projecthub.base.student.infrastructure.messaging;

import com.projecthub.base.student.domain.event.StudentDomainEvent;
import com.projecthub.base.student.infrastructure.event.adapter.StudentEventAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudentEventPublisher implements StudentEventAdapter {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(StudentDomainEvent event) {
        String routingKey = getRoutingKey(event);
        log.debug("Publishing student event {} with routing key {}", event, routingKey);
        rabbitTemplate.convertAndSend(getExchange(), routingKey, event);
    }

    @Override
    public String getExchange() {
        return StudentMessagingConfig.STUDENT_EXCHANGE;
    }

    @Override
    public String getRoutingKey(StudentDomainEvent event) {
        return switch (event) {
            case StudentDomainEvent.Created _ -> "student.created";
            case StudentDomainEvent.Updated _ -> "student.updated";
            case StudentDomainEvent.Deleted _ -> "student.deleted";
            case StudentDomainEvent.Activated _ -> "student.activated";
            case StudentDomainEvent.Deactivated _ -> "student.deactivated";
            case StudentDomainEvent.TeamAssigned _ -> "student.team.assigned";
            case StudentDomainEvent.EmailChanged _ -> "student.email.changed";
            case StudentDomainEvent.ContactUpdated _ -> "student.contact.updated";
            case StudentDomainEvent.DetailsUpdated _ -> "student.details.updated";
        };
    }
}