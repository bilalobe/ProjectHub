package com.projecthub.base.sync.application.service;

import java.util.List;

public interface LocalDataService {
    <T> List<T> getLocalData(Class<T> entityClass);

    <T> void saveLocalData(List<T> entities);

    void clearLocalData(Class<?> entityClass);
}
