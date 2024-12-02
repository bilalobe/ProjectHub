package com.projecthub.repository.csv;

import com.projecthub.model.Task;
import java.util.List;

/**
 * Repository interface for {@link Task} entities.
 */
public interface TaskCsvRepository extends BaseCsvRepository<Task, Long> {

    /**
     * Finds tasks by project ID.
     *
     * @param projectId the ID of the project
     * @return a list of {@code Task} objects belonging to the project
     */
    List<Task> findByProjectId(Long projectId);
}