package com.projecthub.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.projecthub.dto.TaskSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.mapper.TaskMapper;
import com.projecthub.model.AppUser;
import com.projecthub.model.Project;
import com.projecthub.model.Task;
import com.projecthub.repository.custom.CustomProjectRepository;
import com.projecthub.repository.custom.CustomTaskRepository;
import com.projecthub.repository.custom.CustomUserRepository;

/**
 * Service class for managing tasks within the ProjectHub application.
 * It provides methods to create, retrieve, update, and delete tasks.
 * 
 * <p>
 * This service interacts with the {@link CustomTaskRepository}, {@link CustomUserRepository},
 * and {@link CustomProjectRepository} to perform CRUD operations on {@link TaskSummary} entities.
 * </p>
 * 
 * <p>
 * <strong>Note:</strong> Ensure that the repositories are correctly defined and 
 * injected to avoid runtime issues.
 * </p>
 * 
 * @see TaskSummary
 * @see CustomTaskRepository
 * @see CustomUserRepository
 * @see CustomProjectRepository
 */
@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final CustomTaskRepository customTaskRepository;
    private final CustomUserRepository customUserRepository;
    private final CustomProjectRepository customProjectRepository;

    @Autowired
    public TaskService(CustomTaskRepository customTaskRepository, CustomUserRepository customUserRepository, CustomProjectRepository customProjectRepository) {
        this.customTaskRepository = customTaskRepository;
        this.customUserRepository = customUserRepository;
        this.customProjectRepository = customProjectRepository;
    }

    /**
     * Saves a new task based on the provided {@link TaskSummary} DTO.
     *
     * @param taskSummary the summary of the task to be created
     * @return the saved {@link TaskSummary}
     * @throws ResourceNotFoundException if the associated project or user is not found
     * @throws IllegalArgumentException if taskSummary is null
     */
    @Transactional
    public TaskSummary saveTask(TaskSummary taskSummary) throws ResourceNotFoundException {
        logger.info("Saving new task");

        if (taskSummary == null) {
            throw new IllegalArgumentException("TaskSummary cannot be null");
        }

        // Verify the existence of the project
        Project project = customProjectRepository.findById(taskSummary.getProject())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + taskSummary.getProject()));

        // Verify the existence of the user
        AppUser assignedUser = customUserRepository.findById(taskSummary.getAssignedUser())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + taskSummary.getAssignedUser()));

        // Convert TaskSummary to Task using TaskMapper
        Task task = TaskMapper.toTask(taskSummary, project, assignedUser);

        // Save and return the TaskSummary entity
        Task savedTask = customTaskRepository.save(task);

        logger.info("Task saved with ID {}", savedTask.getId());
        return TaskMapper.toTaskSummary(savedTask);
    }

    /**
     * Retrieves all tasks from the repository.
     * 
     * @return a list of all {@link TaskSummary} objects
     */
    public List<TaskSummary> getAllTasks() {
        logger.info("Retrieving all tasks");
        return customTaskRepository.findAll().stream()
                .map(TaskMapper::toTaskSummary)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a task by its unique identifier.
     *
     * @param id the unique identifier of the task
     * @return the TaskSummary
     * @throws ResourceNotFoundException if the task is not found
     */
    public TaskSummary getTaskById(Long id) throws ResourceNotFoundException {
        logger.info("Retrieving task with ID {}", id);

        if (id == null) {
            throw new IllegalArgumentException("Task ID cannot be null");
        }

        return customTaskRepository.findById(id)
                .map(TaskMapper::toTaskSummary)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));
    }

    /**
     * Retrieves all tasks associated with a specific project.
     * 
     * @param projectId the unique identifier of the project
     * @return a list of {@link TaskSummary} objects linked to the specified project
     * @throws ResourceNotFoundException if the project is not found
     */
    public List<TaskSummary> getTasksByProjectId(Long projectId) throws ResourceNotFoundException {
        logger.info("Retrieving tasks for project with ID {}", projectId);

        // Verify the existence of the project
        if (!customProjectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with ID: " + projectId);
        }

        return customTaskRepository.findByProjectId(projectId).stream()
                .map(TaskMapper::toTaskSummary)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all tasks assigned to a specific user.
     * 
     * @param userId the unique identifier of the user
     * @return a list of {@link TaskSummary} objects assigned to the specified user
     * @throws ResourceNotFoundException if the user is not found
     */
    public List<TaskSummary> getTasksByAssignedUserId(Long userId) throws ResourceNotFoundException {
        logger.info("Retrieving tasks for user with ID {}", userId);

        // Verify the existence of the user
        if (!customUserRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        return customTaskRepository.findByAssignedUserId(userId).stream()
                .map(TaskMapper::toTaskSummary)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a task based on its unique identifier.
     * 
     * @param id the unique identifier of the task to be deleted
     * @throws ResourceNotFoundException if the task is not found
     */
    public void deleteTask(Long id) throws ResourceNotFoundException {
        logger.info("Deleting task with ID {}", id);

        // Verify the existence of the task
        if (!customTaskRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException("Task not found with ID: " + id);
        }

        customTaskRepository.deleteById(id);
        logger.info("Task deleted with ID {}", id);
    }

    /**
     * Updates an existing task based on the provided {@link TaskSummary} DTO.
     * 
     * @param id the unique identifier of the task to be updated
     * @param taskSummary the summary containing updated task information
     * @return the updated {@link TaskSummary} entity
     * @throws ResourceNotFoundException if the task, project, or user is not found
     */
    public TaskSummary updateTask(Long id, TaskSummary taskSummary) throws ResourceNotFoundException {
        logger.info("Updating task with ID {}", id);

        // Fetch the existing task
        Task existingTask = customTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));

        // Update task fields
        existingTask.setName(taskSummary.getName());
        existingTask.setDescription(taskSummary.getDescription());
        existingTask.setStatus(taskSummary.getStatus());
        existingTask.setDueDate(taskSummary.getDueDate() != null ? java.time.LocalDate.parse(taskSummary.getDueDate()) : null);

        // Update project if provided
        if (taskSummary.getProject() != null) {
            Project project = customProjectRepository.findById(taskSummary.getProject())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + taskSummary.getProject()));
            existingTask.setProject(project);
        }

        // Update assigned user if provided
        if (taskSummary.getAssignedUser() != null) {
            AppUser assignedUser = customUserRepository.findById(taskSummary.getAssignedUser())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + taskSummary.getAssignedUser()));
            existingTask.setAssignedUser(assignedUser);
        }

        // Save and return the updated task
        Task savedTask = customTaskRepository.save(existingTask);
        logger.info("Task updated with ID {}", savedTask.getId());
        return TaskMapper.toTaskSummary(savedTask);
    }
}