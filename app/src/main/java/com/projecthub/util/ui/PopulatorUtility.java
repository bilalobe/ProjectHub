package com.projecthub.util.ui;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.projecthub.dto.CohortDTO;
import com.projecthub.dto.ComponentDTO;
import com.projecthub.dto.ProjectDTO;
import com.projecthub.dto.SchoolDTO;
import com.projecthub.dto.StudentDTO;
import com.projecthub.dto.TaskDTO;
import com.projecthub.dto.TeamDTO;
import com.projecthub.mapper.CohortMapper;
import com.projecthub.mapper.ComponentMapper;
import com.projecthub.mapper.ProjectMapper;
import com.projecthub.mapper.SchoolMapper;
import com.projecthub.mapper.StudentMapper;
import com.projecthub.mapper.TaskMapper;
import com.projecthub.mapper.TeamMapper;
import com.projecthub.repository.CohortRepository;
import com.projecthub.repository.ComponentRepository;
import com.projecthub.repository.ProjectRepository;
import com.projecthub.repository.SchoolRepository;
import com.projecthub.repository.StudentRepository;
import com.projecthub.repository.TaskRepository;
import com.projecthub.repository.TeamRepository;

import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;

/**
 * Utility class for populating JavaFX TreeViews with data.
 */
@Component
public class PopulatorUtility {

    private final SchoolRepository schoolRepository;
    private final SchoolMapper schoolMapper;

    private final CohortRepository cohortRepository;
    private final CohortMapper cohortMapper;

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    private final ComponentRepository componentRepository;
    private final ComponentMapper componentMapper;

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public PopulatorUtility(
            SchoolRepository schoolRepository,
            SchoolMapper schoolMapper,
            CohortRepository cohortRepository,
            CohortMapper cohortMapper,
            TeamRepository teamRepository,
            TeamMapper teamMapper,
            ProjectRepository projectRepository,
            ProjectMapper projectMapper,
            ComponentRepository componentRepository,
            ComponentMapper componentMapper,
            StudentRepository studentRepository,
            StudentMapper studentMapper,
            TaskRepository taskRepository,
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
                    .toList();

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
                    .toList();

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
                    .toList();

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
            List<ProjectDTO> projects = projectRepository.findByTeamId(teamId).stream()
                    .map(projectMapper::toProjectDTO)
                    .toList();

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
                    .toList();

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
                    .toList();

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
                    .toList();

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