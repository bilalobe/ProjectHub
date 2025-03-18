package com.projecthub.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project

class WebModuleConventionPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        // Apply Java plugin if not already applied
        if (!project.plugins.hasPlugin('java')) {
            project.plugins.apply('java')
        }
        
        // Apply Spring Boot plugin
        if (!project.plugins.hasPlugin('org.springframework.boot')) {
            project.plugins.apply('org.springframework.boot')
        }
        
        // Apply dependencies using root project extensions
        project.afterEvaluate {
            // Apply base dependencies
            project.rootProject.ext.applyBaseDependencies(project)
            
            // Apply web-specific dependencies
            project.rootProject.ext.applyWebDependencies(project)
        }
        
        // Configure test task
        project.tasks.withType(Test) {
            useJUnitPlatform()
            testLogging {
                events "passed", "skipped", "failed"
            }
        }
    }
}