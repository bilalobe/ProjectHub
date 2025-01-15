package com.projecthub.base.sync.application.service.impl;

import com.projecthub.base.shared.domain.enums.sync.SyncState;
import com.projecthub.base.sync.api.dto.SyncStatus;
import com.projecthub.base.sync.application.service.SyncStatusTracker;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class SyncStatusTrackerImpl implements SyncStatusTracker {
    private final AtomicReference<SyncStatus> currentStatus = new AtomicReference<>(new SyncStatus(null, null, null, SyncState.IDLE));

    @Override
    public void startSync() {
        final SyncStatus status = this.currentStatus.get().withStartTime(LocalDateTime.now()).withSyncing(true);
        this.currentStatus.set(status);
    }

    @Override
    public void syncCompleted() {
        final SyncStatus status = this.currentStatus.get()
            .withEndTime(LocalDateTime.now())
            .withSyncing(false)
            .withLastSuccessful(LocalDateTime.now())
            .withError(null);
        this.currentStatus.set(status);
    }

    @Override
    public void syncFailed(final Exception e) {
        final SyncStatus status = this.currentStatus.get()
            .withEndTime(LocalDateTime.now())
            .withSyncing(false)
            .withError(e.getMessage());
        this.currentStatus.set(status);
    }

    @Override
    public SyncStatus getCurrentStatus() {
        return this.currentStatus.get();
    }
}
