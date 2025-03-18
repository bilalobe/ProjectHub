package com.projecthub.base.milestone.infrastructure.security;

import com.projecthub.base.shared.config.RbacSecurityConfig;
import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.ReviewMgr;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchical.RoleHierarchy;
import org.springframework.security.access.hierarchical.RoleHierarchyImpl;

/**
 * Security configuration for the Milestone module.
 * Extends the base RBAC security configuration with milestone-specific settings.
 */
@Configuration
public class MilestoneSecurityConfig extends RbacSecurityConfig {

    public MilestoneSecurityConfig(AccessMgr accessManager, ReviewMgr reviewManager) {
        super(accessManager, reviewManager);
    }

    /**
     * Defines milestone-specific role hierarchy that extends the base hierarchy.
     *
     * @return A configured RoleHierarchy for the milestone module
     */
    @Bean
    @Override
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        // Define milestone-specific role hierarchy
        hierarchy.setHierarchy("""
            ROLE_TEACHER > ROLE_MILESTONE_CREATOR
            ROLE_MILESTONE_CREATOR > ROLE_MILESTONE_MANAGER
            ROLE_MILESTONE_MANAGER > ROLE_MILESTONE_VIEWER
            """);
        return hierarchy;
    }

    /**
     * Milestone permission object identifiers for use with Fortress.
     */
    public static class ObjectIdentifiers {
        public static final String MILESTONE = "milestone";
        public static final String MILESTONE_ATTACHMENT = "milestone.attachment";
        public static final String MILESTONE_CRITERIA = "milestone.criteria";
        public static final String MILESTONE_SCHEDULE = "milestone.schedule";

        public ObjectIdentifiers() {
        }
    }

    /**
     * Milestone permission operations for use with Fortress.
     */
    public static class Operations {
        public static final String CREATE = "create";
        public static final String READ = "read";
        public static final String UPDATE = "update";
        public static final String DELETE = "delete";
        public static final String PUBLISH = "publish";
        public static final String UNPUBLISH = "unpublish";
        public static final String EXTEND = "extend";
        public static final String MARK_COMPLETED = "mark_completed";
        public static final String ADD_CRITERIA = "add_criteria";
        public static final String REMOVE_CRITERIA = "remove_criteria";

        public Operations() {
        }
    }
}
