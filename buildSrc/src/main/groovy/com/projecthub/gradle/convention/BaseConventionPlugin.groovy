package com.projecthub.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.GradleException
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.bundling.Jar

class BaseConventionPlugin implements Plugin<Project> {
    void apply(Project project) {
        // Apply core plugins
        project.plugins.apply('java-library')
        project.plugins.apply('distribution')
        
        // Load dependencies
        applyDependencies(project)

        // Configure tasks
        project.tasks.withType(JavaCompile).configureEach {
            
            options.encoding = 'UTF-8'
            options.compilerArgs += '--enable-preview'
        }

        project.tasks.withType(Test).configureEach {
            useJUnitPlatform()
            jvmArgs += '--enable-preview'
        }

        project.tasks.withType(JavaExec).configureEach {
            jvmArgs += '--enable-preview'
        }

        // Configure manifest
        project.tasks.withType(Jar).configureEach { jarTask ->
            jarTask.manifest {
                attributes(
                    'Implementation-Title': project.name,
                    'Implementation-Version': project.version,
                    'Created-By': "${System.getProperty('java.version')} (${System.getProperty('java.vendor')})",
                    'Build-Timestamp': new Date().format("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                )
            }
            jarTask.setDuplicatesStrategy(org.gradle.api.file.DuplicatesStrategy.EXCLUDE)
        }

        configureSharedDependencies(project)
    }
    
    private void applyDependencies(Project project) {
        def dependenciesFile = new File("${project.rootProject.projectDir}/buildSrc/src/main/groovy/dependencies.gradle")
        if (dependenciesFile.exists()) {
            project.apply from: dependenciesFile.absolutePath
            project.ext.versions = project.rootProject.ext.versions
        } else {
            throw new GradleException("dependencies.gradle not found")
        }
    }

    private void configureSharedDependencies(Project project) {
        project.dependencies {
            // Spring Boot Platform
            implementation platform("org.springframework.boot:spring-boot-dependencies:${project.ext.versions.springBoot}")
            
            // Modulith Core
            implementation "org.springframework.modulith:spring-modulith-starter-core:${project.ext.versions.springModulith}"
            implementation "org.springframework.modulith:spring-modulith-api:${project.ext.versions.springModulith}"
            implementation "org.springframework.modulith:spring-modulith-events-core:${project.ext.versions.springModulith}"
            
            // Common Tools
            implementation "org.mapstruct:mapstruct:${project.ext.versions.mapstruct}"
            compileOnly "org.projectlombok:lombok:${project.ext.versions.lombok}"
            annotationProcessor "org.projectlombok:lombok:${project.ext.versions.lombok}"
            annotationProcessor "org.mapstruct:mapstruct-processor:${project.ext.versions.mapstruct}"
            
            // Testing
            testImplementation "org.springframework.boot:spring-boot-starter-test:${project.ext.versions.springBoot}"
            testImplementation "org.springframework.modulith:spring-modulith-test:${project.ext.versions.springModulith}"
            testImplementation "org.springframework.modulith:spring-modulith-docs:${project.ext.versions.springModulith}"
        }
    }
}
