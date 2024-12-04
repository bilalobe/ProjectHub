package com.projecthub.repository.csv;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Base repository interface for common CRUD operations.
 *
 * @param <T> the type of the entity
 */
public interface BaseCsvRepository<T> {
    T save(T entity);
    List<T> findAll();
    Optional<T> findById(UUID id);
    void deleteById(UUID id);

    /**
     * Checks if an entity with the given ID exists.
     *
     * @param id the ID of the entity
     * @return true if the entity exists, false otherwise
     */
    default boolean existsById(UUID id) {
        return findById(id).isPresent();
    }
}