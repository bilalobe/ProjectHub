package com.projecthub.base.sync.application.service.impl;

import com.projecthub.base.sync.application.service.LocalDataService;
import com.projecthub.base.sync.application.service.RemoteDataService;
import com.projecthub.base.sync.application.service.UpdateService;
import com.projecthub.base.team.domain.entity.Team;
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
