package com.projecthub.base.user.api.validation;


import com.projecthub.base.auth.domain.entity.Role;
import com.projecthub.base.shared.domain.enums.security.RoleType;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AppUserRoleValidationService {
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
