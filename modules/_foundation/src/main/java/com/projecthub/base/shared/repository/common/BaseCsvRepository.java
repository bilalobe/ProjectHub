package com.projecthub.base.shared.repository.common;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Base repository interface for CSV-based storage operations.
 *
 * @param <T> the type of the entity
 */
public interface BaseCsvRepository<T> {
    T save(T entity);

    List<T> findAll();

    Optional<T> findById(UUID id);

    void delete(T entity);

    boolean existsById(UUID id);
}
