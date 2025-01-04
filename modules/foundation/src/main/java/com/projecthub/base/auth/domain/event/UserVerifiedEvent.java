package com.projecthub.base.auth.domain.event;

import com.projecthub.base.user.domain.entity.AppUser;
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
