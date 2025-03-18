package com.projecthub.core.application.workflow.port.`in`

import com.projecthub.core.domain.model.workflow.WorkflowDefinition
import java.util.Optional

interface WorkflowManagementUseCase {
    fun createWorkflow(name: String): String
    fun updateWorkflow(id: String, name: String)
    fun getWorkflow(id: String): Optional<WorkflowDefinition>
    fun getAllWorkflows(): List<WorkflowDefinition>
}
