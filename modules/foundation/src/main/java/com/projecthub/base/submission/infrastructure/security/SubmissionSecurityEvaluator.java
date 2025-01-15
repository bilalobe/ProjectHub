package com.projecthub.base.submission.infrastructure.security;

import com.projecthub.base.submission.domain.entity.Submission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubmissionSecurityEvaluator {

    public boolean canAccessSubmission(UUID userId, Submission submission) {
        return submission.getStudent().getId().equals(userId) ||
               hasTeacherAccess(userId, submission.getProject().getId());
    }

    public boolean canGradeSubmission(UUID userId, Submission submission) {
        return hasTeacherAccess(userId, submission.getProject().getId());
    }

    public boolean canDeleteSubmission(UUID userId, Submission submission) {
        return hasAdminAccess(userId) || 
               (submission.getStudent().getId().equals(userId) && 
                submission.getStatus() == SubmissionStatus.PENDING);
    }

    private boolean hasTeacherAccess(UUID userId, UUID projectId) {
        // Implement teacher access check logic
        return true; // Placeholder
    }

    private boolean hasAdminAccess(UUID userId) {
        // Implement admin access check logic
        return true; // Placeholder
    }
}