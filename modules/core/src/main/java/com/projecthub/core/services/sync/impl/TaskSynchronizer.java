package com.projecthub.core.services.sync.impl;

import com.projecthub.core.models.Task;
import com.projecthub.core.services.sync.LocalDataService;
import com.projecthub.core.services.sync.RemoteDataService;
import com.projecthub.core.services.sync.UpdateService;
import org.springframework.stereotype.Component;

@Component
public class TaskSynchronizer extends BaseSynchronizer<Task> {

    public TaskSynchronizer(LocalDataService localDataService,
                            RemoteDataService remoteDataService,
                            UpdateService updateService) {
        super(localDataService, remoteDataService, updateService);
    }

    @Override
    public Class<Task> getEntityType() {
        return Task.class;
    }

    @Override
    public String getEntityName() {
        return "tasks";
    }
}
