# ProjectHub

**A comprehensive platform for managing student projects, component evaluations, and project distribution, built with JavaFX, Spring Boot, and PostgreSQL.**

[![JavaFX](https://img.shields.io/badge/JavaFX-23-blue.svg)](https://openjfx.io/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue.svg)](https://www.postgresql.org/)
[![Dependabot Updates](https://github.com/bilalobe/ProjectHub/actions/workflows/dependabot/dependabot-updates/badge.svg)](https://github.com/bilalobe/ProjectHub/actions/workflows/dependabot/dependabot-updates)
[![Qodana](https://github.com/bilalobe/ProjectHub/actions/workflows/qodana_code_quality.yml/badge.svg)](https://github.com/bilalobe/ProjectHub/actions/workflows/qodana_code_quality.yml)
[![CodeQL Advanced](https://github.com/bilalobe/ProjectHub/actions/workflows/codeql.yml/badge.svg)](https://github.com/bilalobe/ProjectHub/actions/workflows/codeql.yml)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Overview

**ProjectHub** is a powerful and intuitive platform designed to streamline the project management workflow for both students and teachers. This application provides a rich desktop experience using JavaFX, robust backend services with Spring Boot, and reliable data storage with PostgreSQL.

## Key Features

### Project Management

- **Create and Edit Projects:** Teachers can create new projects, set deadlines, and assign them to teams.
- **View Project Details:** Teams can view detailed information about their projects, including descriptions, deadlines, and assigned tasks.
- **Manage Project Components:** Add, edit, and delete project components.

### Team Collaboration

- **Team Dashboard:** View team members, assigned projects, and submission status.
- **Submit Components:** Teams can submit project components for evaluation.
- **Track Submissions:** Monitor the status of submitted components and receive feedback.

### Component Evaluation

- **Evaluate Submissions:** Teachers can evaluate submitted components and provide feedback.
- **Grade Submissions:** Assign grades to submitted components.

### Navigation

- **Tree-Based Navigation:** Navigate through schools, cohorts, teams, and projects using an intuitive tree structure.

### Security

- **User Authentication:** Secure login for users with role-based access control.
- **Data Protection:** Ensure data security and privacy.

## Technologies Used

### Frontend

- **JavaFX:** Provides a rich client application platform for creating desktop applications.
- **FXML:** Used for defining the user interface in XML format.

### Backend

- **Spring Boot:** Simplifies the development of production-ready applications.
    - **Spring Data JPA:** Provides easy integration with JPA for database operations.
    - **Spring Security:** Ensures secure authentication and authorization.
    - **Springdoc OpenAPI:** Generates API documentation.

### Database

- **PostgreSQL:** A powerful, open-source object-relational database system.

### Build and Dependency Management

- **Gradle:** An advanced build automation tool.

### Containerization

- **Docker:** Ensures consistent environments for development, testing, and production.

## Getting Started

### Prerequisites

- **Java 23** or higher
- **Gradle 8.11** or higher
- **PostgreSQL** database
- **Docker** (optional, for containerization)

### Installation

1. **Clone the repository:**

     ```sh
     git clone https://github.com/yourusername/projecthub.git
     cd projecthub
     ```

2. **Configure the database:**

     Update the `application.properties` file with your PostgreSQL database configuration.

3. **Build the project:**

     ```sh
     ./gradlew build
     ```

4. **Run the application:**

     ```sh
     ./gradlew bootRun
     ```

5. **Access the application:**

     Open your browser and navigate to `http://localhost:8080`.

### Running with Docker

1. **Build the Docker image:**

     ```sh
     docker build -t projecthub .
     ```

2. **Run the Docker container:**

     ```sh
     docker run -p 8080:8080 projecthub
     ```

## Directory Structure

```
.devcontainer/
    devcontainer.json
    Dockerfile
.github/
    workflows/
        ci.yml
.vscode/
    settings.json
app/
    build/
    build.gradle
    logs/
    README.md
    src/
build/
    reports/
    tmp/
compose-dev.yaml
Dockerfile
gradle/
    libs.versions.toml
    wrapper/
gradle.properties
gradlew
gradlew.bat
LICENSE
README.md
settings.gradle
```

## Configuration

### Database Configuration

Update the `application-dev.properties` file with your PostgreSQL database credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/yourdatabase
spring.datasource.username=yourusername
spring.datasource.password=yourpassword
```

### Security Configuration

Update the `application-dev.properties` file with your security credentials:

```properties
spring.security.user.name=yourusername
spring.security.user.password=yourpassword
```

## Contributing

We welcome contributions! Please read our [Contributing Guidelines](CONTRIBUTING.md) for more details.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

For any inquiries or support, please contact us at [support@projecthub.com](mailto:elkhatabibilal@gmail.com).
