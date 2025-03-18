package com.projecthub.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import org.junit.jupiter.api.Test

/**
 * Architecture tests that verify feature module boundaries and enforce hexagonal architecture
 */
class FeatureModuleBoundaryTest {

    private val basePackage = "com.projecthub"

    private val classes: JavaClasses = ClassFileImporter()
        .withImportOption(ImportOption.DoNotIncludeTests())
        .importPackages(basePackage)

    @Test
    fun `domain model should not depend on application or infrastructure`() {
        // Define architecture rule
        val rule: ArchRule = noClasses()
            .that().resideInAPackage("$basePackage.domain..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "$basePackage.application..",
                "$basePackage.infrastructure..",
                "$basePackage.interfaces.."
            )

        // Validate rule
        rule.check(classes)
    }

    @Test
    fun `application layer should not depend on infrastructure or interfaces`() {
        val rule: ArchRule = noClasses()
            .that().resideInAPackage("$basePackage.application..")
            .should().dependOnClassesThat().resideInAnyPackage(
                "$basePackage.infrastructure..",
                "$basePackage.interfaces.."
            )

        rule.check(classes)
    }

    @Test
    fun `feature packages should not have cyclic dependencies`() {
        val rule: ArchRule = slices()
            .matching("$basePackage.(*)..") // Capture top-level feature packages
            .should().beFreeOfCycles()

        rule.check(classes)
    }

    @Test
    fun `infrastructure adapters must implement domain interfaces`() {
        val rule: ArchRule = classes()
            .that().resideInAPackage("$basePackage.infrastructure.persistence..*Adapter")
            .should().implement().anInterfaceOrClass("$basePackage.domain..*Repository")

        rule.check(classes)
    }

    @Test
    fun `each feature module should have its own events`() {
        val rule: ArchRule = classes()
            .that().resideInAPackage("$basePackage.domain.*.event..*Event")
            .should().beAssignableTo("$basePackage.domain.event.DomainEvent")

        rule.check(classes)
    }
}
