package com.projecthub.base.school.infrastructure.event.adapter;

import com.projecthub.base.school.domain.event.SchoolDomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@Slf4j
public abstract class BaseSchoolEventAdapter {
    protected final RabbitTemplate rabbitTemplate;
    protected final String exchange;
    protected final String routingKeyPrefix;

    protected BaseSchoolEventAdapter(RabbitTemplate rabbitTemplate, String exchange, String routingKeyPrefix) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKeyPrefix = routingKeyPrefix;
    }

    protected String getRoutingKey(SchoolDomainEvent event) {
        return routingKeyPrefix + switch (event) {
            case final SchoolDomainEvent.Created e -> "created";
            case final SchoolDomainEvent.Updated e -> "updated";
            case final SchoolDomainEvent.Deleted e -> "deleted";
            case final SchoolDomainEvent.Archived e -> "archived";
            case final SchoolDomainEvent.CohortAdded e -> "cohort.added";
            default -> "unknown";
        };
    }

    protected String getExchange() {
        return exchange;
    }
}