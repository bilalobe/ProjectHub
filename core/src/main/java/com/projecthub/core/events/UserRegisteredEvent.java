package com.projecthub.core.events;

import com.projecthub.core.entities.AppUser;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegisteredEvent extends ApplicationEvent {
    private final AppUser user;

    public UserRegisteredEvent(AppUser user) {
        super(user);
        this.user = user;
    }
}
