package com.projecthub.base.project.infrastructure.cache;

import com.projecthub.base.project.api.dto.ProjectDTO;
import com.projecthub.base.project.config.ProjectConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectCacheService {
    
    private static final String PROJECT_CACHE = "projects";
    private final CacheManager cacheManager;
    private final ProjectConfig projectConfig;

    @Cacheable(value = PROJECT_CACHE, key = "#id", unless = "#result == null")
    public Optional<ProjectDTO> getCachedProject(UUID id) {
        if (!projectConfig.getCache().isEnabled()) {
            return Optional.empty();
        }
        
        return Optional.ofNullable(cacheManager.getCache(PROJECT_CACHE))
            .map(cache -> cache.get(id, ProjectDTO.class));
    }

    public void cacheProject(ProjectDTO project) {
        if (projectConfig.getCache().isEnabled()) {
            Optional.ofNullable(cacheManager.getCache(PROJECT_CACHE))
                .ifPresent(cache -> cache.put(project.id(), project));
        }
    }

    public void evictProject(UUID id) {
        Optional.ofNullable(cacheManager.getCache(PROJECT_CACHE))
            .ifPresent(cache -> cache.evict(id));
    }

    public void evictAllProjects() {
        Optional.ofNullable(cacheManager.getCache(PROJECT_CACHE))
            .ifPresent(cache -> cache.clear());
    }
}