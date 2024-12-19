package com.projecthub.core.middleware.listener;

import com.projecthub.core.models.Project;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class ProjectEntityListener {

    @PrePersist
    @PreUpdate
    public void beforeSave(Project project) {
        // Custom logic before saving the project
        System.out.println("Before saving project: " + project.getName());
    }
}