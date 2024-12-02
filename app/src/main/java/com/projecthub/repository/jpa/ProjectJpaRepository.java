package com.projecthub.repository.jpa;

import com.projecthub.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Project} entities.
 */
@Repository("jpaProjectRepository")
@Profile("jpa")
public interface ProjectJpaRepository extends JpaRepository<Project, Long> {

    /**
     * Finds all projects by team ID.
     *
     * @param teamId the ID of the team
     * @return a list of {@code Project} objects belonging to the team
     */
    List<Project> findAllByTeamId(Long teamId);

    /**
     * Finds a project with its components by project ID.
     *
     * @param projectId the ID of the project
     * @return an {@code Optional} containing the project with its components if
     *         found
     */
    @Query("SELECT p FROM Project p JOIN FETCH p.components WHERE p.id = :projectId")
    Optional<Project> findProjectWithComponentsById(Long projectId);
}