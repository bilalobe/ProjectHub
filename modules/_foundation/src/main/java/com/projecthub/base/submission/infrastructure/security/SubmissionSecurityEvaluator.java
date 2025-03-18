package com.projecthub.base.submission.infrastructure.security;

import com.projecthub.base.auth.service.fortress.FortressSessionService;
import com.projecthub.base.shared.config.RbacSecurityConfig;
import com.projecthub.base.submission.domain.entity.Submission;
import com.projecthub.base.submission.domain.repository.SubmissionRepository;
import com.projecthub.base.submission.infrastructure.security.SubmissionSecurityConfig.ObjectIdentifiers;
import com.projecthub.base.submission.infrastructure.security.SubmissionSecurityConfig.Operations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.directory.fortress.core.AccessMgr;
import org.apache.directory.fortress.core.model.Session;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.UUID;

/**
 * Security evaluator for the Submission module.
 * Evaluates permissions based on Fortress RBAC and submission-specific business rules.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SubmissionSecurityEvaluator {

    private final AccessMgr accessManager;
    private final FortressSessionService sessionService;
    private final SubmissionRepository submissionRepository;
    @Qualifier("")
    private final RbacSecurityConfig securityConfig;

    /**
     * Checks if the user can grade the submission
     *
     * @param auth The authentication object
     * @param submissionId The submission ID
     * @return true if the user can grade, false otherwise
     */
    public boolean canGradeSubmission(Authentication auth, UUID submissionId) {
        try {
            Session session = sessionService.getSession(auth);

            // First check Fortress RBAC permission
            boolean hasPermission = securityConfig.hasPermission(session,
                ObjectIdentifiers.SUBMISSION_GRADE, Operations.GRADE);

            if (!hasPermission) {
                return false;
            }

            // Additional business rules: Instructors can only grade their assigned submissions
            Submission submission = getSubmission(submissionId);
            return isAdmin(auth) || isInstructor(auth) && isProjectOwner(auth, submission);
        } catch (RuntimeException e) {
            log.error("Error checking grade permission for submission {}: {}", submissionId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Checks if the user can submit to the submission
     *
     * @param auth The authentication object
     * @param submissionId The submission ID
     * @return true if the user can submit, false otherwise
     */
    public boolean canSubmit(Authentication auth, UUID submissionId) {
        try {
            Session session = sessionService.getSession(auth);

            // First check Fortress RBAC permission
            boolean hasPermission = securityConfig.hasPermission(session,
                ObjectIdentifiers.SUBMISSION, Operations.SUBMIT);

            if (!hasPermission) {
                return false;
            }

            // Additional business rule: Students can only submit to their own submissions
            Submission submission = getSubmission(submissionId);
            return isAdmin(auth) || isSubmissionOwner(auth, submission);
        } catch (RuntimeException e) {
            log.error("Error checking submit permission for submission {}: {}", submissionId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Checks if the user can access the submission
     *
     * @param auth The authentication object
     * @param submissionId The submission ID
     * @return true if the user can access, false otherwise
     */
    public boolean canAccessSubmission(Authentication auth, UUID submissionId) {
        try {
            Session session = sessionService.getSession(auth);
            Submission submission = getSubmission(submissionId);

            // Check if the user is the submission owner
            if (isSubmissionOwner(auth, submission)) {
                // Owner needs read_own permission
                return securityConfig.hasPermission(session, ObjectIdentifiers.SUBMISSION, Operations.READ_OWN);
            }

            // Others need read permission
            boolean hasReadPermission = securityConfig.hasPermission(session,
                ObjectIdentifiers.SUBMISSION, Operations.READ);

            // Additional rules: Project owners (instructors) can access project submissions
            return hasReadPermission && (isAdmin(auth) || isProjectOwner(auth, submission));
        } catch (RuntimeException e) {
            log.error("Error checking access permission for submission {}: {}", submissionId, e.getMessage(), e);
            return false;
        }
    }

    private static boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private static boolean isInstructor(Authentication auth) {
        return auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_INSTRUCTOR"));
    }

    private static boolean isSubmissionOwner(Principal auth, Submission submission) {
        return submission.getStudentId().toString().equals(auth.getName());
    }

    private boolean isProjectOwner(Authentication auth, Submission submission) {
        // This would need to be implemented based on your project ownership rules
        // For now, instructors are considered project owners
        return isInstructor(auth);
    }

    private Submission getSubmission(UUID submissionId) {
        return submissionRepository.findById(submissionId)
            .orElseThrow(() -> new AccessDeniedException("Submission not found or access denied"));
    }
}
