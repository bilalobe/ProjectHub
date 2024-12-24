package com.projecthub.core.repositories.jpa;

import com.projecthub.core.models.Project;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link Project} entities.
 */
@Repository("jpaProjectRepository")
@Profile("jpa")
public interface ProjectJpaRepository extends JpaRepository<Project, UUID> {

    /**
     * Finds all projects by team ID.
     *
     * @param teamId the UUID of the team
     * @return a list of {@code Project} objects belonging to the team
     */
    List<Project> findAllByTeamId(UUID teamId);

    /**
     * Finds a project with its components by project ID.
     *
     * @param projectId the UUID of the project
     * @return an {@code Optional} containing the project with its components if found
     */
    @NonNull
    @EntityGraph(attributePaths = {"components"})
    Optional<Project> findById(@NonNull UUID projectId);
}