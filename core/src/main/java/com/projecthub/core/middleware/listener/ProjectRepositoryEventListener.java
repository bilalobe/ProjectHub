package com.projecthub.core.middleware.listener;

import com.projecthub.core.models.Project;
import org.springframework.stereotype.Component;


@Component
public class ProjectRepositoryEventListener extends AbstractRepositoryEventListener<Project> {

    @Override
    protected void onBeforeCreate(Project project) {
        // Custom logic before creating a project
        System.out.println("Before creating project: " + project.getName());
    }

    @Override
    protected void onBeforeSave(Project project) {
        // Custom logic before saving a project
        System.out.println("Before saving project: " + project.getName());
    }
}