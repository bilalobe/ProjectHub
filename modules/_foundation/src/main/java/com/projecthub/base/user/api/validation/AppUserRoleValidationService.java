package com.projecthub.base.user.api.validation;


import com.projecthub.base.auth.domain.entity.Role;
import com.projecthub.base.shared.domain.enums.security.RoleType;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

@Component
public class AppUserRoleValidationService {
    public AppUserRoleValidationService() {
    }

    public boolean hasAdminRole(final Set<Role> roles) {
        return AppUserRoleValidationService.hasRole(roles, RoleType.ADMIN);
    }

    public boolean hasModeratorRole(final Set<Role> roles) {
        return AppUserRoleValidationService.hasRole(roles, RoleType.MODERATOR);
    }

    public boolean hasGuestRole(final Set<Role> roles) {
        return AppUserRoleValidationService.hasRole(roles, RoleType.GUEST);
    }

    private static boolean hasRole(final Collection<Role> roles, final RoleType roleType) {
        return roles.stream()
            .anyMatch(role -> role.getRoleType() == roleType);
    }
}
