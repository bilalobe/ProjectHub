package com.projecthub.core.dto;

import com.projecthub.core.enums.SyncState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
/*
 * Represents the synchronization status of a remote system.
 */
public class SyncStatus {
    private LocalDateTime lastSyncAttempt;
    private LocalDateTime lastSuccessfulSync;
    private String lastError;
    private SyncState state;

    public void setStartTime(LocalDateTime now) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setStartTime'");
    }

    public void setEndTime(LocalDateTime now) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setEndTime'");
    }

    public void setLastSuccessful(LocalDateTime now) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setLastSuccessful'");
    }

    public void setSyncing(boolean b) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setSyncing'");
    }

    public void setError(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setError'");
    }

}