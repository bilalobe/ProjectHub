package com.projecthub.base.project.infrastructure.health;

import com.projecthub.base.project.config.ProjectConfig;
import com.projecthub.base.project.infrastructure.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectHealthIndicator implements HealthIndicator {

    private final ProjectRepository projectRepository;
    private final ProjectConfig projectConfig;

    @Override
    public Health health() {
        try {
            long totalProjects = projectRepository.count();
            long overdueProjects = projectRepository.findOverdueProjects().size();
            double overduePercentage = (double) overdueProjects / totalProjects * 100;

            Health.Builder builder = Health.up()
                .withDetail("totalProjects", totalProjects)
                .withDetail("overdueProjects", overdueProjects)
                .withDetail("overduePercentage", String.format("%.2f%%", overduePercentage));

            // If more than 20% of projects are overdue, mark as DOWN
            if (overduePercentage > 20) {
                return builder.down()
                    .withDetail("error", "High number of overdue projects")
                    .build();
            }

            return builder.build();
        } catch (Exception e) {
            return Health.down()
                .withException(e)
                .build();
        }
    }
}