package com.projecthub.gradle.plugins.documentation.utils

import groovy.text.SimpleTemplateEngine
import groovy.util.slurpersupport.GPathResult
import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.NodeChild
import org.gradle.api.Project

class DocUtils {
    static Properties loadDocProperties(Project project) {
        def properties = new Properties()
        def resourceStream = DocUtils.class.getResourceAsStream('/documentation.properties')
        if (resourceStream) {
            properties.load(resourceStream)
            // Replace project version placeholder
            properties.each { key, value ->
                if (value instanceof String && value.contains('${project.version}')) {
                    properties[key] = value.replace('${project.version}', project.version.toString())
                }
            }
        }
        return properties
    }

    static boolean isArchitectureTest(String className) {
        return className.endsWith('ArchitectureTest') ||
               className.contains('architecture.') ||
               className.endsWith('ArchUnit')
    }

    static String generateApiVersionSummary(List<File> apiFiles) {
        def summary = new StringBuilder()
        summary.append("## API Version Summary\n\n")

        Map<String, List<String>> versions = new HashMap<>()
        apiFiles.each { file ->
            file.eachLine { line ->
                def matcher = line =~ /@ApiVersion\(major\s*=\s*(\d+),\s*minor\s*=\s*(\d+)/
                if (matcher.find()) {
                    def major = matcher.group(1)
                    def minor = matcher.group(2)
                    String versionKey = "v${major}.${minor}"
                    if (!versions.containsKey(versionKey)) {
                        versions.put(versionKey, new ArrayList<String>())
                    }
                    versions.get(versionKey).add(file.name)
                }
            }
        }

        versions.keySet().sort().each { String version ->
            summary.append("### ${version}\n")
            versions.get(version).each { String fileName ->
                summary.append("- ${fileName}\n")
            }
            summary.append("\n")
        }

        return summary.toString()
    }

    static String generateArchitectureViolationReport(File violationsFile) {
        def report = new StringBuilder()
        report.append("## Architecture Violations\n\n")

        if (violationsFile.exists()) {
            def violations = violationsFile.readLines()
                .findAll { it.trim() }
                .collect { line ->
                    def parts = line.split(':', 2)
                    [rule: parts[0], details: parts.size() > 1 ? parts[1] : '']
                }

            violations.groupBy { it.rule }.each { rule, ruleViolations ->
                report.append("### ${rule}\n")
                ruleViolations.each {
                    report.append("- ${it.details}\n")
                }
                report.append("\n")
            }
        } else {
            report.append("No violations found.\n")
        }

        return report.toString()
    }

    static void copyTemplateResources(Project project, File outputDir) {
        def templateDir = new File(project.rootProject.projectDir, 'src/docs/templates')
        if (templateDir.exists()) {
            project.copy {
                from templateDir
                into new File(outputDir, 'templates')
            }
        }
    }

    /**
     * Creates a standardized index page using the specified template
     * @param project The Gradle project
     * @param sections List of documentation sections to include in index
     * @param templateFile Template file for index generation
     * @param outputFile Output file for generated index
     */
    static void generateIndex(Project project, List<Map<String, Object>> sections, File templateFile, File outputFile) {
        def binding = [
            projectName: project.name,
            projectVersion: project.version,
            generationDate: new Date().format("yyyy-MM-dd"),
            sections: sections
        ]

        def engine = new SimpleTemplateEngine()
        def template = engine.createTemplate(templateFile.text)
        def result = template.make(binding)

        outputFile.parentFile.mkdirs()
        outputFile.text = result.toString()

        project.logger.lifecycle("Generated documentation index at ${outputFile.absolutePath}")
    }

    /**
     * Copies generated documentation to unified output directory
     * @param project The Gradle project
     * @param sourceDir Source directory of generated documentation
     * @param targetSubDir Target subdirectory within docs output directory
     * @param excludes Optional list of exclude patterns
     */
    static void copyDocs(Project project, File sourceDir, String targetSubDir, List<String> excludes = []) {
        def targetDir = new File(project.buildDir, "docs/${targetSubDir}")

        project.copy {
            from sourceDir
            into targetDir
            if (excludes) {
                exclude excludes
            }
        }

        project.logger.lifecycle("Copied documentation from ${sourceDir} to ${targetDir}")
    }

    /**
     * Resolves placeholder variables in a string using project properties
     * @param project The Gradle project
     * @param input String with potential placeholders
     * @return Resolved string with placeholders replaced by actual values
     */
    static String resolvePlaceholders(Project project, String input) {
        if (!input) return input

        def pattern = ~/\$\{([^}]+)\}/
        def matcher = pattern.matcher(input)
        def result = new StringBuilder()

        int lastEnd = 0
        while (matcher.find()) {
            result.append(input.substring(lastEnd, matcher.start()))
            def propName = matcher.group(1)
            def propValue = project.findProperty(propName) ?: System.getProperty(propName) ?: ""
            result.append(propValue)
            lastEnd = matcher.end()
        }

        if (lastEnd < input.length()) {
            result.append(input.substring(lastEnd))
        }

        return result.toString()
    }
}
