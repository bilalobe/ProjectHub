package com.projecthub.core.services.sync;

import java.util.List;

public interface LocalDataService {
    <T> List<T> getLocalData(Class<T> entityClass);

    <T> void saveLocalData(List<T> entities);

    void clearLocalData(Class<?> entityClass);
}