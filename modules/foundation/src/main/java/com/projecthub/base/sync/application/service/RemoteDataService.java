package com.projecthub.base.sync.application.service;

import java.util.List;

public interface RemoteDataService {
    <T> List<T> getRemoteData(Class<T> entityClass);

    <T> void saveRemoteData(List<T> entities);
}
