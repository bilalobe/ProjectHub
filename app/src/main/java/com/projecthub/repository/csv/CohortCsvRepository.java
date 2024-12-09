package com.projecthub.repository.csv;

import com.projecthub.model.Cohort;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for {@link Cohort} entities.
 */
public interface CohortCsvRepository extends BaseCsvRepository<Cohort> {

    /**
     * Finds cohorts by school ID.
     *
     * @param schoolId the ID of the school
     * @return a list of {@code Cohort} objects belonging to the school
     */
    List<Cohort> findBySchoolId(UUID schoolId);
}