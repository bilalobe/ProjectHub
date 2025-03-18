# ProjectHub Architecture

## Overview
The ProjectHub application follows a Hexagonal/Clean architecture pattern with the following structure:

```
com.projecthub
│
├── application                # Application layer (business logic)
│   └── project
│       ├── domain            # Domain model and business rules
│       │   ├── Project.kt
│       │   └── ProjectStatus.kt
│       │
│       ├── port              # Ports (interfaces) to be implemented by adapters
│       │   ├── api           # Application service interfaces (primary ports)
│       │   │   └── ProjectService.kt
│       │   │
│       │   ├── repository    # Repository interfaces (secondary ports)
│       │   │   └── ProjectRepositoryPort.kt
│       │   │
│       │   └── event         # Event interfaces (secondary ports)
│       │       └── ProjectEventPublisher.kt
│       │
│       ├── service           # Service implementations
│       │   └── ProjectServiceImpl.kt
│       │
│       └── event             # Domain events
│           └── ProjectEvent.kt
│
└── infrastructure            # Infrastructure layer (adapters)
    └── project
        ├── api               # API controllers (primary adapters)
        │   └── ProjectController.kt
        │
        └── adapter           # Secondary adapters
            ├── repository    # Repository implementations
            │   ├── ProjectJpaEntity.kt
            │   ├── ProjectJpaRepository.kt
            │   └── ProjectRepositoryAdapter.kt
            │
            └── event         # Event publisher implementations
                └── ProjectEventPublisherAdapter.kt
```

## Feature Blocks

### 1. Project Core Domain
- **Purpose**: Defines the core domain model and business rules
- **Key Components**:
  - Project.kt: Core domain entity with business logic
  - ProjectStatus.kt: Domain enumeration

### 2. Project Application Services
- **Purpose**: Application services that implement use cases
- **Key Components**:
  - ProjectServiceImpl.kt: Implementation of the ProjectService interface

### 3. Project API Ports
- **Purpose**: Defines interfaces for the application's API
- **Key Components**:
  - ProjectService.kt: Service interface with DTOs and request/response objects

### 4. Project Repository Ports
- **Purpose**: Defines interfaces for persistence operations
- **Key Components**:
  - ProjectRepositoryPort.kt: Repository interface for data access

### 5. Project Event System
- **Purpose**: Domain events and event publishing interfaces
- **Key Components**:
  - ProjectEvent.kt: Domain events
  - ProjectEventPublisher.kt: Event publishing interface

### 6. Project Infrastructure - API Layer
- **Purpose**: REST API endpoints and controllers
- **Key Components**:
  - ProjectController.kt: REST controller exposing API endpoints

### 7. Project Infrastructure - Persistence Layer
- **Purpose**: Data access and storage
- **Key Components**:
  - ProjectJpaEntity.kt: JPA entity for database mapping
  - ProjectJpaRepository.kt: Spring Data JPA repository interface
  - ProjectRepositoryAdapter.kt: Implementation of the repository port

### 8. Project Infrastructure - Event System
- **Purpose**: Event publishing implementation
- **Key Components**:
  - ProjectEventPublisherAdapter.kt: Implementation of event publishing
```

## Architectural Decisions

1. **Hexagonal Architecture**
   - The application follows a hexagonal architecture separating domain logic from infrastructure concerns
   - Domain logic is completely independent of frameworks and external systems

2. **Domain-Driven Design**
   - Rich domain model with encapsulated business rules
   - Domain events for capturing business-significant occurrences

3. **CQRS-inspired Structure**
   - Clear separation between commands (create, update) and queries (get, list)
   - DTOs used for transferring data across boundaries

4. **Dependency Inversion**
   - Application core defines ports (interfaces)
   - Infrastructure implements adapters for these ports
   - Dependencies point inward toward the domain
