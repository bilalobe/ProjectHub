package com.projecthub.core.resilience;

import java.time.Duration;

public interface CircuitBreakerAware {
    int getFailureThreshold();
    Duration getResetTimeout();
    String getFallbackMethod();
    boolean isFailFast();
    Class<? extends Exception>[] getHandledExceptions();
    MetricsCollector getMetricsCollector();
    
    interface MetricsCollector {
        void recordSuccess();
        void recordFailure(Throwable error);
        double getErrorRate();
        long getTotalCalls();
    }
}