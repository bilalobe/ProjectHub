package com.projecthub.base.auth.domain.event;

import com.projecthub.base.user.domain.entity.AppUser;
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
