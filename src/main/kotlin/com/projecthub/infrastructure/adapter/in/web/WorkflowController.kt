package com.projecthub.infrastructure.adapter.in.web

import com.projecthub.infrastructure.adapter.in.dto.WorkflowDefinitionDTO
import com.projecthub.infrastructure.adapter.in.dto.WorkflowTransitionRequestDTO
import com.projecthub.infrastructure.adapter.in.mapper.WorkflowMapper
import com.projecthub.core.application.service.WorkflowManagementService
import com.projecthub.core.domain.model.workflow.WorkflowContext
import com.projecthub.core.domain.model.workflow.WorkflowDefinition
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/workflows")
class WorkflowController(
    private val workflowService: WorkflowManagementService,
    private val workflowMapper: WorkflowMapper
) {
    @GetMapping
    fun getAllWorkflows(): ResponseEntity<List<WorkflowDefinitionDTO>> {
        val workflows = workflowService.getAllWorkflows()
        return ResponseEntity.ok(workflowMapper.toDtoList(workflows))
    }

    @GetMapping("/{id}")
    fun getWorkflow(@PathVariable id: UUID): ResponseEntity<WorkflowDefinitionDTO> {
        val workflow = workflowService.getWorkflowById(id)
        return ResponseEntity.ok(workflowMapper.toDto(workflow))
    }

    @PostMapping
    fun createWorkflow(@RequestBody workflowDto: WorkflowDefinitionDTO): ResponseEntity<WorkflowDefinitionDTO> {
        val workflow = workflowMapper.toEntity(workflowDto)
        val created = workflowService.createWorkflow(workflow)
        return ResponseEntity.ok(workflowMapper.toDto(created))
    }

    @PostMapping("/{workflowId}/transitions")
    fun executeTransition(
        @PathVariable workflowId: UUID,
        @RequestBody request: WorkflowTransitionRequestDTO
    ): ResponseEntity<Void> {
        val context = workflowMapper.toContext(request)
        workflowService.executeTransition(workflowId, context)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{id}")
    fun deleteWorkflow(@PathVariable id: UUID): ResponseEntity<Void> {
        workflowService.deleteWorkflow(id)
        return ResponseEntity.ok().build()
    }
}
