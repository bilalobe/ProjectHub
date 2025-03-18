package com.projecthub.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AnalyzeClasses(packages = "com.projecthub")
public class ApiEvolutionTest {

    @ArchTest
    static final ArchRule api_endpoints_should_be_versioned = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(RestController.class)
        .should().beAnnotatedWith(ApiVersion.class)
        .andShould().beAnnotatedWith(RequestMapping.class)
        .andShould(new ApiVersioningCondition())
        .because("All API endpoints must be properly versioned");

    @ArchTest
    static final ArchRule deprecated_apis_should_have_replacement = ArchRuleDefinition.methods()
        .that().areAnnotatedWith(Deprecated.class)
        .and().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
        .should().beAnnotatedWith(ApiReplacement.class)
        .because("Deprecated APIs must document their replacement");

    @ArchTest
    static final ArchRule breaking_changes_must_increase_major_version = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(BreakingChange.class)
        .and().areAnnotatedWith(ApiVersion.class)
        .should(new MajorVersionIncrementCondition())
        .because("Breaking changes require major version increment");

    @ArchTest
    static final ArchRule api_models_should_be_immutable = ArchRuleDefinition.classes()
        .that().resideInAPackage("..api.model..")
        .should().beAnnotatedWith(Immutable.class)
        .andShould().haveOnlyFinalFields()
        .because("API models should be immutable for versioning stability");

    @ArchTest
    static final ArchRule api_backward_compatibility = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(RestController.class)
        .should().implement("com.projecthub.core.api.BackwardCompatible")
        .because("APIs should maintain backward compatibility");

    @ArchTest
    static final ArchRule versioned_endpoints_require_documentation = ArchRuleDefinition.methods()
        .that().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
        .and().areAnnotatedWith(ApiVersion.class)
        .should().beAnnotatedWith(Operation.class)
        .andShould().beAnnotatedWith(ApiResponse.class)
        .because("Versioned endpoints must be fully documented");

    @ArchTest
    static final ArchRule api_migrations_must_be_tested = ArchRuleDefinition.classes()
        .that().haveNameMatching(".*ApiMigrationTest")
        .should().beAnnotatedWith(ApiMigrationTest.class)
        .andShould().implement("com.projecthub.core.api.VersionMigrationTest")
        .because("API migrations must have corresponding tests");

    @ArchTest
    static final ArchRule api_changes_must_be_logged = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(ApiVersion.class)
        .should().beAnnotatedWith(ApiChangeLog.class)
        .because("API changes must be documented in the changelog");

    @ArchTest
    static final ArchRule versioned_models_must_be_serializable = ArchRuleDefinition.classes()
        .that().resideInAPackage("..api.model.v*..")
        .should().beAnnotatedWith(JsonSerializable.class)
        .andShould().implement(Serializable.class)
        .because("Versioned models must support serialization for compatibility");

    @ArchTest
    static final ArchRule api_consumers_must_specify_version = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(ApiClient.class)
        .should().callMethodWhere(new ApiVersionHeaderCondition())
        .because("API consumers must specify version in requests");

    public ApiEvolutionTest() {
    }
}
