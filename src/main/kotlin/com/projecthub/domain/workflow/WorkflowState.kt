package foundation.core.domain.workflow

data class WorkflowState(
    val id: String,
    val workflowDefinitionId: String,
    var currentStageId: String
)
