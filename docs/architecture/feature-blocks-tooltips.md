# Feature Blocks and Discrepancy Tooltips

This document maps the project's feature blocks to their respective layers in the hexagonal architecture and highlights discrepancies with tooltips.

## Feature Blocks Mapping

### Foundation (Domain & Core)
- **Path**: `src/main/kotlin/com/...`
- **Description**: Defines the domain logic and core interfaces.
- **Discrepancy Tooltip**: 
  > "Foundation should define domain logic, but includes some UI references. Move UI code to `compose-ui`."

---

### Application Layer
- **Path**: `src/main/kotlin/Application.kt`
- **Description**: Orchestrates domain operations and interacts with ports.
- **Discrepancy Tooltip**: 
  > "Application contains direct references to infrastructure code. Introduce ports to reduce coupling."

---

### Adapters (Infrastructure)
- **Examples**: Repositories, controllers, integrations
- **Description**: Implements ports to interact with external systems.
- **Discrepancy Tooltip**: 
  > "Adapters overlapping with domain code. Check [scripts/cleanup-duplicates.sh](../../scripts/cleanup-duplicates.sh) for DB references."

---

## Validation and Cleanup

- **Reference Architecture**: [Overview](overview.adoc)
- **Validation Tests**: [Architecture Tests](architecture-tests.adoc)
- **Cleanup Scripts**: [scripts/cleanup-duplicates.sh](../../scripts/cleanup-duplicates.sh)
