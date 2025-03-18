package com.projecthub.base.shared.infrastructure.audit;

import java.time.LocalDateTime;

/**
 * Interface for entities that need to track creation and modification timestamps.
 * This is a cross-cutting concern used across different domain entities.
 */
public interface Auditable {
    LocalDateTime getCreatedDate();

    void setCreatedDate(LocalDateTime createdDate);

    LocalDateTime getLastModifiedDate();

    void setLastModifiedDate(LocalDateTime lastModifiedDate);
}