package com.projecthub.base.task.infrastructure.security;

import com.projecthub.base.shared.config.RbacSecurityConfig;
import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.ReviewMgr;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchical.RoleHierarchy;
import org.springframework.security.access.hierarchical.RoleHierarchyImpl;

/**
 * Security configuration for the Task module.
 * Extends the base RBAC security configuration with task-specific settings.
 */
@Configuration
public class TaskSecurityConfig extends RbacSecurityConfig {

    public TaskSecurityConfig(AccessMgr accessManager, ReviewMgr reviewManager) {
        super(accessManager, reviewManager);
    }

    /**
     * Defines task-specific role hierarchy that extends the base hierarchy.
     *
     * @return A configured RoleHierarchy for the task module
     */
    @Bean
    @Override
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        // Define task-specific role hierarchy
        hierarchy.setHierarchy("""
            ROLE_TEACHER > ROLE_TASK_CREATOR
            ROLE_TASK_CREATOR > ROLE_TASK_MANAGER
            ROLE_TASK_MANAGER > ROLE_TASK_VIEWER
            """);
        return hierarchy;
    }

    /**
     * Task permission object identifiers for use with Fortress.
     */
    public static class ObjectIdentifiers {
        public static final String TASK = "task";
        public static final String TASK_ATTACHMENT = "task.attachment";
        public static final String TASK_COMMENT = "task.comment";
        public static final String TASK_ASSIGNMENT = "task.assignment";

        public ObjectIdentifiers() {
        }
    }

    /**
     * Task permission operations for use with Fortress.
     */
    public static class Operations {
        public static final String CREATE = "create";
        public static final String READ = "read";
        public static final String UPDATE = "update";
        public static final String DELETE = "delete";
        public static final String ASSIGN = "assign";
        public static final String COMPLETE = "complete";
        public static final String REVIEW = "review";
        public static final String COMMENT = "comment";
        public static final String ATTACH = "attach";
        public static final String DOWNLOAD = "download";

        public Operations() {
        }
    }
}
