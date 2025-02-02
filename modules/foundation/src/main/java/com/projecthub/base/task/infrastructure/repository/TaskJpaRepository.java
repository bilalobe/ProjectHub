package com.projecthub.base.task.infrastructure.repository;

import com.projecthub.base.task.domain.entity.Task;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for {@link Task} entities.
 */
@Repository("jpaTaskRepository")
@Profile("jpa")
public interface TaskJpaRepository extends JpaRepository<Task, UUID> {

    /**
     * Finds tasks by project ID.
     *
     * @param projectId the UUID of the project
     * @return a list of {@code Task} objects belonging to the project
     */
    List<Task> findByProjectId(UUID projectId);

    /**
     * Finds tasks by assigned user ID.
     *
     * @param userId the UUID of the user
     * @return a list of {@code Task} objects assigned to the user
     */
    List<Task> findByAssignedUserId(UUID userId);
}
