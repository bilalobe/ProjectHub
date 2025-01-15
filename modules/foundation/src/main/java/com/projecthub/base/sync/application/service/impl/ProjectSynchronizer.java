package com.projecthub.base.sync.application.service.impl;

import com.projecthub.base.project.domain.entity.Project;
import com.projecthub.base.sync.application.service.LocalDataService;
import com.projecthub.base.sync.application.service.RemoteDataService;
import com.projecthub.base.sync.application.service.UpdateService;
import org.springframework.stereotype.Component;

@Component
public class ProjectSynchronizer extends BaseSynchronizer<Project> {

    public ProjectSynchronizer(final LocalDataService localDataService,
                               final RemoteDataService remoteDataService,
                               final UpdateService updateService) {
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
