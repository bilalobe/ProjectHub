package com.projecthub.base.user.application.role.service;


import com.projecthub.base.auth.domain.entity.Role;
import com.projecthub.base.shared.domain.enums.security.RoleType;

import java.util.List;
import java.util.Set;


public interface AppUserRoleService {
    List<Role> getAllRoles();

    Role getRoleByName(String name);

    Set<Role> getRolesByNames(Set<String> roleNames);

    Role getDefaultRole();

    Set<Role> getRolesByType(RoleType roleType);
}
