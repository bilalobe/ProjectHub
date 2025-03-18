/**
 * ProjectHub Foundation Module - Hexagonal Architecture Implementation
 *
 * Package Organization:
 * 
 * com.projecthub.base
 * ├── core/                           # Core business logic
 * │   ├── domain/                     # Domain model (entities, value objects)
 * │   │   ├── common/                 # Shared domain concepts
 * │   │   ├── project/               
 * │   │   ├── workflow/
 * │   │   └── task/
 * │   └── application/                # Use cases and ports
 * │       ├── port/
 * │       │   ├── in/                 # Input ports (use cases)
 * │       │   └── out/                # Output ports (repositories, etc)
 * │       └── service/                # Use case implementations
 * │
 * ├── adapter/                        # Adapters for external systems
 * │   ├── in/                         # Inbound adapters
 * │   │   ├── web/                    # REST controllers
 * │   │   ├── graphql/               # GraphQL resolvers
 * │   │   └── messaging/             # Message consumers
 * │   └── out/                        # Outbound adapters
 * │       ├── persistence/            # Database adapters
 * │       └── messaging/              # Message publishers
 * │
 * └── infrastructure/                 # Technical concerns
 *     ├── config/                     # Configuration
 *     ├── persistence/                # JPA entities & repositories
 *     └── security/                   # Security configuration
 */
package com.projecthub.base;