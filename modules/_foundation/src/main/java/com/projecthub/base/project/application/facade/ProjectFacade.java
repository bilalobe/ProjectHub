package com.projecthub.base.project.application.facade;

import com.projecthub.base.project.api.dto.ProjectDTO;
import com.projecthub.base.project.application.service.ProjectCommandService;
import com.projecthub.base.project.application.service.ProjectQueryService;
import com.projecthub.base.project.domain.enums.ProjectStatus;
import com.projecthub.base.project.domain.validation.ProjectValidator;
import com.projecthub.base.shared.validation.ValidationException;
import com.projecthub.base.shared.validation.ValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProjectFacade {
    
    private final ProjectCommandService commandService;
    private final ProjectQueryService queryService;
    private final ProjectValidator validator;

    public ProjectDTO createProject(ProjectDTO projectDTO) {
        ValidationResult result = validator.validateCreate(projectDTO);
        if (!result.isValid()) {
            throw new ValidationException("Invalid project data", result.getErrors());
        }
        return commandService.createProject(projectDTO);
    }

    public ProjectDTO updateProject(UUID id, ProjectDTO projectDTO) {
        ValidationResult result = validator.validateUpdate(id, projectDTO);
        if (!result.isValid()) {
            throw new ValidationException("Invalid project data", result.getErrors());
        }
        return commandService.updateProject(id, projectDTO);
    }

    public void updateProjectStatus(UUID id, ProjectStatus status) {
        commandService.updateProjectStatus(id, status);
    }

    public void deleteProject(UUID id) {
        commandService.deleteProject(id);
    }

    public ProjectDTO getProjectById(UUID id) {
        return queryService.getProjectById(id);
    }

    public List<ProjectDTO> getAllProjects() {
        return queryService.getAllProjects();
    }

    public Page<ProjectDTO> getProjectsByTeam(UUID teamId, Pageable pageable) {
        return queryService.getProjectsByTeam(teamId, pageable);
    }

    public List<ProjectDTO> searchProjects(String query) {
        return queryService.searchProjects(query);
    }
}