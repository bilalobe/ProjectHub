package com.projecthub.base.shared.middleware.listener;

import com.projecthub.base.project.domain.entity.Project;
import org.springframework.stereotype.Component;


@Component
public class ProjectRepositoryEventListener extends AbstractRepositoryEventListener<Project> {

    @Override
    protected void onBeforeCreate(final Project project) {
        // Custom logic before creating a project
        System.out.println("Before creating project: " + project.getName());
    }

    @Override
    protected void onBeforeSave(final Project project) {
        // Custom logic before saving a project
        System.out.println("Before saving project: " + project.getName());
    }
}
