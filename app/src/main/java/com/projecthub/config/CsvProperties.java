package com.projecthub.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
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

}