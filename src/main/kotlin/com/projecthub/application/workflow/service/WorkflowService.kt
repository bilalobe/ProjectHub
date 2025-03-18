package com.projecthub.core.application.workflow

import com.projecthub.core.application.workflow.port.`in`.WorkflowManagementUseCase
import com.projecthub.core.application.workflow.port.out.WorkflowRepository
import com.projecthub.core.domain.model.workflow.WorkflowDefinition

import org.springframework.stereotype.Service

import java.util.ArrayList
import java.util.Optional
import java.util.UUID

@Service
class WorkflowService(private val workflowRepository: WorkflowRepository) : WorkflowManagementUseCase {

    override fun createWorkflow(name: String): String {
        val id = UUID.randomUUID().toString()
        val workflow = WorkflowDefinition(id, name, ArrayList())
        workflowRepository.save(workflow)
        return id
    }

    override fun updateWorkflow(id: String, name: String) {
        workflowRepository.findById(id).ifPresent { workflow ->
            val updated = WorkflowDefinition(id, name, workflow.getStages())
            workflowRepository.save(updated)
        }
    }

    override fun getWorkflow(id: String): Optional<WorkflowDefinition> {
        return workflowRepository.findById(id)
    }

    override fun getAllWorkflows(): List<WorkflowDefinition> {
        return workflowRepository.findAll()
    }
}
