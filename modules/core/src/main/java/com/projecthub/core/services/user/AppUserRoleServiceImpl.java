package com.projecthub.core.services.user;

import com.projecthub.core.entities.Role;
import com.projecthub.core.enums.RoleType;
import com.projecthub.core.exceptions.ResourceNotFoundException;
import com.projecthub.core.repositories.jpa.RoleJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AppUserRoleServiceImpl implements AppUserRoleService {
    private static final Logger logger = LoggerFactory.getLogger(AppUserRoleServiceImpl.class);
    private static final String DEFAULT_ROLE = "USER";

    private final RoleJpaRepository roleRepository;

    public AppUserRoleServiceImpl(RoleJpaRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Cacheable("roles")
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        logger.info("Retrieving all roles");
        return roleRepository.findAll();
    }

    @Override
    @Cacheable(value = "roles", key = "#name")
    @Transactional(readOnly = true)
    public Role getRoleByName(String name) {
        logger.info("Retrieving role by name: {}", name);
        return roleRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + name));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Role> getRolesByNames(Set<String> roleNames) {
        return roleNames.stream()
                .map(this::getRoleByName)
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public Role getDefaultRole() {
        return getRoleByName(DEFAULT_ROLE);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Role> getRolesByType(RoleType roleType) {
        logger.info("Retrieving roles by type: {}", roleType);
        return roleRepository.findByRoleType(roleType).stream()
                .collect(Collectors.toSet());
    }
}