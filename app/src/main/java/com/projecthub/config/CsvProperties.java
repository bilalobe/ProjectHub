package com.projecthub.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")

public class CsvProperties {

    private String componentsFilepath;
    private String projectsFilepath;
    private String schoolsFilepath;
    private String studentsFilepath;
    private String submissionsFilepath;
    private String tasksFilepath;
    private String teachersFilepath;
    private String teamsFilepath;
    private String cohortsFilepath;
    private String usersFilepath;

    public String getComponentsFilepath() {
        return componentsFilepath;
    }

    public void setComponentsFilepath(String componentsFilepath) {
        this.componentsFilepath = componentsFilepath;
    }

    public String getProjectsFilepath() {
        return projectsFilepath;
    }

    public void setProjectsFilepath(String projectsFilepath) {
        this.projectsFilepath = projectsFilepath;
    }

    public String getSchoolsFilepath() {
        return schoolsFilepath;
    }

    public void setSchoolsFilepath(String schoolsFilepath) {
        this.schoolsFilepath = schoolsFilepath;
    }

    public String getStudentsFilepath() {
        return studentsFilepath;
    }

    public void setStudentsFilepath(String studentsFilepath) {
        this.studentsFilepath = studentsFilepath;
    }

    public String getSubmissionsFilepath() {
        return submissionsFilepath;
    }

    public void setSubmissionsFilepath(String submissionsFilepath) {
        this.submissionsFilepath = submissionsFilepath;
    }

    public String getTasksFilepath() {
        return tasksFilepath;
    }

    public void setTasksFilepath(String tasksFilepath) {
        this.tasksFilepath = tasksFilepath;
    }

    public String getTeachersFilepath() {
        return teachersFilepath;
    }

    public void setTeachersFilepath(String teachersFilepath) {
        this.teachersFilepath = teachersFilepath;
    }

    public String getTeamsFilepath() {
        return teamsFilepath;
    }

    public void setTeamsFilepath(String teamsFilepath) {
        this.teamsFilepath = teamsFilepath;
    }

    public String getCohortsFilepath() {
        return cohortsFilepath;
    }

    public void setCohortsFilepath(String cohortsFilepath) {
        this.cohortsFilepath = cohortsFilepath;
    }

    public String getUsersFilepath() {
        return usersFilepath;
    }
}