package com.projecthub.base.auth.service.fortress;

import org.apache.directory.fortress.core.AdminMgr;
import org.apache.directory.fortress.core.DelAdminMgr;
import org.apache.directory.fortress.core.model.OrgUnit;
import org.apache.directory.fortress.core.model.Permission;
import org.apache.directory.fortress.core.model.Role;
import org.apache.directory.fortress.core.model.User;
import org.jetbrains.annotations.NonNls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service to bootstrap the Apache Fortress environment with initial users, roles, and permissions.
 * This service will run on application startup and ensure the required security data is present.
 */
@Service
public class FortressBootstrapService {

    private static final Logger log = LoggerFactory.getLogger(FortressBootstrapService.class);

    private final AdminMgr adminManager;
    private final DelAdminMgr delAdminManager;

    @Value("${projecthub.auth.fortress.bootstrap:true}")
    private boolean bootstrapEnabled;

    @Value("${projecthub.auth.fortress.admin.username:admin}")
    private String adminUsername;

    @Value("${projecthub.auth.fortress.admin.password:admin123}")
    private String adminPassword;

    public FortressBootstrapService(AdminMgr adminManager, DelAdminMgr delAdminManager) {
        this.adminManager = adminManager;
        this.delAdminManager = delAdminManager;
    }

    /**
     * Initialize the Fortress environment when the application is ready.
     * This will create default organizational units, roles, permissions, and users.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initializeFortress() {
        if (!bootstrapEnabled) {
            log.info("Fortress bootstrap is disabled, skipping initialization");
            return;
        }

        try {
            log.info("Starting Fortress bootstrap process");

            // Create organizational units
            createOrganizationalUnits();

            // Create roles
            createRoles();

            // Create permissions
            createPermissions();

            // Create default admin user
            createAdminUser();

            // Create sample users
            createSampleUsers();

            log.info("Fortress bootstrap process completed successfully");

        } catch (Exception e) {
            log.error("Failed to bootstrap Fortress environment", e);
        }
    }

    /**
     * Create the organizational units needed by the application.
     */
    private void createOrganizationalUnits() throws Exception {
        log.info("Creating organizational units");

        // Create People OU if it doesn't exist
        try {
            OrgUnit peopleOU = new OrgUnit("People");
            peopleOU.setDescription("ProjectHub People");
            peopleOU.setType(OrgUnit.Type.USER);
            adminManager.addOrganizationalUnit(peopleOU);
            log.info("Created People organizational unit");
        } catch (RuntimeException e) {
            log.warn("Failed to create People OU - it may already exist: {}", e.getMessage());
        }

        // Create Permission OU if it doesn't exist
        try {
            OrgUnit permOU = new OrgUnit("Permissions");
            permOU.setDescription("ProjectHub Permissions");
            permOU.setType(OrgUnit.Type.PERM);
            adminManager.addOrganizationalUnit(permOU);
            log.info("Created Permissions organizational unit");
        } catch (RuntimeException e) {
            log.warn("Failed to create Permissions OU - it may already exist: {}", e.getMessage());
        }
    }

    /**
     * Create the default roles needed by the application.
     */
    private void createRoles() throws Exception {
        log.info("Creating roles");

        List<String> roles = Arrays.asList(
            "ADMIN", "USER", "INSTRUCTOR", "STUDENT", "MODERATOR"
        );

        for (String roleName : roles) {
            try {
                @NonNls @NonNls Role role = new Role(roleName);
                role.setDescription("ProjectHub " + roleName + " Role");
                adminManager.addRole(role);
                log.info("Created role: {}", roleName);
            } catch (RuntimeException e) {
                log.warn("Failed to create role {} - it may already exist: {}", roleName, e.getMessage());
            }
        }
    }

    /**
     * Create the default permissions needed by the application.
     */
    private void createPermissions() throws Exception {
        log.info("Creating permissions");

        // Project permissions
        createPermission("project", "view", "Allow viewing projects");
        createPermission("project", "create", "Allow creating projects");
        createPermission("project", "edit", "Allow editing projects");
        createPermission("project", "delete", "Allow deleting projects");

        // Submission permissions
        createPermission("submission", "view", "Allow viewing submissions");
        createPermission("submission", "create", "Allow creating submissions");
        createPermission("submission", "grade", "Allow grading submissions");

        // User management permissions
        createPermission("user", "view", "Allow viewing users");
        createPermission("user", "create", "Allow creating users");
        createPermission("user", "edit", "Allow editing users");
        createPermission("user", "delete", "Allow deleting users");

        // Assign permissions to roles
        assignPermission("ADMIN", "project", "view");
        assignPermission("ADMIN", "project", "create");
        assignPermission("ADMIN", "project", "edit");
        assignPermission("ADMIN", "project", "delete");
        assignPermission("ADMIN", "submission", "view");
        assignPermission("ADMIN", "submission", "grade");
        assignPermission("ADMIN", "user", "view");
        assignPermission("ADMIN", "user", "create");
        assignPermission("ADMIN", "user", "edit");
        assignPermission("ADMIN", "user", "delete");

        assignPermission("INSTRUCTOR", "project", "view");
        assignPermission("INSTRUCTOR", "project", "create");
        assignPermission("INSTRUCTOR", "project", "edit");
        assignPermission("INSTRUCTOR", "submission", "view");
        assignPermission("INSTRUCTOR", "submission", "grade");

        assignPermission("STUDENT", "project", "view");
        assignPermission("STUDENT", "submission", "create");
        assignPermission("STUDENT", "submission", "view");

        assignPermission("USER", "project", "view");
    }

    /**
     * Create a permission in Fortress.
     */
    private void createPermission(String objName, String operation, String description) {
        try {
            Permission permission = new Permission(objName, operation);
            permission.setDescription(description);
            adminManager.addPermission(permission);
            log.info("Created permission: {}:{}", objName, operation);
        } catch (RuntimeException e) {
            log.warn("Failed to create permission {}:{} - it may already exist: {}",
                    objName, operation, e.getMessage());
        }
    }

    /**
     * Assign a permission to a role.
     */
    private void assignPermission(String roleName, String objName, String operation) {
        try {
            Permission permission = new Permission(objName, operation);
            adminManager.addPermissionToRole(new Role(roleName), permission);
            log.info("Assigned permission {}:{} to role {}", objName, operation, roleName);
        } catch (RuntimeException e) {
            log.warn("Failed to assign permission {}:{} to role {} - {}",
                    objName, operation, roleName, e.getMessage());
        }
    }

    /**
     * Create the default admin user.
     */
    private void createAdminUser() throws Exception {
        log.info("Creating admin user");

        try {
            User adminUser = new User(adminUsername);
            adminUser.setPassword(adminPassword);
            adminUser.setUserId(UUID.randomUUID().toString());
            adminUser.setDescription("ProjectHub Administrator");
            adminUser.setOu("People");

            // Add user
            adminManager.addUser(adminUser);

            // Assign admin role
            adminManager.assignUser(adminUser, new Role("ADMIN"));

            log.info("Created admin user: {}", adminUsername);
        } catch (RuntimeException e) {
            log.warn("Failed to create admin user - it may already exist: {}", e.getMessage());
        }
    }

    /**
     * Create sample users for testing.
     */
    private void createSampleUsers() throws Exception {
        log.info("Creating sample users");

        // Create instructor
        createUser("instructor", "password", "INSTRUCTOR", "ProjectHub Instructor");

        // Create student
        createUser("student", "password", "STUDENT", "ProjectHub Student");

        // Create regular user
        createUser("user", "password", "USER", "ProjectHub Regular User");

        // Create moderator
        createUser("moderator", "password", "MODERATOR", "ProjectHub Moderator");
    }

    /**
     * Helper method to create a user with a role.
     */
    private void createUser(String username, String password, String roleName, String description) {
        try {
            User user = new User(username);
            user.setPassword(password);
            user.setUserId(UUID.randomUUID().toString());
            user.setDescription(description);
            user.setOu("People");

            // Add user
            adminManager.addUser(user);

            // Assign role
            adminManager.assignUser(user, new Role(roleName));

            log.info("Created user: {} with role: {}", username, roleName);
        } catch (RuntimeException e) {
            log.warn("Failed to create user {} - it may already exist: {}", username, e.getMessage());
        }
    }
}
