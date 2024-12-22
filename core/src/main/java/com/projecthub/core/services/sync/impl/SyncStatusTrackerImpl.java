package com.projecthub.core.services.sync.impl;

import com.projecthub.core.dto.SyncStatus;
import com.projecthub.core.services.sync.SyncStatusTracker;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class SyncStatusTrackerImpl implements SyncStatusTracker {
    private final AtomicReference<SyncStatus> currentStatus = new AtomicReference<>(new SyncStatus());

    @Override
    public void startSync() {
        SyncStatus status = new SyncStatus();
        status.setStartTime(LocalDateTime.now());
        status.setSyncing(true);
        currentStatus.set(status);
    }

    @Override
    public void syncCompleted() {
        SyncStatus status = currentStatus.get();
        status.setEndTime(LocalDateTime.now());
        status.setSyncing(false);
        status.setLastSuccessful(LocalDateTime.now());
        status.setError(null);
    }

    @Override
    public void syncFailed(Exception e) {
        SyncStatus status = currentStatus.get();
        status.setEndTime(LocalDateTime.now());
        status.setSyncing(false);
        status.setError(e.getMessage());
    }

    @Override
    public SyncStatus getCurrentStatus() {
        return currentStatus.get();
    }
}
