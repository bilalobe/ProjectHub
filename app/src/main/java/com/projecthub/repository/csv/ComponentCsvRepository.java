package com.projecthub.repository.csv;

import com.projecthub.model.Component;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link Component} entities.
 */
@Repository("componentCsvRepository")
@Profile("csv")
public interface ComponentCsvRepository extends BaseCsvRepository<Component> {

    /**
     * Finds components by project ID.
     *
     * @param projectId the ID of the project
     * @return a list of {@code Component} objects belonging to the project
     */
    List<Component> findByProjectId(UUID projectId);
}