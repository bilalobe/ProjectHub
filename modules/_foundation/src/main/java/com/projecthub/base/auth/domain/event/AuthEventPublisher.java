package com.projecthub.base.auth.domain.event;

import com.projecthub.base.shared.config.RabbitMQConfig;
import com.projecthub.base.shared.infrastructure.event.DomainEventPublisher;
import com.projecthub.base.user.domain.entity.AppUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEventPublisher implements DomainEventPublisher<AuthDomainEvent> {
    private final RabbitTemplate rabbitTemplate;

    public void publishUserRegistered(final UUID userId) {
        AuthEventPublisher.log.debug("Publishing user registered event: {}", userId);
        this.rabbitTemplate.convertAndSend(
            RabbitMQConfig.AUTH_EXCHANGE,
            "auth.user.registered",
            new UserRegisteredEvent(userId)
        );
    }

    public void publishUserLoggedIn(final UUID userId) {
        AuthEventPublisher.log.debug("Publishing user login event: {}", userId);
        this.rabbitTemplate.convertAndSend(
            RabbitMQConfig.AUTH_EXCHANGE,
            "auth.user.login",
            new UserLoggedInEvent(userId)
        );
    }

    public void publishUserRegistered(AppUser user) {
        publish(new AuthDomainEvent.UserRegistered(
            generateEventId(),
            user.getId(),
            user.getUsername(),
            getTimestamp()
        ));
    }

    public void publishUserLoggedIn(UUID userId, String ipAddress) {
        publish(new AuthDomainEvent.UserLoggedIn(
            generateEventId(),
            userId,
            ipAddress,
            getTimestamp()
        ));
    }

    public void publishUserLoggedOut(UUID userId, String sessionId) {
        publish(new AuthDomainEvent.UserLoggedOut(
            generateEventId(),
            userId,
            sessionId,
            getTimestamp()
        ));
    }

    public void publishPasswordChanged(UUID userId, boolean isReset) {
        publish(new AuthDomainEvent.PasswordChanged(
            generateEventId(),
            userId,
            isReset,
            getTimestamp()
        ));
    }

    public void publishAccountLocked(UUID userId, String reason) {
        publish(new AuthDomainEvent.AccountLocked(
            generateEventId(),
            userId,
            reason,
            getTimestamp()
        ));
    }
}
