package com.projecthub.base.school.infrastructure.event.adapter;

import com.projecthub.base.school.domain.event.SchoolDomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SyncSchoolEventAdapter extends BaseSchoolEventAdapter implements SchoolEventAdapter {
    private static final String EXCHANGE = "school.sync.exchange";
    private static final String ROUTING_PREFIX = "school.sync.";

    public SyncSchoolEventAdapter(RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate, EXCHANGE, ROUTING_PREFIX);
    }

    @Override
    public void publish(final SchoolDomainEvent event) {
        log.debug("Publishing sync event: {}", event);
        this.rabbitTemplate.convertAndSend(
            this.getExchange(),
            this.getRoutingKey(event),
            event
        );
    }
}
