package com.projecthub.core.services.user;

import com.projecthub.core.entities.Role;
import com.projecthub.core.enums.RoleType;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class AppUserRoleValidation {
    public boolean hasAdminRole(Set<Role> roles) {
        return hasRole(roles, RoleType.ADMIN);
    }

    public boolean hasModeratorRole(Set<Role> roles) {
        return hasRole(roles, RoleType.MODERATOR);
    }

    public boolean hasGuestRole(Set<Role> roles) {
        return hasRole(roles, RoleType.GUEST);
    }

    private boolean hasRole(Set<Role> roles, RoleType roleType) {
        return roles.stream()
                .anyMatch(role -> role.getRoleType() == roleType);
    }
}