package com.projecthub.utils.ui;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.projecthub.model.School;
import com.projecthub.repository.CohortRepository;
import com.projecthub.repository.ComponentRepository;
import com.projecthub.repository.ProjectRepository;
import com.projecthub.repository.SchoolRepository;
import com.projecthub.repository.StudentRepository;
import com.projecthub.repository.TaskRepository;
import com.projecthub.repository.TeamRepository;

import javafx.scene.control.TreeItem;

/**
 * Utility class for populating JavaFX TreeViews with data.
 */
@Component
public class PopulatorUtility {

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private CohortRepository cohortRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ComponentRepository componentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SchoolMapper schoolMapper;

    @Autowired
    private CohortMapper cohortMapper;

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ComponentMapper componentMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TaskMapper taskMapper;

    /**
     * Populates the TreeView with Schools.
     *
     * @param parentItem the root TreeItem to which Schools are added
     */
    public void populateSchools(TreeItem<TreeItemWrapper> parentItem) {
        List<SchoolDTO> schools = ((List<School>) schoolRepository.findAll()).stream()
                .map(schoolMapper::toSchoolDTO)
                .collect(Collectors.toList());
        for (SchoolDTO school : schools) {
            TreeItemWrapper schoolWrapper = new TreeItemWrapper(school.getName(), school);
            TreeItem<TreeItemWrapper> schoolItem = new TreeItem<>(schoolWrapper);
            parentItem.getChildren().add(schoolItem);
        }
    }

    /**
     * Populates the TreeView with Cohorts under a School.
     *
     * @param parentItem the TreeItem of the School
     * @param schoolId   the ID of the School
     */
    public void populateCohorts(TreeItem<TreeItemWrapper> parentItem, UUID schoolId) {
        List<CohortDTO> cohorts = cohortRepository.findBySchoolId(schoolId).stream()
                .map(cohortMapper::toCohortDTO)
                .collect(Collectors.toList());
        for (CohortDTO cohort : cohorts) {
            TreeItemWrapper cohortWrapper = new TreeItemWrapper(cohort.getName(), cohort);
            TreeItem<TreeItemWrapper> cohortItem = new TreeItem<>(cohortWrapper);
            parentItem.getChildren().add(cohortItem);
        }
    }

    /**
     * Populates the TreeView with Teams under a Cohort.
     *
     * @param parentItem the TreeItem of the Cohort
     * @param cohortId   the ID of the Cohort
     */
    public void populateTeams(TreeItem<TreeItemWrapper> parentItem, UUID cohortId) {
        List<TeamDTO> teams = teamRepository.findByCohortId(cohortId).stream()
                .map(teamMapper::toTeamDTO)
                .collect(Collectors.toList());
        for (TeamDTO team : teams) {
            TreeItemWrapper teamWrapper = new TreeItemWrapper(team.getName(), team);
            TreeItem<TreeItemWrapper> teamItem = new TreeItem<>(teamWrapper);
            parentItem.getChildren().add(teamItem);
        }
    }

    /**
     * Populates the TreeView with Projects under a Team.
     *
     * @param parentItem the TreeItem of the Team
     * @param teamId     the ID of the Team
     */
    public void populateProjects(TreeItem<TreeItemWrapper> parentItem, UUID teamId) {
        List<ProjectDTO> projects = projectRepository.findByTeamId(teamId).stream()
                .map(projectMapper::toProjectDTO)
                .collect(Collectors.toList());
        for (ProjectDTO project : projects) {
            TreeItemWrapper projectWrapper = new TreeItemWrapper(project.getName(), project);
            TreeItem<TreeItemWrapper> projectItem = new TreeItem<>(projectWrapper);
            parentItem.getChildren().add(projectItem);
        }
    }

    /**
     * Populates the TreeView with Components under a Project.
     *
     * @param parentItem the TreeItem of the Project
     * @param projectId  the ID of the Project
     */
    public void populateComponents(TreeItem<TreeItemWrapper> parentItem, UUID projectId) {
        List<ComponentDTO> components = componentRepository.findByProjectId(projectId).stream()
                .map(componentMapper::toComponentDTO)
                .collect(Collectors.toList());
        for (ComponentDTO component : components) {
            TreeItemWrapper componentWrapper = new TreeItemWrapper(component.getName(), component);
            TreeItem<TreeItemWrapper> componentItem = new TreeItem<>(componentWrapper);
            parentItem.getChildren().add(componentItem);
        }
    }

    /**
     * Populates the TreeView with Students under a Cohort.
     *
     * @param parentItem the TreeItem of the Cohort
     * @param cohortId   the ID of the Cohort
     */
    public void populateStudents(TreeItem<TreeItemWrapper> parentItem, UUID cohortId) {
        List<StudentDTO> students = studentRepository.findByCohortId(cohortId).stream()
                .map(studentMapper::toStudentDTO)
                .collect(Collectors.toList());
        for (StudentDTO student : students) {
            TreeItemWrapper studentWrapper = new TreeItemWrapper(student.getFirstName(), student);
            TreeItem<TreeItemWrapper> studentItem = new TreeItem<>(studentWrapper);
            parentItem.getChildren().add(studentItem);
        }
    }

    /**
     * Populates the TreeView with Tasks under a Project.
     *
     * @param parentItem the TreeItem of the Project
     * @param projectId  the ID of the Project
     */
    public void populateTasks(TreeItem<TreeItemWrapper> parentItem, UUID projectId) {
        List<TaskDTO> tasks = taskRepository.findByProjectId(projectId).stream()
                .map(taskMapper::toTaskDTO)
                .collect(Collectors.toList());
        for (TaskDTO task : tasks) {
            TreeItemWrapper taskWrapper = new TreeItemWrapper(task.getName(), task);
            TreeItem<TreeItemWrapper> taskItem = new TreeItem<>(taskWrapper);
            parentItem.getChildren().add(taskItem);
        }
    }
}