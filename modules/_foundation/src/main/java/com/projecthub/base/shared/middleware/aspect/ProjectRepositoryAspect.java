package com.projecthub.base.shared.middleware.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ProjectRepositoryAspect {

    public ProjectRepositoryAspect() {
    }

    @Before("execution(* com.projecthub.repository.jpa.ProjectRepository.save(..))")
    public static void beforeSave() {
        // Custom logic before saving a project
        System.out.println("Before saving a project");
    }
}
