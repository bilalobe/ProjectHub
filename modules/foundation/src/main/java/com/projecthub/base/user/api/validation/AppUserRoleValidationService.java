package com.projecthub.base.user.api.validation;


import com.projecthub.base.auth.domain.entity.Role;
import com.projecthub.base.shared.domain.enums.security.RoleType;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AppUserRoleValidationService {
    public boolean hasAdminRole(final Set<Role> roles) {
        return this.hasRole(roles, RoleType.ADMIN);
    }

    public boolean hasModeratorRole(final Set<Role> roles) {
        return this.hasRole(roles, RoleType.MODERATOR);
    }

    public boolean hasGuestRole(final Set<Role> roles) {
        return this.hasRole(roles, RoleType.GUEST);
    }

    private boolean hasRole(final Set<Role> roles, final RoleType roleType) {
        return roles.stream()
            .anyMatch(role -> role.getRoleType() == roleType);
    }
}
