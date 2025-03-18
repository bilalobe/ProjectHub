package com.projecthub.base.sync.application.service.impl;

import com.projecthub.base.cohort.domain.entity.Cohort;
import com.projecthub.base.sync.application.service.LocalDataService;
import com.projecthub.base.sync.application.service.RemoteDataService;
import com.projecthub.base.sync.application.service.UpdateService;
import org.springframework.stereotype.Component;

@Component
public class CohortSynchronizer extends BaseSynchronizer<Cohort> {

    public CohortSynchronizer(final LocalDataService localDataService,
                            final RemoteDataService remoteDataService,
                            final UpdateService updateService) {
        super(localDataService, remoteDataService, updateService);
    }

    @Override
    public Class<Cohort> getEntityType() {
        return Cohort.class;
    }

    @Override
    public String getEntityName() {
        return "cohorts";
    }
}