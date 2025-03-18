package com.projecthub.base.component.infrastructure.event;

import com.projecthub.base.component.domain.event.ComponentDomainEvent;
import com.projecthub.base.component.domain.event.ComponentEventPublisher;
import com.projecthub.base.component.infrastructure.event.config.ComponentRabbitMQConfig;
import com.projecthub.base.shared.infrastructure.event.BaseAsyncEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AsyncComponentEventAdapter extends BaseAsyncEventPublisher<ComponentDomainEvent> implements ComponentEventPublisher {

    public AsyncComponentEventAdapter(final RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate, "componentEventExecutor");
    }

    @Override
    public String getExchange() {
        return ComponentRabbitMQConfig.COMPONENT_EXCHANGE;
    }

    @Override
    public String getRoutingKey(final ComponentDomainEvent event) {
        return switch (event) {
            case ComponentDomainEvent.Created _ -> ComponentRabbitMQConfig.COMPONENT_CREATED_KEY;
            case ComponentDomainEvent.Updated _ -> ComponentRabbitMQConfig.COMPONENT_UPDATED_KEY;
            case ComponentDomainEvent.Deleted _ -> ComponentRabbitMQConfig.COMPONENT_DELETED_KEY;
        };
    }
}