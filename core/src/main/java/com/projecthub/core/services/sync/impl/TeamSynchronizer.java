package com.projecthub.core.services.sync.impl;

import com.projecthub.core.models.Team;
import com.projecthub.core.services.sync.LocalDataService;
import com.projecthub.core.services.sync.RemoteDataService;
import com.projecthub.core.services.sync.UpdateService;
import org.springframework.stereotype.Component;

@Component
public class TeamSynchronizer extends BaseSynchronizer<Team> {

    public TeamSynchronizer(LocalDataService localDataService,
                            RemoteDataService remoteDataService,
                            UpdateService updateService) {
        super(localDataService, remoteDataService, updateService);
    }

    @Override
    public Class<Team> getEntityType() {
        return Team.class;
    }

    @Override
    public String getEntityName() {
        return "teams";
    }
}
