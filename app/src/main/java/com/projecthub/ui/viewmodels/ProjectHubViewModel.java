package com.projecthub.ui.viewmodels;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.projecthub.dto.CohortSummary;
import com.projecthub.dto.ComponentSummary;
import com.projecthub.dto.SchoolSummary;
import com.projecthub.dto.TeamSummary;
import com.projecthub.service.CohortService;
import com.projecthub.service.ComponentService;
import com.projecthub.service.SchoolService;
import com.projecthub.service.TeamService;
import com.projecthub.utils.ui.TreeItemWrapper;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@org.springframework.stereotype.Component
public class ProjectHubViewModel {

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private CohortService classService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private ComponentService componentService;

    private final ObservableList<TreeItemWrapper> treeItems = FXCollections.observableArrayList();
    private final StringProperty searchQuery = new SimpleStringProperty();

    public ProjectHubViewModel() {
        loadTreeItems();
        setupSearchListener();
    }

    public ObservableList<TreeItemWrapper> getTreeItems() {
        return treeItems;
    }

    public StringProperty searchQueryProperty() {
        return searchQuery;
    }

    private void loadTreeItems() {
        List<SchoolSummary> schools = schoolService.getAllSchools();
        for (SchoolSummary school : schools) {
            TreeItemWrapper schoolWrapper = new TreeItemWrapper(school.getName(), school);
            treeItems.add(schoolWrapper);
        }
    }

    private void setupSearchListener() {
        searchQuery.addListener((observable, oldValue, newValue) -> {
            // Implement filtering logic here, e.g., filter treeItems based on searchQuery
            // Example:
            // filterTreeItems(newValue);
        });
    }

    /**
     * Retrieves the list of classes associated with a specific school ID.
     *
     * @param id the ID of the school
     * @return a list of Class objects associated with the school
     */
    public List<CohortSummary> getClassesBySchoolId(Long id) {
        return classService.getCohortsBySchoolId(id);
    }

    /**
     * Provides access to the ComponentService.
     *
     * @return the ComponentService instance
     */
    public ComponentService getComponentService() {
        return componentService;
    }

    /**
     * Retrieves the list of component summaries associated with a specific
     * project ID.
     *
     * @param projectId the ID of the project
     * @return a list of ComponentSummary objects
     */
    public List<ComponentSummary> getComponentsByProjectId(Long projectId) {
        List<ComponentSummary> components = componentService.getComponentsByProjectId(projectId);
        return components.stream()
                .map(c -> new ComponentSummary(projectId, c.getName(), c.getDescription(), projectId))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the name of the team associated with a specific team ID.
     *
     * @param teamId the ID of the team
     * @return the name of the team, or "N/A" if not found
     */
    public String getTeamNameById(Long teamId) {
        TeamSummary team = teamService.getTeamById(teamId);
        return team != null ? team.getName() : "N/A";
    }
}
