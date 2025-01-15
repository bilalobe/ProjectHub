package com.projecthub.base.shared.validation;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ValidationAspect {
    @Before("@annotation(Validated)")
    public void validate(final JoinPoint jp) {
        // Validation logic
    }
}
