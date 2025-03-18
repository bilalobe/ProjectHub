package com.projecthub.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.caching.configuration.BuildCacheConfiguration

/**
 * Configures build cache settings for the ProjectHub project.
 * Updated to be compatible with Gradle 8.x Worker API.
 */
class BuildCacheConventionPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        // Only configure build cache at the root project level
        if (project != project.rootProject) {
            return
        }

        configureBuildCache(project.gradle.buildCache)
        configureTaskCaching(project)

        // Apply task-specific caching to all projects
        project.allprojects { p ->
            configureTaskCaching(p)
        }
    }

    private void configureTaskCaching(Project project) {
        project.tasks.withType(JavaCompile).configureEach { task ->
            task.outputs.cacheIf { true }
            task.options.incremental = true
        }
        
        project.tasks.withType(Test).configureEach { task ->
            task.outputs.cacheIf { true }
            task.maxParallelForks = Runtime.runtime.availableProcessors().intdiv(2) ?: 1
        }

        project.tasks.configureEach { task ->
            if (!task.name.startsWith('clean') && 
                !task.name.contains('publish') && 
                !task.name.contains('upload')) {
                task.outputs.cacheIf { true }
            }
        }
    }

    private void configureBuildCache(BuildCacheConfiguration buildCache) {
        // Enable local cache
        buildCache.local {
            enabled = true
            directory = "${System.getProperty('user.home')}/.gradle/projecthub-cache"
            removeUnusedEntriesAfterDays = 30
        }

        // Configure remote cache if environment variables are set
        def remoteCacheUrl = System.getenv('GRADLE_REMOTE_CACHE_URL')
        if (remoteCacheUrl) {
            buildCache.remote(org.gradle.caching.http.HttpBuildCache) {
                url = remoteCacheUrl
                enabled = true
                push = true
                
                def username = System.getenv('GRADLE_REMOTE_CACHE_USERNAME')
                def password = System.getenv('GRADLE_REMOTE_CACHE_PASSWORD')
                if (username && password) {
                    credentials {
                        username = username
                        password = password
                    }
                }
            }
        }
    }
}