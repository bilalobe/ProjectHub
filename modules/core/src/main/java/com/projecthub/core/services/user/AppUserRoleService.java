package com.projecthub.core.services.user;

import com.projecthub.core.entities.Role;
import com.projecthub.core.enums.RoleType;
import java.util.List;
import java.util.Set;

public interface AppUserRoleService {
    List<Role> getAllRoles();

    Role getRoleByName(String name);

    Set<Role> getRolesByNames(Set<String> roleNames);

    Role getDefaultRole();

    Set<Role> getRolesByType(RoleType roleType);
}