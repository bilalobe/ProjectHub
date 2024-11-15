package com.projecthub.utils;

// Model imports
import com.projecthub.model.Class;
import com.projecthub.model.School;
import com.projecthub.model.Team;

// Service imports
import com.projecthub.service.ClassService;
import com.projecthub.service.ComponentService;
import com.projecthub.service.ProjectService;
import com.projecthub.service.SchoolService;
import com.projecthub.service.TeamService;

// JavaFX imports
import javafx.scene.control.TreeItem;

import java.util.List;

// Spring imports
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Component
public class PopulatorUtility {

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private ClassService classService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ComponentService componentService;

    public void populateSchools(TreeItem<String> parentItem) {
        List<School> schools = schoolService.getAllSchools();
        for (School school : schools) {
            TreeItem<String> schoolItem = new TreeItem<>(school.getName());
            parentItem.getChildren().add(schoolItem);
        }
    }

    public void populateClasses(TreeItem<String> parentItem, Long parentId) {
        List<Class> classes = classService.getClassesBySchoolId(parentId);
        for (Class cls : classes) {
            TreeItem<String> classItem = new TreeItem<>(cls.getName());
            classItem.setExpanded(false);
            parentItem.getChildren().add(classItem);
        }
    }

    public void populateTeams(TreeItem<String> parentItem, Long parentId) {
        List<Team> teams = teamService.getTeamsByClassId(parentId);
        for (Team team : teams) {
            TreeItem<String> teamItem = new TreeItem<>(team.getName());
            teamItem.setExpanded(false);
            parentItem.getChildren().add(teamItem);
        }
    }

    public void populateProjects(TreeItem<String> parentItem, Long parentId) {
        var projects = projectService.getProjectsByTeamId(parentId);
        for (var project : projects) {
            TreeItem<String> projectItem = new TreeItem<>(project.getName());
            projectItem.setExpanded(false);
            parentItem.getChildren().add(projectItem);
        }
    }

    public void populateComponents(TreeItem<String> parentItem, Long parentId) {
        List<com.projecthub.model.Component> components = componentService.getComponentsByProjectId(parentId);
        for (com.projecthub.model.Component component : components) {
            TreeItem<String> componentItem = new TreeItem<>(component.getName());
            parentItem.getChildren().add(componentItem);
        }
    }
}