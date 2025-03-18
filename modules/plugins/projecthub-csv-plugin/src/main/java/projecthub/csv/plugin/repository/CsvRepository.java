package projecthub.csv.plugin.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Generic interface for CSV-based repositories.
 * Provides common CRUD operations for entities stored in CSV files.
 *
 * @param <T> the entity type
 */
public interface CsvRepository<T> {
    /**
     * Save an entity to the CSV store
     */
    T save(T entity);

    /**
     * Save multiple entities in a batch operation
     */
    List<T> saveAll(List<T> entities);

    /**
     * Find an entity by its ID
     */
    Optional<T> findById(UUID id);

    /**
     * Find all entities
     */
    List<T> findAll();

    /**
     * Delete an entity by ID
     */
    void deleteById(UUID id);

    /**
     * Delete multiple entities by their IDs
     */
    void deleteAllById(List<UUID> ids);
}