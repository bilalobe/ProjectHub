package com.projecthub.repository;

import com.projecthub.dto.TaskDTO;
import com.projecthub.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for {@link Task} entities.
 */
public interface TaskRepository {

    List<Task> findByProjectId(UUID projectId);
    List<Task> findByAssignedUserId(UUID userId);
    boolean existsById(UUID id);
    void deleteById(UUID id);
    Optional<Task> findById(UUID id);
    List<Task> findAll();
    List<TaskDTO> getAllTasks();
    Task save(Task task);
}