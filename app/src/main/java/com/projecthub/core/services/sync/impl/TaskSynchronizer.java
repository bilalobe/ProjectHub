package com.projecthub.core.services.sync.impl;

import com.projecthub.core.models.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskSynchronizer extends BaseSynchronizer<Task> {

    public TaskSynchronizer(LocalDataService localDataService,
                            RemoteDataService remoteDataService) {
        super(localDataService, remoteDataService);
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
