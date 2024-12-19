package com.projecthub.core.services.sync.impl;

import com.projecthub.core.exceptions.SynchronizationException;
import com.projecthub.core.models.BaseEntity;
import com.projecthub.core.services.sync.EntitySynchronizer;
import com.projecthub.core.services.sync.LocalDataService;
import com.projecthub.core.services.sync.RemoteDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;

public abstract class BaseSynchronizer<T extends BaseEntity> implements EntitySynchronizer<T> {
    private static final Logger logger = LoggerFactory.getLogger(BaseSynchronizer.class);

    protected final LocalDataService localDataService;
    protected final RemoteDataService remoteDataService;

    protected BaseSynchronizer(LocalDataService localDataService, RemoteDataService remoteDataService) {
        this.localDataService = localDataService;
        this.remoteDataService = remoteDataService;
    }

    @Override
    public void synchronize() {
        try {
            List<T> localData = localDataService.getLocalData(getEntityType());
            List<T> remoteData = remoteDataService.getRemoteData(getEntityType());
            List<T> mergedData = merge(localData, remoteData);
            updateBothStores(mergedData);
            logger.debug("Synchronized {} entities", getEntityName());
        } catch (Exception e) {
            logger.error("Failed to sync {} entities", getEntityName(), e);
            throw new SynchronizationException("Failed to sync " + getEntityName(), e);
        }
    }

    protected void updateBothStores(List<T> mergedData) {
        localDataService.saveLocalData(mergedData);
        remoteDataService.saveRemoteData(mergedData);
    }

    protected List<T> merge(List<T> local, List<T> remote) {
        Map<UUID, T> mergedMap = new HashMap<>();

        // Add all remote entries as base
        remote.forEach(item -> mergedMap.put(item.getId(), item));

        // Merge local entries, preferring newer versions based on lastModifiedDate
        local.forEach(item -> {
            UUID id = item.getId();
            T existingItem = mergedMap.get(id);

            if (existingItem == null || item.getLastModifiedDate().orElse(LocalDateTime.MIN).isAfter(existingItem.getLastModifiedDate().orElse(LocalDateTime.MIN))) {
                mergedMap.put(id, item);
            }
        });

        return new ArrayList<>(mergedMap.values());
    }
}
