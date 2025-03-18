package com.projecthub.gradle.core

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test

class CorePlugin implements Plugin<Project> {
    void apply(Project project) {
        // Apply core plugins
        project.plugins.apply(JavaPlugin)
        
        // Configure core tasks
        configureTasks(project)
        configureSpringBoot(project)
        
        // Load dependencies and configurations
        applyDependencies(project)
    }

    private void configureTasks(Project project) {
        // Configure Java compilation
        project.tasks.withType(JavaCompile).configureEach { task ->
            task.options.with {
                encoding = 'UTF-8'
                compilerArgs += '--enable-preview'
                fork = true
                forkOptions.jvmArgs += '--enable-preview'
            }
            
            // Enable incremental compilation
            task.options.incremental = true
            
            // Configure outputs caching
            task.outputs.cacheIf { true }
        }
        
        // Configure test execution
        project.tasks.withType(Test).configureEach { task ->
            task.useJUnitPlatform()
            task.jvmArgs += '--enable-preview'
            
            // Configure worker execution
            task.maxParallelForks = Runtime.runtime.availableProcessors().intdiv(2) ?: 1
            task.failFast = false
            
            // Enable test output caching
            task.outputs.cacheIf { true }
        }
    }

    private void configureSpringBoot(Project project) {
        // Apply Spring Boot plugin
        project.plugins.apply('org.springframework.boot')

        project.tasks.named('bootRun').configure { task ->
            task.mainClass = 'com.projecthub.core.CoreApplication'
            task.systemProperty('spring.profiles.active', 'dev')
            task.jvmArgs = ['-Xmx1024m', '--enable-preview']
            
            // Configure worker execution for bootRun
            task.systemProperty('spring.task.execution.pool.core-size', 
                Runtime.runtime.availableProcessors().toString())
                
            // Enable output caching where applicable
            task.outputs.cacheIf { false } // Disable caching for run tasks
        }

        project.dependencies {
            // Use string coordinates with project version
            implementation(platform("org.springframework.boot:spring-boot-dependencies:${project.ext.versions.springBoot}"))
        }
    }

    private static void applyDependencies(Project project) {
        def dependenciesFile = new File(project.rootDir, "buildSrc/src/main/groovy/dependencies.gradle")
        if (!dependenciesFile.exists()) {
            throw new GradleException("dependencies.gradle not found at ${dependenciesFile.absolutePath}")
        }
        project.apply from: dependenciesFile.absolutePath
    }
}
