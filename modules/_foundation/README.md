# Foundation Module

The foundation module serves as the core backend framework of ProjectHub, providing essential services, security implementations, and data management capabilities.

## Architecture

This module implements a modular monolith architecture using Spring Modulith, with clear boundaries between different domains:

```
foundation/
├── src/
│   ├── main/java/com/projecthub/foundation/
│   │   ├── core/              # Core framework components
│   │   │   ├── api/          # API contracts and versioning
│   │   │   ├── architecture/ # Architecture annotations
│   │   │   ├── testing/     # Test support
│   │   │   └── resilience/  # Reliability patterns
│   │   ├── security/         # Security implementation
│   │   ├── domain/          # Domain model
│   │   └── infrastructure/  # Technical infrastructure
│   └── test/java/com/projecthub/foundation/
│       └── architecture/    # Architecture tests
```

## Architectural Validation

The module uses ArchUnit to enforce architectural rules and patterns:

### Key Architecture Rules

1. **Layered Architecture**
   - Strict layering: Controllers → Services → Repositories
   - Domain model protection
   - Clear module boundaries

2. **Security Validation**
   - Mandatory security annotations
   - Proper integration with Fortress
   - Audit logging enforcement

3. **API Evolution**
   - Version control
   - Backward compatibility
   - Migration path documentation

4. **Performance Patterns**
   - N+1 query prevention
   - Caching requirements
   - Pagination enforcement

### Running Architecture Tests

```bash
./gradlew :modules:foundation:test --tests "*ArchitectureTest"
```

## Design Principles

### 1. Module Independence

- Modules communicate through events
- No direct dependencies between modules
- Clear public APIs

### 2. Security First

- All endpoints require explicit security
- Security configurations are internal
- Mandatory security testing

### 3. API Stability

- Semantic versioning
- Breaking change controls
- Migration documentation

### 4. Performance by Design

- Query optimization rules
- Caching strategy
- Resource monitoring

## Testing Strategy

### Architecture Tests

- `ArchitectureTest`: Core architecture rules
- `SecurityArchitectureTest`: Security patterns
- `ApiEvolutionTest`: API versioning rules
- `PerformanceArchitectureTest`: Performance patterns

### Custom Annotations

```java
@SecurityCritical
@ExpensiveOperation
@ApiVersion
```

## Development Guidelines

### Adding New Features

1. **Architecture Compliance**
   - Run architecture tests
   - Use provided annotations
   - Follow layering rules

2. **Security Requirements**
   - Mark security-critical code
   - Include security tests
   - Document security review

3. **API Changes**
   - Version appropriately
   - Document breaking changes
   - Provide migration guides

4. **Performance Considerations**
   - Cache expensive operations
   - Paginate large responses
   - Monitor resource usage

## Configuration

### Database Setup

1. Configure PostgreSQL connection in `application.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/projecthub
       username: ${DB_USER}
       password: ${DB_PASS}
   ```

2. Run migrations:
   ```bash
   ./gradlew flywayMigrate
   ```

### Security Configuration

1. Set up Apache Fortress (see [README-fortress.md](README-fortress.md))
2. Configure JWT settings:
   ```yaml
   app:
     security:
       token:
         access-token-validity-minutes: 30
         refresh-token-validity-days: 7
   ```

## Development

### Building
```bash
./gradlew :modules:foundation:build
```

### Testing
```bash
./gradlew :modules:foundation:test
```

### API Documentation

Once running, access OpenAPI documentation at:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Integration Points

- **Desktop UI**: Via direct Java API calls
- **Web Frontend**: Through REST APIs
- **Mobile Apps**: Through REST APIs
- **External Systems**: Through plugin system

## Monitoring

The module exposes various metrics and health endpoints:
- `/actuator/health` - System health information
- `/actuator/metrics` - Performance metrics
- `/actuator/prometheus` - Prometheus metrics

## Troubleshooting

Common issues and solutions:

1. **Database Connection Issues**
   - Verify PostgreSQL is running
   - Check connection settings in application.yml
   - Ensure database user has required permissions

2. **Security Configuration**
   - Verify LDAP connection for Fortress
   - Check JWT signing key configuration
   - Validate role mappings in security config

## Related Documentation

- [Event System](../../projecthub_documentation-main/docs/pages/docs/architecture/events.md)
- [Security Architecture](../../projecthub_documentation-main/docs/pages/docs/architecture/security.md)
- [Testing Guide](../../projecthub_documentation-main/docs/pages/docs/development/testing.md)