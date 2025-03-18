package com.projecthub.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@AnalyzeClasses(packages = "com.projecthub")
public class SecurityArchitectureTest {

    @ArchTest
    static final ArchRule sensitive_operations_should_be_secured = ArchRuleDefinition.methods()
        .that().arePublic()
        .and().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
        .and().areNotAnnotatedWith(Public.class)
        .should().beAnnotatedWith(PreAuthorize.class)
        .orShould().beAnnotatedWith(Secured.class)
        .because("All public REST endpoints should be secured unless explicitly marked as public");

    @ArchTest
    static final ArchRule security_configuration_should_be_internal = ArchRuleDefinition.classes()
        .that().resideInAPackage("..security.config..")
        .should().bePackagePrivate()
        .andShould().notBeAnnotatedWith(Public.class)
        .because("Security configurations should not be exposed");

    @ArchTest
    static final ArchRule fortress_integration_points = ArchRuleDefinition.classes()
        .that().haveNameMatching(".*Fortress.*")
        .should().resideInAPackage("..security.fortress..")
        .andShould().implement("org.apache.directory.fortress.core.interfaces.AccessMgr")
        .orShould().implement("org.apache.directory.fortress.core.interfaces.AdminMgr")
        .because("Fortress integration should be properly structured");

    @ArchTest
    static final ArchRule security_event_logging = ArchRuleDefinition.methods()
        .that().areDeclaredInClassesThat().resideInAPackage("..security..")
        .and().areAnnotatedWith(Secured.class)
        .or().areAnnotatedWith(PreAuthorize.class)
        .should().beAnnotatedWith(AuditLog.class)
        .because("Security-related operations must be audited");

    @ArchTest
    static final ArchRule passkey_webauthn_structure = ArchRuleDefinition.classes()
        .that().haveNameMatching(".*Passkey.*|.*WebAuthn.*")
        .should().resideInAPackage("..security.webauthn..")
        .andShould().dependOnClassesThat()
        .resideInAnyPackage(
            "com.yubico.webauthn..",
            "..security.model..",
            "java.."
        );

    @ArchTest
    static final ArchRule security_filters_chain = ArchRuleDefinition.classes()
        .that().haveNameMatching(".*Filter")
        .and().resideInAPackage("..security..")
        .should().implement("org.springframework.web.filter.OncePerRequestFilter")
        .because("Security filters should only execute once per request");

    @ArchTest
    static final ArchRule jwt_token_handling = ArchRuleDefinition.classes()
        .that().haveNameMatching(".*JWT.*|.*Token.*")
        .should().resideInAPackage("..security.jwt..")
        .andShould().beAnnotatedWith(SecurityComponent.class)
        .because("JWT components should be properly organized and marked");

    @ArchTest
    static final ArchRule role_based_access = ArchRuleDefinition.methods()
        .that().areAnnotatedWith(PreAuthorize.class)
        .should().haveNameMatching(".*")
        .andShould().beDeclaredInClassesThat()
        .areAnnotatedWith(RestController.class)
        .orShould().beAnnotatedWith(Service.class);

    @ArchTest
    static final ArchRule security_exception_handling = ArchRuleDefinition.classes()
        .that().haveNameMatching(".*SecurityException")
        .should().beAssignableTo(RuntimeException.class)
        .andShould().bePublic()
        .andShould().resideInAPackage("..security.exception..");

    @ArchTest
    static final ArchRule security_utils_immutability = ArchRuleDefinition.classes()
        .that().resideInAPackage("..security.util..")
        .should().haveOnlyPrivateConstructors()
        .andShould().haveOnlyFinalFields()
        .because("Security utility classes should be immutable");

    public SecurityArchitectureTest() {
    }
}
