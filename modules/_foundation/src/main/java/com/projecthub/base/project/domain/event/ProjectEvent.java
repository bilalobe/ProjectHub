package com.projecthub.base.project.domain.event;

import com.projecthub.base.core.event.DomainEvent;
import java.util.UUID;

public interface ProjectEvent extends DomainEvent {
    UUID getProjectId();
    String getProjectName();
}