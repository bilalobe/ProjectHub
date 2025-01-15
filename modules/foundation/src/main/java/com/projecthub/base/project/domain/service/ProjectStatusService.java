package com.projecthub.base.project.domain.service;

import com.projecthub.base.project.domain.enums.ProjectStatus;
import com.projecthub.base.project.domain.event.ProjectEventPublisher;
import com.projecthub.base.project.domain.validation.ProjectValidator;
import com.projecthub.base.project.infrastructure.port.out.ProjectStoragePort;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectStatusService {
    private final ProjectStoragePort projectStorage;
    private final ProjectValidator validator;
    private final ProjectEventPublisher eventPublisher;

    public void updateStatus(final UUID projectId, final ProjectStatus newStatus) {
        var project = this.projectStorage.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        final var oldStatus = project.getStatus();
        this.validator.validateStatusTransition(project, newStatus);

        project.setStatus(newStatus);
        project = this.projectStorage.save(project);

        this.eventPublisher.publishStatusChanged(project, oldStatus);
    }
}
