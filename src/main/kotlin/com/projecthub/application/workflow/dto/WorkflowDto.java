package foundation.core.application.workflow.dto;

public class WorkflowDto {
    private String id;
    private String name;

    public WorkflowDto(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
