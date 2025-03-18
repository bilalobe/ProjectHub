package com.projecthub.base.sync.application.service;

import com.projecthub.base.shared.exception.SynchronizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SyncService {
    private static final Logger logger = LoggerFactory.getLogger(SyncService.class);

    private final SyncStatusTracker syncStatusTracker;
    private final NetworkStatusChecker networkChecker;
    private final List<EntitySynchronizer<?>> synchronizers;

    @Autowired
    public SyncService(
        final SyncStatusTracker syncStatusTracker,
        final NetworkStatusChecker networkChecker,
        final List<EntitySynchronizer<?>> synchronizers) {
        this.syncStatusTracker = syncStatusTracker;
        this.networkChecker = networkChecker;
        this.synchronizers = synchronizers;
    }

    @Transactional
    @Scheduled(fixedDelay = 300000L) // 5 minutes
    public void synchronizeData() {
        if (!this.networkChecker.isNetworkAvailable()) {
            SyncService.logger.info("Network unavailable, skipping sync");
            return;
        }

        try {
            this.syncStatusTracker.startSync();
            this.synchronizeEntities();
            this.syncStatusTracker.syncCompleted();
            SyncService.logger.info("Synchronization completed successfully");
        } catch (final RuntimeException e) {
            final String errorMessage = "Synchronization failed unexpectedly";
            SyncService.logger.error(errorMessage, e);
            this.syncStatusTracker.syncFailed(e);
            throw new SynchronizationException(errorMessage, e);
        }
    }

    private void synchronizeEntities() {
        this.synchronizers.forEach(synchronizer -> {
            try {
                synchronizer.synchronize();
            } catch (final RuntimeException e) {
                final String errorMessage = String.format("Failed to synchronize entity type: %s",
                    synchronizer.getEntityName());
                SyncService.logger.error(errorMessage, e);
                throw new SynchronizationException(errorMessage, e);
            }
        });
    }
}
