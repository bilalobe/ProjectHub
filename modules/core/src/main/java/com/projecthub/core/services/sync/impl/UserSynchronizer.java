package com.projecthub.core.services.sync.impl;

import com.projecthub.core.entities.AppUser;
import com.projecthub.core.services.sync.UpdateService;
import com.projecthub.core.services.sync.LocalDataService;
import com.projecthub.core.services.sync.RemoteDataService;
import org.springframework.stereotype.Component;

@Component
public class UserSynchronizer extends BaseSynchronizer<AppUser> {

    public UserSynchronizer(LocalDataService localDataService,
                            RemoteDataService remoteDataService,
                            UpdateService updateService) {
        super(localDataService, remoteDataService, updateService);
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
