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

    protected BaseSynchronizer(final LocalDataService localDataService, final RemoteDataService remoteDataService, final UpdateService updateService) {
        this.localDataService = localDataService;
        this.remoteDataService = remoteDataService;
        this.updateService = updateService;
    }

    @Override
    @Transactional
    @Retryable(maxAttempts = BaseSynchronizer.MAX_RETRY_ATTEMPTS, backoff = @Backoff(delay = 1000), retryFor = SynchronizationException.class)
    public void synchronize() {
        try {
            final List<T> localData = this.fetchLocalData();
            final List<T> remoteData = this.fetchRemoteData();
            final List<T> mergedData = this.merge(localData, remoteData);
            this.updateService.updateBothStores(mergedData, this.getEntityType());
            this.logSyncSuccess();
        } catch (final Exception e) {
            this.handleSyncError(e);
        }
    }

    private List<T> fetchLocalData() {
        try {
            return this.localDataService.getLocalData(this.getEntityType());
        } catch (final Exception e) {
            throw new SynchronizationException("Failed to fetch local " + this.getEntityName(), e);
        }
    }

    private List<T> fetchRemoteData() {
        try {
            return this.remoteDataService.getRemoteData(this.getEntityType());
        } catch (final Exception e) {
            throw new SynchronizationException("Failed to fetch remote " + this.getEntityName(), e);
        }
    }

    protected List<T> merge(final List<T> local, final List<T> remote) {
        final Map<UUID, T> mergedMap = new HashMap<>();
        final Map<UUID, LocalDateTime> lastModifiedMap = new HashMap<>();

        // Process remote entries first
        remote.forEach(item -> {
            mergedMap.put(item.getId(), item);
            lastModifiedMap.put(item.getId(), item.getLastModifiedDate());
        });

        // Merge local entries with conflict resolution
        local.forEach(item -> {
            final UUID id = item.getId();
            final LocalDateTime localModified = item.getLastModifiedDate();
            final LocalDateTime remoteModified = lastModifiedMap.get(id);

            if (this.shouldUseLocalVersion(localModified, remoteModified)) {
                mergedMap.put(id, item);
            }
        });

        return new ArrayList<>(mergedMap.values());
    }

    private boolean shouldUseLocalVersion(final LocalDateTime localModified, final LocalDateTime remoteModified) {
        if (null == localModified) return false;
        if (null == remoteModified) return true;
        return localModified.isAfter(remoteModified);
    }

    private void logSyncSuccess() {
        BaseSynchronizer.logger.debug("Successfully synchronized {} entities", this.getEntityName());
    }

    private void handleSyncError(final Exception e) {
        final String errorMessage = String.format("Failed to sync %s entities", this.getEntityName());
        BaseSynchronizer.logger.error(errorMessage, e);
        throw new SynchronizationException(errorMessage, e);
    }
}
