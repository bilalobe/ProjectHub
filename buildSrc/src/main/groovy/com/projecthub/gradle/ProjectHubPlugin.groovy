package com.projecthub.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.GradleException

class ProjectHubPlugin implements Plugin<Project> {
    void apply(Project project) {
        // Apply required plugins
        applyPlugins(project)
        
        // Apply core configurations
        applyConfigurations(project)
        
        // Configure DGS
        configureDgs(project)
        
        // Configure boot tasks
        configureBootTasks(project)
    }
    
    private void applyPlugins(Project project) {
        project.plugins.with {
            apply('org.beryx.jlink')
            apply('org.springframework.boot')
            apply('io.spring.dependency-management')
            apply('com.netflix.dgs.codegen')
            apply('java-library')
        }
    }
    
    private void applyConfigurations(Project project) {
        // Load dependencies first to ensure versions are available
        loadDependencies(project)
        
        // Apply configurations
        project.apply from: "${project.rootProject.projectDir}/buildSrc/src/main/groovy/modulith.gradle"
    }
    
    private void configureDgs(Project project) {
        project.generateJava {
            schemaPaths = ["${project.projectDir}/src/main/resources/schema/"]
            packageName = 'com.projecthub.core.graphql.generated'
            generateClient = true
        }
    }
    
    private void configureBootTasks(Project project) {
        project.bootRun {
            mainClass = 'com.projecthub.core.CoreApplication'
            systemProperty 'spring.profiles.active', 'dev'
            jvmArgs = ['-Xmx1024m']
        }

        project.tasks.register('startWeb') {
            group = 'application'
            description = 'Starts the web application'
            dependsOn 'bootRun'
        }
    }
    
    private void loadDependencies(Project project) {
        def dependenciesFile = new File("${project.rootProject.projectDir}/buildSrc/src/main/groovy/dependencies.gradle")
        if (!dependenciesFile.exists()) {
            throw new GradleException("dependencies.gradle not found")
        }
        project.apply from: dependenciesFile.absolutePath
    }
}