package com.projecthub.core.services.sync.impl;

import com.projecthub.core.models.AppUser;
import com.projecthub.core.services.sync.LocalDataService;
import com.projecthub.core.services.sync.RemoteDataService;
import org.springframework.stereotype.Component;

@Component
public class UserSynchronizer extends BaseSynchronizer<AppUser> {

    public UserSynchronizer(LocalDataService localDataService,
                            RemoteDataService remoteDataService) {
        super(localDataService, remoteDataService);
    }

    @Override
    public Class<AppUser> getEntityType() {
        return AppUser.class;
    }

    @Override
    public String getEntityName() {
        return "users";
    }
}
