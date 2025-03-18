package com.projecthub.core.domain.model.workflow

data class WorkflowDefinition(
    val id: String,
    val name: String,
    val stages: List<WorkflowStage>
)
