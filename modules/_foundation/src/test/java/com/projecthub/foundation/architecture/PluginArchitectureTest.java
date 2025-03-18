package com.projecthub.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.pf4j.Extension;
import org.pf4j.PluginWrapper;
import org.springframework.plugin.core.Plugin;

@AnalyzeClasses(packages = "com.projecthub.plugins")
public class PluginArchitectureTest {

    @ArchTest
    static final ArchRule plugin_classes_should_follow_conventions = ArchRuleDefinition.classes()
        .that().resideInAPackage("..plugins..")
        .should().beAnnotatedWith(Plugin.class)
        .andShould().bePublic()
        .andShould().haveOnlyFinalFields()
        .because("Plugin classes should be immutable and well-defined");

    @ArchTest
    static final ArchRule plugin_extensions_should_be_properly_marked = ArchRuleDefinition.classes()
        .that().implement(Plugin.class)
        .should().beAnnotatedWith(Extension.class)
        .andShould().bePublic()
        .andShould().haveSimpleNameEndingWith("Plugin");

    @ArchTest
    static final ArchRule plugins_should_not_access_core_internals = ArchRuleDefinition.noClasses()
        .that().resideInAPackage("..plugins..")
        .should().accessClassesThat()
        .resideInAPackage("..foundation.internal..");

    @ArchTest
    static final ArchRule plugin_dependencies_should_be_explicit = ArchRuleDefinition.classes()
        .that().resideInAPackage("..plugins..")
        .should().dependOnClassesThat()
        .resideInAnyPackage(
            "..foundation.api..",
            "..foundation.spi..",
            "java..",
            "org.pf4j..",
            "org.springframework.plugin.."
        )
        .because("Plugins should only depend on public APIs");

    @ArchTest
    static final ArchRule plugin_configuration_location = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(org.springframework.context.annotation.Configuration.class)
        .and().resideInAPackage("..plugins..")
        .should().resideInAPackage("..plugins.*.config")
        .because("Plugin configurations should be in a config package");

    @ArchTest
    static final ArchRule plugin_lifecycle_methods = ArchRuleDefinition.methods()
        .that().areDeclaredInClassesThat().implement(Plugin.class)
        .and().haveName("start", "stop", "delete")
        .should().beAnnotatedWith(Override.class)
        .andShould().bePublic();

    @ArchTest
    static final ArchRule plugin_service_implementation = ArchRuleDefinition.classes()
        .that().resideInAPackage("..plugins.*.service..")
        .should().implement(Plugin.class)
        .orShould().beAnnotatedWith(Extension.class)
        .because("Plugin services should implement plugin interface or be extensions");

    @ArchTest
    static final ArchRule plugin_event_handlers = ArchRuleDefinition.methods()
        .that().areDeclaredInClassesThat().resideInAPackage("..plugins..")
        .and().areAnnotatedWith(org.springframework.context.event.EventListener.class)
        .should().bePublic()
        .andShould().beAnnotatedWith(org.springframework.plugin.core.OrderAwarePluginRegistry.class);

    @ArchTest
    static final ArchRule plugin_wrapper_usage = ArchRuleDefinition.classes()
        .that().areAssignableTo(PluginWrapper.class)
        .should().beAssignableTo(Plugin.class)
        .andShould().bePackagePrivate()
        .because("Plugin wrappers should only be used internally by the plugin system");

    public PluginArchitectureTest() {
    }
}
