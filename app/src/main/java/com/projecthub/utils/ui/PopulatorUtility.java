package com.projecthub.utils.ui;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.projecthub.dto.CohortSummary;
import com.projecthub.dto.ComponentSummary;
import com.projecthub.dto.ProjectSummary;
import com.projecthub.dto.SchoolSummary;
import com.projecthub.dto.TeamSummary;
import com.projecthub.service.CohortService;
import com.projecthub.service.ComponentService;
import com.projecthub.service.ProjectService;
import com.projecthub.service.SchoolService;
import com.projecthub.service.TeamService;

import javafx.scene.control.TreeItem;

/**
 * Utility class for populating JavaFX TreeViews with data.
 */
@Component
public class PopulatorUtility {

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private CohortService cohortService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ComponentService componentService;

    /**
     * Populates the TreeView with Schools.
     *
     * @param parentItem the root TreeItem to which Schools are added
     */
    public void populateSchools(TreeItem<TreeItemWrapper> parentItem) {
        List<SchoolSummary> schools = schoolService.getAllSchools();
        for (SchoolSummary school : schools) {
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
    public void populateCohorts(TreeItem<TreeItemWrapper> parentItem, Long schoolId) {
        List<CohortSummary> cohorts = cohortService.getCohortsBySchoolId(schoolId);
        for (CohortSummary cohort : cohorts) {
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
    public void populateTeams(TreeItem<TreeItemWrapper> parentItem, Long cohortId) {
        List<TeamSummary> teams = teamService.getTeamsByCohortId(cohortId);
        for (TeamSummary team : teams) {
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
    public void populateProjects(TreeItem<TreeItemWrapper> parentItem, Long teamId) {
        List<ProjectSummary> projects;
        projects = projectService.getProjectsByTeamId(teamId);
        for (ProjectSummary project : projects) {
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
    public void populateComponents(TreeItem<TreeItemWrapper> parentItem, Long projectId) {
        List<ComponentSummary> components = componentService.getComponentsByProjectId(projectId);
        for (ComponentSummary component : components) {
            TreeItemWrapper componentWrapper = new TreeItemWrapper(component.getName(), component);
            TreeItem<TreeItemWrapper> componentItem = new TreeItem<>(componentWrapper);
            parentItem.getChildren().add(componentItem);
        }
    }
}