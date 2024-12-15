package com.projecthub.core.controllers;

import com.projecthub.core.dto.TaskDTO;
import com.projecthub.core.services.TaskService;
import com.projecthub.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Task API", description = "Operations pertaining to tasks in ProjectHub")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Get all tasks")
    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks() {
        logger.info("Retrieving all tasks");
        List<TaskDTO> tasks = taskService.getAllTasks();
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Get task by ID")
    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable UUID id) {
        logger.info("Retrieving task with ID {}", id);
        TaskDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Get tasks by project ID")
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<TaskDTO>> getTasksByProjectId(@PathVariable UUID projectId) {
        logger.info("Retrieving tasks for project ID {}", projectId);
        List<TaskDTO> tasks = taskService.getTasksByProjectId(projectId);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Get tasks by assigned user ID")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TaskDTO>> getTasksByAssignedUserId(@PathVariable UUID userId) {
        logger.info("Retrieving tasks for user ID {}", userId);
        List<TaskDTO> tasks = taskService.getTasksByAssignedUserId(userId);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Create a new task")
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO taskSummary) {
        logger.info("Creating a new task");
        TaskDTO createdTask = taskService.createTask(taskSummary);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @Operation(summary = "Update a task")
    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable UUID id, @Valid @RequestBody TaskDTO taskSummary) {
        logger.info("Updating task with ID {}", id);
        TaskDTO updatedTask = taskService.updateTask(id, taskSummary);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "Delete a task by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable UUID id) {
        logger.info("Deleting task with ID {}", id);
        taskService.deleteTask(id);
        return ResponseEntity.ok("Task deleted successfully");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.error("Resource not found", ex);
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}