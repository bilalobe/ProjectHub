package com.projecthub.gradle.cache

import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test

@groovy.transform.CompileStatic(groovy.transform.TypeCheckingMode.SKIP)
class TaskCacheConfig {
    private final Project project
    
    TaskCacheConfig(Project project) {
        this.project = project
    }
    
    void configure() {
        configureCompileTasks()
        configureTestTasks()
        configureGeneralTasks()
    }
    
    private void configureCompileTasks() {
        project.tasks.withType(JavaCompile).configureEach { task ->
            task.outputs.cacheIf { true }
            task.options.with {
                incremental = true
                fork = true
                forkOptions.jvmArgs += '--enable-preview'
            }
        }
    }
    
    private void configureTestTasks() {
        project.tasks.withType(Test).configureEach { task ->
            task.outputs.cacheIf { true }
            task.maxParallelForks = Runtime.runtime.availableProcessors().intdiv(2) ?: 1
            task.failFast = false
            
            // Configure test JVM args for modern Java
            task.jvmArgs += [
                '--enable-preview',
                '--add-opens=java.base/java.lang=ALL-UNNAMED',
                '--add-opens=java.base/java.util=ALL-UNNAMED'
            ]
        }
    }
    
    private void configureGeneralTasks() {
        project.tasks.configureEach { task ->
            if (!task.name.startsWith('clean') && 
                !task.name.contains('publish') && 
                !task.name.contains('upload')) {
                task.outputs.cacheIf { true }
            }
        }
    }
}