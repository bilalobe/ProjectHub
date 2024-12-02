package com.projecthub.repository;

import com.projecthub.model.School;
import java.util.Optional;

/**
 * Repository interface for {@link School} entities.
 */
public interface SchoolRepository {
    School save(School school);
    void deleteById(Long id);
    Optional<School> findById(Long id);
    Iterable<School> findAll();
    boolean existsById(Long id);

    public School getReferenceById(Long schoolId);
}