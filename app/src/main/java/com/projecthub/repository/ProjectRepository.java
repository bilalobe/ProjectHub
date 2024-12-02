package com.projecthub.repository;

import com.projecthub.model.Project;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Project} entities.
 */
public interface ProjectRepository {
    Project save(Project project);
    Optional<Project> findById(Long id);
    List<Project> findAll();
    void delete(Project project);
    List<Project> findAllByTeamId(Long teamId);

    public boolean existsById(Long projectId);

    public Project getReferenceById(Long projectId);
}