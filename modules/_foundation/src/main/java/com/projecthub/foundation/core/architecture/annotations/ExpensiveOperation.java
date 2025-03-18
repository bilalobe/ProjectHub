package com.projecthub.core.architecture.annotations;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExpensiveOperation {
    String description() default "";
    long expectedExecutionTime() default 0;
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
    String optimizationPlan() default "";
    boolean requiresMonitoring() default true;
}