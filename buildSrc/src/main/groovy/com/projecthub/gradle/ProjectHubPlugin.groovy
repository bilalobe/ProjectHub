package com.projecthub.gradle

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.compile.JavaCompile

class ProjectHubPlugin implements Plugin<Project> {
    void apply(Project project) {
        // Load dependencies first to ensure versions are available
        loadDependencies(project)
        
        // Apply required plugins
        applyPlugins(project)

        // Apply core configurations
        applyConfigurations(project)

        // Configure tasks after evaluation to ensure proper ordering
        project.afterEvaluate {
            configureDgs(project)
            configureBootTasks(project)
            configureWorkerExecution(project)
        }
    }

    private static void applyPlugins(Project project) {
        project.plugins.with {
            apply('org.beryx.jlink')
            apply('org.springframework.boot')
            apply('io.spring.dependency-management')
            apply('com.netflix.dgs.codegen')
            apply('java-library')
        }
    }

    private void applyConfigurations(Project project) {
        // Apply modulith configuration
        project.apply from: "${project.rootProject.projectDir}/buildSrc/src/main/groovy/modulith.gradle"
        
        // Configure common compile options
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
    }

    private void configureDgs(Project project) {
        project.tasks.named('generateJava').configure { task ->
            task.schemaPaths = ["${project.projectDir}/src/main/resources/schema/"]
            task.packageName = 'com.projecthub.core.graphql.generated'
            task.generateClient = true
            
            // Configure task inputs/outputs for caching
            task.inputs.files(task.schemaPaths)
            task.outputs.dir("${project.buildDir}/generated/dgs")
            
            // Enable caching
            task.outputs.cacheIf { true }
        }
    }

    private void configureBootTasks(Project project) {
        project.tasks.named('bootRun').configure { task ->
            task.mainClass = 'com.projecthub.core.CoreApplication'
            task.systemProperty 'spring.profiles.active', 'dev'
            task.jvmArgs = ['-Xmx1024m', '--enable-preview']
            
            // Configure Spring Boot worker threads
            task.systemProperty 'spring.task.execution.pool.core-size', 
                Runtime.runtime.availableProcessors().toString()
            
            // Disable caching for run tasks
            task.outputs.cacheIf { false }
        }

        project.tasks.register('startWeb', JavaExec) {
            group = 'application'
            description = 'Starts the web application'
            
            dependsOn project.tasks.named('bootRun')
            
            // Configure execution
            mainClass = 'com.projecthub.core.CoreApplication'
            classpath = project.sourceSets.main.runtimeClasspath
            
            // Configure JVM options
            jvmArgs = ['-Xmx1024m', '--enable-preview']
            systemProperties = [
                'spring.profiles.active': 'dev'
            ]
            
            // Disable caching for run tasks
            outputs.cacheIf { false }
        }
    }
    
    private void configureWorkerExecution(Project project) {
        // Configure global worker settings
        project.gradle.projectsEvaluated {
            project.tasks.withType(JavaExec).configureEach { task ->
                task.jvmArgs += '--enable-preview'
                
                // Configure standard streams
                task.standardOutput = new ByteArrayOutputStream()
                task.errorOutput = new ByteArrayOutputStream()
                
                task.doLast {
                    project.logger.lifecycle(task.standardOutput.toString())
                    if (task.errorOutput.toString()) {
                        project.logger.error(task.errorOutput.toString())
                    }
                }
            }
        }
    }

    private static void loadDependencies(Project project) {
        def dependenciesFile = new File("${project.rootProject.projectDir}/buildSrc/src/main/groovy/dependencies.gradle")
        if (!dependenciesFile.exists()) {
            throw new GradleException("dependencies.gradle not found")
        }
        project.apply from: dependenciesFile.absolutePath
    }
}
