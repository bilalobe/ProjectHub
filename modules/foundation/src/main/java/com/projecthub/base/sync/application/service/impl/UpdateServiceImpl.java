package com.projecthub.base.sync.application.service.impl;

import com.projecthub.base.shared.domain.entity.BaseEntity;
import com.projecthub.base.shared.exception.SynchronizationException;
import com.projecthub.base.sync.application.service.LocalDataService;
import com.projecthub.base.sync.application.service.RemoteDataService;
import com.projecthub.base.sync.application.service.UpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UpdateServiceImpl implements UpdateService {
    private static final Logger logger = LoggerFactory.getLogger(UpdateServiceImpl.class);

    private final LocalDataService localDataService;
    private final RemoteDataService remoteDataService;

    public UpdateServiceImpl(LocalDataService localDataService, RemoteDataService remoteDataService) {
        this.localDataService = localDataService;
        this.remoteDataService = remoteDataService;
    }

    @Override
    @Transactional
    public <T extends BaseEntity> void updateBothStores(List<T> mergedData, Class<T> entityClass) {
        try {
            logger.debug("Updating local store with {} entities of type {}", mergedData.size(), entityClass.getSimpleName());
            localDataService.clearLocalData(entityClass);
            localDataService.saveLocalData(mergedData);

            logger.debug("Updating remote store with {} entities", mergedData.size());
            remoteDataService.saveRemoteData(mergedData);
        } catch (Exception e) {
            String errorMessage = String.format("Failed to update stores for entity type %s", entityClass.getSimpleName());
            logger.error(errorMessage, e);
            throw new SynchronizationException(errorMessage, e);
        }
    }
}
