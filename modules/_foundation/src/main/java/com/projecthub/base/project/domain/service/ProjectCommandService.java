package com.projecthub.base.project.domain.service;

import com.projecthub.base.project.api.command.CreateProjectCommand;
import com.projecthub.base.project.api.command.DeleteProjectCommand;
import com.projecthub.base.project.api.command.UpdateProjectCommand;
import com.projecthub.base.project.api.dto.ProjectDTO;
import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.domain.event.ProjectEventPublisher;
import com.projecthub.base.project.domain.validation.ProjectValidator;
import com.projecthub.base.project.infrastructure.mapper.ProjectMapper;
import com.projecthub.base.project.infrastructure.port.out.ProjectStoragePort;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import com.projecthub.base.shared.exception.ValidationException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProjectCommandService {
    private final ProjectStoragePort projectStorage;
    private final ProjectValidator validator;
    @Qualifier("")
    private final ProjectEventPublisher eventPublisher;
    private final ProjectMapper projectMapper;

    public ProjectDTO handleCreate(final CreateProjectCommand command) {
        ProjectCommandService.log.debug("Creating project from command: {}", command);
        final Project project = this.projectMapper.fromCreateCommand(command);

        try {
            this.validator.validateCreate(project);
            final Project savedProject = this.projectStorage.save(project);
            this.eventPublisher.publishCreated(savedProject, command.getInitiatorId());
            return this.projectMapper.toDto(savedProject);
        } catch (final RuntimeException e) {
            ProjectCommandService.log.error("Failed to create project", e);
            throw new ValidationException("Failed to create project: " + e.getMessage());
        }
    }

    public ProjectDTO handleUpdate(final UpdateProjectCommand command) {
        ProjectCommandService.log.debug("Updating project with ID: {}", command.getProjectId());
        final Project project = this.findProjectById(command.getProjectId());

        try {
            this.projectMapper.updateFromCommand(command, project);
            this.validator.validateUpdate(project);
            final Project savedProject = this.projectStorage.save(project);
            this.eventPublisher.publishUpdated(savedProject, command.getInitiatorId());
            return this.projectMapper.toDto(savedProject);
        } catch (final RuntimeException e) {
            ProjectCommandService.log.error("Failed to update project with ID: {}", command.getProjectId(), e);
            throw new ValidationException("Failed to update project: " + e.getMessage());
        }
    }

    public void handleDelete(final DeleteProjectCommand command) {
        ProjectCommandService.log.debug("Deleting project with ID: {}", command.getProjectId());
        final Project project = this.findProjectById(command.getProjectId());

        try {
            this.projectStorage.delete(project);
            this.eventPublisher.publishDeleted(command.getProjectId(), command.getInitiatorId());
        } catch (final RuntimeException e) {
            ProjectCommandService.log.error("Failed to delete project with ID: {}", command.getProjectId(), e);
            throw new ValidationException("Failed to delete project: " + e.getMessage());
        }
    }

    private Project findProjectById(final UUID id) {
        return this.projectStorage.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));
    }
}
