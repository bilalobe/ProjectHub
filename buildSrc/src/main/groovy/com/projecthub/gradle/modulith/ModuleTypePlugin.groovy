package com.projecthub.gradle.modulith

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

abstract class ModuleTypePlugin implements Plugin<Project> {
    void apply(Project project) {
        project.plugins.apply('java-library')
        
        configureModulithTesting(project)
        configureModule(project)
    }
    
    private void configureModulithTesting(Project project) {
        project.dependencies {
            testImplementation "org.springframework.modulith:spring-modulith-test:${project.ext.versions.springModulith}"
            testImplementation "org.springframework.modulith:spring-modulith-docs:${project.ext.versions.springModulith}"
        }
        
        project.tasks.withType(Test).configureEach {
            useJUnitPlatform()
            systemProperty('spring.modulith.docs.main.basePath', project.projectDir)
        }
    }
    
    abstract void configureModule(Project project)
}
