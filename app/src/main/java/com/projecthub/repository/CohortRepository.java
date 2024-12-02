package com.projecthub.repository;

import com.projecthub.model.Cohort;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Cohort} entities.
 */
public interface CohortRepository {

    Cohort save(Cohort cohort);

    Optional<Cohort> findById(Long id);

    List<Cohort> findAll();

    void delete(Cohort cohort);

    List<Cohort> findBySchoolId(Long schoolId);

    public Cohort getReferenceById(Long cohortId);
}