package com.projecthub.utils.ui;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.projecthub.dto.CohortSummary;
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
    public void populateSchools(TreeItem<String> parentItem) {
        List<SchoolSummary> schools = schoolService.getAllSchools();
        for (SchoolSummary school : schools) {
            TreeItem<String> schoolItem = new TreeItem<>(school.getName());
            schoolItem.setExpanded(false);
            parentItem.getChildren().add(schoolItem);
            populateCohorts(schoolItem, school.getId());
        }
    }

    /**
     * Populates the TreeView with Cohorts under a School.
     *
     * @param parentItem the TreeItem of the School
     * @param schoolId   the ID of the School
     */
    public void populateCohorts(TreeItem<String> parentItem, Long schoolId) {
        List<CohortSummary> cohorts = cohortService.getCohortsBySchoolId(schoolId);
        for (CohortSummary cohort : cohorts) {
            TreeItem<String> cohortItem = new TreeItem<>(cohort.getName());
            cohortItem.setExpanded(false);
            parentItem.getChildren().add(cohortItem);
            populateTeams(cohortItem, cohort.getId());
        }
    }

    /**
     * Populates the TreeView with Teams under a Cohort.
     *
     * @param parentItem the TreeItem of the Cohort
     * @param cohortId   the ID of the Cohort
     */
    public void populateTeams(TreeItem<String> parentItem, Long cohortId) {
        List<TeamSummary> teams = teamService.getTeamsByCohortId(cohortId);
        for (TeamSummary team : teams) {
            TreeItem<String> teamItem = new TreeItem<>(team.getName());
            teamItem.setExpanded(false);
            parentItem.getChildren().add(teamItem);
            populateProjects(teamItem, team.getId());
        }
    }

    /**
     * Populates the TreeView with Projects under a Team.
     *
     * @param parentItem the TreeItem of the Team
     * @param teamId     the ID of the Team
     */
    public void populateProjects(TreeItem<String> parentItem, Long teamId) {
        // Implement this method based on your Project model and service
    }

    /**
     * Populates the TreeView with Components under a Project.
     *
     * @param parentItem the TreeItem of the Project
     * @param projectId  the ID of the Project
     */
    public void populateComponents(TreeItem<String> parentItem, Long projectId) {
        // Implement this method based on your Component model and service
    }
}