package com.projecthub.base.sync.application.service.impl;

import com.projecthub.base.sync.application.service.LocalDataService;
import com.projecthub.base.sync.application.service.RemoteDataService;
import com.projecthub.base.sync.application.service.UpdateService;
import com.projecthub.base.task.domain.entity.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskSynchronizer extends BaseSynchronizer<Task> {

    public TaskSynchronizer(final LocalDataService localDataService,
                            final RemoteDataService remoteDataService,
                            final UpdateService updateService) {
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
