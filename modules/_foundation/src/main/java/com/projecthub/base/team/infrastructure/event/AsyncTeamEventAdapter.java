package com.projecthub.base.team.infrastructure.event;

import com.projecthub.base.shared.infrastructure.event.BaseAsyncEventPublisher;
import com.projecthub.base.team.domain.event.TeamDomainEvent;
import com.projecthub.base.team.domain.event.TeamEventPublisher;
import com.projecthub.base.team.infrastructure.event.config.TeamRabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AsyncTeamEventAdapter extends BaseAsyncEventPublisher<TeamDomainEvent> implements TeamEventPublisher {

    public AsyncTeamEventAdapter(final RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate, "teamEventExecutor");
    }

    @Override
    public String getExchange() {
        return TeamRabbitMQConfig.TEAM_EXCHANGE;
    }

    @Override
    public String getRoutingKey(final TeamDomainEvent event) {
        return switch (event) {
            case TeamDomainEvent.Created _ -> TeamRabbitMQConfig.TEAM_CREATED_KEY;
            case TeamDomainEvent.Updated _ -> TeamRabbitMQConfig.TEAM_UPDATED_KEY;
            case TeamDomainEvent.Deleted _ -> TeamRabbitMQConfig.TEAM_DELETED_KEY;
            case TeamDomainEvent.StatusChanged _ -> TeamRabbitMQConfig.TEAM_STATUS_CHANGED_KEY;
            case TeamDomainEvent.MemberAdded _ -> TeamRabbitMQConfig.TEAM_MEMBER_ADDED_KEY;
            case TeamDomainEvent.MemberRemoved _ -> TeamRabbitMQConfig.TEAM_MEMBER_REMOVED_KEY;
            case TeamDomainEvent.AssignedToCohort _ -> TeamRabbitMQConfig.TEAM_ASSIGNED_TO_COHORT_KEY;
        };
    }
}