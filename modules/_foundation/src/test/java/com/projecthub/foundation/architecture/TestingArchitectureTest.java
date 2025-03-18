package com.projecthub.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@AnalyzeClasses(packages = "com.projecthub")
public class TestingArchitectureTest {

    @ArchTest
    static final ArchRule security_critical_code_must_have_tests = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(SecurityCritical.class)
        .should().implement("com.projecthub.core.testing.SecurityTestRequired")
        .andShould(new SecurityTestCoverageCondition())
        .because("Security-critical components must have comprehensive tests");

    @ArchTest
    static final ArchRule integration_tests_should_use_testcontainers = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(SpringBootTest.class)
        .and().haveSimpleNameEndingWith("IT")
        .should().dependOnClassesThat().resideInAPackage("org.testcontainers..")
        .because("Integration tests should use Testcontainers for consistency");

    @ArchTest
    static final ArchRule test_data_builders_must_be_immutable = ArchRuleDefinition.classes()
        .that().haveNameMatching(".*Builder")
        .and().resideInAPackage("..test..")
        .should().beAnnotatedWith(ImmutableTestData.class)
        .andShould().haveOnlyFinalFields()
        .because("Test data builders should be immutable");

    @ArchTest
    static final ArchRule repository_tests_should_use_test_transactions = ArchRuleDefinition.methods()
        .that().areAnnotatedWith(Test.class)
        .and().areDeclaredInClassesThat().haveNameMatching(".*RepositoryTest")
        .should().beAnnotatedWith(Transactional.class)
        .andShould().beAnnotatedWith(Rollback.class)
        .because("Repository tests should use test transactions");

    @ArchTest
    static final ArchRule api_tests_must_verify_authorization = ArchRuleDefinition.methods()
        .that().areAnnotatedWith(Test.class)
        .and().areDeclaredInClassesThat().haveNameMatching(".*ControllerTest")
        .should().beAnnotatedWith(WithMockUser.class)
        .orShould().beAnnotatedWith(WithAnonymousUser.class)
        .because("API tests must verify authorization");

    @ArchTest
    static final ArchRule event_tests_must_verify_handlers = ArchRuleDefinition.classes()
        .that().haveNameMatching(".*EventTest")
        .should().implement("com.projecthub.core.testing.EventHandlerVerification")
        .because("Event tests must verify all handlers are called");

    @ArchTest
    static final ArchRule performance_sensitive_code_must_have_benchmarks = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(PerformanceSensitive.class)
        .should().implement("com.projecthub.core.testing.PerformanceTestRequired")
        .because("Performance-sensitive code must have benchmark tests");

    @ArchTest
    static final ArchRule concurrent_code_must_have_stress_tests = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(ThreadSafe.class)
        .should().implement("com.projecthub.core.testing.ConcurrencyTestRequired")
        .because("Concurrent code must have stress tests");

    @ArchTest
    static final ArchRule test_inheritance_hierarchy = ArchRuleDefinition.classes()
        .that().haveNameMatching(".*Test")
        .should().beAssignableTo("com.projecthub.core.testing.BaseTest")
        .because("Tests should follow the standard inheritance hierarchy");

    @ArchTest
    static final ArchRule mocked_dependencies_must_be_interfaces = ArchRuleDefinition.fields()
        .that().areAnnotatedWith(Mock.class)
        .or().areAnnotatedWith(MockBean.class)
        .should().haveRawType(type -> type.isInterface())
        .because("Only interfaces should be mocked, not concrete classes");

    public TestingArchitectureTest() {
    }
}
