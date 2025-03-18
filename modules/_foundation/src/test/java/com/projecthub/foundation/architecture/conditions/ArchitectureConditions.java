package com.projecthub.architecture.conditions;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.jetbrains.annotations.NonNls;
import org.springframework.data.jpa.repository.Query;

import java.util.Locale;

public class ArchitectureConditions {

    public ArchitectureConditions() {
    }

    public static class NoPotentialNPlusOneCondition extends ArchCondition<JavaMethod> {
        public NoPotentialNPlusOneCondition() {
            super("should not have potential N+1 query issues");
        }

        @Override
        public void check(JavaMethod method, ConditionEvents events) {
            Query query = method.getAnnotationOfType(Query.class);
            if (query != null && !containsJoinFetch(query.value())) {
                boolean hasCollectionMapping = method.getRawReturnType().isAssignableTo(Collection.class);
                if (hasCollectionMapping && !method.isAnnotatedWith("BatchSize.class")) {
                    String message = String.format(
                        "Method %s may cause N+1 queries. Consider using JOIN FETCH or @BatchSize",
                        method.getFullName());
                    events.add(SimpleConditionEvent.violated(method, message));
                }
            }
        }

        private static boolean containsJoinFetch(String query) {
            return query.toLowerCase(Locale.ROOT).contains("join fetch");
        }
    }

    public static class ApiVersioningCondition extends ArchCondition<JavaClass> {
        public ApiVersioningCondition() {
            super("should follow API versioning rules");
        }

        @Override
        public void check(JavaClass clazz, ConditionEvents events) {
            if (!hasVersionInPath(clazz) && !hasVersionHeader(clazz)) {
                String message = String.format(
                    "API %s must specify version either in path (/v1/) or through @ApiVersion",
                    clazz.getName());
                events.add(SimpleConditionEvent.violated(clazz, message));
            }
        }

        private static boolean hasVersionInPath(JavaClass clazz) {
            return clazz.getAnnotationOfType(RequestMapping.class)
                .value()[0].matches(".*/v\\d+/.*");
        }

        private static boolean hasVersionHeader(JavaClass clazz) {
            return clazz.isAnnotatedWith(ApiVersion.class);
        }
    }

    public static class SecurityTestCoverageCondition extends ArchCondition<JavaClass> {
        public SecurityTestCoverageCondition() {
            super("should have comprehensive security tests");
        }

        @Override
        public void check(JavaClass clazz, ConditionEvents events) {
            @NonNls String testClassName = clazz.getName() + "SecurityTest";
            boolean hasSecurityTest = clazz.getSourceCodeLocation()
                .contains(JavaClass.Predicates.name(testClassName));

            if (!hasSecurityTest) {
                String message = String.format(
                    "Security-critical class %s must have a dedicated security test class",
                    clazz.getName());
                events.add(SimpleConditionEvent.violated(clazz, message));
            }
        }
    }

    public static class NoRedundantDatabaseCallsCondition extends ArchCondition<JavaMethod> {
        public NoRedundantDatabaseCallsCondition() {
            super("should not contain redundant database calls");
        }

        @Override
        public void check(JavaMethod method, ConditionEvents events) {
            AccessesToRepositoryMethods accessCollector = new AccessesToRepositoryMethods();
            method.getMethodCallsFromSelf().forEach(call -> {
                if (isRepositoryMethod(call) && accessCollector.isDuplicate(call)) {
                    String message = String.format(
                        "Method %s makes redundant call to repository method %s",
                        method.getName(), call.getTargetName());
                    events.add(SimpleConditionEvent.violated(method, message));
                }
            });
        }

        private static boolean isRepositoryMethod(JavaMethodCall call) {
            return call.getTargetOwner().isAnnotatedWith(Repository.class);
        }
    }

    private static class AccessesToRepositoryMethods {
        private final Set<String> accessedMethods = new HashSet<>();

        boolean isDuplicate(JavaMethodCall call) {
            return !accessedMethods.add(getMethodSignature(call));
        }

        @NonNls
        @NonNls
        private static String getMethodSignature(JavaMethodCall call) {
            return call.getTargetOwner().getName() + "#" + call.getTargetName();
        }
    }
}
