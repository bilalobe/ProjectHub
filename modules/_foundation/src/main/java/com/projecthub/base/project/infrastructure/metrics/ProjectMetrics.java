package com.projecthub.base.project.infrastructure.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ProjectMetrics {
    private final Counter projectCreatedCounter;
    private final Counter projectUpdatedCounter;
    private final Counter projectDeletedCounter;
    private final Timer projectOperationTimer;
    private final Counter projectStatusChangeCounter;

    public ProjectMetrics(MeterRegistry registry) {
        this.projectCreatedCounter = Counter.builder("projecthub.projects.created")
            .description("Number of projects created")
            .register(registry);
            
        this.projectUpdatedCounter = Counter.builder("projecthub.projects.updated")
            .description("Number of projects updated")
            .register(registry);
            
        this.projectDeletedCounter = Counter.builder("projecthub.projects.deleted")
            .description("Number of projects deleted")
            .register(registry);
            
        this.projectOperationTimer = Timer.builder("projecthub.projects.operation.duration")
            .description("Time taken for project operations")
            .register(registry);
            
        this.projectStatusChangeCounter = Counter.builder("projecthub.projects.status.changed")
            .description("Number of project status changes")
            .register(registry);
    }

    public void recordProjectCreation() {
        projectCreatedCounter.increment();
    }

    public void recordProjectUpdate() {
        projectUpdatedCounter.increment();
    }

    public void recordProjectDeletion() {
        projectDeletedCounter.increment();
    }

    public void recordStatusChange() {
        projectStatusChangeCounter.increment();
    }

    public Timer.Sample startTimer() {
        return Timer.start();
    }

    public void stopTimer(Timer.Sample sample) {
        sample.stop(projectOperationTimer);
    }
}