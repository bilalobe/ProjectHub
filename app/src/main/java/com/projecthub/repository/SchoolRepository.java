package com.projecthub.repository;

import com.projecthub.model.School;
import java.util.Optional;
import java.util.UUID;

import java.util.List;

/**
 * Repository interface for {@link School} entities.
 */
public interface SchoolRepository {
    School save(School school);
    void deleteById(UUID id);
    Optional<School> findById(UUID id);
    List<School> findAll();
    boolean existsById(UUID id);
    School getReferenceById(UUID schoolId);
}