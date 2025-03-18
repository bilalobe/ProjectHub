package com.projecthub.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@AnalyzeClasses(packages = "com.projecthub")
public class PerformanceArchitectureTest {

    @ArchTest
    static final ArchRule prevent_n_plus_1_queries = ArchRuleDefinition.methods()
        .that().areAnnotatedWith(Query.class)
        .and().areDeclaredInClassesThat().areAnnotatedWith(Repository.class)
        .should(new NoPotentialNPlusOneCondition())
        .because("Queries should not cause N+1 problems");

    @ArchTest
    static final ArchRule expensive_operations_should_be_cached = ArchRuleDefinition.methods()
        .that().areAnnotatedWith(ExpensiveOperation.class)
        .should().beAnnotatedWith(Cacheable.class)
        .andShould().beAnnotatedWith(MonitorPerformance.class)
        .because("Expensive operations should use caching");

    @ArchTest
    static final ArchRule batch_operations_should_use_pagination = ArchRuleDefinition.methods()
        .that().haveNameMatching(".*All.*|.*Batch.*|.*Bulk.*")
        .and().areDeclaredInClassesThat().areAnnotatedWith(Repository.class)
        .should().beAnnotatedWith(BatchSize.class)
        .orShould().beAnnotatedWith(Pageable.class)
        .because("Batch operations must use pagination");

    @ArchTest
    static final ArchRule eager_loads_must_be_justified = ArchRuleDefinition.fields()
        .that().areDeclaredInClassesThat().areAnnotatedWith(Entity.class)
        .and().areAnnotatedWith(OneToMany.class)
        .or().areAnnotatedWith(ManyToMany.class)
        .should().beAnnotatedWith(JustifyEagerLoad.class)
        .orShould().notBeAnnotatedWith(FetchType.EAGER)
        .because("Eager loading must be explicitly justified");

    @ArchTest
    static final ArchRule heavy_computations_should_be_async = ArchRuleDefinition.methods()
        .that().areAnnotatedWith(HeavyComputation.class)
        .should().beAnnotatedWith(Async.class)
        .andShould().haveRawReturnType(CompletableFuture.class)
        .because("Heavy computations should be executed asynchronously");

    @ArchTest
    static final ArchRule cache_configurations_must_be_documented = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(CacheConfig.class)
        .should().beAnnotatedWith(CacheConfiguration.class)
        .andShould().implement("com.projecthub.core.cache.CacheConfigurationAware")
        .because("Cache configurations must be documented and consistent");

    @ArchTest
    static final ArchRule large_responses_should_be_paginated = ArchRuleDefinition.methods()
        .that().areAnnotatedWith(GetMapping.class)
        .and().haveRawReturnType(List.class)
        .or().haveRawReturnType(Set.class)
        .should().beAnnotatedWith(PaginationRequired.class)
        .because("API endpoints returning collections should be paginated");

    @ArchTest
    static final ArchRule long_transactions_must_be_identified = ArchRuleDefinition.methods()
        .that().areAnnotatedWith(Transactional.class)
        .and().haveModifier(PUBLIC)
        .should().beAnnotatedWith(TransactionBoundary.class)
        .andShould().beAnnotatedWith(MonitorTransactionTime.class)
        .because("Long-running transactions must be identified and monitored");

    @ArchTest
    static final ArchRule resource_intensive_ops_need_circuit_breaker = ArchRuleDefinition.classes()
        .that().areAnnotatedWith(ResourceIntensive.class)
        .should().beAnnotatedWith(CircuitBreaker.class)
        .andShould().implement("com.projecthub.core.resilience.CircuitBreakerAware")
        .because("Resource-intensive operations need circuit breakers");

    @ArchTest
    static final ArchRule prevent_redundant_db_calls = ArchRuleDefinition.methods()
        .that().areDeclaredInClassesThat().areAnnotatedWith(Service.class)
        .should(new NoRedundantDatabaseCallsCondition())
        .because("Services should not make redundant database calls");

    public PerformanceArchitectureTest() {
    }
}
