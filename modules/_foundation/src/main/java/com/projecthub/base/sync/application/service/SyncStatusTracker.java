package com.projecthub.base.sync.application.service;

import com.projecthub.base.sync.api.dto.SyncStatus;

public interface SyncStatusTracker {
    void startSync();

    void syncCompleted();

    void syncFailed(Exception e);

    SyncStatus getCurrentStatus();
}
