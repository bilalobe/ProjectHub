package com.projecthub.ui.utils;

import com.projecthub.core.dto.*;
import com.projecthub.core.mappers.*;
import com.projecthub.core.repositories.jpa.*;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Utility class for populating JavaFX TreeViews with data.
 */
@Component
public class PopulatorUtility {

    private final SchoolJpaRepository schoolRepository;
    private final SchoolMapper schoolMapper;

    private final CohortJpaRepository cohortRepository;
    private final CohortMapper cohortMapper;

    private final TeamJpaRepository teamRepository;
    private final TeamMapper teamMapper;

    private final ProjectJpaRepository projectRepository;
    private final ProjectMapper projectMapper;

    private final ComponentJpaRepository componentRepository;
    private final ComponentMapper componentMapper;

    private final StudentJpaRepository studentRepository;
    private final StudentMapper studentMapper;

    private final TaskJpaRepository taskRepository;
    private final TaskMapper taskMapper;

    public PopulatorUtility(
            SchoolJpaRepository schoolRepository,
            SchoolMapper schoolMapper,
            CohortJpaRepository cohortRepository,
            CohortMapper cohortMapper,
            TeamJpaRepository teamRepository,
            TeamMapper teamMapper,
            ProjectJpaRepository projectRepository,
            ProjectMapper projectMapper,
            ComponentJpaRepository componentRepository,
            ComponentMapper componentMapper,
            StudentJpaRepository studentRepository,
            StudentMapper studentMapper,
            TaskJpaRepository taskRepository,
            TaskMapper taskMapper) {

        this.schoolRepository = schoolRepository;
        this.schoolMapper = schoolMapper;
        this.cohortRepository = cohortRepository;
        this.cohortMapper = cohortMapper;
        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.componentRepository = componentRepository;
        this.componentMapper = componentMapper;
        this.studentRepository = studentRepository;
        this.studentMapper = studentMapper;
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    /**
     * Populates the TreeView with Schools.
     *
     * @param parentItem the root TreeItem to which Schools are added
     */
    public void populateSchools(TreeItem<TreeItemWrapper> parentItem) {
        try {
            List<SchoolDTO> schools = schoolRepository.findAll().stream()
                    .map(schoolMapper::toSchoolDTO)
                    .collect(Collectors.toList());

            for (SchoolDTO school : schools) {
                TreeItemWrapper schoolWrapper = new TreeItemWrapper(school.getName(), school);
                TreeItem<TreeItemWrapper> schoolItem = new TreeItem<>(schoolWrapper);
                parentItem.getChildren().add(schoolItem);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to populate schools: " + e.getMessage());
        }
    }

    /**
     * Populates the TreeView with Cohorts under a School.
     *
     * @param parentItem the TreeItem of the School
     * @param schoolId   the ID of the School
     */
    public void populateCohorts(TreeItem<TreeItemWrapper> parentItem, UUID schoolId) {
        try {
            List<CohortDTO> cohorts = cohortRepository.findBySchoolId(schoolId).stream()
                    .map(cohortMapper::toCohortDTO)
                    .collect(Collectors.toList());

            for (CohortDTO cohort : cohorts) {
                TreeItemWrapper cohortWrapper = new TreeItemWrapper(cohort.getName(), cohort);
                TreeItem<TreeItemWrapper> cohortItem = new TreeItem<>(cohortWrapper);
                parentItem.getChildren().add(cohortItem);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to populate cohorts: " + e.getMessage());
        }
    }

    /**
     * Populates the TreeView with Teams under a Cohort.
     *
     * @param parentItem the TreeItem of the Cohort
     * @param cohortId   the ID of the Cohort
     */
    public void populateTeams(TreeItem<TreeItemWrapper> parentItem, UUID cohortId) {
        try {
            List<TeamDTO> teams = teamRepository.findByCohortId(cohortId).stream()
                    .map(teamMapper::toTeamDTO)
                    .collect(Collectors.toList());

            for (TeamDTO team : teams) {
                TreeItemWrapper teamWrapper = new TreeItemWrapper(team.getName(), team);
                TreeItem<TreeItemWrapper> teamItem = new TreeItem<>(teamWrapper);
                parentItem.getChildren().add(teamItem);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to populate teams: " + e.getMessage());
        }
    }

    /**
     * Populates the TreeView with Projects under a Team.
     *
     * @param parentItem the TreeItem of the Team
     * @param teamId     the ID of the Team
     */
    public void populateProjects(TreeItem<TreeItemWrapper> parentItem, UUID teamId) {
        try {
            List<ProjectDTO> projects = projectRepository.findAllByTeamId(teamId).stream()
                    .map(projectMapper::toProjectDTO)
                    .collect(Collectors.toList());

            for (ProjectDTO project : projects) {
                TreeItemWrapper projectWrapper = new TreeItemWrapper(project.getName(), project);
                TreeItem<TreeItemWrapper> projectItem = new TreeItem<>(projectWrapper);
                parentItem.getChildren().add(projectItem);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to populate projects: " + e.getMessage());
        }
    }

    /**
     * Populates the TreeView with Components under a Project.
     *
     * @param parentItem the TreeItem of the Project
     * @param projectId  the ID of the Project
     */
    public void populateComponents(TreeItem<TreeItemWrapper> parentItem, UUID projectId) {
        try {
            List<ComponentDTO> components = componentRepository.findByProjectId(projectId).stream()
                    .map(componentMapper::toComponentDTO)
                    .collect(Collectors.toList());

            for (ComponentDTO component : components) {
                TreeItemWrapper componentWrapper = new TreeItemWrapper(component.getName(), component);
                TreeItem<TreeItemWrapper> componentItem = new TreeItem<>(componentWrapper);
                parentItem.getChildren().add(componentItem);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to populate components: " + e.getMessage());
        }
    }

    /**
     * Populates the TreeView with Students under a Cohort.
     *
     * @param parentItem the TreeItem of the Cohort
     * @param cohortId   the ID of the Cohort
     */
    public void populateStudents(TreeItem<TreeItemWrapper> parentItem, UUID cohortId) {
        try {
            List<StudentDTO> students = studentRepository.findByCohortId(cohortId).stream()
                    .map(studentMapper::toStudentDTO)
                    .collect(Collectors.toList());

            for (StudentDTO student : students) {
                TreeItemWrapper studentWrapper = new TreeItemWrapper(student.getFirstName(), student);
                TreeItem<TreeItemWrapper> studentItem = new TreeItem<>(studentWrapper);
                parentItem.getChildren().add(studentItem);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to populate students: " + e.getMessage());
        }
    }

    /**
     * Populates the TreeView with Tasks under a Project.
     *
     * @param parentItem the TreeItem of the Project
     * @param projectId  the ID of the Project
     */
    public void populateTasks(TreeItem<TreeItemWrapper> parentItem, UUID projectId) {
        try {
            List<TaskDTO> tasks = taskRepository.findByProjectId(projectId).stream()
                    .map(taskMapper::toTaskDTO)
                    .collect(Collectors.toList());

            for (TaskDTO task : tasks) {
                TreeItemWrapper taskWrapper = new TreeItemWrapper(task.getName(), task);
                TreeItem<TreeItemWrapper> taskItem = new TreeItem<>(taskWrapper);
                parentItem.getChildren().add(taskItem);
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to populate tasks: " + e.getMessage());
        }
    }

    /**
     * Shows an alert with the specified title and message.
     *
     * @param title   the title of the alert
     * @param message the message of the alert
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}