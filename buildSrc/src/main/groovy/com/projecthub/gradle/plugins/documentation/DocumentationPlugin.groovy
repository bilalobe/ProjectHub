package com.projecthub.gradle.plugins.documentation

import com.projecthub.gradle.plugins.documentation.tasks.*
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.javadoc.Javadoc
import org.yaml.snakeyaml.Yaml

class DocumentationPlugin implements Plugin<Project> {
    void apply(Project project) {
        // Load centralized configuration using proper resource loading
        def configStream = Thread.currentThread().contextClassLoader.getResourceAsStream('documentation-config.yaml')
        def config = new Yaml().load(configStream)

        // Apply required plugins
        project.plugins.apply('org.asciidoctor.jvm.convert')
        project.plugins.apply('org.asciidoctor.jvm.pdf')

        // Configure documentation extension
        def extension = project.extensions.create('projectHubDocs', DocumentationExtension)
        extension.projectName = config.documentation.project.name
        extension.version = project.version
        extension.outputDir = config.documentation.directories.output

        // Register all documentation tasks
        registerDocumentationTasks(project, config)

        // Configure tasks after project evaluation
        project.afterEvaluate {
            configureDocumentationTasks(project, extension, config)
        }
    }

    private void registerDocumentationTasks(Project project, Map config) {
        // Register docToolchain setup task
        project.tasks.register('docToolchainSetup', DocToolchainSetupTask) {
            group = 'documentation'
            description = 'Sets up docToolchain for architecture documentation'
            version = config.documentation.tools.docToolchain.version
            plugins = config.documentation.tools.docToolchain.plugins
        }

        // Register API documentation task
        project.tasks.register('generateApiDocs', ApiDocsTask) {
            group = 'documentation'
            description = 'Generates API documentation using JavaDoc'
            sourceFiles = project.files(project.fileTree(dir: 'src/main/java', include: '**/*.java'))
            title = "${config.documentation.project.name} API Documentation"
            footer = "© ${new Date().format('yyyy')} ${config.documentation.project.name}"
        }

        // Register AsciiDoc documentation task
        project.tasks.register('generateAsciiDocs', AsciiDocsTask) {
            group = 'documentation'
            description = 'Generates documentation from AsciiDoc sources'
            sourceDir = project.file(config.documentation.directories.source)
            attributes = config.documentation.tools.asciidoctor.attributes
            requires = ['asciidoctor-diagram']
        }

        // Register PDF documentation task
        project.tasks.register('generatePdfDocs', PdfDocsTask) {
            group = 'documentation'
            description = 'Generates PDF documentation from AsciiDoc sources'
            sourceDir = project.file(config.documentation.directories.source)
            theme = 'default'
        }

        // Register Swagger documentation task
        project.tasks.register('generateSwaggerDocs', SwaggerDocsTask) {
            group = 'documentation'
            description = 'Generates Swagger/OpenAPI documentation'
            basePackage = config.documentation.tools.swagger.basePackage
            apiTitle = "${config.documentation.project.name} REST API"
        }

        // Register Architecture documentation task
        project.tasks.register('generateArchitectureDocs', AsciiDocsTask) {
            group = 'documentation'
            description = 'Generates architecture documentation from AsciiDoc sources'
            sourceDir = project.file("${config.documentation.directories.source}/${config.documentation.directories.architecture}")
            outputDir = project.file("${project.buildDir}/docs/architecture")
        }

        // Register MkDocs tasks
        project.tasks.register('mkDocsBuild', MkDocsTask) {
            group = 'documentation'
            description = 'Builds the MkDocs documentation'
            command = 'build'
            sourceDir = project.file(config.documentation.tools.mkdocs.source)
            outputDir = project.file(config.documentation.tools.mkdocs.output)
        }

        project.tasks.register('mkDocsServe', MkDocsTask) {
            group = 'documentation'
            description = 'Serves the MkDocs documentation locally'
            command = 'serve'
            sourceDir = project.file(config.documentation.tools.mkdocs.source)
            environment = [
                'MKDOCS_PORT': config.documentation.tools.mkdocs.serve.port.toString()
            ]
        }

        // Register unified index task
        project.tasks.register('generateUnifiedIndex', UnifiedDocIndexTask) {
            group = 'documentation'
            description = 'Generates unified documentation index'
            outputDir = project.file("${project.buildDir}/docs")
            dependsOn project.tasks.named('mkDocsBuild') ? 'mkDocsBuild' : [],
                     'generateArchitectureDocs',
                     'generateApiDocs',
                     'generateAsciiDocs',
                     'generatePdfDocs',
                     'generateSwaggerDocs'
        }

        // Register main documentation task
        project.tasks.register('generateDocs', DefaultTask) {
            group = 'documentation'
            description = 'Generates all documentation'
            dependsOn 'docToolchainSetup',
                     'generateArchitectureDocs',
                     'generateApiDocs',
                     'generateAsciiDocs',
                     'generatePdfDocs',
                     'generateSwaggerDocs',
                     'generateUnifiedIndex'

            if (project.tasks.named('mkDocsBuild')) {
                dependsOn 'mkDocsBuild'
            }

            // Always run unifiedIndex as the final task
            finalizedBy 'generateUnifiedIndex'
        }
    }

    private void configureDocumentationTasks(Project project, DocumentationExtension extension, Map config) {
        // Apply custom configuration from extension
        project.tasks.withType(ApiDocsTask).configureEach { task ->
            task.title = extension.includePrivate ? 
                "${extension.projectName} API Documentation (Including Private)" :
                "${extension.projectName} API Documentation"
        }

        // Configure Javadoc task
        project.tasks.withType(Javadoc).configureEach { task ->
            task.options.source = config.documentation.tools.java.source
            task.options.encoding = config.documentation.tools.java.encoding
            task.options.memberLevel = extension.includePrivate ? 'PRIVATE' : 'PROTECTED'
        }
    }
}
