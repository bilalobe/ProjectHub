package com.projecthub.core.repositories.csv;

import com.projecthub.core.models.Team;

import java.util.List;
import java.util.UUID;

public interface TeamCsvRepository extends BaseCsvRepository<Team> {

    /**
     * Finds teams by cohort ID.
     *
     * @param cohortId the ID of the cohort
     * @return a list of {@code Team} objects belonging to the cohort
     */
    List<Team> findByCohortId(UUID cohortId);
}