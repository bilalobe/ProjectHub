package com.projecthub

/**
 * Foundation module providing core infrastructure for all ProjectHub modules.
 *
 * This module contains shared components, DDD building blocks, CQRS infrastructure,
 * and event sourcing capabilities.
 *
 * Note: This is a Kotlin representation of the Java module-info definition.
 * Kotlin doesn't directly support Java 9 modules, but we maintain this representation
 * for documentation purposes.
 *
 * Original Java module definition includes:
 * - Requirements: java.base, java.desktop, java.sql, java.annotation, spring.* modules
 * - Exports: com.projecthub.domain, com.projecthub.application,
 *            com.projecthub.events, com.projecthub.config
 * - Opens various packages to Spring for reflection
 */

@org.springframework.modulith.NamedInterface("foundation-domain")
package com.projecthub.domain;
