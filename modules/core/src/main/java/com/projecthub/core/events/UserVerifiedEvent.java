package com.projecthub.core.events;

import com.projecthub.core.entities.AppUser;
import org.springframework.context.ApplicationEvent;

public class UserVerifiedEvent extends ApplicationEvent {
    private final AppUser user;

    public UserVerifiedEvent(AppUser user) {
        super(user);
        this.user = user;
    }

    public AppUser getUser() {
        return user;
    }
}
