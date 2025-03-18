package com.projecthub.base.school.infrastructure.event.adapter;

import com.projecthub.base.school.domain.event.SchoolDomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AsyncSchoolEventAdapter extends BaseSchoolEventAdapter implements SchoolEventAdapter {
    private static final String EXCHANGE = "school.exchange";
    private static final String ROUTING_PREFIX = "school.";

    public AsyncSchoolEventAdapter(RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate, EXCHANGE, ROUTING_PREFIX);
    }

    @Async("schoolEventExecutor")
    @Override
    public void publish(final SchoolDomainEvent event) {
        log.debug("Publishing async event: {}", event);
        this.rabbitTemplate.convertAndSend(
            this.getExchange(),
            this.getRoutingKey(event),
            event
        );
    }
}
