package com.projecthub.base.task.api.rest;

import com.projecthub.base.shared.api.rest.BaseApi;
import com.projecthub.base.task.api.dto.TaskDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Tasks", description = "Task management API")
@RequestMapping("/api/v1/tasks")
public interface TaskApi extends BaseApi<TaskDTO, UUID> {

    @Operation(summary = "Get all tasks")
    @GetMapping
    ResponseEntity<List<TaskDTO>> getAllTasks();

    @Operation(summary = "Create a new task")
    @PostMapping
    ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskDTO task);

    @Operation(summary = "Get tasks by project")
    @GetMapping("/project/{projectId}")
    ResponseEntity<List<TaskDTO>> getTasksByProjectId(@PathVariable UUID projectId);

    @Operation(summary = "Get tasks by user")
    @GetMapping("/user/{userId}")
    ResponseEntity<List<TaskDTO>> getTasksByAssignedUserId(@PathVariable UUID userId);

    @Operation(summary = "Update task status")
    @PatchMapping("/{taskId}/status")
    ResponseEntity<TaskDTO> updateTaskStatus(
        @PathVariable UUID taskId,
        @RequestBody String status);
}
