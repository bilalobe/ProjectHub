package com.projecthub.base.project.infrastructure.event;

import com.projecthub.base.project.domain.event.ProjectEvent;
import com.projecthub.base.project.domain.event.ProjectStatusChangedEvent;
import com.projecthub.base.security.audit.SecurityAuditService;
import com.projecthub.base.security.audit.SecurityEventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectEventHandler {

    private final SecurityAuditService securityAuditService;

    @Async
    @EventListener
    public void handleProjectStatusChanged(ProjectStatusChangedEvent event) {
        log.info("Project status changed - Project: {}, Old Status: {}, New Status: {}", 
            event.projectName(), event.oldStatus(), event.newStatus());
            
        securityAuditService.logSecurityEvent(
            SecurityEventType.PROJECT_STATUS_CHANGED,
            event.initiatorId().toString(),
            "project",
            event.projectId().toString(),
            String.format("Status changed from %s to %s", event.oldStatus(), event.newStatus())
        );
    }
}