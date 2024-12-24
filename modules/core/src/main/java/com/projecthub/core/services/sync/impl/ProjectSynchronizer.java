package com.projecthub.core.services.sync.impl;

import com.projecthub.core.models.Project;
import com.projecthub.core.services.sync.LocalDataService;
import com.projecthub.core.services.sync.RemoteDataService;
import com.projecthub.core.services.sync.UpdateService;
import org.springframework.stereotype.Component;

@Component
public class ProjectSynchronizer extends BaseSynchronizer<Project> {

    public ProjectSynchronizer(LocalDataService localDataService,
                               RemoteDataService remoteDataService,
                               UpdateService updateService) {
        super(localDataService, remoteDataService, updateService);
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
