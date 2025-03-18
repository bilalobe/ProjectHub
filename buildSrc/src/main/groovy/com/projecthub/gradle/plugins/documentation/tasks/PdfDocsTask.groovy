package com.projecthub.gradle.plugins.documentation.tasks

import com.projecthub.gradle.plugins.documentation.utils.DocUtils
import org.asciidoctor.gradle.jvm.pdf.AsciidoctorPdfTask
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*

class PdfDocsTask extends DefaultTask {
    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    File sourceDir = project.file('src/docs')

    @OutputDirectory
    File outputDir = new File(project.buildDir, "docs/pdf")

    @Input
    @Optional
    Map<String, Object> attributes = [:]

    @Input
    @Optional
    String theme = 'default'
    
    private String asciidoctorPdfTaskName

    PdfDocsTask() {
        description = "Generates PDF documentation"
        group = "Documentation"
        
        // Initialize the task name that we'll depend on
        asciidoctorPdfTaskName = "_${name}_asciidoctorPdf"
        
        // Configure dependencies in doFirst to ensure task graph is ready
        doFirst {
            configureAsciidoctorPdfTask()
        }
    }

    @TaskAction
    void generatePdfDocs() {
        // Copy PDF files to unified docs directory
        DocUtils.copyDocs(project, outputDir, "pdf")
        logger.lifecycle("Generated PDF documentation in ${outputDir}")
    }
    
    private void configureAsciidoctorPdfTask() {
        // Ensure asciidoctor-pdf plugin is applied
        if (!project.plugins.hasPlugin('org.asciidoctor.jvm.pdf')) {
            project.plugins.apply('org.asciidoctor.jvm.pdf')
        }
        
        // Configure PDF attributes
        Map<String, Object> defaultAttrs = [
            'source-highlighter': 'coderay',
            'toc': '',
            'toclevels': '3',
            'sectnums': '',
            'project-version': project.version,
            'project-name': project.name,
            'pdf-stylesdir': new File(sourceDir, 'theme').absolutePath,
            'pdf-style': theme,
            'pdf-fontsdir': new File(sourceDir, 'fonts').absolutePath
        ]

        // Merge default and custom attributes
        Map<String, Object> mergedAttrs = new HashMap<>(defaultAttrs)
        mergedAttrs.putAll(attributes)
        
        // Create and configure asciidoctor-pdf task if it doesn't exist yet
        if (!project.tasks.findByName(asciidoctorPdfTaskName)) {
            project.tasks.create(asciidoctorPdfTaskName, AsciidoctorPdfTask) { pdfTask ->
                pdfTask.setSourceDir(sourceDir)
                pdfTask.setOutputDir(outputDir)
                
                // Configure attributes using proper method
                pdfTask.attributes(mergedAttrs)
                
                // Configure requires
                pdfTask.requires('asciidoctor-diagram')
            }
            
            // Make this task depend on the asciidoctor task
            dependsOn(asciidoctorPdfTaskName)
        }
    }
}
