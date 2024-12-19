package com.projecthub.core.models;

import java.time.LocalDateTime;

public class SyncStatus {
    private LocalDateTime lastSyncAttempt;
    private LocalDateTime lastSuccessfulSync;
    private String lastError;
    private SyncState state;

    public LocalDateTime getLastSyncAttempt() {
        return lastSyncAttempt;
    }

    public void setLastSyncAttempt(LocalDateTime lastSyncAttempt) {
        this.lastSyncAttempt = lastSyncAttempt;
    }

    public LocalDateTime getLastSuccessfulSync() {
        return lastSuccessfulSync;
    }

    public void setLastSuccessfulSync(LocalDateTime lastSuccessfulSync) {
        this.lastSuccessfulSync = lastSuccessfulSync;
    }

    public String getLastError() {
        return lastError;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }

    public SyncState getState() {
        return state;
    }

    public void setState(SyncState state) {
        this.state = state;
    }

    public void setStartTime(LocalDateTime now) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setStartTime'");
    }

    public void setSyncing(boolean b) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setSyncing'");
    }

    public void setEndTime(LocalDateTime now) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setEndTime'");
    }

    public void setLastSuccessful(LocalDateTime now) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setLastSuccessful'");
    }

    public void setError(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setError'");
    }

    public enum SyncState {
        IDLE, IN_PROGRESS, FAILED, COMPLETED
    }
}