package com.projecthub.base.submission.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class SubmissionMetrics {
    private final Counter submissionsCreated;
    private final Counter submissionsSubmitted;
    private final Counter submissionsGraded;
    private final Counter submissionsRevoked;
    private final Timer gradingDuration;
    private final Timer submissionProcessingTime;

    public SubmissionMetrics(MeterRegistry registry) {
        this.submissionsCreated = Counter.builder("submissions.created")
            .description("Total number of submissions created")
            .register(registry);

        this.submissionsSubmitted = Counter.builder("submissions.submitted")
            .description("Total number of submissions submitted for grading")
            .register(registry);

        this.submissionsGraded = Counter.builder("submissions.graded")
            .description("Total number of submissions graded")
            .register(registry);

        this.submissionsRevoked = Counter.builder("submissions.revoked")
            .description("Total number of submissions revoked")
            .register(registry);

        this.gradingDuration = Timer.builder("submissions.grading.duration")
            .description("Time taken to grade submissions")
            .register(registry);

        this.submissionProcessingTime = Timer.builder("submissions.processing.time")
            .description("Time taken to process submission operations")
            .register(registry);
    }

    @Around("execution(* com.projecthub.base.submission.application.service.SubmissionCommandService.handleCreate(..))")
    public Object trackSubmissionCreation(ProceedingJoinPoint joinPoint) throws Throwable {
        submissionsCreated.increment();
        return submissionProcessingTime.record(() -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Around("execution(* com.projecthub.base.submission.application.service.SubmissionCommandService.handleSubmit(..))")
    public Object trackSubmissionSubmission(ProceedingJoinPoint joinPoint) throws Throwable {
        submissionsSubmitted.increment();
        return submissionProcessingTime.record(() -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Around("execution(* com.projecthub.base.submission.application.service.SubmissionCommandService.handleGrade(..))")
    public Object trackSubmissionGrading(ProceedingJoinPoint joinPoint) throws Throwable {
        submissionsGraded.increment();
        return gradingDuration.record(() -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Around("execution(* com.projecthub.base.submission.application.service.SubmissionCommandService.handleRevoke(..))")
    public Object trackSubmissionRevocation(ProceedingJoinPoint joinPoint) throws Throwable {
        submissionsRevoked.increment();
        return submissionProcessingTime.record(() -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }
}
