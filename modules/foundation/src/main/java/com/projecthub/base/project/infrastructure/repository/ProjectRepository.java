package com.projecthub.base.project.infrastructure.repository;

import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.project.domain.enums.ProjectStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ProjectRepository {
    Project save(Project project);

    Optional<Project> findById(UUID id);

    List<Project> findByTeamId(UUID teamId);

    List<Project> findByStatus(ProjectStatus status);

    boolean existsByName(String name);

    void deleteById(UUID id);

    List<Project> findOverdueProjects();

    List<Project> findTemplates();

    long countActiveProjectsByTeam(UUID teamId);
}
