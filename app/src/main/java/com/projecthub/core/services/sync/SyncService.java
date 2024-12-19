package com.projecthub.core.services.sync;

import com.projecthub.core.exceptions.SynchronizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

@Service
public class SyncService {
    private static final Logger logger = LoggerFactory.getLogger(SyncService.class);

    private final SyncStatusTracker syncStatusTracker;
    private final NetworkStatusChecker networkChecker;
    private final List<EntitySynchronizer<?>> synchronizers;

    public SyncService(
            SyncStatusTracker syncStatusTracker,
            NetworkStatusChecker networkChecker,
            List<EntitySynchronizer<?>> synchronizers) {
        this.syncStatusTracker = syncStatusTracker;
        this.networkChecker = networkChecker;
        this.synchronizers = synchronizers;
    }

    public SyncService(DataSource dataSource) {
        //TODO Auto-generated constructor stub
    }

    @Transactional
    @Scheduled(fixedDelay = 300000) // 5 minutes
    public void synchronizeData() {
        if (!networkChecker.isNetworkAvailable()) {
            logger.info("Network unavailable, skipping sync");
            return;
        }

        try {
            syncStatusTracker.startSync();
            synchronizeEntities();
            syncStatusTracker.syncCompleted();
            logger.info("Synchronization completed successfully");
        } catch (Exception e) {
            String errorMessage = "Synchronization failed unexpectedly";
            logger.error(errorMessage, e);
            syncStatusTracker.syncFailed(e);
            throw new SynchronizationException(errorMessage, e);
        }
    }

    private void synchronizeEntities() {
        synchronizers.forEach(synchronizer -> {
            try {
                synchronizer.synchronize();
            } catch (Exception e) {
                String errorMessage = String.format("Failed to synchronize entity type: %s",
                        synchronizer.getEntityName());
                logger.error(errorMessage, e);
                throw new SynchronizationException(errorMessage, e);
            }
        });
    }
}