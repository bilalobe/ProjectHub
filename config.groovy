outputPath = 'build/docs'

// Directory where the input files are located
inputPath = 'src/docs'

// Input directory for AsciiDoc files
inputFiles = [
    [file: 'arc42-template.adoc', formats: ['html', 'pdf']],
    [file: 'architecture-decisions.adoc', formats: ['html', 'pdf']],
    [file: 'architecture-tests.adoc', formats: ['html', 'pdf']]
]

// Enable PlantUML integration
plantUML = [:]
plantUML.enabled = true

// Enable Mermaid.js integration
mermaid = [:]
mermaid.enabled = true

// Enable JUnit test results
taskInputsDirs = [
    "${projectDir}/modules/foundation/build/test-results/test"
]

// ArchUnit documentation generation
archUnit = [:]
archUnit.enabled = true
archUnit.packages = ['com.projecthub.architecture']
archUnit.outputPath = 'architecture/tests'

// Configure diagrams
diagrams = [:]
diagrams.plantUML = true
diagrams.mermaid = true

// Custom jBake configuration
jbake = [:]
jbake.template = 'templates/html5'
jbake.theme = 'themes/projecthub'
jbake.highlighter = 'prettify'

// Documentation sections
documentation = [
    sections: [
        'architecture',
        'development',
        'operations',
        'security'
    ]
]

// Asciidoctor configuration
asciidoc = [:]
asciidoc.attributes = [
    'source-highlighter': 'prettify',
    'icons': 'font',
    'experimental': '',
    'project-version': project.version,
    'revnumber': project.version
]

// docToolchain pipeline configuration
docToolchain = [
    tasks: [
        'generateHTML',
        'generatePDF',
        'generateDocbook',
        'convertToDocx',
        'generateDependencyUpdates',
        'generateJavaDocs'
    ]
]