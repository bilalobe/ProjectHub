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

    public ProjectDTO findById(final UUID id) {
        return this.projectStorage.findById(id)
            .map(this.projectMapper::toDto)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    public List<ProjectDTO> findAll() {
        return this.projectStorage.findAll().stream()
            .map(this.projectMapper::toDto)
            .toList();
    }
}
