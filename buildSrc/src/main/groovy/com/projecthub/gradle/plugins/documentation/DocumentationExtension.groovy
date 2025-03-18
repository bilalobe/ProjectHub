package com.projecthub.gradle.plugins.documentation

class DocumentationExtension {
    String projectName
    String version
    String outputDir
    boolean includePrivate = false
    boolean includeArchitecture = true
    boolean includeSwagger = true
    Map<String, String> additionalAttributes = [:]
}