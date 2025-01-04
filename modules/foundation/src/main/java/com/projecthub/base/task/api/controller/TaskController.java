package com.projecthub.base.task.api.controller;

import com.projecthub.base.task.api.dto.TaskDTO;
import com.projecthub.base.task.api.rest.TaskApi;
import com.projecthub.base.task.infrastructure.service.TaskService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController implements TaskApi {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        logger.info("Retrieving all tasks");
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @Override
    public ResponseEntity<TaskDTO> getById(UUID id) {
        logger.info("Retrieving task with ID {}", id);
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @Override
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO task) {
        logger.info("Creating new task");
        return ResponseEntity.ok(taskService.saveTask(task));
    }

    public ResponseEntity<TaskDTO> updateTask(UUID id, @Valid @RequestBody TaskDTO task) {
        logger.info("Updating task with ID {}", id);
        return ResponseEntity.ok(taskService.updateTask(id, task));
    }

    @Override
    public ResponseEntity<Void> deleteById(UUID id) {
        logger.info("Deleting task with ID {}", id);
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> updateTaskStatus(UUID id, @RequestParam String status) {
        logger.info("Updating status of task with ID {}", id);
        taskService.updateTaskStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<TaskDTO>> getTasksByProjectId(UUID projectId) {
        logger.info("Retrieving tasks for project with ID {}", projectId);
        return ResponseEntity.ok(taskService.getTasksByProjectId(projectId));
    }

    @Override
    public ResponseEntity<List<TaskDTO>> getTasksByAssignedUserId(UUID userId) {
        logger.info("Retrieving tasks assigned to user with ID {}", userId);
        return ResponseEntity.ok(taskService.getTasksByAssignedUserId(userId));
    }
}
