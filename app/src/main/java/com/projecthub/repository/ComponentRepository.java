package com.projecthub.repository;

import com.projecthub.model.Component;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/*
 * Repository interface for the Component entity.
 */

public interface ComponentRepository {

    Component save(Component component);
    Optional<Component> findById(UUID id);
    List<Component> findAll();
    void delete(Component component);
    List<Component> findByProjectId(UUID projectId);
    void deleteById(UUID id);
    boolean existsById(UUID id);
}