package com.projecthub.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.projecthub.model.Cohort;

/**
 * Repository interface for {@link Cohort} entities.
 */
@Repository

public interface CohortRepository {

    Cohort save(Cohort cohort);

    Optional<Cohort> findById(UUID id);

    List<Cohort> findAll();

    void delete(Cohort cohort);

    List<Cohort> findBySchoolId(UUID schoolId);

    Cohort getReferenceById(UUID cohortId);

    void deleteById(UUID id);
}