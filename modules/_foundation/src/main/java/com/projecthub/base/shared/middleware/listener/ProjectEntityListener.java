package com.projecthub.base.shared.middleware.listener;

import com.projecthub.base.project.domain.entity.Project;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class ProjectEntityListener {

    public ProjectEntityListener() {
    }

    @PrePersist
    @PreUpdate
    public static void beforeSave(final Project project) {
        // Custom logic before saving the project
        System.out.println("Before saving project: " + project.getName());
    }
}
