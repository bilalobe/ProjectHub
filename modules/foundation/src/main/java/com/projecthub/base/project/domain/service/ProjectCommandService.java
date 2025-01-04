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
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProjectCommandService {
    private final ProjectStoragePort projectStorage;
    private final ProjectValidator validator;
    private final ProjectEventPublisher eventPublisher;
    private final ProjectMapper projectMapper;

    public ProjectDTO handleCreate(CreateProjectCommand command) {
        log.debug("Creating project from command: {}", command);
        Project project = projectMapper.fromCreateCommand(command);

        try {
            validator.validateCreate(project);
            Project savedProject = projectStorage.save(project);
            eventPublisher.publishCreated(savedProject, command.getInitiatorId());
            return projectMapper.toDto(savedProject);
        } catch (Exception e) {
            log.error("Failed to create project", e);
            throw new ValidationException("Failed to create project: " + e.getMessage());
        }
    }

    public ProjectDTO handleUpdate(UpdateProjectCommand command) {
        log.debug("Updating project with ID: {}", command.getProjectId());
        Project project = findProjectById(command.getProjectId());

        try {
            projectMapper.updateFromCommand(command, project);
            validator.validateUpdate(project);
            Project savedProject = projectStorage.save(project);
            eventPublisher.publishUpdated(savedProject, command.getInitiatorId());
            return projectMapper.toDto(savedProject);
        } catch (Exception e) {
            log.error("Failed to update project with ID: {}", command.getProjectId(), e);
            throw new ValidationException("Failed to update project: " + e.getMessage());
        }
    }

    public void handleDelete(DeleteProjectCommand command) {
        log.debug("Deleting project with ID: {}", command.getProjectId());
        Project project = findProjectById(command.getProjectId());

        try {
            projectStorage.delete(project);
            eventPublisher.publishDeleted(command.getProjectId(), command.getInitiatorId());
        } catch (Exception e) {
            log.error("Failed to delete project with ID: {}", command.getProjectId(), e);
            throw new ValidationException("Failed to delete project: " + e.getMessage());
        }
    }

    private Project findProjectById(UUID id) {
        return projectStorage.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + id));
    }
}
