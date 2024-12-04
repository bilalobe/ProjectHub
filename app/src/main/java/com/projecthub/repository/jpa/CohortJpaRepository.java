package com.projecthub.repository.jpa;

import com.projecthub.model.Cohort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for {@link Cohort} entities.
 */
@Repository("jpaCohortRepository")
@Profile("jpa")
public interface CohortJpaRepository extends JpaRepository<Cohort, UUID> {

    /**
     * Finds cohorts by school ID.
     *
     * @param schoolId the UUID of the school
     * @return a list of {@code Cohort} objects belonging to the school
     */
    List<Cohort> findBySchoolId(UUID schoolId);
}