package com.projecthub.base.project.domain.event;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event published when a project's workflow status changes.
 */
@Value
public class ProjectStatusChangedEvent {
    UUID projectId;
    String fromStatus;
    String toStatus;
    LocalDateTime timestamp;
}
