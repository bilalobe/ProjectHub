package com.projecthub.base.shared.domain.enums.sync;


/**
 * Represents the synchronization state of an operation.
 */
public enum SyncState {
    /**
     * No synchronization in progress
     */
    IDLE,
    /**
     * Synchronization is currently running
     */
    IN_PROGRESS,
    /**
     * Synchronization failed to complete
     */
    FAILED,
    /**
     * Synchronization process has completed
     */
    COMPLETED,
    /**
     * Synchronization completed successfully
     */
    SUCCESS
}
