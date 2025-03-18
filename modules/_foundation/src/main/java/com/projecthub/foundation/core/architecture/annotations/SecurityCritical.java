package com.projecthub.core.architecture.annotations;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SecurityCritical {
    String justification() default "";
    String reviewedBy() default "";
    String lastReviewDate() default "";
}