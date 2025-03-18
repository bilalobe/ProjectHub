package com.projecthub.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@AnalyzeClasses(packages = "com.projecthub")
public class ExceptionArchitectureTest {

    @ArchTest
    static final ArchRule exceptions_should_be_hierarchical = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("Exception")
        .should().beAssignableTo(RuntimeException.class)
        .orShould().beAssignableTo(Exception.class)
        .andShould().bePublic()
        .because("Exceptions should follow a consistent hierarchy");

    @ArchTest
    static final ArchRule domain_exceptions_should_be_in_domain = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("Exception")
        .and().areNotAssignableTo(RuntimeException.class)
        .should().resideInAPackage("..domain..")
        .because("Domain exceptions should be in the domain package");

    @ArchTest
    static final ArchRule controllers_should_handle_exceptions = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(RestController.class)
        .should(new ControllerExceptionHandlingCondition())
        .because("Controllers should handle their exceptions");

    @ArchTest
    static final ArchRule global_exception_handlers_should_be_consistent = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(ControllerAdvice.class)
        .should().implement("com.projecthub.core.error.GlobalExceptionHandler")
        .andShould().beAnnotatedWith(Order.class)
        .because("Global exception handlers should follow consistent patterns");

    @ArchTest
    static final ArchRule exception_handlers_should_return_error_response = ArchRuleDefinition.methods()
        .that().areAnnotatedWith(ExceptionHandler.class)
        .should().haveRawReturnType(ResponseEntity.class)
        .andShould().beAnnotatedWith(ResponseStatus.class)
        .because("Exception handlers should return proper error responses");

    @ArchTest
    static final ArchRule business_exceptions_should_be_documented = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("Exception")
        .and().areNotAssignableTo(RuntimeException.class)
        .should().beAnnotatedWith(ApiResponse.class)
        .because("Business exceptions should be documented in API specs");

    @ArchTest
    static final ArchRule exception_constructors_should_be_consistent = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("Exception")
        .should().haveOnlyFinalFields()
        .andShould().haveOnlyPrivateConstructors()
        .orShould().haveOnlyConstructorsThat()
        .arePublic()
        .andTakeParameter(String.class)
        .orTakeParameter(Throwable.class)
        .orTakeParameter(String.class, Throwable.class)
        .because("Exception constructors should follow standard patterns");

    @ArchTest
    static final ArchRule exception_messages_should_be_internationalized = ArchRuleDefinition.fields()
        .that().areDeclaredInClassesThat().haveSimpleNameEndingWith("Exception")
        .and().haveModifier(STATIC)
        .and().haveModifier(FINAL)
        .and().haveNameMatching(".*_KEY")
        .should().haveRawType(String.class)
        .because("Exception messages should use message keys for i18n");

    @ArchTest
    static final ArchRule security_exceptions_should_be_audited = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("SecurityException")
        .should().beAnnotatedWith(AuditLog.class)
        .because("Security exceptions must be audited");

    @ArchTest
    static final ArchRule validation_exceptions_should_provide_details = ArchRuleDefinition.classes()
        .that().haveSimpleNameEndingWith("ValidationException")
        .should().implement("com.projecthub.core.validation.ValidationFailureProvider")
        .because("Validation exceptions should provide detailed failure information");

    public ExceptionArchitectureTest() {
    }
}
