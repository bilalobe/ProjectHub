# ProjectHub Hexagonal Architecture Implementation

## Overview

ProjectHub implements a hexagonal architecture (ports and adapters) to achieve clear separation of concerns, domain isolation, and technology independence. This document outlines how we structure our code and organize components within the hexagonal architecture paradigm.

## Core Principles

1. **Domain-Centric Design**: Business logic remains independent of external systems
2. **Technology Agnosticism**: The domain model doesn't depend on UI, database, or external services
3. **Explicit Boundaries**: Clear interfaces define how the domain interacts with the outside world
4. **Testability**: Domain logic can be tested without external dependencies

## Architecture Layers

### Domain Layer

The domain layer contains:
- Domain entities and value objects
- Business logic and domain services
- Domain events
- Domain exceptions

```kotlin
// Example domain entity
@DomainEntity
class Project(
    val id: ProjectId,
    val name: String,
    val description: String,
    val deadline: LocalDateTime,
    private val tasks: MutableList<Task> = mutableListOf()
) {
    fun addTask(task: Task) {
        validateTaskDeadline(task)
        tasks.add(task)
    }

    private fun validateTaskDeadline(task: Task) {
        if (task.deadline.isAfter(deadline)) {
            throw InvalidTaskDeadlineException("Task deadline cannot be after project deadline")
        }
    }
}
```

### Ports

Ports define interfaces for interacting with external systems:

**Primary/Driving Ports**: Used by the outside world to interact with our application
- Service interfaces
- Event listeners

**Secondary/Driven Ports**: Used by our application to interact with external systems
- Repository interfaces
- External service interfaces
- Messaging interfaces

```kotlin
// Example secondary port
@Port
interface ProjectRepository {
    fun findById(id: ProjectId): Project?
    fun save(project: Project): Project
    fun findByDeadlineBefore(date: LocalDateTime): List<Project>
}
```

### Adapters

Adapters implement ports to connect with specific technologies:

**Primary/Driving Adapters**: 
- REST Controllers (Spring MVC, Ktor)
- GraphQL Resolvers
- UI Components

**Secondary/Driven Adapters**:
- Database Repositories (JPA, R2DBC)
- External API Clients
- Message Brokers

```kotlin
// Example secondary adapter (JPA implementation)
@Adapter
class JpaProjectRepository(
    private val projectJpaRepository: ProjectJpaRepository,
    private val mapper: ProjectMapper
) : ProjectRepository {
    
    override fun findById(id: ProjectId): Project? {
        return projectJpaRepository.findById(id.value)
            .map { mapper.toDomain(it) }
            .orElse(null)
    }
    
    override fun save(project: Project): Project {
        val entity = mapper.toEntity(project)
        return mapper.toDomain(projectJpaRepository.save(entity))
    }
    
    override fun findByDeadlineBefore(date: LocalDateTime): List<Project> {
        return projectJpaRepository.findByDeadlineBefore(date)
            .map { mapper.toDomain(it) }
    }
}
```

### Application Services

Application services orchestrate use cases by:
1. Receiving input from primary adapters
2. Coordinating domain objects
3. Using secondary ports when needed
4. Returning output to primary adapters

```kotlin
@ApplicationService
class ProjectService(
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository,
    private val notificationPort: NotificationPort
) {
    
    fun createProject(command: CreateProjectCommand): ProjectDto {
        val project = Project(
            id = ProjectId.generate(),
            name = command.name,
            description = command.description,
            deadline = command.deadline
        )
        
        val savedProject = projectRepository.save(project)
        notificationPort.sendProjectCreatedNotification(savedProject)
        
        return projectMapper.toDto(savedProject)
    }
    
    // Other use cases...
}
```

## Migration Progress

We are transitioning from a traditional layered architecture to hexagonal architecture:

| Component | Status | Next Steps |
|-----------|--------|------------|
| Domain Model | 80% Complete | Finish extracting business rules from services |
| Ports | 65% Complete | Define messaging and external service ports |
| Adapters | 50% Complete | Implement Ktor adapters, migrate from direct Spring dependencies |
| Application Services | 70% Complete | Remove infrastructure dependencies |

## Discrepancy Remediation

To address architectural discrepancies:

1. Run architecture tests: `./gradlew :modules:foundation:test --tests "*HexagonalArchitectureTest"`
2. Check the discrepancy report: `modules/foundation/build/reports/architecture-discrepancies/`
3. Use the remediation scripts: `./scripts/cleanup-duplicates.sh`

## Benefits Achieved

- **Improved testability**: 90% unit test coverage of domain logic
- **Better maintainability**: Clearer boundaries between components
- **Technology flexibility**: Easier migration from Spring to Ktor
- **Cross-platform support**: Domain logic shared across desktop, web, and mobile

## Future Enhancements

- Complete migration to pure Kotlin domain model
- Implement event sourcing for core domain events
- Integrate reactive repositories with Kotlin Flow
