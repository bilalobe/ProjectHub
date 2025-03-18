package com.projecthub.core.testing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public abstract class BaseTest {

    protected BaseTest() {
    }

    @BeforeEach
    public void baseSetUp() {
        configureTestEnvironment();
        resetTestState();
        initializeTestData();
    }

    @AfterEach
    public void baseTearDown() {
        cleanupTestData();
        verifyNoUnexpectedChanges();
        resetTestState();
    }

    protected abstract void configureTestEnvironment();
    protected abstract void initializeTestData();
    protected abstract void cleanupTestData();

    protected void resetTestState() {
        // Default implementation can be overridden
    }

    protected void verifyNoUnexpectedChanges() {
        // Default implementation can be overridden
    }

    protected final void assertTestPrerequisites() {
        if (!isTestEnvironment()) {
            throw new IllegalStateException("Tests must run in test environment");
        }
    }

    private static boolean isTestEnvironment() {
        return System.getProperty("spring.profiles.active", "")
            .contains("test");
    }
}
