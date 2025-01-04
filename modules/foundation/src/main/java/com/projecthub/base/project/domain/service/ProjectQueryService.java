package com.projecthub.base.project.domain.service;

import com.projecthub.base.project.api.dto.ProjectDTO;
import com.projecthub.base.project.infrastructure.mapper.ProjectMapper;
import com.projecthub.base.project.infrastructure.port.out.ProjectStoragePort;
import com.projecthub.base.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProjectQueryService {
    private final ProjectStoragePort projectStorage;
    private final ProjectMapper projectMapper;

    public ProjectDTO findById(UUID id) {
        return projectStorage.findById(id)
            .map(projectMapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    public List<ProjectDTO> findAll() {
        return projectStorage.findAll().stream()
            .map(projectMapper::toDto)
            .toList();
    }
}
