package com.projecthub.base.project.infrastructure.event;

import com.projecthub.base.project.domain.event.ProjectDomainEvent;
import com.projecthub.base.project.domain.event.ProjectEventPublisher;
import com.projecthub.base.shared.infrastructure.event.BaseAsyncEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AsyncProjectEventPublisher extends BaseAsyncEventPublisher<ProjectDomainEvent> implements ProjectEventPublisher {
    private static final String EXCHANGE = "project.events";

    public AsyncProjectEventPublisher(final RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate, "projectEventExecutor");
    }

    @Override
    public String getExchange() {
        return EXCHANGE;
    }

    @Override
    public String getRoutingKey(final ProjectDomainEvent event) {
        return switch (event) {
            case ProjectDomainEvent.Created _ -> "project.created";
            case ProjectDomainEvent.Updated _ -> "project.updated";
            case ProjectDomainEvent.Deleted _ -> "project.deleted";
            case ProjectDomainEvent.StatusChanged _ -> "project.status.changed";
        };
    }
}
