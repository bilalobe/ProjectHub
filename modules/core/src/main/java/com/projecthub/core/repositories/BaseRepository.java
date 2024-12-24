
package com.projecthub.core.repositories;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T, I> {
    List<T> findAll();
    Optional<T> findById(I id);
    T save(T entity);
    void delete(T entity);
    boolean existsById(I id);
}