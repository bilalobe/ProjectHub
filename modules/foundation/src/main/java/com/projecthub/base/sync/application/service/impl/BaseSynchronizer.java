package com.projecthub.base.sync.application.service.impl;

import com.projecthub.base.shared.domain.entity.BaseEntity;
import com.projecthub.base.shared.exception.SynchronizationException;
import com.projecthub.base.sync.application.service.EntitySynchronizer;
import com.projecthub.base.sync.application.service.LocalDataService;
import com.projecthub.base.sync.application.service.RemoteDataService;
import com.projecthub.base.sync.application.service.UpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

public abstract class BaseSynchronizer<T extends BaseEntity> implements EntitySynchronizer<T> {
    private static final Logger logger = LoggerFactory.getLogger(BaseSynchronizer.class);
    private static final int MAX_RETRY_ATTEMPTS = 3;

    protected final LocalDataService localDataService;
    protected final RemoteDataService remoteDataService;
    protected final UpdateService updateService;

    protected BaseSynchronizer(LocalDataService localDataService, RemoteDataService remoteDataService, UpdateService updateService) {
        this.localDataService = localDataService;
        this.remoteDataService = remoteDataService;
        this.updateService = updateService;
    }

    @Override
    @Transactional
    @Retryable(
        maxAttempts = MAX_RETRY_ATTEMPTS,
        backoff = @Backoff(delay = 1000),
        retryFor = {SynchronizationException.class}
    )
    public void synchronize() {
        try {
            List<T> localData = fetchLocalData();
            List<T> remoteData = fetchRemoteData();
            List<T> mergedData = merge(localData, remoteData);
            updateService.updateBothStores(mergedData, getEntityType());
            logSyncSuccess();
        } catch (Exception e) {
            handleSyncError(e);
        }
    }

    private List<T> fetchLocalData() {
        try {
            return localDataService.getLocalData(getEntityType());
        } catch (Exception e) {
            throw new SynchronizationException("Failed to fetch local " + getEntityName(), e);
        }
    }

    private List<T> fetchRemoteData() {
        try {
            return remoteDataService.getRemoteData(getEntityType());
        } catch (Exception e) {
            throw new SynchronizationException("Failed to fetch remote " + getEntityName(), e);
        }
    }

    protected List<T> merge(List<T> local, List<T> remote) {
        Map<UUID, T> mergedMap = new HashMap<>();
        Map<UUID, LocalDateTime> lastModifiedMap = new HashMap<>();

        // Process remote entries first
        remote.forEach(item -> {
            mergedMap.put(item.getId(), item);
            lastModifiedMap.put(item.getId(), item.getLastModifiedDate());
        });

        // Merge local entries with conflict resolution
        local.forEach(item -> {
            UUID id = item.getId();
            LocalDateTime localModified = item.getLastModifiedDate();
            LocalDateTime remoteModified = lastModifiedMap.get(id);

            if (shouldUseLocalVersion(localModified, remoteModified)) {
                mergedMap.put(id, item);
            }
        });

        return new ArrayList<>(mergedMap.values());
    }

    private boolean shouldUseLocalVersion(LocalDateTime localModified, LocalDateTime remoteModified) {
        if (localModified == null) return false;
        if (remoteModified == null) return true;
        return localModified.isAfter(remoteModified);
    }

    private void logSyncSuccess() {
        logger.debug("Successfully synchronized {} entities", getEntityName());
    }

    private void handleSyncError(Exception e) {
        String errorMessage = String.format("Failed to sync %s entities", getEntityName());
        logger.error(errorMessage, e);
        throw new SynchronizationException(errorMessage, e);
    }
}
