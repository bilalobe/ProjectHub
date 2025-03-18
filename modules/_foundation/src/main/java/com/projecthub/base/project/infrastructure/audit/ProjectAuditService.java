package com.projecthub.base.project.infrastructure.audit;

import com.projecthub.base.project.domain.event.ProjectEvent;
import com.projecthub.base.project.domain.event.ProjectStatusChangedEvent;
import com.projecthub.base.security.audit.AuditEntry;
import com.projecthub.base.security.audit.AuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectAuditService {

    private final AuditRepository auditRepository;

    @Async
    @EventListener
    public void handleProjectEvent(ProjectEvent event) {
        AuditEntry entry = createAuditEntry(event);
        auditRepository.save(entry);
    }

    private AuditEntry createAuditEntry(ProjectEvent event) {
        AuditEntry entry = new AuditEntry();
        entry.setTimestamp(Instant.now());
        entry.setEntityType("Project");
        entry.setEntityId(event.getProjectId().toString());
        entry.setAction(determineAction(event));
        entry.setInitiatorId(getInitiatorId(event));
        entry.setDetails(createAuditDetails(event));
        
        return entry;
    }

    private String determineAction(ProjectEvent event) {
        return switch (event) {
            case ProjectStatusChangedEvent e -> "STATUS_CHANGED";
            case ProjectCreatedEvent e -> "CREATED";
            case ProjectUpdatedEvent e -> "UPDATED";
            case ProjectDeletedEvent e -> "DELETED";
            default -> "UNKNOWN";
        };
    }

    private String getInitiatorId(ProjectEvent event) {
        if (event instanceof ProjectStatusChangedEvent statusEvent) {
            return statusEvent.initiatorId().toString();
        }
        // Handle other event types or return system user
        return "SYSTEM";
    }

    private Map<String, Object> createAuditDetails(ProjectEvent event) {
        return switch (event) {
            case ProjectStatusChangedEvent e -> Map.of(
                "oldStatus", e.oldStatus(),
                "newStatus", e.newStatus(),
                "projectName", e.projectName()
            );
            case ProjectCreatedEvent e -> Map.of(
                "projectName", e.projectName(),
                "teamId", e.teamId()
            );
            case ProjectUpdatedEvent e -> Map.of(
                "projectName", e.projectName(),
                "updatedFields", e.updatedFields()
            );
            case ProjectDeletedEvent e -> Map.of(
                "projectName", e.projectName()
            );
            default -> Map.of("eventType", event.getClass().getSimpleName());
        };
    }

    public List<AuditEntry> getProjectAuditTrail(UUID projectId) {
        return auditRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(
            "Project", projectId.toString());
    }

    public List<AuditEntry> getTeamProjectAuditTrail(UUID teamId) {
        return auditRepository.findByEntityTypeAndTeamIdOrderByTimestampDesc(
            "Project", teamId.toString());
    }
}