package com.projecthub.ui.modules.project;

import com.projecthub.base.project.api.dto.ProjectDTO;
import com.projecthub.base.project.mgmt.domain.service.ProjectService;
import com.projecthub.base.task.api.dto.TaskDTO;
import com.projecthub.base.task.infrastructure.service.TaskService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * ViewModel for managing project dashboard data.
 */
public class ProjectDashboardViewModel {

    private final ProjectService projectService;
    private final TaskService taskService;

    private final ObservableList<ProjectDTO> projects = FXCollections.observableArrayList();
    private final ObjectProperty<ProjectDTO> selectedProject = new SimpleObjectProperty<>();

    private final IntegerProperty totalTasks = new SimpleIntegerProperty();
    private final IntegerProperty completedTasks = new SimpleIntegerProperty();
    private final IntegerProperty inProgressTasks = new SimpleIntegerProperty();
    private final IntegerProperty pendingTasks = new SimpleIntegerProperty();
    private final StringProperty lastUpdated = new SimpleStringProperty();

    public ProjectDashboardViewModel(ProjectService projectService, TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
        loadProjects();
    }

    private void loadProjects() {
        List<ProjectDTO> projectList = projectService.getAllProjects();
        projects.setAll(projectList);
    }

    public void updateDashboardData() {
        ProjectDTO project = selectedProject.get();
        if (project != null) {
            UUID projectId = project.getId();
            List<TaskDTO> tasks = taskService.getTasksByProjectId(projectId);

            totalTasks.set(tasks.size());
            completedTasks.set((int) tasks.stream()
                .filter(task -> task.getStatus() == com.projecthub.base.project.TaskStatus.COMPLETED)
                .count());
            inProgressTasks.set((int) tasks.stream()
                .filter(task -> task.getStatus() == com.projecthub.base.project.TaskStatus.IN_PROGRESS)
                .count());
            pendingTasks.set((int) tasks.stream()
                .filter(task -> task.getStatus() == com.projecthub.base.project.TaskStatus.PENDING)
                .count());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            lastUpdated.set("Last updated: " + LocalDateTime.now().format(formatter));
        } else {
            // Reset counts if no project is selected
            totalTasks.set(0);
            completedTasks.set(0);
            inProgressTasks.set(0);
            pendingTasks.set(0);
            lastUpdated.set("");
        }
    }

    // Getters for properties
    public ObservableList<ProjectDTO> getProjects() {
        return projects;
    }

    public ObjectProperty<ProjectDTO> selectedProjectProperty() {
        return selectedProject;
    }

    public IntegerProperty totalTasksProperty() {
        return totalTasks;
    }

    public IntegerProperty completedTasksProperty() {
        return completedTasks;
    }

    public IntegerProperty inProgressTasksProperty() {
        return inProgressTasks;
    }

    public IntegerProperty pendingTasksProperty() {
        return pendingTasks;
    }

    public StringProperty lastUpdatedProperty() {
        return lastUpdated;
    }
}
