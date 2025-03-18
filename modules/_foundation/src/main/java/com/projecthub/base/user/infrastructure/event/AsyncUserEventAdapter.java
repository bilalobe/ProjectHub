package com.projecthub.base.user.infrastructure.event;

import com.projecthub.base.shared.infrastructure.event.BaseAsyncEventPublisher;
import com.projecthub.base.user.domain.event.UserDomainEvent;
import com.projecthub.base.user.domain.event.UserEventPublisher;
import com.projecthub.base.user.infrastructure.event.config.UserRabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AsyncUserEventAdapter extends BaseAsyncEventPublisher<UserDomainEvent> implements UserEventPublisher {

    public AsyncUserEventAdapter(final RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate, "userEventExecutor");
    }

    @Override
    public String getExchange() {
        return UserRabbitMQConfig.USER_EXCHANGE;
    }

    @Override
    public String getRoutingKey(final UserDomainEvent event) {
        return switch (event) {
            case UserDomainEvent.Created _ -> UserRabbitMQConfig.USER_CREATED_KEY;
            case UserDomainEvent.ProfileUpdated _ -> UserRabbitMQConfig.USER_PROFILE_UPDATED_KEY;
            case UserDomainEvent.AvatarUpdated _ -> UserRabbitMQConfig.USER_AVATAR_UPDATED_KEY;
            case UserDomainEvent.StatusUpdated _ -> UserRabbitMQConfig.USER_STATUS_UPDATED_KEY;
            case UserDomainEvent.RolesChanged _ -> UserRabbitMQConfig.USER_ROLES_CHANGED_KEY;
            case UserDomainEvent.AccountStatusChanged _ -> UserRabbitMQConfig.USER_ACCOUNT_STATUS_CHANGED_KEY;
            case UserDomainEvent.Verified _ -> UserRabbitMQConfig.USER_VERIFIED_KEY;
            case UserDomainEvent.Deleted _ -> UserRabbitMQConfig.USER_DELETED_KEY;
        };
    }
}