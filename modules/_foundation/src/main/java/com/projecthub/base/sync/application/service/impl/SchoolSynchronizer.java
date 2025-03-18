package com.projecthub.base.sync.application.service.impl;

import com.projecthub.base.school.domain.entity.School;
import com.projecthub.base.sync.application.service.LocalDataService;
import com.projecthub.base.sync.application.service.RemoteDataService;
import com.projecthub.base.sync.application.service.UpdateService;
import org.springframework.stereotype.Component;

@Component
public class SchoolSynchronizer extends BaseSynchronizer<School> {

    public SchoolSynchronizer(final LocalDataService localDataService,
                            final RemoteDataService remoteDataService,
                            final UpdateService updateService) {
        super(localDataService, remoteDataService, updateService);
    }

    @Override
    public Class<School> getEntityType() {
        return School.class;
    }

    @Override
    public String getEntityName() {
        return "schools";
    }
}