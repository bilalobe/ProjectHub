package com.projecthub.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.projecthub.dto.TaskSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Task API", description = "Operations pertaining to tasks in ProjectHub")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskSummary> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskSummary> getTaskById(@PathVariable Long id) {
        TaskSummary task = taskService.getTaskById(id);
        if (task != null) {
            return ResponseEntity.ok(task);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

    @GetMapping("/project/{projectId}")
    public List<TaskSummary> getTasksByProjectId(@PathVariable Long projectId) throws ResourceNotFoundException {
        return taskService.getTasksByProjectId(projectId);
    }

    @GetMapping("/user/{userId}")
    public List<TaskSummary> getTasksByAssignedUserId(@PathVariable Long userId) throws ResourceNotFoundException {
        return taskService.getTasksByAssignedUserId(userId);
    }

    @Operation(summary = "Create a new task")
    @PostMapping
    public ResponseEntity<TaskSummary> createTask(@Valid @RequestBody TaskSummary taskSummary) {
        try {
            TaskSummary createdTask = taskService.saveTask(taskSummary);
            return ResponseEntity.ok(createdTask);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @Operation(summary = "Update a task")
    @PutMapping("/{id}")
    public ResponseEntity<TaskSummary> updateTask(@PathVariable Long id, @Valid @RequestBody TaskSummary taskSummary) {
        try {
            TaskSummary updatedTask = taskService.updateTask(id, taskSummary);
            return ResponseEntity.ok(updatedTask);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @Operation(summary = "Delete a task by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok("Task deleted successfully");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(404).body("Task not found with id " + id);
        }
    }
}