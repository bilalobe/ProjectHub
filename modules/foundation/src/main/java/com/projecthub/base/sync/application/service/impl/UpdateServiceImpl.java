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

    public UpdateServiceImpl(final LocalDataService localDataService, final RemoteDataService remoteDataService) {
        this.localDataService = localDataService;
        this.remoteDataService = remoteDataService;
    }

    @Override
    @Transactional
    public <T extends BaseEntity> void updateBothStores(final List<T> mergedData, final Class<T> entityClass) {
        try {
            UpdateServiceImpl.logger.debug("Updating local store with {} entities of type {}", mergedData.size(), entityClass.getSimpleName());
            this.localDataService.clearLocalData(entityClass);
            this.localDataService.saveLocalData(mergedData);

            UpdateServiceImpl.logger.debug("Updating remote store with {} entities", mergedData.size());
            this.remoteDataService.saveRemoteData(mergedData);
        } catch (final Exception e) {
            final String errorMessage = String.format("Failed to update stores for entity type %s", entityClass.getSimpleName());
            UpdateServiceImpl.logger.error(errorMessage, e);
            throw new SynchronizationException(errorMessage, e);
        }
    }
}
