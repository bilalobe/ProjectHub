package com.projecthub.base.task.infrastructure.service;


import com.projecthub.base.shared.exception.ResourceNotFoundException;
import com.projecthub.base.task.api.dto.TaskDTO;
import com.projecthub.base.task.domain.entity.Task;
import com.projecthub.base.task.infrastructure.mapper.TaskMapper;
import com.projecthub.base.task.infrastructure.repository.TaskJpaRepository;
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

    private final TaskService self;

    public TaskService(final TaskJpaRepository taskRepository, final TaskMapper taskMapper, final TaskService self) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.self = self;
    }

    /**
     * Creates a new task.
     *
     * @param taskDTO the task data transfer object
     * @return the saved task DTO
     * @throws IllegalArgumentException if taskDTO is null
     */
    @Transactional
    public TaskDTO createTask(final TaskDTO taskDTO) {
        TaskService.logger.info("Creating a new task");
        TaskService.validateTaskDTO(taskDTO);
        final Task task = this.taskMapper.toEntity(taskDTO);
        final Task savedTask = this.taskRepository.save(task);
        TaskService.logger.info("Task created with ID: {}", savedTask.getId());
        return this.taskMapper.toDto(savedTask);
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
    public TaskDTO updateTask(final UUID id, final TaskDTO taskDTO) {
        TaskService.logger.info("Updating task with ID: {}", id);
        TaskService.validateTaskDTO(taskDTO);
        final Task existingTask = this.findTaskById(id);
        this.taskMapper.updateEntityFromDto(taskDTO, existingTask);
        final Task updatedTask = this.taskRepository.save(existingTask);
        TaskService.logger.info("Task updated with ID: {}", updatedTask.getId());
        return this.taskMapper.toDto(updatedTask);
    }

    /**
     * Deletes a task by ID.
     *
     * @param id the ID of the task to delete
     * @throws ResourceNotFoundException if the task is not found
     */
    @Transactional
    public void deleteTask(final UUID id) {
        TaskService.logger.info("Deleting task with ID: {}", id);
        if (!this.taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with ID: " + id);
        }
        this.taskRepository.deleteById(id);
        TaskService.logger.info("Task deleted with ID: {}", id);
    }

    /**
     * Retrieves a task by ID.
     *
     * @param id the ID of the task to retrieve
     * @return the task DTO
     * @throws ResourceNotFoundException if the task is not found
     */
    public TaskDTO getTaskById(final UUID id) {
        TaskService.logger.info("Retrieving task with ID: {}", id);
        final Task task = this.findTaskById(id);
        return this.taskMapper.toDto(task);
    }

    /**
     * Retrieves all tasks.
     *
     * @return a list of task DTOs
     */
    public List<TaskDTO> getAllTasks() {
        TaskService.logger.info("Retrieving all tasks");
        return this.taskRepository.findAll().stream()
            .map(this.taskMapper::toDto)
            .toList();
    }

    private static void validateTaskDTO(final TaskDTO taskDTO) {
        if (null == taskDTO) {
            throw new IllegalArgumentException("TaskDTO cannot be null");
        }
        // Additional validation logic can be added here
    }

    private Task findTaskById(final UUID id) {
        return this.taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));
    }

    /**
     * Retrieves tasks by project ID.
     *
     * @param projectId the ID of the project
     * @return a list of task DTOs
     */
    public List<TaskDTO> getTasksByProjectId(final UUID projectId) {
        TaskService.logger.info("Retrieving tasks for project ID: {}", projectId);
        return this.taskRepository.findByProjectId(projectId).stream()
            .map(this.taskMapper::toDto)
            .toList();
    }

    /**
     * Retrieves tasks by assigned user ID.
     *
     * @param userId the ID of the user
     * @return a list of task DTOs
     */
    public List<TaskDTO> getTasksByAssignedUserId(final UUID userId) {
        TaskService.logger.info("Retrieving tasks for user ID: {}", userId);
        return this.taskRepository.findByAssignedUserId(userId).stream()
            .map(this.taskMapper::toDto)
            .toList();
    }

    /**
     * Saves a task (creates or updates).
     *
     * @param taskDTO the task data transfer object
     * @throws IllegalArgumentException if taskDTO is null
     */
    @Transactional
    public void saveTask(final TaskDTO taskDTO) {
        this.self.updateTask(taskDTO.id(), taskDTO);
        TaskService.validateTaskDTO(taskDTO);
        if (null != taskDTO.id()) {
            this.self.updateTask(taskDTO.id(), taskDTO);
        } else {
            this.self.createTask(taskDTO);
        }
    }
}
