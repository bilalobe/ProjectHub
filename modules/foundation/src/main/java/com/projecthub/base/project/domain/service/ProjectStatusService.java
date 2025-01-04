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

    public void updateStatus(UUID projectId, ProjectStatus newStatus) {
        var project = projectStorage.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        var oldStatus = project.getStatus();
        validator.validateStatusTransition(project, newStatus);

        project.setStatus(newStatus);
        project = projectStorage.save(project);

        eventPublisher.publishStatusChanged(project, oldStatus);
    }
}
