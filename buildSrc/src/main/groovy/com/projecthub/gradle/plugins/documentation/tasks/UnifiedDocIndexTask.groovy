package com.projecthub.gradle.plugins.documentation.tasks

import groovy.text.SimpleTemplateEngine
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class UnifiedDocIndexTask extends DefaultTask {
    @OutputDirectory
    File outputDir = project.file("${project.buildDir}/docs")

    @Input
    @Optional
    Map<String, Boolean> enabledSections = [
        'architecture': true,
        'api': true,
        'swagger': true,
        'guides': true,
        'pdf': true
    ]

    @Input
    @Optional
    String projectTitle = "ProjectHub Documentation"

    @TaskAction
    void generateIndex() {
        // Create sections based on enabled documentation types
        List<Map<String, Object>> sections = []

        if (enabledSections.architecture) {
            sections << [
                title: 'Architecture Documentation',
                links: [
                    [href: 'architecture/index.html', title: 'Architecture Overview'],
                    [href: 'architecture/tests.html', title: 'Architecture Tests'],
                    [href: 'architecture/decisions.html', title: 'Architecture Decisions']
                ]
            ]
        }

        if (enabledSections.api) {
            sections << [
                title: 'API Documentation',
                links: [
                    [href: 'api/index.html', title: 'API Reference (JavaDoc)']
                ]
            ]
        }

        if (enabledSections.swagger) {
            if (!sections.find { it.title == 'API Documentation' }) {
                sections << [
                    title: 'API Documentation',
                    links: []
                ]
            }

            sections.find { it.title == 'API Documentation' }.links <<
                [href: 'swagger/index.html', title: 'REST API (Swagger/OpenAPI)']
        }

        if (enabledSections.guides) {
            sections << [
                title: 'User Guides',
                links: [
                    [href: 'guides/index.html', title: 'User Manual'],
                    [href: 'guides/development.html', title: 'Development Guide'],
                    [href: 'guides/deployment.html', title: 'Deployment Guide']
                ]
            ]
        }

        // Always add additional resources section
        List<Map<String, Object>> additionalResources = []

        if (enabledSections.pdf) {
            additionalResources << [href: 'pdf/projecthub.pdf', title: 'Complete Documentation (PDF)']
        }

        if (new File(outputDir, 'docbook/projecthub.html').exists()) {
            additionalResources << [href: 'docbook/projecthub.html', title: 'Complete Documentation (HTML)']
        }

        if (new File(outputDir, 'coverage/index.html').exists()) {
            additionalResources << [href: 'coverage/index.html', title: 'Test Coverage Report']
        }

        if (additionalResources) {
            sections << [
                title: 'Additional Resources',
                links: additionalResources
            ]
        }

        // Generate the index page using a template
        def templateFile = findTemplateFile()
        def indexFile = new File(outputDir, "index.html")

        if (templateFile && templateFile.exists()) {
            // Use template file
            def engine = new SimpleTemplateEngine()
            def binding = [
                projectName: project.name,
                projectVersion: project.version,
                projectTitle: projectTitle,
                sections: sections,
                generationDate: new Date().format('yyyy-MM-dd HH:mm:ss')
            ]

            def template = engine.createTemplate(templateFile.text)
            def result = template.make(binding)

            indexFile.text = result.toString()
        } else {
            // Use default template
            generateDefaultIndex(indexFile, sections)
        }

        // Copy CSS and other resources
        copyCssResources()

        logger.lifecycle("Generated unified documentation index at ${indexFile.absolutePath}")
    }

    private File findTemplateFile() {
        // Look for template file in standard locations
        def locations = [
            "${project.rootDir}/src/docs/templates/index-template.html",
            "${project.rootDir}/src/docs/templates/html5/index-template.html",
            "${project.rootDir}/src/docs/templates/index.html.template"
        ]

        for (String location : locations) {
            def file = project.file(location)
            if (file.exists()) {
                return file
            }
        }

        return null
    }

    private void generateDefaultIndex(File indexFile, List<Map<String, Object>> sections) {
        StringBuilder html = new StringBuilder()
        html.append("""
<!DOCTYPE html>
<html>
<head>
    <title>${projectTitle}</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/projecthub.css">
</head>
<body>
    <header>
        <nav class="top-nav">
            <div class="nav-logo">${projectTitle}</div>
        </nav>
    </header>
    <main>
        <div class="content">
            <h1>${projectTitle}</h1>

""")

        // Add each section
        sections.each { section ->
            html.append("""
            <section class="doc-section">
                <h2>${section.title}</h2>
                <ul>
""")

            section.links.each { link ->
                html.append("""                    <li><a href="${link.href}">${link.title}</a></li>\n""")
            }

            html.append("""                </ul>
            </section>

""")
        }

        html.append("""
        </div>
    </main>
    <footer>
        <p>Generated on ${new Date().format('yyyy-MM-dd HH:mm:ss')} - Version ${project.version}</p>
    </footer>
</body>
</html>
""")

        indexFile.text = html.toString()
    }

    private void copyCssResources() {
        // Ensure CSS directory exists
        File cssDir = new File(outputDir, "css")
        cssDir.mkdirs()

        // Check if custom CSS exists
        File customCss = project.file("${project.rootDir}/src/docs/templates/html5/css/projecthub.css")

        if (customCss.exists()) {
            project.copy {
                from customCss.parentFile
                into cssDir.parentFile
                include "**/*.css"
                include "**/*.js"
            }
        } else {
            // Create a default CSS file
            File defaultCss = new File(cssDir, "projecthub.css")
            defaultCss.text = """
body {
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    line-height: 1.6;
    color: #333;
    margin: 0;
    padding: 0;
    background-color: #f8f9fa;
}

.top-nav {
    background-color: #2c3e50;
    color: white;
    padding: 1rem;
    box-shadow: 0 2px 5px rgba(0,0,0,0.1);
}

.nav-logo {
    font-size: 1.5rem;
    font-weight: bold;
}

.content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem;
    background-color: white;
    box-shadow: 0 0 10px rgba(0,0,0,0.05);
    min-height: 80vh;
}

h1 {
    color: #2c3e50;
    margin-bottom: 2rem;
    padding-bottom: 0.5rem;
    border-bottom: 2px solid #eee;
}

.doc-section {
    margin-bottom: 2rem;
}

.doc-section h2 {
    color: #3498db;
    font-size: 1.5rem;
    margin-bottom: 1rem;
}

.doc-section ul {
    list-style-type: none;
    padding-left: 1rem;
}

.doc-section li {
    margin-bottom: 0.5rem;
}

.doc-section a {
    color: #2980b9;
    text-decoration: none;
    padding: 5px;
    display: inline-block;
    border-radius: 3px;
    transition: background-color 0.2s ease;
}

.doc-section a:hover {
    background-color: #f0f7fd;
    color: #3498db;
}

footer {
    background-color: #2c3e50;
    color: #ecf0f1;
    text-align: center;
    padding: 1rem;
    font-size: 0.9rem;
}
"""
        }
    }
}
