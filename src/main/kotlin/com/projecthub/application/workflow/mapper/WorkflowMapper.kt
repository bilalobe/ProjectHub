package foundation.core.application.workflow.mapper

import foundation.core.application.workflow.dto.WorkflowDto
import foundation.core.domain.workflow.WorkflowDefinition

class WorkflowMapper {

    fun toDto(workflowDefinition: WorkflowDefinition): WorkflowDto {
        return WorkflowDto(
            workflowDefinition.getId(),
            workflowDefinition.getName()
        )
    }
}
