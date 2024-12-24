package com.projecthub.core.services.user;

import com.projecthub.core.entities.Role;
import com.projecthub.core.exceptions.ResourceAlreadyExistsException;
import com.projecthub.core.exceptions.ResourceNotFoundException;
import com.projecthub.core.repositories.jpa.RoleJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AppUserRoleManagementService {
    private static final Logger logger = LoggerFactory.getLogger(AppUserRoleManagementService.class);
    private static final String ROLE_NOT_FOUND = "Role not found with ID: ";

    private final RoleJpaRepository roleRepository;

    public AppUserRoleManagementService(RoleJpaRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Role getRoleById(UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ROLE_NOT_FOUND + id));
    }

    @Transactional
    public Role createRole(Role role) throws ResourceAlreadyExistsException {
        if (roleRepository.existsByNameIgnoreCase(role.getName())) {
            throw new ResourceAlreadyExistsException("Role already exists with name: " + role.getName());
        }
        logger.info("Creating new role: {}", role.getName());
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(UUID id, Role roleDetails) throws ResourceAlreadyExistsException {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ROLE_NOT_FOUND + id));

        if (existingRole.getName() != null && roleDetails.getName() != null
                && !existingRole.getName().toString().equalsIgnoreCase(roleDetails.getName().toString())
                && roleRepository.existsByNameIgnoreCase(roleDetails.getName())) {
            throw new ResourceAlreadyExistsException("Role already exists with name: " + roleDetails.getName());
        }

        existingRole.setName(roleDetails.getName());
        existingRole.setDescription(roleDetails.getDescription());

        logger.info("Updating role with ID: {}", id);
        return roleRepository.save(existingRole);
    }

    @Transactional
    public void deleteRole(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ROLE_NOT_FOUND + id));
        logger.info("Deleting role: {}", role.getName());
        roleRepository.delete(role);
    }
}