package com.projecthub.core.repositories.csv;

import com.projecthub.core.models.Project;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for {@link Project} entities.
 */
public interface ProjectCsvRepository extends BaseCsvRepository<Project> {

    /**
     * Finds projects by team ID.
     *
     * @param teamId the ID of the team
     * @return a list of {@code Project} objects belonging to the team
     */
    List<Project> findAllByTeamId(UUID teamId);
}