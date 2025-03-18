package com.projecthub.base.auth.domain.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class UserRegisteredEvent extends ApplicationEvent {
    private final UUID user;

    public UserRegisteredEvent(final UUID userId) {
        super(userId);
        this.user = userId;
    }
}
