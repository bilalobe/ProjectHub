package com.projecthub.core.repositories.csv;

import com.projecthub.core.models.Task;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for {@link Task} entities.
 */
public interface TaskCsvRepository extends BaseCsvRepository<Task> {

    /**
     * Finds tasks by project ID.
     *
     * @param projectId the ID of the project
     * @return a list of {@code Task} objects belonging to the project
     */
    List<Task> findByProjectId(UUID projectId);
}