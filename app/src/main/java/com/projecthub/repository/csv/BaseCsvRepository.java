package com.projecthub.repository.csv;

import java.util.List;
import java.util.Optional;

/**
 * Base repository interface for common CRUD operations.
 *
 * @param <T>  the type of the entity
 * @param <ID> the type of the entity's identifier
 */
public interface BaseCsvRepository<T, ID> {
    T save(T entity);
    List<T> findAll();
    Optional<T> findById(ID id);
    void deleteById(ID id);

    /**
     * Checks if an entity with the given ID exists.
     *
     * @param id the ID of the entity
     * @return true if the entity exists, false otherwise
     */
    default boolean existsById(ID id) {
        return findById(id).isPresent();
    }
}