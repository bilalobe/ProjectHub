package com.projecthub.base.school.infrastructure.event;

import com.projecthub.base.school.domain.event.SchoolDomainEvent;
import com.projecthub.base.school.domain.event.SchoolEventPublisher;
import com.projecthub.base.shared.infrastructure.event.BaseAsyncEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AsyncSchoolEventAdapter extends BaseAsyncEventPublisher<SchoolDomainEvent> implements SchoolEventPublisher {
    private static final String EXCHANGE = "school.events";

    public AsyncSchoolEventAdapter(final RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate, "schoolEventExecutor");
    }

    @Override
    public String getExchange() {
        return EXCHANGE;
    }

    @Override
    public String getRoutingKey(final SchoolDomainEvent event) {
        return switch (event) {
            case SchoolDomainEvent.Created _ -> "school.created";
            case SchoolDomainEvent.Updated _ -> "school.updated";
            case SchoolDomainEvent.Deleted _ -> "school.deleted";
            case SchoolDomainEvent.TypeChanged _ -> "school.type.changed";
            case SchoolDomainEvent.CohortAdded _ -> "school.cohort.added";
            case SchoolDomainEvent.NameUpdated _ -> "school.name.updated";
            case SchoolDomainEvent.ContactInfoUpdated _ -> "school.contact.updated";
        };
    }
}