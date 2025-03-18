package com.projecthub.base.project.application.service;

import com.projecthub.base.project.api.dto.ProjectDTO;
import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.infrastructure.mapper.ProjectMapper;
import com.projecthub.base.project.infrastructure.repository.ProjectRepository;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectQueryService {
    
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public List<ProjectDTO> getAllProjects() {
        return projectRepository.findAll().stream()
            .map(projectMapper::toDto)
            .toList();
    }

    public ProjectDTO getProjectById(UUID id) {
        return projectRepository.findById(id)
            .map(projectMapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
    }

    public Page<ProjectDTO> getProjectsByTeam(UUID teamId, Pageable pageable) {
        return projectRepository.findByTeamId(teamId, pageable)
            .map(projectMapper::toDto);
    }

    public List<ProjectDTO> searchProjects(String query) {
        return projectRepository.searchByNameOrDescription(query).stream()
            .map(projectMapper::toDto)
            .toList();
    }
}