package com.projecthub.base.submission.infrastructure.security;

import lombok.RequiredArgsConstructor;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.projecthub.base.submission.domain.command.CreateSubmissionCommand;

@Aspect
@Component
@RequiredArgsConstructor
public class SubmissionSecurityAspect {
    
    private final SubmissionSecurityEvaluator securityEvaluator;

    @Before("execution(* com.projecthub.base.submission.application.service.SubmissionCommandService.*(..)) && args(command,..)")
    public void validateCommandAccess(CreateSubmissionCommand command) {
        if (!securityEvaluator.canAccessSubmission(command.getInitiatorId(), command.getStudentId())) {
            throw new AccessDeniedException("Not authorized to perform this operation");
        }
    }
}