package com.projecthub.base.user.application.role.service;


import com.projecthub.base.auth.domain.entity.Role;
import com.projecthub.base.repository.jpa.RoleJpaRepository;
import com.projecthub.base.shared.exception.ResourceAlreadyExistsException;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AppUserRoleManagementService {
    private static final Logger logger = LoggerFactory.getLogger(AppUserRoleManagementService.class);
    @NonNls
    @NonNls
    @NonNls
    private static final String ROLE_NOT_FOUND = "Role not found with ID: ";

    private final RoleJpaRepository roleRepository;

    public AppUserRoleManagementService(final RoleJpaRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return this.roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Role getRoleById(final UUID id) {
        return this.roleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(AppUserRoleManagementService.ROLE_NOT_FOUND + id));
    }

    /**
     * @param role
     * @return
     * @throws ResourceAlreadyExistsException
     */
    @Transactional
    public Role createRole(final Role role) {
        if (this.roleRepository.existsByNameIgnoreCase(role.getName())) {
            throw new ResourceAlreadyExistsException("Role already exists with name: " + role.getName());
        }
        AppUserRoleManagementService.logger.info("Creating new role: {}", role.getName());
        return this.roleRepository.save(role);
    }

    /**
     * @param id
     * @param roleDetails
     * @return
     * @throws ResourceAlreadyExistsException
     */
    @Transactional
    public Role updateRole(final UUID id, final Role roleDetails) {
        final Role existingRole = this.roleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(AppUserRoleManagementService.ROLE_NOT_FOUND + id));

        if (null != existingRole.getName() && null != roleDetails.getName()
            && !existingRole.getName().toString().equalsIgnoreCase(roleDetails.getName().toString())
            && this.roleRepository.existsByNameIgnoreCase(roleDetails.getName())) {
            throw new ResourceAlreadyExistsException("Role already exists with name: " + roleDetails.getName());
        }

        existingRole.setName(roleDetails.getName());
        existingRole.setDescription(roleDetails.getDescription());

        AppUserRoleManagementService.logger.info("Updating role with ID: {}", id);
        return this.roleRepository.save(existingRole);
    }

    @Transactional
    public void deleteRole(final UUID id) {
        final Role role = this.roleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(AppUserRoleManagementService.ROLE_NOT_FOUND + id));
        AppUserRoleManagementService.logger.info("Deleting role: {}", role.getName());
        this.roleRepository.delete(role);
    }
}
