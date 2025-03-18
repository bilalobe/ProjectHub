package foundation.core.domain.workflow

data class WorkflowTransition(
    val id: String,
    val fromStageId: String,
    val toStageId: String
)
