package com.projecthub.repository;

import com.projecthub.model.Project;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Project} entities.
 */
@Repository

public interface ProjectRepository {
    Project save(Project project);
    Optional<Project> findById(UUID id);
    List<Project> findAll();
    void delete(Project project);
    List<Project> findAllByTeamId(UUID teamId);
    boolean existsById(UUID projectId);
    Project getReferenceById(UUID projectId);
    List<Project> findByTeamId(UUID teamId);
    void deleteById(UUID id);
}