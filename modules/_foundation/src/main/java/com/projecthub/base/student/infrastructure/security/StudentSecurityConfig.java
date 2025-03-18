package com.projecthub.base.student.infrastructure.security;

import com.projecthub.base.shared.config.RbacSecurityConfig;
import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.ReviewMgr;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchical.RoleHierarchy;
import org.springframework.security.access.hierarchical.RoleHierarchyImpl;

/**
 * Security configuration for the Student module.
 * Extends the base RBAC security configuration with student-specific settings.
 */
@Configuration
public class StudentSecurityConfig extends RbacSecurityConfig {

    public StudentSecurityConfig(AccessMgr accessManager, ReviewMgr reviewManager) {
        super(accessManager, reviewManager);
    }

    /**
     * Defines student-specific role hierarchy that extends the base hierarchy.
     *
     * @return A configured RoleHierarchy for the student module
     */
    @Bean
    @Override
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        // Define student-specific role hierarchy
        hierarchy.setHierarchy("""
            ROLE_ADMIN > ROLE_STUDENT_ADMIN
            ROLE_TEACHER > ROLE_STUDENT_ADVISOR
            """);
        return hierarchy;
    }

    /**
     * Student permission object identifiers for use with Fortress.
     */
    public static class ObjectIdentifiers {
        public static final String STUDENT = "student";
        public static final String STUDENT_PROFILE = "student.profile";
        public static final String STUDENT_ACADEMIC = "student.academic";
        public static final String STUDENT_ATTENDANCE = "student.attendance";

        public ObjectIdentifiers() {
        }
    }

    /**
     * Student permission operations for use with Fortress.
     */
    public static class Operations {
        public static final String CREATE = "create";
        public static final String READ = "read";
        public static final String UPDATE = "update";
        public static final String DELETE = "delete";
        public static final String ENROLL = "enroll";
        public static final String UNENROLL = "unenroll";
        public static final String REVIEW = "review";
        public static final String GRADE = "grade";
        public static final String VIEW_RECORDS = "view_records";

        public Operations() {
        }
    }
}
