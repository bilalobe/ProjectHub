package com.projecthub.core.repositories.csv;

import com.projecthub.core.models.Component;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for {@link Component} entities.
 */
public interface ComponentCsvRepository extends BaseCsvRepository<Component> {

    /**
     * Finds components by project ID.
     *
     * @param projectId the ID of the project
     * @return a list of {@code Component} objects belonging to the project
     */
    List<Component> findByProjectId(UUID projectId);
}