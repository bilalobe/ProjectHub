package com.projecthub.base.project.application.service;

import com.projecthub.base.project.api.dto.ProjectDTO;
import com.projecthub.base.project.application.port.in.ProjectManagementUseCase;
import com.projecthub.base.project.domain.aggregate.ProjectAggregate;
import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.domain.enums.ProjectStatus;
import com.projecthub.base.project.infrastructure.mapper.ProjectMapper;
import com.projecthub.base.project.infrastructure.metrics.ProjectMetrics;
import com.projecthub.base.project.infrastructure.repository.ProjectRepository;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import io.micrometer.core.instrument.Timer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectCommandService implements ProjectManagementUseCase {
    
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectMetrics metrics;

    @Override
    @Transactional
    public ProjectDTO createProject(ProjectDTO projectDTO) {
        Timer.Sample timer = metrics.startTimer();
        try {
            Project project = projectMapper.toEntity(projectDTO);
            project.setStatus(ProjectStatus.CREATED);
            
            ProjectAggregate aggregate = new ProjectAggregate(project);
            project = projectRepository.save(project);
            
            metrics.recordProjectCreation();
            return projectMapper.toDto(project);
        } finally {
            metrics.stopTimer(timer);
        }
    }

    @Override
    @Transactional
    public void updateProjectStatus(UUID id, ProjectStatus newStatus) {
        Timer.Sample timer = metrics.startTimer();
        try {
            Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
                
            ProjectAggregate aggregate = new ProjectAggregate(project);
            aggregate.updateStatus(newStatus, getUserId());
            
            projectRepository.save(project);
            metrics.recordStatusChange();
        } finally {
            metrics.stopTimer(timer);
        }
    }

    @Transactional
    public ProjectDTO updateProject(UUID id, ProjectDTO projectDTO) {
        Timer.Sample timer = metrics.startTimer();
        try {
            Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
                
            projectMapper.updateEntityFromDto(projectDTO, existingProject);
            Project savedProject = projectRepository.save(existingProject);
            
            metrics.recordProjectUpdate();
            return projectMapper.toDto(savedProject);
        } finally {
            metrics.stopTimer(timer);
        }
    }

    @Transactional
    public void deleteProject(UUID id) {
        Timer.Sample timer = metrics.startTimer();
        try {
            if (!projectRepository.existsById(id)) {
                throw new ResourceNotFoundException("Project not found: " + id);
            }
            projectRepository.deleteById(id);
            metrics.recordProjectDeletion();
        } finally {
            metrics.stopTimer(timer);
        }
    }

    private UUID getUserId() {
        // TODO: Get from security context
        return UUID.randomUUID();
    }
}