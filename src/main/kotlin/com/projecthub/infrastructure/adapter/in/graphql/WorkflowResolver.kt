package foundation.adapter.`in`.graphql.workflow

import foundation.core.application.workflow.dto.WorkflowDto
import foundation.core.application.workflow.port.`in`.WorkflowManagementUseCase
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class WorkflowResolver(private val workflowManagementUseCase: WorkflowManagementUseCase) {

    @QueryMapping
    fun workflowById(@Argument workflowId: String): WorkflowDto {
        return workflowManagementUseCase.getWorkflow(workflowId)
    }
}
