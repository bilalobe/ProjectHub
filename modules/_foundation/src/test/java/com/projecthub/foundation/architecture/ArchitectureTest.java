package com.projecthub.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitRunner;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.tngtech.archunit.library.Architectures;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.modulith.core.ApplicationModule;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

@RunWith(ArchUnitRunner.class)
@AnalyzeClasses(packages = "com.projecthub")
public class ArchitectureTest {

    // Layered Architecture Validation
    @ArchTest
    static final ArchRule layered_architecture_is_respected = Architectures.layeredArchitecture()
        .consideringAllDependencies()
        .layer("Controllers").definedBy("..controller..", "..rest..")
        .layer("Services").definedBy("..service..")
        .layer("Repositories").definedBy("..repository..")
        .layer("Domain").definedBy("..domain..", "..model..")

        .whereLayer("Controllers").mayNotBeAccessedByAnyLayer()
        .whereLayer("Services").mayOnlyBeAccessedByLayers("Controllers")
        .whereLayer("Repositories").mayOnlyBeAccessedByLayers("Services")
        .whereLayer("Domain").mayOnlyBeAccessedByLayers("Services", "Repositories", "Controllers");

    // Module Boundaries
    @ArchTest
    static final ArchRule modules_should_not_depend_on_each_other = SlicesRuleDefinition.slices()
        .matching("com.projecthub.(*)..")
        .should().notDependOnEachOther()
        .as("Modules should be independent and communicate through events");

    // Domain Model Protection
    @ArchTest
    static final ArchRule domain_model_classes_should_only_use_domain_packages = ArchRuleDefinition.classes()
        .that().resideInAPackage("..domain..")
        .should().onlyDependOnClassesThat()
        .resideInAnyPackage(
            "..domain..",
            "java..",
            "javax..",
            "org.springframework.modulith..",
            "org.jmolecules.."
        );

    // Security Rules
    @ArchTest
    static final ArchRule security_checks_should_be_used_in_services = ArchRuleDefinition.methods()
        .that().arePublic()
        .and().areDeclaredInClassesThat().areAnnotatedWith(Service.class)
        .and().areNotAnnotatedWith(PermitAll.class)
        .should().beAnnotatedWith(PreAuthorize.class)
        .orShould().beAnnotatedWith(Secured.class);

    // Plugin System Rules
    @ArchTest
    static final ArchRule plugins_should_implement_plugin_interface = ArchRuleDefinition.classes()
        .that().resideInAPackage("..plugins..")
        .should().implement(ProjectHubPlugin.class)
        .orShould().beAnnotatedWith(Plugin.class);

    // Event Handling
    @ArchTest
    static final ArchRule event_handlers_should_be_properly_annotated = ArchRuleDefinition.methods()
        .that().areAnnotatedWith(EventListener.class)
        .should().beAnnotatedWith(TransactionalEventListener.class)
        .orShould().beAnnotatedWith(Async.class)
        .because("Event handlers should be transactional or asynchronous");

    // Service Layer Rules
    @ArchTest
    static final ArchRule services_should_only_be_accessed_by_controllers = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(Service.class)
        .should().onlyBeAccessed().byAnyPackage(
            "..controller..",
            "..rest..",
            "..service..",
            "..config.."
        );

    // Repository Usage Rules
    @ArchTest
    static final ArchRule repositories_should_only_be_accessed_by_services = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(Repository.class)
        .should().onlyBeAccessed().byClassesThat().areAnnotatedWith(Service.class)
        .orShould().beAnnotatedWith(Repository.class);

    // Configuration Rules
    @ArchTest
    static final ArchRule configuration_classes_should_be_in_config_package = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(Configuration.class)
        .should().resideInAPackage("..config..");

    // Modulith Rules
    @ArchTest
    static final ArchRule modulith_boundaries_should_be_respected = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(ApplicationModule.class)
        .should().onlyBeAccessed().byClassesThat()
        .resideInTheSamePackage()
        .orShould().beAnnotatedWith(ApplicationModule.class);

    // MVVM Pattern Rules
    @ArchTest
    static final ArchRule view_models_should_follow_pattern = ArchRuleDefinition.classes()
        .that().haveNameMatching(".*ViewModel")
        .should().implement("com.projecthub.core.viewmodel.ViewModel")
        .andShould().beAnnotatedWith(Scope.class)
        .andShould().onlyDependOnClassesThat().areNotAnnotatedWith(Repository.class);

    // API Contract Rules
    @ArchTest
    static final ArchRule rest_controllers_should_have_proper_annotations = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(RestController.class)
        .should().beAnnotatedWith(RequestMapping.class)
        .andShould().beAnnotatedWith(Tag.class)
        .andShould().beAnnotatedWith(Validated.class);

    // Async Operation Rules
    @ArchTest
    static final ArchRule async_methods_should_return_future = ArchRuleDefinition.methods()
        .that().areAnnotatedWith(Async.class)
        .should().haveRawReturnType(CompletableFuture.class)
        .orShould().haveRawReturnType(Future.class)
        .because("Async methods should return Future types for proper handling");

    // Testing Rules
    @ArchTest
    static final ArchRule test_classes_naming_convention = ArchRuleDefinition.classes()
        .that().resideInAPackage("..test..")
        .and().areTopLevelClasses()
        .should().haveSimpleNameEndingWith("Test")
        .orShould().haveSimpleNameEndingWith("IT")
        .orShould().haveSimpleNameEndingWith("TestCase");

    public ArchitectureTest() {
    }
}
