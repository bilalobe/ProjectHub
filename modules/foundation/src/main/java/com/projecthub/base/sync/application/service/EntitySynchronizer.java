package com.projecthub.base.sync.application.service;

public interface EntitySynchronizer<T> {
    void synchronize();

    Class<T> getEntityType();

    String getEntityName();
}
