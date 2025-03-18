package com.projecthub.base.plugin.api;

import java.time.Instant;

/**
 * Data class representing environmental sensor readings
 */
public record EnvironmentalData(
    String sensorId,
    Instant timestamp,
    double temperature,
    double humidity,
    double pressure,
    double gasResistance,
    String status
) {}