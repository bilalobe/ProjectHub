package com.projecthub.gradle.plugins.documentation.tasks

import com.projecthub.gradle.plugins.documentation.utils.DocUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*
import org.asciidoctor.gradle.jvm.AsciidoctorTask

/**
 * Custom task for generating AsciiDoc documentation.
 * Updated implementation for Gradle 8.13 compatibility.
 */
class AsciiDocsTask extends DefaultTask {
    
    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    File sourceDir = project.file('src/docs')

    @OutputDirectory
    File outputDir = new File(project.buildDir, "docs/asciidoc")

    @Input
    @Optional
    Map<String, Object> attributes = [:]

    @Input
    @Optional
    List<String> requires = []

    private String asciidoctorTaskName

    AsciiDocsTask() {
        description = "Generates AsciiDoc documentation"
        group = "Documentation"
        
        // Initialize the task name that we'll depend on
        asciidoctorTaskName = "_${name}_asciidoctor"
        
        // Configure dependencies in doFirst to ensure task graph is ready
        doFirst {
            configureAsciidoctorTask()
        }
    }

    @TaskAction
    void generateAsciiDocs() {
        // Copy AsciiDoc files to unified docs directory
        DocUtils.copyDocs(project, outputDir, "asciidoc")

        // Copy images directory if it exists
        File imagesDir = new File(sourceDir, "images")
        if (imagesDir.exists()) {
            project.copy {
                from imagesDir
                into new File(outputDir, "images")
            }
        }

        logger.lifecycle("Generated AsciiDoc documentation in ${outputDir}")
    }
    
    private void configureAsciidoctorTask() {
        // Ensure asciidoctor plugin is applied
        if (!project.plugins.hasPlugin('org.asciidoctor.convert')) {
            project.plugins.apply('org.asciidoctor.convert')
        }
        
        // Create default attributes map
        def defaultAttrs = [
            'source-highlighter': 'prettify',
            'toc': 'left',
            'toclevels': '3',
            'sectlinks': '',
            'project-version': project.version,
            'project-name': project.name,
            'docinfodir': new File(sourceDir, 'docinfo').absolutePath,
            'docinfo': 'shared'
        ]

        def mergedAttrs = new HashMap<>(defaultAttrs)
        mergedAttrs.putAll(attributes)
        
        def requiresList = requires ?: ['asciidoctor-diagram']
        
        // Create and configure asciidoctor task if it doesn't exist yet
        if (!project.tasks.findByName(asciidoctorTaskName)) {
            project.tasks.create(asciidoctorTaskName, AsciidoctorTask) { task ->
                task.sourceDir = sourceDir
                task.outputDir = outputDir
                
                // Configure attributes using proper method
                task.attributes(mergedAttrs)
                
                // For Asciidoctor 1.6.1, we need to add requirements one by one
                requiresList.each { req ->
                    task.requires(req)
                }
            }
            
            // Make this task depend on the asciidoctor task
            dependsOn(asciidoctorTaskName)
        }
    }
}
