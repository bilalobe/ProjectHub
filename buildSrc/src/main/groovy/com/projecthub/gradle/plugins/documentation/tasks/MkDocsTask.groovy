package com.projecthub.gradle.plugins.documentation.tasks

import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.*

@CompileStatic
class MkDocsTask extends DefaultTask {
    @Input
    String command = 'build'

    @InputDirectory
    File sourceDir = project.file('projecthub_documentation-main/docs')

    @OutputDirectory
    File outputDir = project.file('build/docs/mkdocs')

    @Input
    @Optional
    Map<String, String> environment = [:]

    @TaskAction
    void executeMkDocs() {
        project.exec {
            workingDir = sourceDir.parentFile
            commandLine 'mkdocs', command
            environment << this.environment

            if (command == 'build') {
                args '--clean', '--site-dir', outputDir.absolutePath
            }
        }

        // Copy generated site to the unified documentation directory
        if (command == 'build') {
            project.copy {
                from outputDir
                into new File(project.buildDir, 'docs/guides')
                exclude 'javadoc/**'
                exclude 'swagger/**'
            }
        }
    }
}
