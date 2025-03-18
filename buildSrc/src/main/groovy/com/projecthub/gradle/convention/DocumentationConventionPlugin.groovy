package com.projecthub.gradle.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskProvider

/**
 * Documentation convention plugin for ProjectHub
 * Contains all documentation-related tasks and configurations
 */
class DocumentationConventionPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        // Apply asciidoctor plugin
        project.plugins.apply('org.asciidoctor.jvm.convert')
        
        // Configure DocToolchain
        project.configurations {
            asciidoctorExtensions
        }
        
        project.dependencies {
            asciidoctorExtensions 'org.asciidoctor:asciidoctorj-diagram'
            asciidoctorExtensions 'org.asciidoctor:asciidoctorj-pdf'
        }
        
        // Define documentation properties with defaults
        def docExtension = project.extensions.create('projectHubDocs', DocumentationExtension)
        
        // Register documentation tasks
        registerDocToolchainTasks(project)
        registerArchitectureDocsTasks(project, docExtension)
    }
    
    private void registerDocToolchainTasks(Project project) {
        // Download DocToolchain
        TaskProvider<Exec> downloadTask = project.tasks.register('downloadDocToolchain', Exec) {
            description = 'Downloads DocToolchain from GitHub'
            group = 'documentation'
            
            commandLine 'git', 'clone', 'https://github.com/docToolchain/docToolchain.git'
            
            // Only execute if docToolchain directory doesn't exist
            onlyIf {
                !project.file('docToolchain').exists()
            }
        }

        // Initialize documentation
        TaskProvider<Exec> initTask = project.tasks.register('initDocumentation', Exec) {
            description = 'Initializes documentation structure'
            group = 'documentation'
            
            dependsOn downloadTask
            commandLine './docToolchain/bin/doctoolchain', '.', 'init'
            
            // Only execute if init hasn't been done
            onlyIf {
                !project.file('src/docs/arc42').exists()
            }
        }

        // Generate HTML documentation
        TaskProvider<Exec> generateHtmlTask = createDocToolchainTask(project, 'generateHTML')
        
        // Generate PDF documentation
        TaskProvider<Exec> generatePdfTask = createDocToolchainTask(project, 'generatePDF')

        // Combined documentation generation task
        project.tasks.register('generateDocs') {
            description = 'Generates all documentation'
            group = 'documentation'
            
            dependsOn downloadTask
            dependsOn initTask
            dependsOn project.tasks.withType(org.asciidoctor.gradle.jvm.AsciidoctorTask)
            dependsOn generateHtmlTask
            dependsOn generatePdfTask
        }
    }
    
    private TaskProvider<Exec> createDocToolchainTask(Project project, String command) {
        return project.tasks.register("docToolchain${command.capitalize()}", Exec) {
            description = "Runs DocToolchain ${command} command"
            group = 'documentation'
            
            workingDir project.projectDir
            commandLine './docToolchain/bin/doctoolchain', '.', command
            
            // Configure standard streams
            standardOutput = new ByteArrayOutputStream()
            errorOutput = new ByteArrayOutputStream()
            
            // Log the command output
            doLast {
                logger.lifecycle("DocToolchain ${command} output:")
                logger.lifecycle(standardOutput.toString())
                if (errorOutput.toString()) {
                    logger.error(errorOutput.toString())
                }
            }
        }
    }
    
    private void registerArchitectureDocsTasks(Project project, DocumentationExtension docExt) {
        // Generate architecture documentation from test results
        project.tasks.register('generateArchitectureDocs', org.asciidoctor.gradle.jvm.AsciidoctorTask) {
            description = 'Generates architecture documentation'
            group = 'documentation'
            
            dependsOn ':modules:foundation:test'

            sourceDir = project.file('src/docs')
            sources {
                include 'architecture-tests.adoc'
            }
            outputDir = project.file('build/docs/architecture')

            inputs.property('version', project.provider { project.version })
            inputs.property('projectName', project.provider { docExt.projectName })
            inputs.dir(project.file('modules/foundation/build/test-results/test'))
                .withPathSensitivity(PathSensitivity.RELATIVE)
                .withPropertyName('testResults')

            doFirst {
                attributes = [
                    'source-highlighter': 'prettify',
                    'toc'               : 'left',
                    'toclevels'         : '3',
                    'sectlinks'         : '',
                    'project-version'   : project.version,
                    'testResultsDir'    : project.file('modules/foundation/build/test-results/test'),
                    'project-name'      : docExt.projectName
                ]
                
                // Add any additional attributes defined in extension
                if (docExt.additionalAttributes) {
                    attributes.putAll(docExt.additionalAttributes)
                }
            }
        }
    }
}

// Extension class to configure documentation properties
class DocumentationExtension {
    String projectName = "ProjectHub"
    String version = "1.0.0"
    boolean includeArchitecture = true
    boolean includeSwagger = false
    Map<String, String> additionalAttributes = [:]
}