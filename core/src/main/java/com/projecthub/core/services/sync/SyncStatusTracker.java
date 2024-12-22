package com.projecthub.core.services.sync;

import com.projecthub.core.dto.SyncStatus;

public interface SyncStatusTracker {
    void startSync();

    void syncCompleted();

    void syncFailed(Exception e);

    SyncStatus getCurrentStatus();
}