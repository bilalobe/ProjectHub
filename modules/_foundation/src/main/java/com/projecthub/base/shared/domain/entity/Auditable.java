package com.projecthub.base.shared.domain.entity;

import java.time.LocalDateTime;

public interface Auditable {
    LocalDateTime getCreatedDate();

    void setCreatedDate(LocalDateTime createdDate);

    LocalDateTime getLastModifiedDate();

    void setLastModifiedDate(LocalDateTime lastModifiedDate);
}
