package com.projecthub.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.projecthub.dto.TaskSummary;
import com.projecthub.exception.ResourceNotFoundException;
import com.projecthub.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskSummary> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/{id}")
    public TaskSummary getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id " + id));
    }

    @GetMapping("/project/{projectId}")
    public List<TaskSummary> getTasksByProjectId(@PathVariable Long projectId) throws ResourceNotFoundException {
        return taskService.getTasksByProjectId(projectId);
    }

    @GetMapping("/user/{userId}")
    public List<TaskSummary> getTasksByAssignedUserId(@PathVariable Long userId) throws ResourceNotFoundException {
        return taskService.getTasksByAssignedUserId(userId);
    }

    @PostMapping
    public TaskSummary createTask(@RequestBody TaskSummary taskSummary) throws ResourceNotFoundException {
        return taskService.saveTask(taskSummary);
    }

    @PutMapping("/{id}")
    public TaskSummary updateTask(@PathVariable Long id, @RequestBody TaskSummary taskSummary) throws ResourceNotFoundException {
        taskSummary.setId(id);
        return taskService.updateTask(id, taskSummary);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTask(id);
        } catch (ResourceNotFoundException e) {
            throw new RuntimeException("Task not found with id " + id);
        }
    }
}