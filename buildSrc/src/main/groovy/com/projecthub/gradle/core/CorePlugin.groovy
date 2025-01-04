package com.projecthub.gradle.core

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.GradleException
import org.gradle.api.plugins.JavaPlugin
import org.springframework.boot.gradle.plugin.SpringBootPlugin

class CorePlugin implements Plugin<Project> {
    void apply(Project project) {
        // Apply core plugins
        project.plugins.apply(JavaPlugin)
        
        // Load dependencies and configurations
        applyDependencies(project)
        configureSpringBoot(project)
    }
    
    private void configureSpringBoot(Project project) {
        project.plugins.apply(SpringBootPlugin)
        
        project.tasks.named('bootRun') {
            mainClass = 'com.projecthub.core.CoreApplication'
            systemProperty('spring.profiles.active', 'dev')
            jvmArgs = ['-Xmx1024m', '--enable-preview']
        }
        
        project.dependencies {
            implementation(platform(project.dependencies.create(SpringBootPlugin.BOM_COORDINATES)))
        }
    }
    
    private void applyDependencies(Project project) {
        def dependenciesFile = new File(project.rootDir, "buildSrc/src/main/groovy/dependencies.gradle")
        if (!dependenciesFile.exists()) {
            throw new GradleException("dependencies.gradle not found at ${dependenciesFile.absolutePath}")
        }
        project.apply from: dependenciesFile.absolutePath
    }
}
