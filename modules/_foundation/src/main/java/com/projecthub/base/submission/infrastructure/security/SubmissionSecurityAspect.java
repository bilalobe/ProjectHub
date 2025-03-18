package com.projecthub.base.submission.infrastructure.security;

import com.projecthub.base.shared.exception.AccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class SubmissionSecurityAspect {
    private final SubmissionSecurityEvaluator securityEvaluator;

    @Before("execution(* com.projecthub.base.submission.application.service.SubmissionCommandService.handleGrade(..)) && args(command,..)")
    public void checkGradeAccess(Object command) {
        UUID submissionId = extractSubmissionId(command);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!securityEvaluator.canGradeSubmission(auth, submissionId)) {
            throw new AccessDeniedException("You don't have permission to grade this submission");
        }
    }

    @Before("execution(* com.projecthub.base.submission.application.service.SubmissionCommandService.handleSubmit(..)) && args(command,..)")
    public void checkSubmitAccess(Object command) {
        UUID submissionId = extractSubmissionId(command);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!securityEvaluator.canSubmit(auth, submissionId)) {
            throw new AccessDeniedException("You don't have permission to submit this submission");
        }
    }

    @Before("execution(* com.projecthub.base.submission.application.service.SubmissionCommandService.handle*(..)) && args(command,..)")
    public void checkGeneralAccess(Object command) {
        UUID submissionId = extractSubmissionId(command);
        if (submissionId != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (!securityEvaluator.canAccessSubmission(auth, submissionId)) {
                throw new AccessDeniedException("You don't have permission to access this submission");
            }
        }
    }

    private static UUID extractSubmissionId(Object command) {
        try {
            return (UUID) command.getClass().getMethod("submissionId").invoke(command);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            return null;
        }
    }
}
