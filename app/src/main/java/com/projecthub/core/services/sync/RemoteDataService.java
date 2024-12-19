package com.projecthub.core.services.sync;

import java.util.List;

public interface RemoteDataService {
    <T> List<T> getRemoteData(Class<T> entityClass);

    <T> void saveRemoteData(List<T> entities);
}