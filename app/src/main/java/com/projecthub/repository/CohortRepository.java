package com.projecthub.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.projecthub.model.Cohort;

/**
 * Repository interface for {@link Cohort} entities.
 */
public interface CohortRepository {

    Cohort save(Cohort cohort);

    Optional<Cohort> findById(UUID id);

    List<Cohort> findAll();

    void delete(Cohort cohort);

    List<Cohort> findBySchoolId(UUID schoolId);

    public Cohort getReferenceById(Long cohortId);
}