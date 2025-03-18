package com.projecthub.base.sync.api.dto;

import com.projecthub.base.shared.domain.enums.sync.SyncState;

import java.time.LocalDateTime;


public record SyncStatus(
    LocalDateTime lastSyncAttempt,
    LocalDateTime lastSuccessfulSync,
    String lastError,
    SyncState state
) {
    public SyncStatus withStartTime(final LocalDateTime now) {
        return new SyncStatus(now, this.lastSuccessfulSync, this.lastError, SyncState.IN_PROGRESS);
    }

    public SyncStatus withEndTime(final LocalDateTime now) {
        return new SyncStatus(this.lastSyncAttempt, this.lastSuccessfulSync, this.lastError, SyncState.COMPLETED);
    }

    public SyncStatus withLastSuccessful(final LocalDateTime now) {
        return new SyncStatus(this.lastSyncAttempt, now, this.lastError, SyncState.SUCCESS);
    }

    public SyncStatus withSyncing(final boolean syncing) {
        return new SyncStatus(this.lastSyncAttempt, this.lastSuccessfulSync, this.lastError, syncing ? SyncState.IN_PROGRESS : SyncState.IDLE);
    }

    public SyncStatus withError(final String error) {
        return new SyncStatus(this.lastSyncAttempt, this.lastSuccessfulSync, error, SyncState.FAILED);
    }
}
