# ProjectHub Hexagonal Architecture Guide

## Overview

ProjectHub has been restructured using Hexagonal Architecture (also known as Ports and Adapters) to create a maintainable, testable, and flexible codebase. This document explains the architecture, its benefits, and how to work with it.

## Core Principles

1. **Domain-Centric**: Business logic is isolated from external concerns
2. **Dependency Rule**: Dependencies point inward (infrastructure → application → domain)
3. **Ports & Adapters**: Clean interfaces between layers
4. **Testability**: Each layer can be tested in isolation

## Project Structure

```
foundation/
│
├── core/                                # Domain & application layer
│   ├── domain/                         # Pure domain models (no dependencies)
│   │   ├── common/                     # Shared value objects
│   │   │   ├── vo/                     # Value objects
│   │   │   │   ├── Email.java
│   │   │   │   └── Identifier.java
│   │   │   └── exception/              # Domain exceptions
│   │   │
│   │   ├── project/                    # Project domain
│   │   │   ├── Project.java            # Aggregate root
│   │   │   ├── ProjectId.java          # Value object
│   │   │   ├── ProjectStatus.java      # Value object
│   │   │   ├── ProjectMember.java      # Entity
│   │   │   └── event/                  # Domain events
│   │   │       └── ProjectCreatedEvent.java
│   │   │
│   │   ├── workflow/                   # Workflow domain
│   │   │   ├── WorkflowDefinition.java # Aggregate root
│   │   │   ├── WorkflowState.java      # Entity
│   │   │   ├── WorkflowStage.java      # Value object
│   │   │   ├── WorkflowTransition.java # Value object
│   │   │   └── event/
│   │   │       └── StateTransitionEvent.java
│   │   │
│   │   └── task/                       # Task domain
│   │       └── ...
│   │
│   └── application/                   # Application services & use cases
│       ├── project/                   # Project application services
│       │   ├── ProjectService.java    # Implements use cases
│       │   ├── dto/                   # Data transfer objects
│       │   │   ├── ProjectDto.java
│       │   │   └── CreateProjectRequest.java
│       │   ├── mapper/                # Object mappers
│       │   │   └── ProjectMapper.java
│       │   └── port/                  # Ports (interfaces)
│       │       ├── in/                # Input ports
│       │       │   └── ProjectManagementUseCase.java
│       │       └── out/               # Output ports
│       │           ├── ProjectRepository.java
│       │           └── ProjectEventPublisher.java
│       │
│       └── workflow/                  # Workflow application services
│           ├── WorkflowService.java
│           ├── dto/
│           │   └── WorkflowDto.java 
│           ├── port/
│           │   ├── in/
│           │   │   └── WorkflowManagementUseCase.java
│           │   └── out/
│           │       └── WorkflowRepository.java
│           └── mapper/
│               └── WorkflowMapper.java
│
├── adapter/                           # Adapters connect outside world to app
│   ├── in/                           # Inbound adapters
│   │   ├── web/                      # Web controllers
│   │   │   ├── project/
│   │   │   │   └── ProjectController.java
│   │   │   └── workflow/
│   │   │       └── WorkflowController.java
│   │   │
│   │   ├── graphql/                  # GraphQL resolvers
│   │   │   ├── project/
│   │   │   │   └── ProjectResolver.java
│   │   │   └── workflow/
│   │   │       └── WorkflowResolver.java
│   │   │
│   │   └── messaging/                # Message consumers
│   │       └── EventConsumer.java
│   │
│   └── out/                          # Outbound adapters
│       ├── persistence/              # Database adapters
│       │   ├── project/
│       │   │   └── ProjectPersistenceAdapter.java
│       │   └── workflow/
│       │       └── WorkflowPersistenceAdapter.java
│       │
│       └── event/                    # Event publishers
│           └── EventPublisherAdapter.java
│
└── infrastructure/                   # Technical infrastructure
    ├── persistence/                  # Database entities & repositories
    │   ├── project/
    │   │   ├── ProjectJpaEntity.java
    │   │   └── ProjectJpaRepository.java
    │   └── workflow/
    │       ├── WorkflowJpaEntity.java
    │       └── WorkflowJpaRepository.java
    │
    ├── messaging/                    # Messaging infrastructure
    │   └── KafkaConfiguration.java
    │
    ├── security/                     # Security configuration
    │   └── SecurityConfig.java
    │
    └── config/                       # Application configuration
        ├── project/
        │   └── ProjectConfig.java
        └── workflow/
            └── WorkflowConfig.java
```

## Layer Explanation

### Domain Layer (`core/domain`)

The heart of the system containing the business logic, entities, and rules.

- **No external dependencies**: Pure Java/Kotlin with no frameworks
- **Domain models**: Rich entities with behavior, not anemic data holders
- **Value objects**: Immutable objects representing domain concepts
- **Domain events**: Objects representing significant state changes

Example:
```java
public class Project {
    private final ProjectId id;
    private ProjectName name;
    private ProjectStatus status;
    
    public void activate() {
        if (this.status == ProjectStatus.DRAFT) {
            this.status = ProjectStatus.ACTIVE;
        }
    }
}
```

### Application Layer (`core/application`)

Coordinates the domain objects to perform use cases.

- **Ports (interfaces)**: Define how external systems interact with the application
- **Input ports**: Used by controllers/UI to call the application
- **Output ports**: Define what the application needs from infrastructure
- **Services**: Implement the use cases, orchestrate domain objects

Example:
```java
public interface ProjectManagementUseCase {  // Input port
    ProjectId createProject(ProjectName name, ProjectDescription description);
}

public interface ProjectRepository {  // Output port
    void save(Project project);
    Optional<Project> findById(ProjectId id);
}
```

### Infrastructure Layer (`infrastructure`)

Implements the technical concerns required by the application.

- **Adapters**: Connect the application to external systems
- **Persistence**: Database implementation (JPA, MongoDB, etc.)
- **API**: REST controllers, GraphQL resolvers
- **Messaging**: Message broker integration

Example:
```java
@RestController
public class ProjectController {
    private final ProjectManagementUseCase projectManagementUseCase;
    // Methods mapping HTTP requests to use case calls
}

@Component
public class ProjectJpaAdapter implements ProjectRepository {
    private final ProjectJpaRepository jpaRepository;
    // Methods implementing ProjectRepository using JPA
}
```

### Bootstrap Layer (`bootstrap`)

Configures and starts the application.

- **Configuration**: Spring configuration, properties
- **Dependency Injection**: Wires components together
- **Main class**: Application entry point

## Working with the Architecture

### Adding New Features

1. **Start with the Domain**: Define entities and business rules
2. **Define Ports**: Create interfaces for input and output
3. **Implement Services**: Use case implementation with domain objects
4. **Add Adapters**: Connect to the technical world (databases, APIs)

### Testing Strategy

1. **Domain Tests**: Pure unit tests, no mocks
2. **Application Tests**: Mock the output ports (repositories)
3. **Adapter Tests**: Test adapters with test doubles or real systems
4. **Integration Tests**: Test interactions between components
5. **E2E Tests**: Test the full system

## Benefits of This Architecture

1. **Technology Independence**: Domain isn't tied to frameworks
2. **Maintainability**: Clear separation of concerns
3. **Testability**: Easy to test in isolation
4. **Flexibility**: Easier to adopt new technologies
5. **Future-Proofing**: Path to microservices if needed

## Example Flow: Creating a Project

1. REST request comes to `ProjectController`
2. Controller calls `ProjectManagementUseCase.createProject()`
3. `ProjectManagementService` creates a `Project` domain entity
4. Service calls `ProjectRepository.save()`
5. `ProjectJpaAdapter` maps domain entity to JPA entity and saves
6. Service publishes `ProjectCreatedEvent`
7. Event listeners react to the event (e.g., milestone creation)

## Maintaining the Architecture

- Use static code analysis to enforce dependencies
- Regular architecture reviews
- Document architectural decisions
- Ensure new team members understand the architecture

## Common Pitfalls

- Leaking infrastructure concerns into domain
- Anemic domain models (behavior in services only)
- Complex mapping between layers
- Over-engineering for simple use cases