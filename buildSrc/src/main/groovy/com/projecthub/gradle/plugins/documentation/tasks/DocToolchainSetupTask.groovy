package com.projecthub.gradle.plugins.documentation.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class DocToolchainSetupTask extends DefaultTask {
    @Input
    String version = '2.0.0'

    @Input
    @Optional
    List<String> plugins = []

    DocToolchainSetupTask() {
        description = "Sets up docToolchain documentation generator"
        group = "Documentation"
    }

    @TaskAction
    void setupDocToolchain() {
        // Create docToolchain directory if it doesn't exist
        def docToolchainDir = project.file('docToolchain')
        if (!docToolchainDir.exists()) {
            logger.lifecycle("Setting up docToolchain ${version}...")

            // Clone docToolchain repository
            project.exec {
                commandLine 'git', 'clone', 'https://github.com/docToolchain/docToolchain.git'
                workingDir project.projectDir
                standardOutput = new ByteArrayOutputStream()
                errorOutput = new ByteArrayOutputStream()
            }

            // Checkout specific version
            project.exec {
                commandLine 'git', 'checkout', "v${version}"
                workingDir docToolchainDir
                standardOutput = new ByteArrayOutputStream()
                errorOutput = new ByteArrayOutputStream()
            }

            // Initialize docToolchain
            project.exec {
                commandLine './bin/doctoolchain', '.', 'init'
                workingDir project.projectDir
                environment 'PATH', System.getenv('PATH')
                standardOutput = new ByteArrayOutputStream()
                errorOutput = new ByteArrayOutputStream()
            }

            // Install plugins if specified
            if (plugins) {
                plugins.each { plugin ->
                    logger.lifecycle("Installing plugin: ${plugin}")
                    project.exec {
                        commandLine './bin/doctoolchain', '.', 'installPlugin', plugin
                        workingDir project.projectDir
                        environment 'PATH', System.getenv('PATH')
                        standardOutput = new ByteArrayOutputStream()
                        errorOutput = new ByteArrayOutputStream()
                    }
                }
            }

            logger.lifecycle("docToolchain ${version} setup completed")
        } else {
            logger.lifecycle("docToolchain already set up, skipping installation")
        }
    }
}
