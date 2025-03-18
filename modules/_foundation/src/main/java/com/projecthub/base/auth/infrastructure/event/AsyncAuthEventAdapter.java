package com.projecthub.base.auth.infrastructure.event;

import com.projecthub.base.auth.domain.event.AuthDomainEvent;
import com.projecthub.base.auth.domain.event.AuthEventPublisher;
import com.projecthub.base.shared.infrastructure.event.BaseAsyncEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AsyncAuthEventAdapter extends BaseAsyncEventPublisher<AuthDomainEvent> implements AuthEventPublisher {
    private static final String EXCHANGE = "auth.events";

    public AsyncAuthEventAdapter(final RabbitTemplate rabbitTemplate) {
        super(rabbitTemplate, "authEventExecutor");
    }

    @Override
    public String getExchange() {
        return EXCHANGE;
    }

    @Override
    public String getRoutingKey(final AuthDomainEvent event) {
        return switch (event) {
            case AuthDomainEvent.UserRegistered _ -> "auth.user.registered";
            case AuthDomainEvent.UserLoggedIn _ -> "auth.user.login";
            case AuthDomainEvent.UserLoggedOut _ -> "auth.user.logout";
            case AuthDomainEvent.PasswordChanged _ -> "auth.user.password.changed";
            case AuthDomainEvent.AccountLocked _ -> "auth.user.account.locked";
        };
    }
}