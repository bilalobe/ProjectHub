package com.projecthub.base.milestone.infrastructure.event;

import com.projecthub.base.milestone.domain.event.MilestoneDomainEvent;
import com.projecthub.base.milestone.domain.event.MilestoneEventPublisher;
import com.projecthub.base.milestone.infrastructure.event.config.MilestoneRabbitMQConfig;
import com.projecthub.base.shared.infrastructure.event.BaseAsyncEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AsyncMilestoneEventAdapter extends BaseAsyncEventPublisher<MilestoneDomainEvent> implements MilestoneEventPublisher {

    public AsyncMilestoneEventAdapter(final RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate, "milestoneEventExecutor");
    }

    @Override
    public String getExchange() {
        return MilestoneRabbitMQConfig.MILESTONE_EXCHANGE;
    }

    @Override
    public String getRoutingKey(final MilestoneDomainEvent event) {
        return switch (event) {
            case MilestoneDomainEvent.Created _ -> MilestoneRabbitMQConfig.MILESTONE_CREATED_KEY;
            case MilestoneDomainEvent.Updated _ -> MilestoneRabbitMQConfig.MILESTONE_UPDATED_KEY;
            case MilestoneDomainEvent.Deleted _ -> MilestoneRabbitMQConfig.MILESTONE_DELETED_KEY;
            case MilestoneDomainEvent.Completed _ -> MilestoneRabbitMQConfig.MILESTONE_COMPLETED_KEY;
        };
    }
}