#!/bin/bash

echo "Initializing ProjectHub documentation system..."

# Create documentation directory structure
mkdir -p src/docs/{architecture,api,guides,images,theme,templates/html5/{css,js}}

# Check if docToolchain is already installed
if [ ! -d "docToolchain" ]; then
    echo "Cloning docToolchain repository..."
    git clone https://github.com/docToolchain/docToolchain.git
    cd docToolchain
    git checkout v2.0.0
    cd ..
fi

# Copy initial configuration files if they don't exist
if [ ! -f "src/docs/index.adoc" ]; then
    echo "Creating initial documentation files..."
    
    # Create main index file
    cat > src/docs/index.adoc << 'EOF'
= ProjectHub Documentation
:doctype: book
:toc: left
:toclevels: 3
:sectnums:

== Overview
ProjectHub is a comprehensive platform for managing student projects.

== Getting Started
See the link:guides/getting-started.adoc[Getting Started Guide].

== Architecture
See the link:architecture/overview.adoc[Architecture Overview].

== API Reference
See the link:api/index.html[API Reference].
EOF

    # Create sample getting started guide
    mkdir -p src/docs/guides
    cat > src/docs/guides/getting-started.adoc << 'EOF'
= Getting Started with ProjectHub
:doctype: book
:toc: left
:icons: font

== Introduction
This guide will help you get started with ProjectHub.

== Installation
Instructions for installing ProjectHub.

== Configuration
Instructions for configuring ProjectHub.

== First Steps
Basic usage instructions.
EOF
fi

# Set up CSS file if it doesn't exist
if [ ! -f "src/docs/templates/html5/css/projecthub.css" ]; then
    echo "Setting up documentation templates..."
    mkdir -p src/docs/templates/html5/css
    
    # Create CSS directory and file (simplified version)
    cat > src/docs/templates/html5/css/projecthub.css << 'EOF'
body {
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    line-height: 1.6;
    color: #333;
    margin: 0;
    padding: 0;
    background-color: #f8f9fa;
}

.content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem;
    background-color: white;
}

h1 {
    color: #2c3e50;
    margin-bottom: 2rem;
    padding-bottom: 0.5rem;
    border-bottom: 2px solid #eee;
}
EOF
fi

# Run initial documentation generation
echo "Generating documentation..."
if [ -f "./gradlew" ]; then
    ./gradlew :modules:documentation:generateDocs
else
    gradle :modules:documentation:generateDocs
fi

# Install Git hooks
cp .github/hooks/pre-commit.sh .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit

echo "Documentation system initialized successfully!"
echo "Generated documentation is available in build/docs/"
echo "To serve the documentation locally, run: ./gradlew :modules:documentation:serveAllDocs"