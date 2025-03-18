package com.projecthub.gradle.convention

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion

class BaseConventionPlugin implements Plugin<Project> {
    void apply(Project project) {
        // Apply core plugins
        project.plugins.apply('java-library')
        project.plugins.apply('distribution')

        // Load dependencies
        applyDependencies(project)

        // Configure Java toolchain
        project.java {
            toolchain {
                languageVersion = JavaLanguageVersion.of(17)
            }
        }

        // Configure compilation tasks
        configureJavaCompilation(project)
        configureTestExecution(project)
        configureJarTasks(project)

        // Configure shared dependencies
        configureSharedDependencies(project)
    }

    private void configureJavaCompilation(Project project) {
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

        project.tasks.withType(JavaExec).configureEach { task ->
            task.jvmArgs += '--enable-preview'
            task.outputs.cacheIf { true }
        }
    }

    private void configureTestExecution(Project project) {
        project.tasks.withType(Test).configureEach { task ->
            task.useJUnitPlatform()
            task.jvmArgs += '--enable-preview'
            
            // Configure test execution
            task.maxParallelForks = Runtime.runtime.availableProcessors().intdiv(2) ?: 1
            task.failFast = false
            
            // Enable test output caching
            task.outputs.cacheIf { true }
            
            // Report configuration
            reports {
                html.required = true
                junitXml.required = true
            }
        }
    }

    private void configureJarTasks(Project project) {
        project.tasks.withType(Jar).configureEach { task ->
            task.manifest {
                attributes(
                    'Implementation-Title': project.name,
                    'Implementation-Version': project.version,
                    'Created-By': "${System.getProperty('java.version')} (${System.getProperty('java.vendor')})",
                    'Build-Timestamp': new Date().format("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
                    'Multi-Release': true
                )
            }
            task.setDuplicatesStrategy(DuplicatesStrategy.EXCLUDE)
            task.outputs.cacheIf { true }
        }
    }

    private static void applyDependencies(Project project) {
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
            implementation(platform("org.springframework.boot:spring-boot-dependencies:${project.ext.versions.springBoot}"))

            // Modulith
            implementation("org.springframework.modulith:spring-modulith-starter-core:${project.ext.versions.springModulith}")
            implementation("org.springframework.modulith:spring-modulith-api:${project.ext.versions.springModulith}")
            implementation("org.springframework.modulith:spring-modulith-events-core:${project.ext.versions.springModulith}")

            // Testing
            testImplementation("org.springframework.modulith:spring-modulith-test:${project.ext.versions.springModulith}")
        }
    }
}
