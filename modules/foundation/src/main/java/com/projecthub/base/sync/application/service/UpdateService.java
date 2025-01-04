package com.projecthub.base.sync.application.service;

import com.projecthub.base.shared.domain.entity.BaseEntity;

import java.util.List;

/**
 * Service interface for updating both local and remote data stores during synchronization.
 *
 * @param <T> the type of entity extending BaseEntity
 */
public interface UpdateService {
    /**
     * Updates both local and remote data stores with the merged data.
     *
     * @param <T>         the type of entity extending BaseEntity
     * @param mergedData  the list of merged entities to update
     * @param entityClass the class of the entity type
     * @throws SynchronizationException if update fails
     */
    <T extends BaseEntity> void updateBothStores(List<T> mergedData, Class<T> entityClass);
}
