package com.projecthub.base.workflow.api;

import com.projecthub.base.workflow.api.dto.WorkflowDefinitionDTO;
import com.projecthub.base.workflow.api.dto.WorkflowTransitionRequestDTO;
import com.projecthub.base.workflow.api.mapper.WorkflowMapper;
import com.projecthub.base.workflow.application.service.WorkflowManagementService;
import com.projecthub.base.workflow.domain.model.WorkflowContext;
import com.projecthub.base.workflow.domain.model.WorkflowDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/workflows")
@RequiredArgsConstructor
public class WorkflowController {
    private final WorkflowManagementService workflowService;
    private final WorkflowMapper workflowMapper;

    @GetMapping
    public ResponseEntity<List<WorkflowDefinitionDTO>> getAllWorkflows() {
        List<WorkflowDefinition> workflows = workflowService.getAllWorkflows();
        return ResponseEntity.ok(workflowMapper.toDtoList(workflows));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkflowDefinitionDTO> getWorkflow(@PathVariable UUID id) {
        WorkflowDefinition workflow = workflowService.getWorkflowById(id);
        return ResponseEntity.ok(workflowMapper.toDto(workflow));
    }

    @PostMapping
    public ResponseEntity<WorkflowDefinitionDTO> createWorkflow(@RequestBody WorkflowDefinitionDTO workflowDto) {
        WorkflowDefinition workflow = workflowMapper.toEntity(workflowDto);
        WorkflowDefinition created = workflowService.createWorkflow(workflow);
        return ResponseEntity.ok(workflowMapper.toDto(created));
    }

    @PostMapping("/{workflowId}/transitions")
    public ResponseEntity<Void> executeTransition(
            @PathVariable UUID workflowId,
            @RequestBody WorkflowTransitionRequestDTO request) {
        WorkflowContext context = workflowMapper.toContext(request);
        workflowService.executeTransition(workflowId, context);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable UUID id) {
        workflowService.deleteWorkflow(id);
        return ResponseEntity.ok().build();
    }
}