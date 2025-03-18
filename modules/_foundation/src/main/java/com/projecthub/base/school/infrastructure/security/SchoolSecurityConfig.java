package com.projecthub.base.school.infrastructure.security;

import com.projecthub.base.shared.config.RbacSecurityConfig;
import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.ReviewMgr;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchical.RoleHierarchy;
import org.springframework.security.access.hierarchical.RoleHierarchyImpl;

/**
 * Security configuration for the School module.
 * Extends the base RBAC security configuration with school-specific settings.
 */
@Configuration
public class SchoolSecurityConfig extends RbacSecurityConfig {

    public SchoolSecurityConfig(AccessMgr accessManager, ReviewMgr reviewManager) {
        super(accessManager, reviewManager);
    }

    /**
     * Defines school-specific role hierarchy that extends the base hierarchy.
     *
     * @return A configured RoleHierarchy for the school module
     */
    @Bean
    @Override
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        // Define school-specific role hierarchy
        hierarchy.setHierarchy("""
            ROLE_ADMIN > ROLE_SCHOOL_ADMIN
            ROLE_SCHOOL_ADMIN > ROLE_SCHOOL_MANAGER
            ROLE_SCHOOL_MANAGER > ROLE_TEACHER
            """);
        return hierarchy;
    }

    /**
     * School permission object identifiers for use with Fortress.
     */
    public static class ObjectIdentifiers {
        public static final String SCHOOL = "school";
        public static final String SCHOOL_ADDRESS = "school.address";
        public static final String SCHOOL_CONTACT = "school.contact";

        public ObjectIdentifiers() {
        }
    }

    /**
     * School permission operations for use with Fortress.
     */
    public static class Operations {
        public static final String CREATE = "create";
        public static final String READ = "read";
        public static final String UPDATE = "update";
        public static final String DELETE = "delete";
        public static final String ARCHIVE = "archive";
        public static final String SEARCH = "search";
        public static final String ASSIGN = "assign";

        public Operations() {
        }
    }
}
