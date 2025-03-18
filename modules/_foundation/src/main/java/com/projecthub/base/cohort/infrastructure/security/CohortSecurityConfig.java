package com.projecthub.base.cohort.infrastructure.security;

import com.projecthub.base.shared.config.RbacSecurityConfig;
import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.ReviewMgr;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchical.RoleHierarchy;
import org.springframework.security.access.hierarchical.RoleHierarchyImpl;

/**
 * Security configuration for the Cohort module.
 * Extends the base RBAC security configuration with cohort-specific settings.
 */
@Configuration
public class CohortSecurityConfig extends RbacSecurityConfig {

    public CohortSecurityConfig(AccessMgr accessManager, ReviewMgr reviewManager) {
        super(accessManager, reviewManager);
    }

    /**
     * Defines cohort-specific role hierarchy that extends the base hierarchy.
     *
     * @return A configured RoleHierarchy for the cohort module
     */
    @Bean
    @Override
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        // Define cohort-specific role hierarchy
        hierarchy.setHierarchy("""
            ROLE_SCHOOL_ADMIN > ROLE_COHORT_ADMIN
            ROLE_COHORT_ADMIN > ROLE_COHORT_MANAGER
            ROLE_COHORT_MANAGER > ROLE_TEACHER
            """);
        return hierarchy;
    }

    /**
     * Cohort permission object identifiers for use with Fortress.
     */
    public static class ObjectIdentifiers {
        public static final String COHORT = "cohort";
        public static final String COHORT_MEMBERSHIP = "cohort.membership";
        public static final String COHORT_SCHEDULE = "cohort.schedule";

        public ObjectIdentifiers() {
        }
    }

    /**
     * Cohort permission operations for use with Fortress.
     */
    public static class Operations {
        public static final String CREATE = "create";
        public static final String READ = "read";
        public static final String UPDATE = "update";
        public static final String DELETE = "delete";
        public static final String ARCHIVE = "archive";
        public static final String ASSIGN = "assign";
        public static final String ADD_TEAM = "add_team";
        public static final String REMOVE_TEAM = "remove_team";
        public static final String ADD_STUDENT = "add_student";
        public static final String REMOVE_STUDENT = "remove_student";

        public Operations() {
        }
    }
}
