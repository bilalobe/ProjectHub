package com.projecthub.base.student.infrastructure.health;

import com.projecthub.base.student.domain.repository.StudentJpaRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class StudentModuleHealthIndicator implements HealthIndicator {

    private final StudentJpaRepository repository;

    public StudentModuleHealthIndicator(StudentJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Health health() {
        try {
            long studentCount = repository.count();
            return Health.up()
                .withDetail("studentCount", Long.valueOf(studentCount))
                .withDetail("status", "operational")
                .build();
        } catch (RuntimeException e) {
            return Health.down()
                .withException(e)
                .withDetail("status", "data_access_error")
                .build();
        }
    }
}
