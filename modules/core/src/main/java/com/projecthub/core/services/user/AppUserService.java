package com.projecthub.core.services.user;

import java.util.Set;

import com.projecthub.core.entities.Role;

public class AppUserService {

    private final AppUserRoleValidation roleValidator;

    public AppUserService(AppUserRoleValidation roleValidator) {
        this.roleValidator = roleValidator;
    }

    public boolean isAdmin(Set<Role> roles) {
        return roleValidator.hasAdminRole(roles);
    }
}