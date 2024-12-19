package com.projecthub.core.services.sync.impl;

import com.projecthub.core.models.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectSynchronizer extends BaseSynchronizer<Project> {

    public ProjectSynchronizer(LocalDataService localDataService,
                               RemoteDataService remoteDataService) {
        super(localDataService, remoteDataService);
    }

    @Override
    public Class<Project> getEntityType() {
        return Project.class;
    }

    @Override
    public String getEntityName() {
        return "projects";
    }
}
