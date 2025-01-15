package com.projecthub.base.auth.domain.event;

import com.projecthub.base.shared.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEventPublisher {
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
}
