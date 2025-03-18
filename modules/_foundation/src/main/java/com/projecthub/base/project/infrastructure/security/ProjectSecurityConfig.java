package com.projecthub.base.project.infrastructure.security;

import com.projecthub.base.shared.config.RbacSecurityConfig;
import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.ReviewMgr;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchical.RoleHierarchy;
import org.springframework.security.access.hierarchical.RoleHierarchyImpl;

/**
 * Security configuration for the Project module.
 * Extends the base RBAC security configuration with project-specific settings.
 */
@Configuration
public class ProjectSecurityConfig extends RbacSecurityConfig {

    public ProjectSecurityConfig(AccessMgr accessManager, ReviewMgr reviewManager) {
        super(accessManager, reviewManager);
    }

    /**
     * Defines project-specific role hierarchy that extends the base hierarchy.
     *
     * @return A configured RoleHierarchy for the project module
     */
    @Bean
    @Override
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        // Define project-specific role hierarchy
        hierarchy.setHierarchy("""
            ROLE_TEACHER > ROLE_PROJECT_CREATOR
            ROLE_PROJECT_CREATOR > ROLE_PROJECT_MANAGER
            ROLE_PROJECT_MANAGER > ROLE_PROJECT_VIEWER
            """);
        return hierarchy;
    }

    /**
     * Project permission object identifiers for use with Fortress.
     */
    public static class ObjectIdentifiers {
        public static final String PROJECT = "project";
        public static final String PROJECT_MILESTONE = "project.milestone";
        public static final String PROJECT_TASK = "project.task";
        public static final String PROJECT_RESOURCE = "project.resource";

        public ObjectIdentifiers() {
        }
    }

    /**
     * Project permission operations for use with Fortress.
     */
    public static class Operations {
        public static final String CREATE = "create";
        public static final String READ = "read";
        public static final String UPDATE = "update";
        public static final String DELETE = "delete";
        public static final String ARCHIVE = "archive";
        public static final String ASSIGN = "assign";
        public static final String GRADE = "grade";
        public static final String PUBLISH = "publish";
        public static final String UNPUBLISH = "unpublish";
        public static final String ADD_MILESTONE = "add_milestone";
        public static final String REMOVE_MILESTONE = "remove_milestone";
        public static final String ADD_TASK = "add_task";
        public static final String REMOVE_TASK = "remove_task";

        public Operations() {
        }
    }
}
