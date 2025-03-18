package foundation.adapter.in.graphql.project;

import foundation.core.application.project.dto.ProjectDto;
import foundation.core.application.project.port.in.ProjectManagementUseCase;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ProjectResolver {

    private final ProjectManagementUseCase projectManagementUseCase;

    public ProjectResolver(ProjectManagementUseCase projectManagementUseCase) {
        this.projectManagementUseCase = projectManagementUseCase;
    }

    @QueryMapping
    public ProjectDto projectById(@Argument String projectId) {
        return projectManagementUseCase.getProject(projectId);
    }
}
