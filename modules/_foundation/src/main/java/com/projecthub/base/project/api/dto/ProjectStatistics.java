package com.projecthub.base.project.api.dto;

public record ProjectStatistics(
    long totalProjects,
    long activeProjects,
    long completedProjects,
    long overdueProjects,
    double completionRate,
    double averageDuration,
    long delayedProjects,
    ProjectRiskMetrics riskMetrics
) {
    public record ProjectRiskMetrics(
        double highRiskPercentage,
        double projectHealthIndex
    ) {}
}