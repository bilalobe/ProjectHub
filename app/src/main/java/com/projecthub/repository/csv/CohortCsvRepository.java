package com.projecthub.repository.csv;

import com.projecthub.model.Cohort;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Cohort} entities.
 */
@Repository("cohortCsvRepository")
@Profile("csv")
public interface CohortCsvRepository extends BaseCsvRepository<Cohort> {

    /**
     * Finds cohorts by school ID.
     *
     * @param schoolId the ID of the school
     * @return a list of {@code Cohort} objects belonging to the school
     */
    List<Cohort> findBySchoolId(UUID schoolId);
}