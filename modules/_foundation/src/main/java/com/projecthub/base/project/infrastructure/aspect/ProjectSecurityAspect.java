package com.projecthub.base.project.infrastructure.aspect;

import com.projecthub.base.project.domain.exception.ProjectException;
import com.projecthub.base.security.facade.SecurityFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ProjectSecurityAspect {

    private final SecurityFacade securityFacade;

    @Around("execution(* com.projecthub.base.project.application.facade.ProjectFacade.updateProject(..)) || " +
           "execution(* com.projecthub.base.project.application.facade.ProjectFacade.deleteProject(..)) || " +
           "execution(* com.projecthub.base.project.application.facade.ProjectFacade.updateProjectStatus(..))")
    public Object enforceProjectSecurity(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof UUID projectId) {
            if (!securityFacade.hasProjectPermission(getCurrentUserId(), projectId.toString(), "update")) {
                throw new ProjectException("User does not have permission to modify this project");
            }
        }
        return joinPoint.proceed();
    }

    private String getCurrentUserId() {
        // TODO: Get from SecurityContext
        return UUID.randomUUID().toString();
    }
}