package com.projecthub.service;

import com.projecthub.dto.TaskSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.mapper.TaskMapper;
import com.projecthub.model.AppUser;
import com.projecthub.model.Project;
import com.projecthub.model.Task;
import com.projecthub.repository.AppUserRepository;
import com.projecthub.repository.ProjectRepository;
import com.projecthub.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final AppUserRepository appUserRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, AppUserRepository appUserRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.appUserRepository = appUserRepository;
        this.taskMapper = taskMapper;
    }

    @Transactional
    public TaskSummary createTask(TaskSummary taskSummary) {
        logger.info("Creating a new task");
        validateTaskSummary(taskSummary);

        Project project = projectRepository.findById(taskSummary.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + taskSummary.getProjectId()));
        AppUser assignedUser = appUserRepository.findById(taskSummary.getAssignedUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + taskSummary.getAssignedUserId()));

        Task task = taskMapper.toTask(taskSummary, project, assignedUser);
        Task savedTask = taskRepository.save(task);

        logger.info("Task created with ID {}", savedTask.getId());
        return taskMapper.toTaskSummary(savedTask);
    }

    @Transactional
    public TaskSummary updateTask(Long id, TaskSummary taskSummary) {
        logger.info("Updating task with ID {}", id);
        validateTaskSummary(taskSummary);

        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));
        Project project = projectRepository.findById(taskSummary.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + taskSummary.getProjectId()));
        AppUser assignedUser = appUserRepository.findById(taskSummary.getAssignedUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + taskSummary.getAssignedUserId()));

        taskMapper.updateTaskFromSummary(taskSummary, existingTask, project, assignedUser);
        Task updatedTask = taskRepository.save(existingTask);

        logger.info("Task updated with ID {}", updatedTask.getId());
        return taskMapper.toTaskSummary(updatedTask);
    }

    @Transactional
    public void deleteTask(Long id) {
        logger.info("Deleting task with ID {}", id);
        taskRepository.deleteById(id);
        logger.info("Task deleted with ID {}", id);
    }

    public TaskSummary getTaskById(Long id) {
        logger.info("Retrieving task with ID {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + id));
        return taskMapper.toTaskSummary(task);
    }

    public List<TaskSummary> getAllTasks() {
        logger.info("Retrieving all tasks");
        return taskRepository.findAll().stream()
                .map(taskMapper::toTaskSummary)
                .collect(Collectors.toList());
    }

    public List<TaskSummary> getTasksByProjectId(Long projectId) {
        logger.info("Retrieving tasks for project ID {}", projectId);
        return taskRepository.findByProjectId(projectId).stream()
                .map(taskMapper::toTaskSummary)
                .collect(Collectors.toList());
    }

    public List<TaskSummary> getTasksByAssignedUserId(Long userId) {
        logger.info("Retrieving tasks for user ID {}", userId);
        return taskRepository.findByAssignedUserId(userId).stream()
                .map(taskMapper::toTaskSummary)
                .collect(Collectors.toList());
    }

    private void validateTaskSummary(TaskSummary taskSummary) {
        if (taskSummary == null) {
            throw new IllegalArgumentException("TaskSummary cannot be null");
        }
        if (taskSummary.getName() == null || taskSummary.getName().isEmpty()) {
            throw new IllegalArgumentException("Task name cannot be null or empty");
        }
        if (taskSummary.getProjectId() == null) {
            throw new IllegalArgumentException("Project ID cannot be null");
        }
        if (taskSummary.getAssignedUserId() == null) {
            throw new IllegalArgumentException("Assigned user ID cannot be null");
        }
    }
}