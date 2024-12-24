package com.projecthub.core.services.sync.impl;

import com.projecthub.core.dto.SyncStatus;
import com.projecthub.core.enums.SyncState;
import com.projecthub.core.services.sync.SyncStatusTracker;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class SyncStatusTrackerImpl implements SyncStatusTracker {
    private final AtomicReference<SyncStatus> currentStatus = new AtomicReference<>(new SyncStatus(null, null, null, SyncState.IDLE));

    @Override
    public void startSync() {
        SyncStatus status = currentStatus.get().withStartTime(LocalDateTime.now()).withSyncing(true);
        currentStatus.set(status);
    }

    @Override
    public void syncCompleted() {
        SyncStatus status = currentStatus.get()
                .withEndTime(LocalDateTime.now())
                .withSyncing(false)
                .withLastSuccessful(LocalDateTime.now())
                .withError(null);
        currentStatus.set(status);
    }

    @Override
    public void syncFailed(Exception e) {
        SyncStatus status = currentStatus.get()
                .withEndTime(LocalDateTime.now())
                .withSyncing(false)
                .withError(e.getMessage());
        currentStatus.set(status);
    }

    @Override
    public SyncStatus getCurrentStatus() {
        return currentStatus.get();
    }
}