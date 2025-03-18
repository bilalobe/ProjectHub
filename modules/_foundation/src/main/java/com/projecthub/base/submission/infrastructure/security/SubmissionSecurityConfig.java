package com.projecthub.base.submission.infrastructure.security;

import com.projecthub.base.shared.config.RbacSecurityConfig;
import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.ReviewMgr;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchical.RoleHierarchy;
import org.springframework.security.access.hierarchical.RoleHierarchyImpl;

/**
 * Security configuration for the Submission module.
 * Extends the base RBAC security configuration with submission-specific settings.
 */
@Configuration
public class SubmissionSecurityConfig extends RbacSecurityConfig {

    public SubmissionSecurityConfig(AccessMgr accessManager, ReviewMgr reviewManager) {
        super(accessManager, reviewManager);
    }

    /**
     * Defines submission-specific role hierarchy that extends the base hierarchy.
     *
     * @return A configured RoleHierarchy for the submission module
     */
    @Bean
    @Override
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        // Define submission-specific role hierarchy
        hierarchy.setHierarchy("""
            ROLE_ADMIN > ROLE_INSTRUCTOR
            ROLE_INSTRUCTOR > ROLE_STUDENT
            ROLE_STUDENT > ROLE_ANONYMOUS
            """);
        return hierarchy;
    }

    /**
     * Submission permission object identifiers for use with Fortress.
     */
    public static class ObjectIdentifiers {
        public static final String SUBMISSION = "submission";
        public static final String SUBMISSION_COMMENT = "submission.comment";
        public static final String SUBMISSION_ATTACHMENT = "submission.attachment";
        public static final String SUBMISSION_GRADE = "submission.grade";

        public ObjectIdentifiers() {
        }
    }

    /**
     * Submission permission operations for use with Fortress.
     */
    public static class Operations {
        public static final String CREATE = "create";
        public static final String READ = "read";
        public static final String READ_OWN = "read_own";
        public static final String UPDATE = "update";
        public static final String DELETE = "delete";
        public static final String GRADE = "grade";
        public static final String SUBMIT = "submit";
        public static final String REVIEW = "review";
        public static final String COMMENT = "comment";
        public static final String ATTACH = "attach";
        public static final String DOWNLOAD = "download";

        public Operations() {
        }
    }
}
