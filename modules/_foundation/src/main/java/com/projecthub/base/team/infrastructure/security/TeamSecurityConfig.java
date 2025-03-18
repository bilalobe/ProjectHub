package com.projecthub.base.team.infrastructure.security;

import com.projecthub.base.shared.config.RbacSecurityConfig;
import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.ReviewMgr;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchical.RoleHierarchy;
import org.springframework.security.access.hierarchical.RoleHierarchyImpl;

/**
 * Security configuration for the Team module.
 * Extends the base RBAC security configuration with team-specific settings.
 */
@Configuration
public class TeamSecurityConfig extends RbacSecurityConfig {

    public TeamSecurityConfig(AccessMgr accessManager, ReviewMgr reviewManager) {
        super(accessManager, reviewManager);
    }

    /**
     * Defines team-specific role hierarchy that extends the base hierarchy.
     *
     * @return A configured RoleHierarchy for the team module
     */
    @Bean
    @Override
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        // Define team-specific role hierarchy
        hierarchy.setHierarchy("""
            ROLE_TEACHER > ROLE_TEAM_MANAGER
            ROLE_TEAM_MANAGER > ROLE_TEAM_LEAD
            ROLE_TEAM_LEAD > ROLE_TEAM_MEMBER
            """);
        return hierarchy;
    }

    /**
     * Team permission object identifiers for use with Fortress.
     */
    public static class ObjectIdentifiers {
        public static final String TEAM = "team";
        public static final String TEAM_MEMBERSHIP = "team.membership";
        public static final String TEAM_LEADERSHIP = "team.leadership";

        public ObjectIdentifiers() {
        }
    }

    /**
     * Team permission operations for use with Fortress.
     */
    public static class Operations {
        public static final String CREATE = "create";
        public static final String READ = "read";
        public static final String UPDATE = "update";
        public static final String DELETE = "delete";
        public static final String ADD_MEMBER = "add_member";
        public static final String REMOVE_MEMBER = "remove_member";
        public static final String ASSIGN_LEAD = "assign_lead";
        public static final String TRANSFER = "transfer";
        public static final String VIEW_ANALYTICS = "view_analytics";

        public Operations() {
        }
    }
}
