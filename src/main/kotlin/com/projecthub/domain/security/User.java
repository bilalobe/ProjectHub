package com.projecthub.core.domain.model.security;

import java.util.Set;

public class User {
    private final UserId id;
    private final String username;
    private final Set<Role> roles;

    public User(UserId id, String username, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public UserId getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }
}
