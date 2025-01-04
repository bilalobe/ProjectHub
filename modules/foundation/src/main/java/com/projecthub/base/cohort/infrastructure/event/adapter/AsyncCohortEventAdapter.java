package com.projecthub.base.cohort.infrastructure.event.adapter;

import com.projecthub.base.cohort.domain.event.CohortDomainEvent;
import com.projecthub.base.cohort.domain.event.CohortEventAdapter;
import com.projecthub.base.cohort.infrastructure.event.config.CohortRabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component("asyncCohortEventAdapter")
@RequiredArgsConstructor
public class AsyncCohortEventAdapter implements CohortEventAdapter {
    private static final String EXCHANGE = CohortRabbitMQConfig.COHORT_EXCHANGE;
    private final RabbitTemplate rabbitTemplate;

    @Async("cohortEventExecutor")
    @Override
    public void publish(CohortDomainEvent event) {
        log.debug("Publishing async event: {}", event);
        rabbitTemplate.convertAndSend(
            getExchange(),
            getRoutingKey(event),
            event
        );
    }

    @Override
    public String getRoutingKey(CohortDomainEvent event) {
        return switch (event) {
            case CohortDomainEvent.Created _ -> CohortRabbitMQConfig.COHORT_CREATED_KEY;
            case CohortDomainEvent.Updated _ -> CohortRabbitMQConfig.COHORT_UPDATED_KEY;
            case CohortDomainEvent.Deleted _ -> CohortRabbitMQConfig.COHORT_DELETED_KEY;
            case CohortDomainEvent.Archived _ -> CohortRabbitMQConfig.COHORT_ARCHIVED_KEY;
            case CohortDomainEvent.StudentAdded _ -> CohortRabbitMQConfig.COHORT_STUDENT_ADDED_KEY;
            case CohortDomainEvent.StudentRemoved _ -> CohortRabbitMQConfig.COHORT_STUDENT_REMOVED_KEY;
            default -> throw new IllegalArgumentException("Unknown event type: " + event.getClass().getSimpleName());
        };
    }

    @Override
    public String getExchange() {
        return EXCHANGE;
    }
}
