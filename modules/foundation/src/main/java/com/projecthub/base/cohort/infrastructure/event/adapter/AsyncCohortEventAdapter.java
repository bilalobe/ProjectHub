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
    public void publish(final CohortDomainEvent event) {
        AsyncCohortEventAdapter.log.debug("Publishing async event: {}", event);
        this.rabbitTemplate.convertAndSend(
            EXCHANGE,
            this.getRoutingKey(event),
            event
        );
    }

    @Override
    public String getRoutingKey(final CohortDomainEvent event) {
        return switch (event) {
            case final CohortDomainEvent.Created _ -> CohortRabbitMQConfig.COHORT_CREATED_KEY;
            case final CohortDomainEvent.Updated _ -> CohortRabbitMQConfig.COHORT_UPDATED_KEY;
            case final CohortDomainEvent.Deleted _ -> CohortRabbitMQConfig.COHORT_DELETED_KEY;
            case final CohortDomainEvent.Archived _ -> CohortRabbitMQConfig.COHORT_ARCHIVED_KEY;
            case final CohortDomainEvent.StudentAdded _ -> CohortRabbitMQConfig.COHORT_STUDENT_ADDED_KEY;
            case final CohortDomainEvent.StudentRemoved _ -> CohortRabbitMQConfig.COHORT_STUDENT_REMOVED_KEY;
            default -> throw new IllegalArgumentException("Unknown event type: " + event.getClass().getSimpleName());
        };
    }

    @Override
    public String getExchange() {
        return AsyncCohortEventAdapter.EXCHANGE;
    }
}
