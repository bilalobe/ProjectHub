package com.projecthub.core.services.sync.impl;


import com.projecthub.core.models.Team;
import com.projecthub.core.services.sync.LocalDataService;
import com.projecthub.core.services.sync.RemoteDataService;
import org.springframework.stereotype.Component;

@Component
public class TeamSynchronizer extends BaseSynchronizer<Team> {

    public TeamSynchronizer(LocalDataService localDataService,
                            RemoteDataService remoteDataService) {
        super(localDataService, remoteDataService);
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

public class TeamSynchronizer {

}
