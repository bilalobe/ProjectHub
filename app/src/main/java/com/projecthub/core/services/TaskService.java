package com.projecthub.core.services;

import com.projecthub.core.dto.TaskDTO;
import com.projecthub.core.mappers.TaskMapper;
import com.projecthub.core.models.Task;
import com.projecthub.core.repositories.jpa.TaskJpaRepository;
import com.projecthub.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service class for managing tasks.
 */
@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskJpaRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskJpaRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    /**
     * Creates a new task.
     *
     * @param taskDTO the task data transfer object
     * @return the saved task DTO
     * @throws IllegalArgumentException if taskDTO is null
     */
    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        logger.info("Creating a new task");
        validateTaskDTO(taskDTO);
        Task task = taskMapper.toTask(taskDTO);
        Task savedTask = taskRepository.save(task);
        logger.info("Task created with ID: {}", savedTask.getId());
        return taskMapper.toTaskDTO(savedTask);
    }

    /**
     * Updates an existing task.
     *
     * @param id      the ID of the task to update
     * @param taskDTO the task data transfer object
     * @return the updated task DTO
     * @throws ResourceNotFoundException if the task is not found
     * @throws IllegalArgumentException  if taskDTO is null
     */
    @Transactional
    public TaskDTO updateTask(UUID id, TaskDTO taskDTO) {
        logger.info("Updating task with ID: {}", id);
        validateTaskDTO(taskDTO);
        Task existingTask = findTaskById(id);
        taskMapper.updateTaskFromDTO(taskDTO, existingTask);
        Task updatedTask = taskRepository.save(existingTask);
        logger.info("Task updated with ID: {}", updatedTask.getId());
        return taskMapper.toTaskDTO(updatedTask);
    }

    /**
     * Deletes a task by ID.
     *
     * @param id the ID of the task to delete
     * @throws ResourceNotFoundException if the task is not found
     */
    @Transactional
    public void deleteTask(UUID id) {
        logger.info("Deleting task with ID: {}", id);
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with ID: " + id);
        }
        taskRepository.deleteById(id);
        logger.info("Task deleted with ID: {}", id);
    }

    /**
     * Retrieves a task by ID.
     *
     * @param id the ID of the task to retrieve
     * @return the task DTO
     * @throws ResourceNotFoundException if the task is not found
     */
    public TaskDTO getTaskById(UUID id) {
        logger.info("Retrieving task with ID: {}", id);
        Task task = findTaskById(id);
        return taskMapper.toTaskDTO(task);
    }

    /**
     * Retrieves all tasks.
     *
     * @return a list of task DTOs
     */
    public List<TaskDTO> getAllTasks() {
        logger.info("Retrieving all tasks");
        return taskRepository.findAll().stream()
                .map(taskMapper::toTaskDTO)
                .toList();
    }

    private void validateTaskDTO(TaskDTO taskDTO) {
        if (taskDTO == null) {
            throw new IllegalArgumentException("TaskDTO cannot be null");
        }
        // Additional validation logic can be added here
    }

    private Task findTaskById(UUID id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));
    }

    /**
     * Retrieves tasks by project ID.
     *
     * @param projectId the ID of the project
     * @return a list of task DTOs
     */
    public List<TaskDTO> getTasksByProjectId(UUID projectId) {
        logger.info("Retrieving tasks for project ID: {}", projectId);
        return taskRepository.findByProjectId(projectId).stream()
                .map(taskMapper::toTaskDTO)
                .toList();
    }

    /**
     * Retrieves tasks by assigned user ID.
     *
     * @param userId the ID of the user
     * @return a list of task DTOs
     */
    public List<TaskDTO> getTasksByAssignedUserId(UUID userId) {
        logger.info("Retrieving tasks for user ID: {}", userId);
        return taskRepository.findByAssignedUserId(userId).stream()
                .map(taskMapper::toTaskDTO)
                .toList();
    }

    /**
     * Saves a task (creates or updates).
     *
     * @param taskDTO the task data transfer object
     * @throws IllegalArgumentException if taskDTO is null
     */
    @Transactional
    public void saveTask(TaskDTO taskDTO) {
        logger.info("Saving task");
        validateTaskDTO(taskDTO);
        if (taskDTO.getId() != null) {
            updateTask(taskDTO.getId(), taskDTO);
        } else {
            createTask(taskDTO);
        }
    }
}