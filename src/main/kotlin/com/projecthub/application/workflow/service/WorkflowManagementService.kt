package com.projecthub.core.application.service

import com.projecthub.core.domain.model.workflow.WorkflowDefinition
import com.projecthub.core.domain.model.workflow.WorkflowContext
import com.projecthub.infrastructure.adapter.out.persistence.WorkflowDefinitionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class WorkflowManagementService(
    private val workflowRepository: WorkflowDefinitionRepository,
    private val workflowEngine: WorkflowEngine
) {

    @Transactional(readOnly = true)
    fun getAllWorkflows(): List<WorkflowDefinition> {
        return workflowRepository.findAll()
    }

    @Transactional(readOnly = true)
    fun getWorkflowById(id: UUID): WorkflowDefinition {
        return workflowRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Workflow not found") }
    }

    @Transactional
    fun createWorkflow(workflow: WorkflowDefinition): WorkflowDefinition {
        // Basic validation
        if (workflow.getStates().isEmpty()) {
            throw IllegalArgumentException("Workflow must have at least one state")
        }
        if (workflow.getInitialState() == null) {
            throw IllegalArgumentException("Workflow must have an initial state")
        }

        return workflowRepository.save(workflow)
    }

    @Transactional
    fun executeTransition(workflowId: UUID, context: WorkflowContext) {
        val workflow = getWorkflowById(workflowId)
        workflowEngine.transition(workflow, context)
    }

    @Transactional
    fun deleteWorkflow(id: UUID) {
        workflowRepository.deleteById(id)
    }
}
