package com.projecthub.base.submission.infrastructure.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SubmissionMetricsConfig {

    public SubmissionMetricsConfig() {
    }

    @Bean
    public SubmissionMetrics submissionMetrics(MeterRegistry registry) {
        return new SubmissionMetrics(registry);
    }
}
