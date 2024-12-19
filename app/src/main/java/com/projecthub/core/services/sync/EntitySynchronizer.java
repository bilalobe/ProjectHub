package com.projecthub.core.services.sync;

public interface EntitySynchronizer<T> {
    void synchronize();

    Class<T> getEntityType();

    String getEntityName();
}
