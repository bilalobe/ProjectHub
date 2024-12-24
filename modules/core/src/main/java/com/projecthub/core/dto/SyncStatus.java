package com.projecthub.core.dto;

import com.projecthub.core.enums.SyncState;
import java.time.LocalDateTime;

public record SyncStatus(
    LocalDateTime lastSyncAttempt,
    LocalDateTime lastSuccessfulSync,
    String lastError,
    SyncState state
) {
    public SyncStatus withStartTime(LocalDateTime now) {
        return new SyncStatus(now, lastSuccessfulSync, lastError, SyncState.IN_PROGRESS);
    }

    public SyncStatus withEndTime(LocalDateTime now) {
        return new SyncStatus(lastSyncAttempt, lastSuccessfulSync, lastError, SyncState.COMPLETED);
    }

    public SyncStatus withLastSuccessful(LocalDateTime now) {
        return new SyncStatus(lastSyncAttempt, now, lastError, SyncState.SUCCESS);
    }

    public SyncStatus withSyncing(boolean syncing) {
        return new SyncStatus(lastSyncAttempt, lastSuccessfulSync, lastError, syncing ? SyncState.IN_PROGRESS : SyncState.IDLE);
    }

    public SyncStatus withError(String error) {
        return new SyncStatus(lastSyncAttempt, lastSuccessfulSync, error, SyncState.FAILED);
    }
}