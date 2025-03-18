package com.projecthub.base.sync.application.service.impl;

import com.projecthub.base.sync.application.service.LocalDataService;
import com.projecthub.base.sync.application.service.RemoteDataService;
import com.projecthub.base.sync.application.service.UpdateService;
import com.projecthub.base.user.domain.entity.AppUser;
import org.springframework.stereotype.Component;

@Component
public class UserSynchronizer extends BaseSynchronizer<AppUser> {

    public UserSynchronizer(final LocalDataService localDataService,
                            final RemoteDataService remoteDataService,
                            final UpdateService updateService) {
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
