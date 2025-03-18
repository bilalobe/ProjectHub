# ProjectHub

![Project Logo](src/desktop-ui/src/main/resources/images/logo.png)

**A comprehensive platform for managing projects with a modern architecture using Kotlin, Ktor, and Jetpack Compose UI, with support for legacy JavaFX/Spring Boot components.**

[![Kotlin](https://img.shields.io/badge/Kotlin-1.8-purple.svg)](https://kotlinlang.org/)
[![Ktor](https://img.shields.io/badge/Ktor-2.3-blue.svg)](https://ktor.io/)
[![Compose UI](https://img.shields.io/badge/Compose-1.4-green.svg)](https://www.jetbrains.com/lp/compose-multiplatform/)
[![JavaFX](https://img.shields.io/badge/JavaFX-23-blue.svg)](https://openjfx.io/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue.svg)](https://www.postgresql.org/)
[![Dependabot Updates](https://github.com/bilalobe/ProjectHub/actions/workflows/dependabot/dependabot-updates/badge.svg)](https://github.com/bilalobe/ProjectHub/actions/workflows/dependabot-updates)
[![Qodana](https://github.com/bilalobe/ProjectHub/actions/workflows/qodana_code_quality.yml/badge.svg)](https://github.com/bilalobe/ProjectHub/actions/workflows/qodana_code_quality.yml)
[![Apache Fortress](https://img.shields.io/badge/Fortress-2.0.7-orange.svg)](https://directory.apache.org/fortress/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.9-orange.svg)](https://www.rabbitmq.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Overview

ProjectHub is a comprehensive project management platform built with a modern, modular architecture. The system is transitioning to a Kotlin-first approach with Ktor for backend services and Compose Multiplatform for UI development, while maintaining compatibility with legacy components.

## Architecture Highlights

- **Hexagonal Architecture**: Clean domain separation with ports and adapters
- **Plugin System**: Extensible architecture with dynamic plugin discovery
- **Security Integration**: Apache Fortress for enterprise-grade RBAC security
- **Cross-Platform UI**: Compose Multiplatform for consistent desktop/mobile interfaces
- **Reactive Design**: Non-blocking, coroutine-based request handling with Ktor

## Key Features

- **Project Management**: Create, edit, and track projects
- **Task Management**: Assign and monitor task progress
- **Resource Planning**: Allocate and track resource usage
- **Team Collaboration**: Foster team communication and coordination
- **Multi-UI Support**: Compose Multiplatform (new) and JavaFX (legacy) interfaces
- **Offline Capability**: Work seamlessly with or without internet connection
- **Advanced Security**: Role-based access control with Apache Fortress integration

## Project Structure

```
projecthub/
├── src/                  # Core functionality src
│   ├── foundation/          # Base framework and utilities
│   │   └── core/           # Domain model and core interfaces
│   ├── compose-ui/         # Modern UI using Compose Multiplatform
│   │   ├── shared/        # Shared UI components and resources
│   │   ├── desktop/       # Desktop-specific implementation
│   │   └── mobile/        # Mobile-specific implementation
│   ├── desktop-ui/         # Legacy desktop client application (JavaFX)
│   ├── plugins/            # Extensibility plugins
│   │   ├── security/      # Security implementation plugin
│   │   ├── monitoring/    # System monitoring plugin
│   │   └── cognitive/     # AI-assisted features plugin
│   └── api/               # Ktor-based API services
├── src/                    # Hexagonal architecture implementation
├── buildSrc/              # Build configuration and plugins
├── k8s/                   # Kubernetes deployment configs
├── scripts/               # Utility scripts
└── docs/                  # Project documentation
```

## Technology Stack

- **Core Language**: Kotlin (new), Java (legacy)
- **Backend**: Ktor (new), Spring Boot (legacy)
- **Frontend**: 
  - Compose Multiplatform (new, cross-platform)
  - JavaFX (legacy desktop)
- **Database**: PostgreSQL (Primary), H2 (Local Development)
- **Build**: Gradle with custom plugins
- **Security**: Apache Fortress, JWT, WebAuthn/Passkey
- **Documentation**: MkDocs with Material theme

## Getting Started

### Prerequisites

- JDK 21 or higher
- Kotlin 1.8 or higher
- PostgreSQL database
- Gradle 8.0+ (included via wrapper)

### Quick Start

1. Clone the repository:
   ```bash
   git clone https://github.com/bilalobe/ProjectHub.git
   cd ProjectHub
   ```

2. Configure the database in application.conf (Ktor) or application.yml (Spring)
3. Start the Ktor backend:
   ```bash
   ./gradlew :src:api:run
   ```

4. Start the desktop application:
   ```bash
   # Modern Compose UI
   ./gradlew :src:compose-ui:desktop:run
   
   # Legacy JavaFX UI
   ./gradlew :src:desktop-ui:run
   ```

5. Build the project:
   ```bash
   ./gradlew build
   ```

## Development

### Architecture Validation

The project uses ArchUnit to enforce architectural rules:
```bash
./gradlew :src:foundation:test --tests "*ArchitectureTest"
```

### Module Documentation

Each module contains its own README with specific setup instructions:
- [Foundation Core](src/foundation/core/README.md)
- [Compose UI](src/compose-ui/README.md)
- [Ktor API](src/api/README.md)
- [Plugin System](src/plugins/README.md)

## Hexagonal Architecture

ProjectHub follows a hexagonal (ports and adapters) architecture:

- **Domain Core**: Business logic independent of external systems
- **Ports**: Interfaces that define how the domain interacts with external systems
- **Adapters**: Implementation of ports for specific technologies
- **Application Services**: Orchestration of domain operations

[Learn more about our hexagonal implementation](docs/architecture/hexagonal.md)

## Plugin System

The plugin system enables extending ProjectHub functionality without modifying core code:

- Dynamic discovery of plugins at runtime
- Security-aware plugin execution
- Configuration-based plugin management
- UI integration through standardized interfaces

[Plugin Development Guide](src/plugins/README.md)

## Contributing

Please read our [Contributing Guidelines](CONTRIBUTING.md) and [Code of Conduct](CODE_OF_CONDUCT.md) before submitting pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.