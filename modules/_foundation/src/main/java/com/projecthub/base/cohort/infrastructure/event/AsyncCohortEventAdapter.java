package com.projecthub.base.cohort.infrastructure.event;

import com.projecthub.base.cohort.domain.event.CohortDomainEvent;
import com.projecthub.base.cohort.domain.event.CohortEventPublisher;
import com.projecthub.base.shared.infrastructure.event.BaseAsyncEventPublisher;
import com.projecthub.base.cohort.infrastructure.event.config.CohortRabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AsyncCohortEventAdapter extends BaseAsyncEventPublisher<CohortDomainEvent> implements CohortEventPublisher {

    public AsyncCohortEventAdapter(final RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate, "cohortEventExecutor");
    }

    @Override
    public String getExchange() {
        return CohortRabbitMQConfig.COHORT_EXCHANGE;
    }

    @Override
    public String getRoutingKey(final CohortDomainEvent event) {
        return switch (event) {
            case CohortDomainEvent.Created _ -> CohortRabbitMQConfig.COHORT_CREATED_KEY;
            case CohortDomainEvent.Updated _ -> CohortRabbitMQConfig.COHORT_UPDATED_KEY;
            case CohortDomainEvent.Deleted _ -> CohortRabbitMQConfig.COHORT_DELETED_KEY;
            case CohortDomainEvent.Archived _ -> CohortRabbitMQConfig.COHORT_ARCHIVED_KEY;
            case CohortDomainEvent.StudentAdded _ -> CohortRabbitMQConfig.COHORT_STUDENT_ADDED_KEY;
            case CohortDomainEvent.StudentRemoved _ -> CohortRabbitMQConfig.COHORT_STUDENT_REMOVED_KEY;
        };
    }
}