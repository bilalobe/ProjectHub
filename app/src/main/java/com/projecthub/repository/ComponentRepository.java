package com.projecthub.repository;

import com.projecthub.model.Component;

import java.util.List;
import java.util.Optional;

/*
 * Repository interface for the Component entity.
 */
public interface ComponentRepository {

    Component save(Component component);

    Optional<Component> findById(Long id);

    List<Component> findAll();

    void delete(Component component);

    List<Component> findByProjectId(Long projectId);
}