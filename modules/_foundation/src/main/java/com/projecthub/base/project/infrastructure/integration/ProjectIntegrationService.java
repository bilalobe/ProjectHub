package com.projecthub.base.project.infrastructure.integration;

import com.projecthub.base.notification.service.NotificationService;
import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.domain.enums.ProjectStatus;
import com.projecthub.base.project.domain.event.ProjectEvent;
import com.projecthub.base.project.domain.event.ProjectStatusChangedEvent;
import com.projecthub.base.project.infrastructure.metrics.ProjectMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.core.MessageBuilder;
import org.springframework.cloud.stream.function.StreamsBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectIntegrationService {

    private final NotificationService notificationService;
    private final ProjectMetrics metrics;

    @EventListener
    public void handleProjectEvent(ProjectEvent event) {
        metrics.recordProjectOperation(event.getClass().getSimpleName());
        
        if (event instanceof ProjectStatusChangedEvent statusEvent) {
            handleStatusChange(statusEvent);
        }
    }

    private void handleStatusChange(ProjectStatusChangedEvent event) {
        log.info("Processing status change for project {}: {} -> {}", 
            event.projectId(), event.oldStatus(), event.newStatus());
            
        // Push to message broker for external system integration
        Message<ProjectStatusChangedEvent> message = MessageBuilder
            .withPayload(event)
            .setHeader("eventType", "PROJECT_STATUS_CHANGED")
            .setHeader("projectId", event.projectId())
            .build();
            
        projectStatusChannel().send(message);
    }

    // Define message channels for integration
    @Bean
    public Consumer<Message<ProjectEvent>> projectEventConsumer() {
        return message -> {
            try {
                ProjectEvent event = message.getPayload();
                log.info("Received project event: {}", event);
                processExternalProjectEvent(event);
            } catch (Exception e) {
                log.error("Error processing project event", e);
                metrics.recordProjectEventError();
            }
        };
    }

    private void processExternalProjectEvent(ProjectEvent event) {
        // Handle integration with external systems
        // This could include updating external task management systems,
        // syncing with CI/CD pipelines, or updating documentation systems
        switch (event) {
            case ProjectStatusChangedEvent statusEvent -> 
                updateExternalSystems(statusEvent);
            default -> 
                log.warn("Unhandled project event type: {}", event.getClass());
        }
    }

    private void updateExternalSystems(ProjectStatusChangedEvent event) {
        // Example: Update CI/CD pipeline status
        if (event.newStatus() == ProjectStatus.COMPLETED) {
            triggerDeploymentPipeline(event.projectId());
        }
        
        // Example: Update documentation system
        updateProjectDocumentation(event);
        
        // Example: Update resource allocation system
        updateResourceAllocation(event);
    }

    private void triggerDeploymentPipeline(UUID projectId) {
        // Implementation for triggering CI/CD pipeline
        log.info("Triggering deployment pipeline for project {}", projectId);
    }

    private void updateProjectDocumentation(ProjectStatusChangedEvent event) {
        // Implementation for updating documentation
        log.info("Updating documentation for project {}", event.projectId());
    }

    private void updateResourceAllocation(ProjectStatusChangedEvent event) {
        // Implementation for updating resource allocation
        log.info("Updating resource allocation for project {}", event.projectId());
    }
}