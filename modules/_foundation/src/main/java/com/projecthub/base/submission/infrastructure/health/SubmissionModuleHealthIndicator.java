package com.projecthub.base.submission.infrastructure.health;

import com.projecthub.base.submission.domain.repository.SubmissionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubmissionModuleHealthIndicator implements HealthIndicator {
    private final SubmissionJpaRepository repository;

    @Override
    public Health health() {
        try {
            long submissionCount = repository.count();
            return Health.up()
                .withDetail("submissionCount", Long.valueOf(submissionCount))
                .build();
        } catch (RuntimeException e) {
            return Health.down()
                .withException(e)
                .build();
        }
    }
}
