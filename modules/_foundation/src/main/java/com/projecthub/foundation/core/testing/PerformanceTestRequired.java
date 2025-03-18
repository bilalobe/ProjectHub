package com.projecthub.core.testing;

import java.util.concurrent.TimeUnit;

public interface PerformanceTestRequired {
    long getExpectedLatency();
    TimeUnit getLatencyUnit();
    int getConcurrentUsers();
    String[] getPerformanceScenarios();
    PerformanceMetrics getAcceptableMetrics();
    
    interface PerformanceMetrics {
        double getMaxCpuUsage();
        long getMaxMemoryUsage();
        int getMaxDatabaseConnections();
        long getMaxResponseTime();
    }
}