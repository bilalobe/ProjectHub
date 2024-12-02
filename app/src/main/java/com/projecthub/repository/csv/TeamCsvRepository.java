package com.projecthub.repository.csv;

import com.projecthub.model.Team;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Team} entities.
 */
@Repository("teamCsvRepository")
@Profile("csv")
public interface TeamCsvRepository extends BaseCsvRepository<Team, Long> {

    /**
     * Finds teams by cohort ID.
     *
     * @param cohortId the ID of the cohort
     * @return a list of {@code Team} objects belonging to the cohort
     */
    List<Team> findByCohortId(Long cohortId);
}