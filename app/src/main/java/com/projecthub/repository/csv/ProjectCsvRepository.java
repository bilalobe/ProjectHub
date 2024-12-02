package com.projecthub.repository.csv;

import com.projecthub.model.Project;
import java.util.List;

/**
 * Repository interface for {@link Project} entities.
 */
public interface ProjectCsvRepository extends BaseCsvRepository<Project, Long> {

    /**
     * Finds projects by team ID.
     *
     * @param teamId the ID of the team
     * @return a list of {@code Project} objects belonging to the team
     */
    List<Project> findAllByTeamId(Long teamId);
}